// ============================================================================
// RF-09: CONTROL DE INVENTARIO DE PIEZAS
// Módulo: ControlInventarioPiezas
//
// INSTRUCCIONES DE INTEGRACIÓN:
// 1. Importar esta clase en RegistroKilometraje.java
// 2. En initComponents(), agregar al JTabbedPane (solo admin):
//
//      if (esAdministrador) {
//          tabs.addTab("Inventario Piezas", new ControlInventarioPiezas(
//              DB_URL, DB_USER, DB_PASS, usuarioIdSesion).crearPanel());
//      }
//
// 3. Asegurarse que DB_URL, DB_USER, DB_PASS estén accesibles
//    (ya sea pasándolos al constructor o usando constantes del mismo paquete)
//
// DEPENDENCIAS SQL: Ejecutar inventario_piezas_rf09.sql antes de usar
// ============================================================================

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;

/**
 * Módulo RF-09 – Control de Inventario de Piezas de Repuesto.
 *
 * Permite:
 *  - Ver el inventario completo con estado y cantidades
 *  - Registrar piezas nuevas
 *  - Registrar ingresos y salidas con trazabilidad
 *  - Ver historial de movimientos de cada pieza
 *  - Detectar piezas con stock bajo (cantidad_actual < cantidad_minima)
 */
public class ControlInventarioPiezas {

    // ── Conexión ──────────────────────────────────────────────────────────────
    private final String dbUrl;
    private final String dbUser;
    private final String dbPass;
    private final int usuarioIdSesion;

    // ── Componentes de tabla de inventario ───────────────────────────────────
    private JTable tablaInventario;
    private DefaultTableModel modeloInventario;

    // ── Componentes de tabla de movimientos ──────────────────────────────────
    private JTable tablaMovimientos;
    private DefaultTableModel modeloMovimientos;

    // ── Campos del formulario "Nueva Pieza" ───────────────────────────────────
    private JTextField campoNombre;
    private JComboBox<String> comboTipoPieza;
    private JTextArea areaDescripcion;
    private JTextField campoCantidadInicial;
    private JTextField campoCantidadMinima;
    private JComboBox<String> comboEstado;
    private JTextField campoUbicacion;
    private JTextField campoProveedor;
    private JTextField campoPrecio;

    // ── Campos del formulario "Registrar Movimiento" ──────────────────────────
    private JComboBox<PiezaItem> comboPiezaMovimiento;
    private JComboBox<String> comboTipoMovimiento;
    private JTextField campoCantidadMovimiento;
    private JTextField campoMotivoMovimiento;
    private JTextArea areaObservacionMovimiento;

    // ── Etiquetas de estado para movimiento ──────────────────────────────────
    private JLabel labelStockActual;
    private JLabel labelAlertaStock;

    // ── Internos ──────────────────────────────────────────────────────────────
    private static final Color COLOR_CABECERA   = new Color(84, 97, 132);
    private static final Color COLOR_FONDO      = new Color(245, 245, 245);
    private static final Color COLOR_ALERTA     = new Color(190, 60, 40);
    private static final Color COLOR_OK         = new Color(40, 140, 70);
    private static final Color COLOR_ADVERTENCIA = new Color(190, 130, 0);

    /** Convierte "hardware_fuente" → "hardware fuente" solo para mostrar en UI. */
    private static String formatearTipo(String tipo) {
        return tipo == null ? "" : tipo.replace('_', ' ');
    }

    private static final String[] TIPOS_PIEZA = {
        "hardware_almacenamiento",
        "hardware_memoria",
        "hardware_refrigeracion",
        "hardware_fuente",
        "hardware_otro",
        "consumible_tinta",
        "consumible_papel",
        "consumible_toner",
        "consumible_otro",
        "repuesto_impresora",
        "repuesto_proyector",
        "repuesto_general"
    };

    private static final String[] ESTADOS_PIEZA = {
        "disponible",
        "en_reparacion",
        "fuera_de_servicio",
        "agotado"
    };

    // ── Constructor ───────────────────────────────────────────────────────────

    public ControlInventarioPiezas(String dbUrl, String dbUser, String dbPass, int usuarioIdSesion) {
        this.dbUrl = dbUrl;
        this.dbUser = dbUser;
        this.dbPass = dbPass;
        this.usuarioIdSesion = usuarioIdSesion;
    }

