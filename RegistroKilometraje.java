import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RegistroKilometraje extends JFrame {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/hirata_flotas?serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";

    private static final String TABLA_USUARIOS = "usuarios";
    private static final String COLUMNA_USUARIO = "usuario";
    private static final String COLUMNA_CONTRASENA = "contrasena";
    private static final String COLUMNA_NOMBRE = "nombre";
    private static final String COLUMNA_ROL = "rol";
    private static final String COLUMNA_ACTIVO = "activo";

    private final int usuarioIdSesion;
    private final String nombreSesion;
    private final String rolSesion;
    private final boolean esAdministrador;

    private JComboBox<CamionItem> camionComboKm;
    private JTextField modeloFieldKm;
    private JComboBox<UsuarioItem> conductorComboKm;
    private JTextField kmAgregarField;
    private JLabel kmActualLabel;

    private JComboBox<CamionItem> camionComboMant;
    private JTextField mantFechaField;
    private JTextArea mantDetalleArea;
    private JComboBox<CamionItem> consultaCamionComboMant;
    private JComboBox<String> consultaFechaComboMant;
    private JTextArea consultaDetalleAreaMant;

    private JTable registrosTable;
    private DefaultTableModel tableModel;

    private JTextField nuevoCamionPlacaField;
    private JTextField nuevoCamionModeloField;
    private JTextField nuevoCamionKmInicialField;

    private static class SesionUsuario {
        private final int id;
        private final String nombre;
        private final String rol;

        SesionUsuario(int id, String nombre, String rol) {
            this.id = id;
            this.nombre = nombre;
            this.rol = rol;
        }

        int getId() {
            return id;
        }

        String getNombre() {
            return nombre;
        }

        String getRol() {
            return rol;
        }
    }

    private static class UsuarioItem {
        private final int id;
        private final String nombre;

        UsuarioItem(int id, String nombre) {
            this.id = id;
            this.nombre = nombre;
        }

        int getId() {
            return id;
        }

        @Override
        public String toString() {
            return nombre;
        }
    }

    private static class CamionItem {
        private final int id;
        private final String placa;
        private final String modelo;
        private final int kilometraje;

        CamionItem(int id, String placa, String modelo, int kilometraje) {
            this.id = id;
            this.placa = placa;
            this.modelo = modelo;
            this.kilometraje = kilometraje;
        }

        int getId() {
            return id;
        }

        String getModelo() {
            return modelo;
        }

        int getKilometraje() {
            return kilometraje;
        }

        @Override
        public String toString() {
            return placa;
        }
    }

    public RegistroKilometraje(SesionUsuario sesion) {
        super("Sistema de Flota - Hirata");
        this.usuarioIdSesion = sesion.getId();
        this.nombreSesion = sesion.getNombre();
        this.rolSesion = sesion.getRol();
        this.esAdministrador = "administrador".equalsIgnoreCase(rolSesion);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initComponents();
        setSize(980, 590);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void initComponents() {
        inicializarTabla();

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Registrar kilometraje", crearPanelKilometraje());
        if (esAdministrador) {
            tabs.addTab("Registrar mantenimiento", crearPanelMantenimiento());
            tabs.addTab("Registrar Camiones", crearPanelGestionCamiones());
        }
        tabs.addTab("Camiones", crearPanelTablaRegistros());

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(tabs, BorderLayout.CENTER);

        refrescarDatosPantalla();
    }

    private void inicializarTabla() {
        tableModel = new DefaultTableModel(new String[]{"ID", "Placa", "Modelo", "Ultimo conductor", "KM actual"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        registrosTable = new JTable(tableModel);
        registrosTable.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        registrosTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 10));
        registrosTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        registrosTable.getColumnModel().getColumn(0).setMinWidth(0);
        registrosTable.getColumnModel().getColumn(0).setMaxWidth(0);
        registrosTable.getColumnModel().getColumn(0).setWidth(0);
    }

    private JPanel crearPanelKilometraje() {
        Font labelFont = new Font("Segoe UI", Font.PLAIN, 11);
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 11);
        Font buttonFont = new Font("Segoe UI", Font.BOLD, 12);

        camionComboKm = new JComboBox<>();
        modeloFieldKm = new JTextField(15);
        modeloFieldKm.setEditable(false);
        conductorComboKm = new JComboBox<>();
        kmAgregarField = new JTextField(10);
        kmActualLabel = new JLabel("KM actual: -");
        kmActualLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));

        JLabel placaLabel = new JLabel("Placa / ID camión:");
        placaLabel.setFont(labelFont);
        JLabel modeloLabel = new JLabel("Modelo (camion):");
        modeloLabel.setFont(labelFont);
        JLabel conductorLabel = new JLabel(esAdministrador ? "Conductor:" : "Conductor (sesion):");
        conductorLabel.setFont(labelFont);
        JLabel kmAgregarLabel = new JLabel("Kilometraje a agregar:");
        kmAgregarLabel.setFont(labelFont);

        camionComboKm.setFont(fieldFont);
        modeloFieldKm.setFont(fieldFont);
        conductorComboKm.setFont(fieldFont);
        kmAgregarField.setFont(fieldFont);

        JButton registrarBtn = new JButton("Registrar kilometraje");
        registrarBtn.setFont(buttonFont);
        registrarBtn.setBackground(new Color(70, 130, 180));
        registrarBtn.setForeground(Color.WHITE);
        registrarBtn.setFocusPainted(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 245, 245));

        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(70, 130, 180));
        JLabel titleLabel = new JLabel("Registrar Kilometraje");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(new Color(245, 245, 245));
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 8, 10, 8);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;

        int y = 0;
        c.gridx = 0;
        c.gridy = y;
        form.add(placaLabel, c);
        c.gridx = 1;
        form.add(camionComboKm, c);
        y++;

        c.gridx = 0;
        c.gridy = y;
        form.add(modeloLabel, c);
        c.gridx = 1;
        form.add(modeloFieldKm, c);
        y++;

        c.gridx = 0;
        c.gridy = y;
        form.add(conductorLabel, c);
        c.gridx = 1;
        form.add(conductorComboKm, c);
        y++;

        c.gridx = 0;
        c.gridy = y;
        form.add(kmAgregarLabel, c);
        c.gridx = 1;
        form.add(kmAgregarField, c);
        y++;

        c.gridx = 0;
        c.gridy = y;
        form.add(new JLabel("Estado:"), c);
        c.gridx = 1;
        form.add(kmActualLabel, c);
        y++;

        c.gridx = 1;
        c.gridy = y;
        form.add(registrarBtn, c);

        camionComboKm.addActionListener(e -> actualizarCamposCamionSeleccionado());
        registrarBtn.addActionListener(e -> registrarKilometraje());

        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(form, BorderLayout.CENTER);
        return mainPanel;
    }

    private JPanel crearPanelMantenimiento() {
        Font labelFont = new Font("Segoe UI", Font.PLAIN, 11);
        Font titleFont = new Font("Segoe UI", Font.BOLD, 14);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 245));

        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(52, 115, 86));
        JLabel titleLabel = new JLabel("Registrar Mantenimiento");
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);

        JPanel leftForm = new JPanel(new GridBagLayout());
        leftForm.setBackground(new Color(245, 245, 245));
        leftForm.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;

        JLabel placaLabel = new JLabel("Placa / ID camión:");
        placaLabel.setFont(labelFont);
        JLabel fechaLabel = new JLabel("Fecha (YYYY-MM-DD):");
        fechaLabel.setFont(labelFont);
        JLabel detalleLabel = new JLabel("Detalle mantenimiento:");
        detalleLabel.setFont(labelFont);

        camionComboMant = new JComboBox<>();
        mantFechaField = new JTextField(15);
        mantDetalleArea = new JTextArea(5, 24);
        mantDetalleArea.setLineWrap(true);
        mantDetalleArea.setWrapStyleWord(true);

        JButton guardarMantBtn = new JButton("Guardar mantenimiento");
        guardarMantBtn.setBackground(new Color(52, 115, 86));
        guardarMantBtn.setForeground(Color.WHITE);
        guardarMantBtn.setFocusPainted(false);

        int y = 0;
        c.gridx = 0;
        c.gridy = y;
        leftForm.add(placaLabel, c);
        c.gridx = 1;
        leftForm.add(camionComboMant, c);
        y++;

        c.gridx = 0;
        c.gridy = y;
        leftForm.add(fechaLabel, c);
        c.gridx = 1;
        leftForm.add(mantFechaField, c);
        y++;

        c.gridx = 0;
        c.gridy = y;
        leftForm.add(detalleLabel, c);
        c.gridx = 1;
        leftForm.add(new JScrollPane(mantDetalleArea), c);
        y++;

        c.gridx = 1;
        c.gridy = y;
        leftForm.add(guardarMantBtn, c);

        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(new Color(245, 245, 245));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints r = new GridBagConstraints();
        r.insets = new Insets(8, 8, 8, 8);
        r.anchor = GridBagConstraints.WEST;
        r.fill = GridBagConstraints.HORIZONTAL;

        JLabel consultaPlacaLabel = new JLabel("Placa (consulta):");
        consultaPlacaLabel.setFont(labelFont);
        JLabel consultaFechaLabel = new JLabel("Fecha (consulta):");
        consultaFechaLabel.setFont(labelFont);
        JLabel consultaDetalleLabel = new JLabel("Detalle:");
        consultaDetalleLabel.setFont(labelFont);

        consultaCamionComboMant = new JComboBox<>();
        consultaFechaComboMant = new JComboBox<>();
        consultaDetalleAreaMant = new JTextArea(8, 26);
        consultaDetalleAreaMant.setEditable(false);
        consultaDetalleAreaMant.setLineWrap(true);
        consultaDetalleAreaMant.setWrapStyleWord(true);

        int yr = 0;
        r.gridx = 0;
        r.gridy = yr;
        rightPanel.add(consultaPlacaLabel, r);
        r.gridx = 1;
        rightPanel.add(consultaCamionComboMant, r);
        yr++;

        r.gridx = 0;
        r.gridy = yr;
        rightPanel.add(consultaFechaLabel, r);
        r.gridx = 1;
        rightPanel.add(consultaFechaComboMant, r);
        yr++;

        r.gridx = 0;
        r.gridy = yr;
        rightPanel.add(consultaDetalleLabel, r);
        r.gridx = 1;
        rightPanel.add(new JScrollPane(consultaDetalleAreaMant), r);

        consultaCamionComboMant.addActionListener(e -> cargarFechasMantenimientoConsulta());
        consultaFechaComboMant.addActionListener(e -> mostrarDetalleMantenimientoSeleccionado());

        guardarMantBtn.addActionListener(e -> guardarMantenimiento());

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftForm, rightPanel);
        splitPane.setResizeWeight(0.52);
        splitPane.setDividerLocation(470);
        splitPane.setBorder(null);

        panel.add(titlePanel, BorderLayout.NORTH);
        panel.add(splitPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelTablaRegistros() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 245));

        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(153, 57, 57));
        JLabel titleLabel = new JLabel("Camiones");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);

        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBackground(new Color(245, 245, 245));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel title = new JLabel("Registros de camiones");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JButton refrescarBtn = new JButton("Refrescar");
        refrescarBtn.setBackground(new Color(70, 130, 180));
        refrescarBtn.setForeground(Color.WHITE);
        refrescarBtn.setFocusPainted(false);
        refrescarBtn.addActionListener(e -> refrescarDatosPantalla());

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(title, BorderLayout.WEST);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        acciones.setOpaque(false);
        acciones.add(refrescarBtn);

        if (esAdministrador) {
            JButton eliminarBtn = new JButton("Eliminar camion");
            eliminarBtn.setBackground(new Color(160, 45, 45));
            eliminarBtn.setForeground(Color.WHITE);
            eliminarBtn.setFocusPainted(false);
            eliminarBtn.addActionListener(e -> eliminarCamionSeleccionado());
            acciones.add(eliminarBtn);
        }

        top.add(acciones, BorderLayout.EAST);

        JScrollPane tableScroll = new JScrollPane(registrosTable);
        tableScroll.setPreferredSize(new Dimension(860, 380));

        contentPanel.add(top, BorderLayout.NORTH);
        contentPanel.add(tableScroll, BorderLayout.CENTER);

        panel.add(titlePanel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelGestionCamiones() {
        Font labelFont = new Font("Segoe UI", Font.PLAIN, 11);
        Font titleFont = new Font("Segoe UI", Font.BOLD, 14);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 245));

        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(84, 97, 132));
        JLabel titleLabel = new JLabel("Registrar Camiones");
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(new Color(245, 245, 245));
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;

        JLabel placaLabel = new JLabel("Placa:");
        placaLabel.setFont(labelFont);
        JLabel modeloLabel = new JLabel("Modelo:");
        modeloLabel.setFont(labelFont);
        JLabel kmLabel = new JLabel("Kilometraje inicial:");
        kmLabel.setFont(labelFont);

        nuevoCamionPlacaField = new JTextField(15);
        nuevoCamionModeloField = new JTextField(15);
        nuevoCamionKmInicialField = new JTextField(10);

        JButton guardarBtn = new JButton("Agregar camión");
        guardarBtn.setBackground(new Color(84, 97, 132));
        guardarBtn.setForeground(Color.WHITE);
        guardarBtn.setFocusPainted(false);

        int y = 0;
        c.gridx = 0;
        c.gridy = y;
        form.add(placaLabel, c);
        c.gridx = 1;
        form.add(nuevoCamionPlacaField, c);
        y++;

        c.gridx = 0;
        c.gridy = y;
        form.add(modeloLabel, c);
        c.gridx = 1;
        form.add(nuevoCamionModeloField, c);
        y++;

        c.gridx = 0;
        c.gridy = y;
        form.add(kmLabel, c);
        c.gridx = 1;
        form.add(nuevoCamionKmInicialField, c);
        y++;

        c.gridx = 1;
        c.gridy = y;
        form.add(guardarBtn, c);

        guardarBtn.addActionListener(e -> agregarCamion());

        panel.add(titlePanel, BorderLayout.NORTH);
        panel.add(form, BorderLayout.CENTER);
        return panel;
    }

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }

    private void refrescarDatosPantalla() {
        cargarConductoresEnCombos();
        cargarCamionesEnCombos();
        cargarRegistros();
        actualizarCamposCamionSeleccionado();
        cargarFechasMantenimientoConsulta();
    }

    private List<UsuarioItem> obtenerCamioneros() {
        List<UsuarioItem> usuarios = new ArrayList<>();
        String sql = "SELECT id, " + COLUMNA_NOMBRE + " FROM " + TABLA_USUARIOS +
            " WHERE " + COLUMNA_ROL + "='camionero' AND " + COLUMNA_ACTIVO + "=1 ORDER BY " + COLUMNA_NOMBRE;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                usuarios.add(new UsuarioItem(rs.getInt("id"), rs.getString(COLUMNA_NOMBRE)));
            }
        } catch (SQLException ex) {
            System.err.println("Error al cargar camioneros: " + ex.getMessage());
        }

        return usuarios;
    }

    private void cargarConductoresEnCombos() {
        conductorComboKm.removeAllItems();

        if (!esAdministrador) {
            UsuarioItem sesion = new UsuarioItem(usuarioIdSesion, nombreSesion);
            conductorComboKm.addItem(sesion);
            conductorComboKm.setEnabled(false);
            return;
        }

        List<UsuarioItem> camioneros = obtenerCamioneros();
        if (camioneros.isEmpty()) {
            UsuarioItem vacio = new UsuarioItem(-1, "Sin camioneros disponibles");
            conductorComboKm.addItem(vacio);
            conductorComboKm.setEnabled(false);
            return;
        }

        for (UsuarioItem item : camioneros) {
            conductorComboKm.addItem(item);
        }

        conductorComboKm.setEnabled(true);
    }

    private List<CamionItem> obtenerCamiones() {
        List<CamionItem> camiones = new ArrayList<>();
        String sql = "SELECT id, placa, modelo, kilometraje FROM camion ORDER BY placa";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                camiones.add(new CamionItem(
                        rs.getInt("id"),
                        rs.getString("placa"),
                        rs.getString("modelo"),
                        rs.getInt("kilometraje")
                ));
            }
        } catch (SQLException ex) {
            System.err.println("Error al cargar camiones: " + ex.getMessage());
        }

        return camiones;
    }

    private void cargarCamionesEnCombos() {
        List<CamionItem> camiones = obtenerCamiones();
        camionComboKm.removeAllItems();
        if (camionComboMant != null) {
            camionComboMant.removeAllItems();
        }
        if (consultaCamionComboMant != null) {
            consultaCamionComboMant.removeAllItems();
        }

        if (camiones.isEmpty()) {
            camionComboKm.setEnabled(false);
            if (camionComboMant != null) {
                camionComboMant.setEnabled(false);
            }
            if (consultaCamionComboMant != null) {
                consultaCamionComboMant.setEnabled(false);
            }
            if (consultaFechaComboMant != null) {
                consultaFechaComboMant.removeAllItems();
            }
            if (consultaDetalleAreaMant != null) {
                consultaDetalleAreaMant.setText("");
            }
            modeloFieldKm.setText("");
            kmActualLabel.setText("KM actual: -");
            return;
        }

        for (CamionItem camion : camiones) {
            camionComboKm.addItem(camion);
            if (camionComboMant != null) {
                camionComboMant.addItem(camion);
            }
            if (consultaCamionComboMant != null) {
                consultaCamionComboMant.addItem(camion);
            }
        }

        camionComboKm.setEnabled(true);
        if (camionComboMant != null) {
            camionComboMant.setEnabled(true);
        }
        if (consultaCamionComboMant != null) {
            consultaCamionComboMant.setEnabled(true);
        }
    }

    private void cargarFechasMantenimientoConsulta() {
        if (consultaFechaComboMant == null) {
            return;
        }

        consultaFechaComboMant.removeAllItems();
        CamionItem camion = (CamionItem) consultaCamionComboMant.getSelectedItem();
        if (camion == null) {
            consultaDetalleAreaMant.setText("");
            return;
        }

        String sql = "SELECT DISTINCT DATE_FORMAT(fecha, '%Y-%m-%d') AS fecha_txt " +
                "FROM mantenimiento WHERE camion_id=? ORDER BY fecha DESC";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, camion.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    consultaFechaComboMant.addItem(rs.getString("fecha_txt"));
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar fechas de mantenimiento:\n" + ex.getMessage(),
                    "Error BD",
                    JOptionPane.ERROR_MESSAGE);
        }

        mostrarDetalleMantenimientoSeleccionado();
    }

    private void mostrarDetalleMantenimientoSeleccionado() {
        if (consultaDetalleAreaMant == null) {
            return;
        }

        CamionItem camion = (CamionItem) consultaCamionComboMant.getSelectedItem();
        String fechaSeleccionada = (String) consultaFechaComboMant.getSelectedItem();

        if (camion == null || fechaSeleccionada == null) {
            consultaDetalleAreaMant.setText("Selecciona una placa y una fecha con mantenimiento registrado.");
            return;
        }

        String sql = "SELECT detalle, kilometraje_servicio FROM mantenimiento " +
                "WHERE camion_id=? AND fecha=? ORDER BY id DESC LIMIT 1";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, camion.getId());
            ps.setDate(2, Date.valueOf(fechaSeleccionada));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String texto = "Placa: " + camion + "\n" +
                            "Fecha: " + fechaSeleccionada + "\n" +
                            "Kilometraje servicio: " + rs.getInt("kilometraje_servicio") + "\n\n" +
                            rs.getString("detalle");
                    consultaDetalleAreaMant.setText(texto);
                } else {
                    consultaDetalleAreaMant.setText("No se encontró detalle para la selección actual.");
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar detalle de mantenimiento:\n" + ex.getMessage(),
                    "Error BD",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarCamposCamionSeleccionado() {
        CamionItem seleccionado = (CamionItem) camionComboKm.getSelectedItem();
        if (seleccionado == null) {
            modeloFieldKm.setText("");
            kmActualLabel.setText("KM actual: -");
            return;
        }

        modeloFieldKm.setText(seleccionado.getModelo());
        kmActualLabel.setText("KM actual: " + seleccionado.getKilometraje());
    }

    private void registrarKilometraje() {
        CamionItem camion = (CamionItem) camionComboKm.getSelectedItem();
        UsuarioItem conductor = (UsuarioItem) conductorComboKm.getSelectedItem();

        if (camion == null || !camionComboKm.isEnabled()) {
            JOptionPane.showMessageDialog(this, "No hay camiones registrados para actualizar.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (conductor == null || conductor.getId() <= 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un conductor válido.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int kmAgregar;
        try {
            kmAgregar = Integer.parseInt(kmAgregarField.getText().trim());
            if (kmAgregar <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ingresa un kilometraje a agregar mayor a 0.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int kmActual = obtenerKilometrajeActualCamion(camion.getId());
        if (kmActual < 0) {
            return;
        }

        int nuevoKilometraje = kmActual + kmAgregar;

        String sql = "UPDATE camion SET kilometraje=?, conductor_usuario_id=? WHERE id=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, nuevoKilometraje);
            ps.setInt(2, conductor.getId());
            ps.setInt(3, camion.getId());

            int filas = ps.executeUpdate();
            if (filas > 0) {
                mostrarAlertaMantenimientoPorCiclo(camion.toString(), camion.getId(), nuevoKilometraje);
                JOptionPane.showMessageDialog(this,
                        "Kilometraje actualizado.\nKM anterior: " + kmActual + "\nKM agregado: " + kmAgregar + "\nKM nuevo: " + nuevoKilometraje,
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
                kmAgregarField.setText("");
                refrescarDatosPantalla();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo actualizar el kilometraje.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al actualizar kilometraje:\n" + ex.getMessage(),
                    "Error BD",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private int obtenerKilometrajeActualCamion(int camionId) {
        String sql = "SELECT kilometraje FROM camion WHERE id=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, camionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("kilometraje");
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al consultar kilometraje actual:\n" + ex.getMessage(),
                    "Error BD",
                    JOptionPane.ERROR_MESSAGE);
        }
        return -1;
    }

    private int obtenerKilometrajeUltimoMantenimiento(int camionId) {
        String sql = "SELECT COALESCE(MAX(kilometraje_servicio), 0) AS km_ultimo FROM mantenimiento WHERE camion_id=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, camionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("km_ultimo");
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al consultar último mantenimiento:\n" + ex.getMessage(),
                    "Error BD",
                    JOptionPane.ERROR_MESSAGE);
        }
        return 0;
    }

    private void mostrarAlertaMantenimientoPorCiclo(String placa, int camionId, int kilometrajeActual) {
        int kmUltimoMant = obtenerKilometrajeUltimoMantenimiento(camionId);
        int proximoObjetivo = kmUltimoMant + 5000;

        if (kilometrajeActual >= proximoObjetivo) {
            JOptionPane.showMessageDialog(this,
                    "Alerta de mantenimiento para " + placa + ".\n" +
                            "Último mantenimiento registrado en: " + kmUltimoMant + " km\n" +
                            "Objetivo de mantenimiento: " + proximoObjetivo + " km\n" +
                            "Kilometraje actual: " + kilometrajeActual + " km",
                    "Alerta de mantenimiento",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void guardarMantenimiento() {
        CamionItem camion = (CamionItem) camionComboMant.getSelectedItem();
        String fecha = mantFechaField.getText().trim();
        String detalle = mantDetalleArea.getText().trim();

        if (camion == null || !camionComboMant.isEnabled()) {
            JOptionPane.showMessageDialog(this, "No hay camiones disponibles para registrar mantenimiento.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (fecha.isEmpty() || detalle.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Completa fecha y detalle de mantenimiento.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int kmActual = obtenerKilometrajeActualCamion(camion.getId());
        if (kmActual < 0) {
            return;
        }

        String sql = "INSERT INTO mantenimiento (camion_id, fecha, detalle, kilometraje_servicio) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, camion.getId());
            ps.setDate(2, Date.valueOf(fecha));
            ps.setString(3, detalle);
            ps.setInt(4, kmActual);

            int filas = ps.executeUpdate();
            if (filas > 0) {
                JOptionPane.showMessageDialog(this,
                        "Mantenimiento registrado en " + kmActual + " km.\nPróximo objetivo: " + (kmActual + 5000) + " km.",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
                mantFechaField.setText("");
                mantDetalleArea.setText("");
                cargarFechasMantenimientoConsulta();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo registrar el mantenimiento.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this,
                    "Formato de fecha inválido. Usa YYYY-MM-DD.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al guardar mantenimiento:\n" + ex.getMessage(),
                    "Error BD",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void agregarCamion() {
        String placa = nuevoCamionPlacaField.getText().trim();
        String modelo = nuevoCamionModeloField.getText().trim();

        if (placa.isEmpty() || modelo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Placa y modelo son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int kmInicial;
        try {
            String kmTexto = nuevoCamionKmInicialField.getText().trim();
            kmInicial = kmTexto.isEmpty() ? 0 : Integer.parseInt(kmTexto);
            if (kmInicial < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Kilometraje inicial inválido.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String sql = "INSERT INTO camion (placa, modelo, kilometraje, conductor_usuario_id) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, placa);
            ps.setString(2, modelo);
            ps.setInt(3, kmInicial);
            ps.setNull(4, Types.INTEGER);

            int filas = ps.executeUpdate();
            if (filas > 0) {
                mostrarAlertaMantenimientoInicialSiCorresponde(placa, kmInicial);
                JOptionPane.showMessageDialog(this, "Camión agregado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                nuevoCamionPlacaField.setText("");
                nuevoCamionModeloField.setText("");
                nuevoCamionKmInicialField.setText("");
                refrescarDatosPantalla();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo agregar el camión.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al agregar camión:\n" + ex.getMessage(),
                    "Error BD",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarCamionSeleccionado() {
        if (!esAdministrador) {
            JOptionPane.showMessageDialog(this,
                    "Solo un administrador puede eliminar camiones.",
                    "Acceso denegado",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int fila = registrosTable.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this,
                    "Selecciona un camion de la tabla para eliminar.",
                    "Dato requerido",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idCamion = (int) tableModel.getValueAt(fila, 0);
        String placa = String.valueOf(tableModel.getValueAt(fila, 1));

        int confirmar = JOptionPane.showConfirmDialog(this,
                "Se eliminara el camion " + placa + " y sus mantenimientos asociados.\n¿Deseas continuar?",
                "Confirmar eliminacion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirmar != JOptionPane.YES_OPTION) {
            return;
        }

        String sqlMant = "DELETE FROM mantenimiento WHERE camion_id=?";
        String sqlCamion = "DELETE FROM camion WHERE id=?";

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement psMant = conn.prepareStatement(sqlMant);
                 PreparedStatement psCamion = conn.prepareStatement(sqlCamion)) {

                psMant.setInt(1, idCamion);
                psMant.executeUpdate();

                psCamion.setInt(1, idCamion);
                int filas = psCamion.executeUpdate();

                conn.commit();

                if (filas > 0) {
                    JOptionPane.showMessageDialog(this,
                            "Camion eliminado correctamente.",
                            "Exito",
                            JOptionPane.INFORMATION_MESSAGE);
                    refrescarDatosPantalla();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "No se encontro el camion seleccionado.",
                            "Aviso",
                            JOptionPane.WARNING_MESSAGE);
                }
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al eliminar camion:\n" + ex.getMessage(),
                    "Error BD",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void mostrarAlertaMantenimientoInicialSiCorresponde(String placa, int kilometrajeInicial) {
        if (kilometrajeInicial >= 5000) {
            JOptionPane.showMessageDialog(this,
                    "Alerta de mantenimiento para " + placa + ".\n" +
                            "Se registró con " + kilometrajeInicial + " km (>= 5000).",
                    "Alerta de mantenimiento",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void cargarRegistros() {
        tableModel.setRowCount(0);
        String sql = "SELECT c.id, c.placa, c.modelo, COALESCE(u.nombre, '-') AS conductor, c.kilometraje " +
                "FROM camion c " +
                "LEFT JOIN usuarios u ON u.id = c.conductor_usuario_id " +
                "ORDER BY c.id DESC";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Object[] row = {
                        rs.getInt("id"),
                        rs.getString("placa"),
                        rs.getString("modelo"),
                        rs.getString("conductor"),
                        rs.getInt("kilometraje")
                };
                tableModel.addRow(row);
            }
        } catch (SQLException ex) {
            System.err.println("Error al cargar registros: " + ex.getMessage());
        }
    }

    private static SesionUsuario autenticarUsuario(String usuario, String contrasena) {
        String sql = "SELECT id, " + COLUMNA_NOMBRE + ", " + COLUMNA_ROL + " FROM " + TABLA_USUARIOS +
            " WHERE " + COLUMNA_USUARIO + "=? AND " + COLUMNA_CONTRASENA + "=? AND " + COLUMNA_ACTIVO + "=1";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, usuario);
            ps.setString(2, contrasena);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new SesionUsuario(
                            rs.getInt("id"),
                            rs.getString(COLUMNA_NOMBRE),
                            rs.getString(COLUMNA_ROL)
                    );
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    "Error al validar usuario en base de datos:\n" + ex.getMessage(),
                    "Error BD",
                    JOptionPane.ERROR_MESSAGE);
        }

        return null;
    }

    private static SesionUsuario mostrarLoginInicial() {
        JTextField usuarioField = new JTextField(15);
        JPasswordField passwordField = new JPasswordField(15);

        JPanel panel = new JPanel(new GridLayout(2, 2, 8, 8));
        panel.add(new JLabel("Usuario:"));
        panel.add(usuarioField);
        panel.add(new JLabel("Contraseña:"));
        panel.add(passwordField);

        String[] opciones = {"Iniciar sesion", "Cancelar"};

        while (true) {
            int opcion = JOptionPane.showOptionDialog(
                    null,
                    panel,
                    "Inicio de sesión",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    opciones,
                    opciones[0]
            );

            if (opcion != 0) {
                return null;
            }

            String usuario = usuarioField.getText().trim();
            String contrasena = new String(passwordField.getPassword()).trim();

            if (usuario.isEmpty() || contrasena.isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "Ingresa Usuario y Contraseña.",
                        "Datos incompletos",
                        JOptionPane.WARNING_MESSAGE);
                continue;
            }

            SesionUsuario sesion = autenticarUsuario(usuario, contrasena);
            if (sesion != null) {
                return sesion;
            }

            JOptionPane.showMessageDialog(null,
                    "Usuario o contraseña incorrectos.",
                    "Acceso denegado",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("No se encontró el driver JDBC de MySQL. Asegúrate de tener el conector en el classpath.");
        }

        SwingUtilities.invokeLater(() -> {
            SesionUsuario sesion = mostrarLoginInicial();
            if (sesion != null) {
                new RegistroKilometraje(sesion).setVisible(true);
            }
        });
    }
}