    // ── Conexión helper ───────────────────────────────────────────────────────

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbUrl, dbUser, dbPass);
    }

    // ── Inner class PiezaItem ─────────────────────────────────────────────────

    private static class PiezaItem {
        private final int id;
        private final String nombre;
        private final int stockActual;
        private final int stockMinimo;

        PiezaItem(int id, String nombre, int stockActual, int stockMinimo) {
            this.id = id;
            this.nombre = nombre;
            this.stockActual = stockActual;
            this.stockMinimo = stockMinimo;
        }

        int getId()          { return id; }
        int getStockActual() { return stockActual; }
        int getStockMinimo() { return stockMinimo; }

        @Override
        public String toString() { return nombre; }
    }

    // ── Panel principal ───────────────────────────────────────────────────────

    /**
     * Construye y retorna el panel completo del módulo RF-09.
     * Llamar este método y agregar el resultado como pestaña en JTabbedPane.
     */
    public JPanel crearPanel() {
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(COLOR_FONDO);

        // Cabecera
        JPanel cabecera = new JPanel();
        cabecera.setBackground(COLOR_CABECERA);
        JLabel titulo = new JLabel("Control de Inventario de Piezas");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titulo.setForeground(Color.WHITE);
        cabecera.add(titulo);

        // Sub-pestañas internas
        JTabbedPane subTabs = new JTabbedPane();
        subTabs.addTab("📦 Inventario", crearSubPanelInventario());
        subTabs.addTab("➕ Nueva Pieza", crearSubPanelNuevaPieza());
        subTabs.addTab("🔄 Registrar Movimiento", crearSubPanelMovimiento());
        subTabs.addTab("📋 Historial de Movimientos", crearSubPanelHistorial());

        panelPrincipal.add(cabecera, BorderLayout.NORTH);
        panelPrincipal.add(subTabs, BorderLayout.CENTER);
        return panelPrincipal;
    }

    // =========================================================================
    // SUB-PANEL 1: INVENTARIO
    // =========================================================================

    private JPanel crearSubPanelInventario() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Tabla
        modeloInventario = new DefaultTableModel(
            new String[]{"ID", "Nombre", "Tipo", "Stock Actual", "Mínimo", "Estado", "Ubicación", "Proveedor", "Precio Unit."},
            0
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaInventario = new JTable(modeloInventario);
        tablaInventario.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        tablaInventario.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        tablaInventario.setRowHeight(22);
        tablaInventario.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Ocultar columna ID
        tablaInventario.getColumnModel().getColumn(0).setMinWidth(0);
        tablaInventario.getColumnModel().getColumn(0).setMaxWidth(0);
        tablaInventario.getColumnModel().getColumn(0).setWidth(0);

        // Colorear filas con stock bajo o estados críticos
        tablaInventario.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                if (!sel) {
                    int stock   = (int) t.getValueAt(row, 3);
                    int minimo  = (int) t.getValueAt(row, 4);
                    String est  = (String) t.getValueAt(row, 5);

                    if ("fuera_de_servicio".equals(est) || "agotado".equals(est)) {
                        c.setBackground(new Color(255, 220, 215));
                    } else if (stock <= minimo) {
                        c.setBackground(new Color(255, 250, 200));
                    } else {
                        c.setBackground(Color.WHITE);
                    }
                }
                return c;
            }
        });

        JScrollPane scroll = new JScrollPane(tablaInventario);

        // Barra de botones
        JButton btnRefrescar = crearBoton("Refrescar", new Color(70, 130, 180));
        JButton btnVerMovimientos = crearBoton("Ver movimientos de pieza", new Color(84, 97, 132));

        btnRefrescar.addActionListener(e -> cargarInventario());
        btnVerMovimientos.addActionListener(e -> verMovimientosDePiezaSeleccionada());

        // Leyenda
        JPanel leyenda = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 2));
        leyenda.setOpaque(false);
        leyenda.add(crearChip("Fondo rojo: stock agotado / fuera de servicio", new Color(255, 220, 215)));
        leyenda.add(crearChip("Fondo amarillo: stock bajo (≤ mínimo)", new Color(255, 250, 200)));

        JPanel botonesPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botonesPanel.setOpaque(false);
        botonesPanel.add(btnRefrescar);
        botonesPanel.add(btnVerMovimientos);

        JPanel sur = new JPanel(new BorderLayout());
        sur.setOpaque(false);
        sur.add(leyenda, BorderLayout.WEST);
        sur.add(botonesPanel, BorderLayout.EAST);

        panel.add(scroll, BorderLayout.CENTER);
        panel.add(sur, BorderLayout.SOUTH);

        cargarInventario();
        return panel;
    }

    private JLabel crearChip(String texto, Color color) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lbl.setOpaque(true);
        lbl.setBackground(color);
        lbl.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
        return lbl;
    }

    private void cargarInventario() {
        modeloInventario.setRowCount(0);
        String sql = "SELECT id, nombre, tipo_pieza, cantidad_actual, cantidad_minima, " +
                     "estado, COALESCE(ubicacion_almacen,'—') AS ubicacion, " +
                     "COALESCE(proveedor,'—') AS proveedor, " +
                     "COALESCE(precio_unitario, 0) AS precio " +
                     "FROM inventario_piezas " +
                     "ORDER BY estado ASC, nombre ASC";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                modeloInventario.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    formatearTipo(rs.getString("tipo_pieza")),
                    rs.getInt("cantidad_actual"),
                    rs.getInt("cantidad_minima"),
                    rs.getString("estado"),
                    rs.getString("ubicacion"),
                    rs.getString("proveedor"),
                    String.format("$%.0f", rs.getDouble("precio"))
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                "Error al cargar inventario:\n" + ex.getMessage(),
                "Error BD", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void verMovimientosDePiezaSeleccionada() {
        int fila = tablaInventario.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(null,
                "Selecciona una pieza en la tabla para ver sus movimientos.",
                "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int piezaId = (int) tablaInventario.getValueAt(fila, 0);
        String nombre = (String) tablaInventario.getValueAt(fila, 1);
        mostrarHistorialPieza(piezaId, nombre);
    }

    // =========================================================================
    // SUB-PANEL 2: NUEVA PIEZA
    // =========================================================================

    private JPanel crearSubPanelNuevaPieza() {
        Font labelFont = new Font("Segoe UI", Font.PLAIN, 11);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_FONDO);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(COLOR_FONDO);
        form.setBorder(BorderFactory.createEmptyBorder(18, 24, 18, 24));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(7, 8, 7, 8);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;

        campoNombre           = new JTextField(25);
        comboTipoPieza = new JComboBox<>(TIPOS_PIEZA);
        comboTipoPieza.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setText(formatearTipo((String) value));
                return this;
            }
        });
        areaDescripcion       = new JTextArea(3, 25);
        areaDescripcion.setLineWrap(true);
        areaDescripcion.setWrapStyleWord(true);
        campoCantidadInicial  = new JTextField("0", 8);
        campoCantidadMinima   = new JTextField("1", 8);
        comboEstado           = new JComboBox<>(ESTADOS_PIEZA);
        campoUbicacion        = new JTextField(25);
        campoProveedor        = new JTextField(25);
        campoPrecio           = new JTextField("0", 10);

        Object[][] campos = {
            {"Nombre de la pieza: *", campoNombre},
            {"Tipo de pieza: *",      comboTipoPieza},
            {"Descripción:",          new JScrollPane(areaDescripcion)},
            {"Cantidad inicial: *",   campoCantidadInicial},
            {"Cantidad mínima: *",    campoCantidadMinima},
            {"Estado: *",             comboEstado},
            {"Ubicación en almacén:", campoUbicacion},
            {"Proveedor:",            campoProveedor},
            {"Precio unitario ($):",  campoPrecio},
        };

        for (int i = 0; i < campos.length; i++) {
            c.gridx = 0; c.gridy = i; c.weightx = 0;
            JLabel lbl = new JLabel((String) campos[i][0]);
            lbl.setFont(labelFont);
            form.add(lbl, c);

            c.gridx = 1; c.weightx = 1.0;
            form.add((Component) campos[i][1], c);
        }

        JButton btnGuardar = crearBoton("Guardar Pieza", COLOR_OK);
        JButton btnLimpiar = crearBoton("Limpiar", new Color(130, 130, 130));
        btnGuardar.addActionListener(e -> guardarNuevaPieza());
        btnLimpiar.addActionListener(e -> limpiarFormNuevaPieza());

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botones.setOpaque(false);
        botones.add(btnLimpiar);
        botones.add(btnGuardar);

        c.gridx = 1; c.gridy = campos.length; c.weightx = 0;
        form.add(botones, c);

        JScrollPane scrollForm = new JScrollPane(form);
        scrollForm.setBorder(null);
        panel.add(scrollForm, BorderLayout.CENTER);
        return panel;
    }

    private void guardarNuevaPieza() {
        String nombre = campoNombre.getText().trim();
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(null, "El nombre de la pieza es obligatorio.",
                "Campo requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int cantInicial, cantMinima;
        double precio;
        try {
            cantInicial = Integer.parseInt(campoCantidadInicial.getText().trim());
            cantMinima  = Integer.parseInt(campoCantidadMinima.getText().trim());
            precio      = Double.parseDouble(campoPrecio.getText().trim().replace(",", "."));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null,
                "Cantidad inicial, mínima y precio deben ser valores numéricos.",
                "Error de formato", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (cantInicial < 0 || cantMinima < 0 || precio < 0) {
            JOptionPane.showMessageDialog(null,
                "Los valores numéricos no pueden ser negativos.",
                "Error de validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String sql = "INSERT INTO inventario_piezas " +
                     "(nombre, tipo_pieza, descripcion, cantidad_actual, cantidad_minima, " +
                     "estado, ubicacion_almacen, proveedor, precio_unitario) " +
                     "VALUES (?,?,?,?,?,?,?,?,?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, nombre);
            ps.setString(2, (String) comboTipoPieza.getSelectedItem());
            ps.setString(3, areaDescripcion.getText().trim());
            ps.setInt(4, cantInicial);
            ps.setInt(5, cantMinima);
            ps.setString(6, (String) comboEstado.getSelectedItem());
            ps.setString(7, campoUbicacion.getText().trim());
            ps.setString(8, campoProveedor.getText().trim());
            ps.setDouble(9, precio);
            ps.executeUpdate();

            // Si hay cantidad inicial > 0, registrar como movimiento de ingreso
            if (cantInicial > 0) {
                try (ResultSet gk = ps.getGeneratedKeys()) {
                    if (gk.next()) {
                        int nuevaId = gk.getInt(1);
                        registrarMovimientoInterno(conn, nuevaId, "ingreso", cantInicial,
                            "Stock inicial al crear la pieza", null, null, "");
                    }
                }
            }

            JOptionPane.showMessageDialog(null,
                "Pieza \"" + nombre + "\" registrada exitosamente.",
                "Pieza guardada", JOptionPane.INFORMATION_MESSAGE);

            limpiarFormNuevaPieza();
            cargarInventario();
            cargarComboPiezasMovimiento();

        } catch (SQLException ex) {
            if (ex.getMessage() != null && ex.getMessage().contains("Duplicate")) {
                JOptionPane.showMessageDialog(null,
                    "Ya existe una pieza con ese nombre. Usa otro nombre.",
                    "Duplicado", JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null,
                    "Error al guardar:\n" + ex.getMessage(),
                    "Error BD", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void limpiarFormNuevaPieza() {
        campoNombre.setText("");
        comboTipoPieza.setSelectedIndex(0);
        areaDescripcion.setText("");
        campoCantidadInicial.setText("0");
        campoCantidadMinima.setText("1");
        comboEstado.setSelectedIndex(0);
        campoUbicacion.setText("");
        campoProveedor.setText("");
        campoPrecio.setText("0");
    }

    // =========================================================================
    // SUB-PANEL 3: REGISTRAR MOVIMIENTO (INGRESO / SALIDA)
    // =========================================================================

    private JPanel crearSubPanelMovimiento() {
        Font labelFont = new Font("Segoe UI", Font.PLAIN, 11);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_FONDO);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(COLOR_FONDO);
        form.setBorder(BorderFactory.createEmptyBorder(18, 24, 18, 24));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;

        comboPiezaMovimiento    = new JComboBox<>();
        comboTipoMovimiento     = new JComboBox<>(new String[]{"ingreso", "salida"});
        campoCantidadMovimiento = new JTextField("1", 8);
        campoMotivoMovimiento   = new JTextField(25);
        areaObservacionMovimiento = new JTextArea(3, 25);
        areaObservacionMovimiento.setLineWrap(true);
        areaObservacionMovimiento.setWrapStyleWord(true);
        labelStockActual = new JLabel("—");
        labelStockActual.setFont(new Font("Segoe UI", Font.BOLD, 12));
        labelAlertaStock = new JLabel("");
        labelAlertaStock.setFont(new Font("Segoe UI", Font.BOLD, 11));

        // Actualizar info de stock al cambiar la pieza seleccionada
        comboPiezaMovimiento.addActionListener(e -> actualizarInfoStockMovimiento());

        int y = 0;

        // Pieza
        c.gridx = 0; c.gridy = y;
        JLabel lPieza = new JLabel("Pieza: *"); lPieza.setFont(labelFont);
        form.add(lPieza, c);
        c.gridx = 1; c.weightx = 1.0;
        form.add(comboPiezaMovimiento, c);
        y++;

        // Stock actual (lectura)
        c.gridx = 0; c.gridy = y; c.weightx = 0;
        JLabel lStock = new JLabel("Stock actual:"); lStock.setFont(labelFont);
        form.add(lStock, c);
        c.gridx = 1; c.weightx = 1.0;
        form.add(labelStockActual, c);
        y++;

        // Alerta
        c.gridx = 1; c.gridy = y;
        form.add(labelAlertaStock, c);
        y++;

        // Tipo movimiento
        c.gridx = 0; c.gridy = y; c.weightx = 0;
        JLabel lTipo = new JLabel("Tipo: *"); lTipo.setFont(labelFont);
        form.add(lTipo, c);
        c.gridx = 1; c.weightx = 1.0;
        form.add(comboTipoMovimiento, c);
        y++;

        // Cantidad
        c.gridx = 0; c.gridy = y; c.weightx = 0;
        JLabel lCant = new JLabel("Cantidad: *"); lCant.setFont(labelFont);
        form.add(lCant, c);
        c.gridx = 1; c.weightx = 1.0;
        form.add(campoCantidadMovimiento, c);
        y++;

        // Motivo
        c.gridx = 0; c.gridy = y; c.weightx = 0;
        JLabel lMotivo = new JLabel("Motivo:"); lMotivo.setFont(labelFont);
        form.add(lMotivo, c);
        c.gridx = 1; c.weightx = 1.0;
        form.add(campoMotivoMovimiento, c);
        y++;

        // Observación
        c.gridx = 0; c.gridy = y; c.weightx = 0;
        JLabel lObs = new JLabel("Observación:"); lObs.setFont(labelFont);
        form.add(lObs, c);
        c.gridx = 1; c.weightx = 1.0;
        form.add(new JScrollPane(areaObservacionMovimiento), c);
        y++;

        // Botón
        JButton btnGuardarMov = crearBoton("Registrar Movimiento", new Color(52, 115, 86));
        JButton btnLimpiarMov = crearBoton("Limpiar", new Color(130, 130, 130));
        btnGuardarMov.addActionListener(e -> registrarMovimiento());
        btnLimpiarMov.addActionListener(e -> limpiarFormMovimiento());

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botones.setOpaque(false);
        botones.add(btnLimpiarMov);
        botones.add(btnGuardarMov);

        c.gridx = 1; c.gridy = y;
        form.add(botones, c);

        cargarComboPiezasMovimiento();

        JScrollPane scrollForm = new JScrollPane(form);
        scrollForm.setBorder(null);
        panel.add(scrollForm, BorderLayout.CENTER);
        return panel;
    }

    private void cargarComboPiezasMovimiento() {
        comboPiezaMovimiento.removeAllItems();
        String sql = "SELECT id, nombre, cantidad_actual, cantidad_minima FROM inventario_piezas ORDER BY nombre ASC";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                comboPiezaMovimiento.addItem(new PiezaItem(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getInt("cantidad_actual"),
                    rs.getInt("cantidad_minima")
                ));
            }
        } catch (SQLException ex) {
            // Silencioso; se mostrará error al intentar usar
        }
        actualizarInfoStockMovimiento();
    }

    private void actualizarInfoStockMovimiento() {
        PiezaItem sel = (PiezaItem) comboPiezaMovimiento.getSelectedItem();
        if (sel == null) {
            labelStockActual.setText("—");
            labelAlertaStock.setText("");
            return;
        }
        labelStockActual.setText(sel.getStockActual() + " unidades");
        if (sel.getStockActual() == 0) {
            labelAlertaStock.setText("⚠ Sin stock");
            labelAlertaStock.setForeground(COLOR_ALERTA);
        } else if (sel.getStockActual() <= sel.getStockMinimo()) {
            labelAlertaStock.setText("⚠ Stock bajo (mínimo: " + sel.getStockMinimo() + ")");
            labelAlertaStock.setForeground(COLOR_ADVERTENCIA);
        } else {
            labelAlertaStock.setText("✓ Stock normal");
            labelAlertaStock.setForeground(COLOR_OK);
        }
    }

    private void registrarMovimiento() {
        PiezaItem pieza = (PiezaItem) comboPiezaMovimiento.getSelectedItem();
        if (pieza == null) {
            JOptionPane.showMessageDialog(null, "Selecciona una pieza.", "Campo requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String tipoMov = (String) comboTipoMovimiento.getSelectedItem();
        int cantidad;
        try {
            cantidad = Integer.parseInt(campoCantidadMovimiento.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "La cantidad debe ser un número entero.", "Error de formato", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (cantidad <= 0) {
            JOptionPane.showMessageDialog(null, "La cantidad debe ser mayor a 0.", "Error de validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validar salida: no puede exceder stock
        if ("salida".equals(tipoMov) && cantidad > pieza.getStockActual()) {
            JOptionPane.showMessageDialog(null,
                "No hay suficiente stock para registrar esta salida.\n" +
                "Stock actual: " + pieza.getStockActual() + " | Solicitado: " + cantidad,
                "Stock insuficiente", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String motivo = campoMotivoMovimiento.getText().trim();
        String observacion = areaObservacionMovimiento.getText().trim();

        int confirmacion = JOptionPane.showConfirmDialog(null,
            "¿Confirmar " + tipoMov + " de " + cantidad + " unidades de \"" + pieza + "\"?",
            "Confirmar movimiento", JOptionPane.YES_NO_OPTION);
        if (confirmacion != JOptionPane.YES_OPTION) return;

        String sqlMovimiento = "INSERT INTO movimiento_inventario " +
                               "(pieza_id, tipo_movimiento, cantidad, motivo, usuario_id, observacion) " +
                               "VALUES (?,?,?,?,?,?)";

        String sqlUpdateStock = "ingreso".equals(tipoMov)
            ? "UPDATE inventario_piezas SET cantidad_actual = cantidad_actual + ? WHERE id = ?"
            : "UPDATE inventario_piezas SET cantidad_actual = cantidad_actual - ? WHERE id = ?";

        // Actualizar estado a "agotado" si stock llega a 0
        String sqlCheckAgotado = "UPDATE inventario_piezas " +
            "SET estado = CASE WHEN cantidad_actual = 0 THEN 'agotado' " +
            "ELSE (CASE WHEN estado = 'agotado' THEN 'disponible' ELSE estado END) END " +
            "WHERE id = ?";

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try {
                // 1. Registrar movimiento
                try (PreparedStatement ps = conn.prepareStatement(sqlMovimiento)) {
                    ps.setInt(1, pieza.getId());
                    ps.setString(2, tipoMov);
                    ps.setInt(3, cantidad);
                    ps.setString(4, motivo.isEmpty() ? null : motivo);
                    ps.setInt(5, usuarioIdSesion);
                    ps.setString(6, observacion.isEmpty() ? null : observacion);
                    ps.executeUpdate();
                }

                // 2. Actualizar stock
                try (PreparedStatement ps = conn.prepareStatement(sqlUpdateStock)) {
                    ps.setInt(1, cantidad);
                    ps.setInt(2, pieza.getId());
                    ps.executeUpdate();
                }

                // 3. Actualizar estado si llega a 0
                try (PreparedStatement ps = conn.prepareStatement(sqlCheckAgotado)) {
                    ps.setInt(1, pieza.getId());
                    ps.executeUpdate();
                }

                conn.commit();

                JOptionPane.showMessageDialog(null,
                    "Movimiento registrado correctamente.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);

                limpiarFormMovimiento();
                cargarInventario();
                cargarComboPiezasMovimiento();

            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                "Error al registrar movimiento:\n" + ex.getMessage(),
                "Error BD", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarFormMovimiento() {
        if (comboPiezaMovimiento.getItemCount() > 0) comboPiezaMovimiento.setSelectedIndex(0);
        comboTipoMovimiento.setSelectedIndex(0);
        campoCantidadMovimiento.setText("1");
        campoMotivoMovimiento.setText("");
        areaObservacionMovimiento.setText("");
        actualizarInfoStockMovimiento();
    }

    // =========================================================================
    // SUB-PANEL 4: HISTORIAL DE MOVIMIENTOS
    // =========================================================================

    private JPanel crearSubPanelHistorial() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Filtros
        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        filtros.setOpaque(false);
        filtros.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Filtros",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 11)
        ));

        JComboBox<String> comboFiltroTipo = new JComboBox<>(new String[]{"Todos", "ingreso", "salida"});
        JTextField campoFiltroNombre = new JTextField(15);

        filtros.add(new JLabel("Tipo:"));
        filtros.add(comboFiltroTipo);
        filtros.add(new JLabel("Nombre pieza contiene:"));
        filtros.add(campoFiltroNombre);

        JButton btnBuscar = crearBoton("Buscar", new Color(70, 130, 180));
        filtros.add(btnBuscar);

        // Tabla historial
        modeloMovimientos = new DefaultTableModel(
            new String[]{"ID", "Pieza", "Tipo", "Cantidad", "Motivo", "Realizado por", "Observación", "Fecha"},
            0
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaMovimientos = new JTable(modeloMovimientos);
        tablaMovimientos.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        tablaMovimientos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        tablaMovimientos.setRowHeight(22);

        // Ocultar ID
        tablaMovimientos.getColumnModel().getColumn(0).setMinWidth(0);
        tablaMovimientos.getColumnModel().getColumn(0).setMaxWidth(0);
        tablaMovimientos.getColumnModel().getColumn(0).setWidth(0);

        // Colorear ingresos/salidas
        tablaMovimientos.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                if (!sel) {
                    String tipo = (String) t.getValueAt(row, 2);
                    c.setBackground("ingreso".equals(tipo) ? new Color(220, 245, 220) : new Color(255, 235, 220));
                }
                return c;
            }
        });

        btnBuscar.addActionListener(e -> cargarHistorial(
            (String) comboFiltroTipo.getSelectedItem(),
            campoFiltroNombre.getText().trim()
        ));

        JScrollPane scroll = new JScrollPane(tablaMovimientos);

        // Leyenda
        JPanel leyenda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
        leyenda.setOpaque(false);
        leyenda.add(crearChip("Verde = Ingreso", new Color(220, 245, 220)));
        leyenda.add(crearChip("Naranja = Salida", new Color(255, 235, 220)));

        panel.add(filtros, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(leyenda, BorderLayout.SOUTH);

        cargarHistorial("Todos", "");
        return panel;
    }

    private void cargarHistorial(String filtroTipo, String filtroNombre) {
        modeloMovimientos.setRowCount(0);
        StringBuilder sql = new StringBuilder(
            "SELECT m.id, p.nombre AS pieza, m.tipo_movimiento, m.cantidad, " +
            "COALESCE(m.motivo,'—') AS motivo, " +
            "COALESCE(u.nombre, u.usuario, '—') AS realizado_por, " +
            "COALESCE(m.observacion,'—') AS observacion, " +
            "DATE_FORMAT(m.fecha_movimiento, '%d-%m-%Y %H:%i') AS fecha " +
            "FROM movimiento_inventario m " +
            "INNER JOIN inventario_piezas p ON p.id = m.pieza_id " +
            "LEFT JOIN usuarios u ON u.id = m.usuario_id " +
            "WHERE 1=1 "
        );
        if (!"Todos".equals(filtroTipo)) sql.append("AND m.tipo_movimiento = ? ");
        if (!filtroNombre.isEmpty())      sql.append("AND p.nombre LIKE ? ");
        sql.append("ORDER BY m.fecha_movimiento DESC LIMIT 300");

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int idx = 1;
            if (!"Todos".equals(filtroTipo)) ps.setString(idx++, filtroTipo);
            if (!filtroNombre.isEmpty())      ps.setString(idx, "%" + filtroNombre + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    modeloMovimientos.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("pieza"),
                        rs.getString("tipo_movimiento"),
                        rs.getInt("cantidad"),
                        rs.getString("motivo"),
                        rs.getString("realizado_por"),
                        rs.getString("observacion"),
                        rs.getString("fecha")
                    });
                }
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                "Error al cargar historial:\n" + ex.getMessage(),
                "Error BD", JOptionPane.ERROR_MESSAGE);
        }
    }

    // =========================================================================
    // DIÁLOGO: historial de una pieza específica (llamado desde tabla inventario)
    // =========================================================================

    private void mostrarHistorialPieza(int piezaId, String nombrePieza) {
        DefaultTableModel modeloDialogo = new DefaultTableModel(
            new String[]{"Tipo", "Cantidad", "Motivo", "Realizado por", "Observación", "Fecha"}, 0
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        String sql = "SELECT m.tipo_movimiento, m.cantidad, COALESCE(m.motivo,'—') AS motivo, " +
                     "COALESCE(u.nombre, u.usuario,'—') AS realizado_por, " +
                     "COALESCE(m.observacion,'—') AS observacion, " +
                     "DATE_FORMAT(m.fecha_movimiento,'%d-%m-%Y %H:%i') AS fecha " +
                     "FROM movimiento_inventario m " +
                     "LEFT JOIN usuarios u ON u.id = m.usuario_id " +
                     "WHERE m.pieza_id = ? " +
                     "ORDER BY m.fecha_movimiento DESC";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, piezaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    modeloDialogo.addRow(new Object[]{
                        rs.getString("tipo_movimiento"),
                        rs.getInt("cantidad"),
                        rs.getString("motivo"),
                        rs.getString("realizado_por"),
                        rs.getString("observacion"),
                        rs.getString("fecha")
                    });
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                "Error al cargar historial:\n" + ex.getMessage(),
                "Error BD", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JTable tabla = new JTable(modeloDialogo);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        tabla.setRowHeight(22);
        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                if (!sel) {
                    String tipo = (String) t.getValueAt(row, 0);
                    c.setBackground("ingreso".equals(tipo) ? new Color(220, 245, 220) : new Color(255, 235, 220));
                }
                return c;
            }
        });

        JScrollPane sp = new JScrollPane(tabla);
        sp.setPreferredSize(new Dimension(750, 320));

        JOptionPane.showMessageDialog(null, sp,
            "Historial de movimientos – " + nombrePieza,
            JOptionPane.PLAIN_MESSAGE);
    }

    // =========================================================================
    // HELPER INTERNO: registrar movimiento sin UI (usado al crear pieza)
    // =========================================================================

    private void registrarMovimientoInterno(Connection conn, int piezaId, String tipo,
                                             int cantidad, String motivo,
                                             Integer equipoId, Integer mantenimientoId,
                                             String observacion) throws SQLException {
        String sql = "INSERT INTO movimiento_inventario " +
                     "(pieza_id, tipo_movimiento, cantidad, motivo, equipo_id, mantenimiento_equipo_id, usuario_id, observacion) " +
                     "VALUES (?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, piezaId);
            ps.setString(2, tipo);
            ps.setInt(3, cantidad);
            ps.setString(4, motivo);
            if (equipoId != null) ps.setInt(5, equipoId); else ps.setNull(5, Types.INTEGER);
            if (mantenimientoId != null) ps.setInt(6, mantenimientoId); else ps.setNull(6, Types.INTEGER);
            ps.setInt(7, usuarioIdSesion);
            ps.setString(8, observacion);
            ps.executeUpdate();
        }
    }

    // =========================================================================
    // HELPER UI
    // =========================================================================

    private JButton crearBoton(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        return btn;
    }
}
