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
    private int mantIdSeleccionado = -1;

    private JTable registrosTable;
    private DefaultTableModel tableModel;

    private JTextField nuevoCamionPlacaField;
    private JTextField nuevoCamionModeloField;
    private JTextField nuevoCamionKmInicialField;

    // Campos para módulo de mantenimiento de equipos
    private JTextField equipoCodigoField;
    private JLabel equipoInfoLabel;
    private JLabel equipoTipoLabel;
    private JLabel equipoModeloLabel;
    private JLabel equipoUbicacionLabel;
    private JLabel equipoEstadoLabel;
    private JLabel equipoResponsableLabel;
    private JRadioButton mantPreventivoBtnEq;
    private JRadioButton mantCorrectivoBtnEq;
    private JLabel fechaHoyLabel;
    private JPanel checklistPanelEq;
    private JLabel progresoBarra;
    private JLabel itemsCriticosLabel;
    private EquipoItem equipoActualEq;
    private List<ChecklistItemEstado> checklistActualEq;
    private JScrollPane checklistScrollEq;

    // Campos para módulo de consulta de mantenciones de equipos
    private JTextField consultaEquipoCodigoFieldEq;
    private JLabel consultaEquipoInfoLabelEq;
    private JComboBox<MantencionFechaItem> consultaFechaMantEqCombo;
    private JTextArea consultaDetalleMantEqArea;
    private EquipoItem equipoConsultaActualEq;

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

    private static class EquipoItem {
        private final int id;
        private final String codigo;
        private final String tipo;
        private final String modelo;
        private final String ubicacion;
        private final String estado;
        private final String responsable;

        EquipoItem(int id, String codigo, String tipo, String modelo, String ubicacion, String estado, String responsable) {
            this.id = id;
            this.codigo = codigo;
            this.tipo = tipo;
            this.modelo = modelo;
            this.ubicacion = ubicacion;
            this.estado = estado;
            this.responsable = responsable;
        }

        int getId() { return id; }
        String getCodigo() { return codigo; }
        String getTipo() { return tipo; }
        String getModelo() { return modelo; }
        String getUbicacion() { return ubicacion; }
        String getEstado() { return estado; }
        String getResponsable() { return responsable; }

        @Override
        public String toString() { return codigo; }
    }

    private static class ChecklistItem {
        private final int id;
        private final String descripcion;
        private final int numero_seccion;
        private final String titulo_seccion;
        private final boolean es_critico;
        private final int orden;

        ChecklistItem(int id, String descripcion, int numero_seccion, String titulo_seccion, boolean es_critico, int orden) {
            this.id = id;
            this.descripcion = descripcion;
            this.numero_seccion = numero_seccion;
            this.titulo_seccion = titulo_seccion;
            this.es_critico = es_critico;
            this.orden = orden;
        }

        int getId() { return id; }
        String getDescripcion() { return descripcion; }
        int getNumeroSeccion() { return numero_seccion; }
        String getTituloSeccion() { return titulo_seccion; }
        boolean esCritico() { return es_critico; }
        int getOrden() { return orden; }
    }

    private static class ChecklistItemEstado {
        private final ChecklistItem item;
        private boolean completado;
        private String observacion;

        ChecklistItemEstado(ChecklistItem item) {
            this.item = item;
            this.completado = false;
            this.observacion = "";
        }

        ChecklistItem getItem() { return item; }
        boolean isCompletado() { return completado; }
        void setCompletado(boolean completado) { this.completado = completado; }
        String getObservacion() { return observacion; }
        void setObservacion(String observacion) { this.observacion = observacion != null ? observacion : ""; }
    }

    private static class MantencionFechaItem {
        private final int mantenimientoId;
        private final String fechaTexto;

        MantencionFechaItem(int mantenimientoId, String fechaTexto) {
            this.mantenimientoId = mantenimientoId;
            this.fechaTexto = fechaTexto;
        }

        int getMantenimientoId() {
            return mantenimientoId;
        }

        @Override
        public String toString() {
            return fechaTexto;
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
        if (esAdministrador) {
            tabs.addTab("Mantenimiento Equipos", crearPanelMantenimientoEquipos());
            tabs.addTab("Consulta Mantenciones Equipos", crearPanelConsultaMantencionesEquipos());
        }

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

        JButton eliminarMantBtn = new JButton("Eliminar");
        eliminarMantBtn.setBackground(new Color(160, 45, 45));
        eliminarMantBtn.setForeground(Color.WHITE);
        eliminarMantBtn.setFocusPainted(false);

        JButton actualizarMantBtn = new JButton("Actualizar");
        actualizarMantBtn.setBackground(new Color(200, 120, 20));
        actualizarMantBtn.setForeground(Color.WHITE);
        actualizarMantBtn.setFocusPainted(false);

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
        yr++;

        JPanel botonesPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botonesPanel.setOpaque(false);
        botonesPanel.add(actualizarMantBtn);
        botonesPanel.add(eliminarMantBtn);
        r.gridx = 0;
        r.gridy = yr;
        r.gridwidth = 2;
        rightPanel.add(botonesPanel, r);
        r.gridwidth = 1;

        consultaCamionComboMant.addActionListener(e -> cargarFechasMantenimientoConsulta());
        consultaFechaComboMant.addActionListener(e -> mostrarDetalleMantenimientoSeleccionado());

        eliminarMantBtn.addActionListener(e -> eliminarMantenimiento());
        actualizarMantBtn.addActionListener(e -> actualizarMantenimiento());

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

    private JPanel crearPanelMantenimientoEquipos() {
        Font labelFont = new Font("Segoe UI", Font.PLAIN, 11);
        Font titleFont = new Font("Segoe UI", Font.BOLD, 14);
        Font boldFont = new Font("Segoe UI", Font.BOLD, 11);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 245, 245));

        // PANEL SUPERIOR: TÍTULO
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(102, 51, 153));
        JLabel titleLabel = new JLabel("Mantenimiento de Equipos");
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);

        // PANEL CENTRAL: Dividido en secciones
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(245, 245, 245));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // ===== SECCIÓN 1: BÚSQUEDA DE EQUIPO =====
        JPanel seccion1 = new JPanel(new GridBagLayout());
        seccion1.setBackground(new Color(245, 245, 245));
        seccion1.setBorder(BorderFactory.createTitledBorder("1. Búsqueda de Equipo"));

        GridBagConstraints c1 = new GridBagConstraints();
        c1.insets = new Insets(8, 8, 8, 8);
        c1.anchor = GridBagConstraints.WEST;
        c1.fill = GridBagConstraints.HORIZONTAL;

        equipoCodigoField = new JTextField(15);
        JButton buscarBtn = new JButton("Buscar Equipo");
        buscarBtn.setFont(boldFont);
        buscarBtn.setBackground(new Color(102, 51, 153));
        buscarBtn.setForeground(Color.WHITE);
        buscarBtn.setFocusPainted(false);

        c1.gridx = 0; c1.gridy = 0;
        seccion1.add(new JLabel("Código del equipo:"), c1);
        c1.gridx = 1;
        seccion1.add(equipoCodigoField, c1);
        c1.gridx = 2;
        seccion1.add(buscarBtn, c1);

        equipoInfoLabel = new JLabel("Ingresa código y busca...");
        equipoInfoLabel.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        c1.gridx = 0; c1.gridy = 1;
        c1.gridwidth = 3;
        seccion1.add(equipoInfoLabel, c1);
        c1.gridwidth = 1;

        // ===== SECCIÓN 2: INFO DEL EQUIPO =====
        JPanel seccion2 = new JPanel(new GridBagLayout());
        seccion2.setBackground(new Color(245, 245, 245));
        seccion2.setBorder(BorderFactory.createTitledBorder("2. Información del Equipo"));

        GridBagConstraints c2 = new GridBagConstraints();
        c2.insets = new Insets(6, 6, 6, 6);
        c2.anchor = GridBagConstraints.WEST;
        c2.fill = GridBagConstraints.HORIZONTAL;

        equipoTipoLabel = new JLabel("Tipo: -");
        equipoTipoLabel.setFont(boldFont);
        equipoModeloLabel = new JLabel("Modelo: -");
        equipoModeloLabel.setFont(boldFont);
        equipoUbicacionLabel = new JLabel("Ubicación: -");
        equipoUbicacionLabel.setFont(boldFont);
        equipoEstadoLabel = new JLabel("Estado: -");
        equipoEstadoLabel.setFont(boldFont);
        equipoResponsableLabel = new JLabel("Responsable: -");
        equipoResponsableLabel.setFont(boldFont);

        c2.gridx = 0; c2.gridy = 0;
        seccion2.add(equipoTipoLabel, c2);
        c2.gridy = 1;
        seccion2.add(equipoModeloLabel, c2);
        c2.gridy = 2;
        seccion2.add(equipoUbicacionLabel, c2);
        c2.gridy = 3;
        seccion2.add(equipoEstadoLabel, c2);
        c2.gridy = 4;
        seccion2.add(equipoResponsableLabel, c2);

        // ===== SECCIÓN 3: TIPO DE MANTENIMIENTO =====
        JPanel seccion3 = new JPanel(new GridBagLayout());
        seccion3.setBackground(new Color(245, 245, 245));
        seccion3.setBorder(BorderFactory.createTitledBorder("3. Tipo de Mantenimiento"));

        GridBagConstraints c3 = new GridBagConstraints();
        c3.insets = new Insets(8, 8, 8, 8);
        c3.anchor = GridBagConstraints.WEST;

        mantPreventivoBtnEq = new JRadioButton("Preventivo", true);
        mantCorrectivoBtnEq = new JRadioButton("Correctivo");
        ButtonGroup grupoMant = new ButtonGroup();
        grupoMant.add(mantPreventivoBtnEq);
        grupoMant.add(mantCorrectivoBtnEq);

        fechaHoyLabel = new JLabel("Fecha: " + java.time.LocalDate.now());
        fechaHoyLabel.setFont(boldFont);

        c3.gridx = 0; c3.gridy = 0;
        seccion3.add(mantPreventivoBtnEq, c3);
        c3.gridx = 1;
        seccion3.add(mantCorrectivoBtnEq, c3);
        c3.gridx = 0; c3.gridy = 1;
        c3.gridwidth = 2;
        seccion3.add(fechaHoyLabel, c3);
        c3.gridwidth = 1;

        // ===== SECCIÓN 4: CHECKLIST =====
        checklistPanelEq = new JPanel();
        checklistPanelEq.setLayout(new BoxLayout(checklistPanelEq, BoxLayout.Y_AXIS));
        checklistPanelEq.setBackground(new Color(245, 245, 245));

        checklistScrollEq = new JScrollPane(checklistPanelEq);
        checklistScrollEq.setPreferredSize(new Dimension(900, 300));
        checklistScrollEq.setBorder(BorderFactory.createTitledBorder("4. Checklist de Mantenimiento"));

        itemsCriticosLabel = new JLabel("Selecciona un equipo primero");
        itemsCriticosLabel.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        itemsCriticosLabel.setForeground(new Color(200, 50, 50));

        progresoBarra = new JLabel("Progreso: 0/0 items [░░░░░░░░░░]");
        progresoBarra.setFont(boldFont);

        // ===== PANEL INFERIOR: BOTONES =====
        JPanel botonesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        botonesPanel.setOpaque(false);

        JButton guardarMantBtn = new JButton("Guardar Mantenimiento");
        guardarMantBtn.setFont(boldFont);
        guardarMantBtn.setBackground(new Color(102, 51, 153));
        guardarMantBtn.setForeground(Color.WHITE);
        guardarMantBtn.setFocusPainted(false);
        guardarMantBtn.setPreferredSize(new Dimension(200, 35));

        JButton limpiarBtn = new JButton("Limpiar");
        limpiarBtn.setFont(boldFont);
        limpiarBtn.setBackground(new Color(100, 100, 100));
        limpiarBtn.setForeground(Color.WHITE);
        limpiarBtn.setFocusPainted(false);
        limpiarBtn.setPreferredSize(new Dimension(150, 35));

        botonesPanel.add(guardarMantBtn);
        botonesPanel.add(limpiarBtn);

        // ===== AGREGAR SECCIONES AL PANEL CENTRAL =====
        JPanel sectionsPanel = new JPanel();
        sectionsPanel.setLayout(new BoxLayout(sectionsPanel, BoxLayout.Y_AXIS));
        sectionsPanel.setBackground(new Color(245, 245, 245));

        seccion1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        seccion2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        seccion3.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        sectionsPanel.add(seccion1);
        sectionsPanel.add(Box.createVerticalStrut(10));
        sectionsPanel.add(seccion2);
        sectionsPanel.add(Box.createVerticalStrut(10));
        sectionsPanel.add(seccion3);
        sectionsPanel.add(Box.createVerticalStrut(10));

        JPanel checklistPanel = new JPanel(new BorderLayout());
        checklistPanel.setBackground(new Color(245, 245, 245));
        checklistPanel.add(checklistScrollEq, BorderLayout.CENTER);

        JPanel infoChecklistPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        infoChecklistPanel.setOpaque(false);
        infoChecklistPanel.add(progresoBarra);
        infoChecklistPanel.add(itemsCriticosLabel);
        checklistPanel.add(infoChecklistPanel, BorderLayout.SOUTH);

        sectionsPanel.add(checklistPanel);
        sectionsPanel.add(Box.createVerticalStrut(10));

        JScrollPane scrollSecciones = new JScrollPane(sectionsPanel);
        scrollSecciones.setBackground(new Color(245, 245, 245));
        contentPanel.add(scrollSecciones, BorderLayout.CENTER);
        contentPanel.add(botonesPanel, BorderLayout.SOUTH);

        // ===== ACTION LISTENERS =====
        buscarBtn.addActionListener(e -> buscarEquipoPorCodigo());
        guardarMantBtn.addActionListener(e -> guardarMantenimientoEquipo());
        limpiarBtn.addActionListener(e -> limpiarFormularioEquipo());

        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        return mainPanel;
    }

    private JPanel crearPanelConsultaMantencionesEquipos() {
        Font labelFont = new Font("Segoe UI", Font.PLAIN, 11);
        Font titleFont = new Font("Segoe UI", Font.BOLD, 14);
        Font boldFont = new Font("Segoe UI", Font.BOLD, 11);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 245, 245));

        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(65, 105, 145));
        JLabel titleLabel = new JLabel("Consulta de Mantenciones de Equipos");
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);

        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBackground(new Color(245, 245, 245));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setBackground(new Color(245, 245, 245));
        topPanel.setBorder(BorderFactory.createTitledBorder("Búsqueda"));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;

        consultaEquipoCodigoFieldEq = new JTextField(12);
        JButton buscarBtn = new JButton("Buscar por código");
        buscarBtn.setBackground(new Color(65, 105, 145));
        buscarBtn.setForeground(Color.WHITE);
        buscarBtn.setFocusPainted(false);

        consultaEquipoInfoLabelEq = new JLabel("Ingresa el código del equipo (ej: PC-001) para consultar mantenciones.");
        consultaEquipoInfoLabelEq.setFont(new Font("Segoe UI", Font.ITALIC, 10));

        c.gridx = 0;
        c.gridy = 0;
        topPanel.add(new JLabel("Código equipo:"), c);
        c.gridx = 1;
        topPanel.add(consultaEquipoCodigoFieldEq, c);
        c.gridx = 2;
        topPanel.add(buscarBtn, c);

        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 3;
        topPanel.add(consultaEquipoInfoLabelEq, c);
        c.gridwidth = 1;

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(new Color(245, 245, 245));
        centerPanel.setBorder(BorderFactory.createTitledBorder("Historial"));

        GridBagConstraints cc = new GridBagConstraints();
        cc.insets = new Insets(8, 8, 8, 8);
        cc.anchor = GridBagConstraints.WEST;
        cc.fill = GridBagConstraints.HORIZONTAL;

        consultaFechaMantEqCombo = new JComboBox<>();
        consultaFechaMantEqCombo.setEnabled(false);

        cc.gridx = 0;
        cc.gridy = 0;
        centerPanel.add(new JLabel("Mantenciones (fecha):"), cc);
        cc.gridx = 1;
        centerPanel.add(consultaFechaMantEqCombo, cc);

        consultaDetalleMantEqArea = new JTextArea(18, 80);
        consultaDetalleMantEqArea.setEditable(false);
        consultaDetalleMantEqArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        consultaDetalleMantEqArea.setLineWrap(true);
        consultaDetalleMantEqArea.setWrapStyleWord(true);
        consultaDetalleMantEqArea.setText("Aquí se mostrará el detalle al seleccionar una mantención.");

        JScrollPane detalleScroll = new JScrollPane(consultaDetalleMantEqArea);
        detalleScroll.setBorder(BorderFactory.createTitledBorder("Detalle de Mantención"));

        JPanel topWrapper = new JPanel();
        topWrapper.setLayout(new BoxLayout(topWrapper, BoxLayout.Y_AXIS));
        topWrapper.setBackground(new Color(245, 245, 245));
        topPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        centerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        topWrapper.add(topPanel);
        topWrapper.add(Box.createVerticalStrut(8));
        topWrapper.add(centerPanel);

        contentPanel.add(topWrapper, BorderLayout.NORTH);
        contentPanel.add(detalleScroll, BorderLayout.CENTER);

        buscarBtn.addActionListener(e -> buscarMantencionesPorEquipoCodigo());
        consultaFechaMantEqCombo.addActionListener(e -> mostrarDetalleMantencionEquipoSeleccionada());

        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        return mainPanel;
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

        String sql = "SELECT id, detalle, kilometraje_servicio FROM mantenimiento " +
                "WHERE camion_id=? AND fecha=? ORDER BY id DESC LIMIT 1";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, camion.getId());
            ps.setDate(2, Date.valueOf(fechaSeleccionada));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    mantIdSeleccionado = rs.getInt("id");
                    String texto = "Placa: " + camion + "\n" +
                            "Fecha: " + fechaSeleccionada + "\n" +
                            "Kilometraje servicio: " + rs.getInt("kilometraje_servicio") + "\n\n" +
                            rs.getString("detalle");
                    consultaDetalleAreaMant.setText(texto);
                } else {
                    mantIdSeleccionado = -1;
                    consultaDetalleAreaMant.setText("No se encontró detalle para la selección actual.");
                }
            }
        } catch (SQLException ex) {
            mantIdSeleccionado = -1;
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

    private void eliminarMantenimiento() {
        if (mantIdSeleccionado < 0) {
            JOptionPane.showMessageDialog(this,
                    "Selecciona una placa y una fecha de mantenimiento para eliminar.",
                    "Dato requerido",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String fechaSeleccionada = (String) consultaFechaComboMant.getSelectedItem();
        CamionItem camion = (CamionItem) consultaCamionComboMant.getSelectedItem();

        int confirmar = JOptionPane.showConfirmDialog(this,
                "Se eliminará el mantenimiento del " + fechaSeleccionada +
                " del camión " + camion + ".\n¿Deseas continuar?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirmar != JOptionPane.YES_OPTION) {
            return;
        }

        String sql = "DELETE FROM mantenimiento WHERE id=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, mantIdSeleccionado);
            int filas = ps.executeUpdate();
            if (filas > 0) {
                JOptionPane.showMessageDialog(this,
                        "Mantenimiento eliminado correctamente.",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
                mantIdSeleccionado = -1;
                consultaDetalleAreaMant.setText("");
                cargarFechasMantenimientoConsulta();
            } else {
                JOptionPane.showMessageDialog(this,
                        "No se encontró el mantenimiento a eliminar.",
                        "Aviso",
                        JOptionPane.WARNING_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al eliminar mantenimiento:\n" + ex.getMessage(),
                    "Error BD",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarMantenimiento() {
        if (mantIdSeleccionado < 0) {
            JOptionPane.showMessageDialog(this,
                    "Selecciona una placa y una fecha de mantenimiento para actualizar.",
                    "Dato requerido",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        CamionItem camion = (CamionItem) consultaCamionComboMant.getSelectedItem();
        String fechaActual = (String) consultaFechaComboMant.getSelectedItem();

        // Obtener detalle actual desde la BD para prellenar el diálogo
        String detalleActual = "";
        String fechaEditada = fechaActual != null ? fechaActual : "";
        String sql = "SELECT detalle, fecha FROM mantenimiento WHERE id=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, mantIdSeleccionado);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    detalleActual = rs.getString("detalle");
                    fechaEditada = rs.getDate("fecha").toString();
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar datos del mantenimiento:\n" + ex.getMessage(),
                    "Error BD",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        JTextField nuevaFechaField = new JTextField(fechaEditada, 15);
        JTextArea nuevoDetalleArea = new JTextArea(detalleActual, 5, 24);
        nuevoDetalleArea.setLineWrap(true);
        nuevoDetalleArea.setWrapStyleWord(true);

        JPanel dialogPanel = new JPanel(new GridBagLayout());
        GridBagConstraints dc = new GridBagConstraints();
        dc.insets = new Insets(6, 6, 6, 6);
        dc.anchor = GridBagConstraints.WEST;
        dc.fill = GridBagConstraints.HORIZONTAL;

        dc.gridx = 0; dc.gridy = 0;
        dialogPanel.add(new JLabel("Camión:"), dc);
        dc.gridx = 1;
        dialogPanel.add(new JLabel(camion != null ? camion.toString() : "-"), dc);

        dc.gridx = 0; dc.gridy = 1;
        dialogPanel.add(new JLabel("Fecha (YYYY-MM-DD):"), dc);
        dc.gridx = 1;
        dialogPanel.add(nuevaFechaField, dc);

        dc.gridx = 0; dc.gridy = 2;
        dialogPanel.add(new JLabel("Detalle:"), dc);
        dc.gridx = 1;
        dialogPanel.add(new JScrollPane(nuevoDetalleArea), dc);

        int opcion = JOptionPane.showConfirmDialog(this,
                dialogPanel,
                "Actualizar mantenimiento",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (opcion != JOptionPane.OK_OPTION) {
            return;
        }

        String nuevaFecha = nuevaFechaField.getText().trim();
        String nuevoDetalle = nuevoDetalleArea.getText().trim();

        if (nuevaFecha.isEmpty() || nuevoDetalle.isEmpty()) {
            JOptionPane.showMessageDialog(this, "La fecha y el detalle no pueden estar vacíos.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String sqlUpdate = "UPDATE mantenimiento SET fecha=?, detalle=? WHERE id=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlUpdate)) {

            ps.setDate(1, Date.valueOf(nuevaFecha));
            ps.setString(2, nuevoDetalle);
            ps.setInt(3, mantIdSeleccionado);

            int filas = ps.executeUpdate();
            if (filas > 0) {
                JOptionPane.showMessageDialog(this,
                        "Mantenimiento actualizado correctamente.",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
                cargarFechasMantenimientoConsulta();
            } else {
                JOptionPane.showMessageDialog(this,
                        "No se pudo actualizar el mantenimiento.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this,
                    "Formato de fecha inválido. Usa YYYY-MM-DD.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al actualizar mantenimiento:\n" + ex.getMessage(),
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

    // =========================================================================
    // MÉTODOS DEL MÓDULO DE MANTENIMIENTO DE EQUIPOS
    // =========================================================================

    private void buscarEquipoPorCodigo() {
        String codigo = equipoCodigoField.getText().trim().toUpperCase();

        if (codigo.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Ingresa el código del equipo a buscar.",
                    "Dato requerido",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        EquipoItem equipo = obtenerEquipoPorCodigo(codigo);

        if (equipo == null) {
            JOptionPane.showMessageDialog(this,
                    "Equipo con código '" + codigo + "' no encontrado.",
                    "Equipo no encontrado",
                    JOptionPane.ERROR_MESSAGE);
            limpiarFormularioEquipo();
            return;
        }

        if (!equipo.getEstado().equalsIgnoreCase("activo")) {
            JOptionPane.showMessageDialog(this,
                    "El equipo '" + codigo + "' no está activo (Estado: " + equipo.getEstado() + ").",
                    "Equipo no disponible",
                    JOptionPane.WARNING_MESSAGE);
            limpiarFormularioEquipo();
            return;
        }

        equipoActualEq = equipo;
        equipoTipoLabel.setText("Tipo: " + equipo.getTipo());
        equipoModeloLabel.setText("Modelo: " + equipo.getModelo());
        equipoUbicacionLabel.setText("Ubicación: " + equipo.getUbicacion());
        equipoEstadoLabel.setText("Estado: " + equipo.getEstado());
        equipoResponsableLabel.setText("Responsable: " + (equipo.getResponsable() != null && !equipo.getResponsable().isEmpty() ? equipo.getResponsable() : "-"));
        equipoInfoLabel.setText("✓ Equipo encontrado: " + equipo.getCodigo() + " - " + equipo.getModelo());
        equipoInfoLabel.setForeground(new Color(50, 150, 50));

        cargarChecklistEquipo(equipo.getTipo());
    }

    private EquipoItem obtenerEquipoPorCodigo(String codigo) {
        String sql = "SELECT e.id, e.codigo, e.tipo, e.modelo, e.ubicacion, e.estado, COALESCE(u.nombre, '-') AS responsable " +
                "FROM equipos e " +
                "LEFT JOIN usuarios u ON u.id = e.responsable_usuario_id " +
                "WHERE e.codigo = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new EquipoItem(
                            rs.getInt("id"),
                            rs.getString("codigo"),
                            rs.getString("tipo"),
                            rs.getString("modelo"),
                            rs.getString("ubicacion"),
                            rs.getString("estado"),
                            rs.getString("responsable")
                    );
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al buscar equipo:\n" + ex.getMessage(),
                    "Error BD",
                    JOptionPane.ERROR_MESSAGE);
        }

        return null;
    }

    private void cargarChecklistEquipo(String tipoEquipo) {
        checklistPanelEq.removeAll();
        checklistActualEq = new ArrayList<>();

        List<ChecklistItem> items = obtenerItemsChecklistPorTipo(tipoEquipo);

        if (items.isEmpty()) {
            JLabel sinItems = new JLabel("No hay items de checklist para este tipo de equipo.");
            sinItems.setFont(new Font("Segoe UI", Font.ITALIC, 11));
            checklistPanelEq.add(sinItems);
            checklistScrollEq.revalidate();
            checklistScrollEq.repaint();
            progresoBarra.setText("Progreso: 0/0 items [░░░░░░░░░░]");
            itemsCriticosLabel.setText("");
            return;
        }

        int numeroSeccionAnterior = -1;
        JPanel panelSeccionActual = null;

        for (ChecklistItem item : items) {
            ChecklistItemEstado itemEstado = new ChecklistItemEstado(item);
            checklistActualEq.add(itemEstado);

            // Crear panel de sección si es nueva
            if (item.getNumeroSeccion() != numeroSeccionAnterior) {
                panelSeccionActual = new JPanel();
                panelSeccionActual.setLayout(new BoxLayout(panelSeccionActual, BoxLayout.Y_AXIS));
                panelSeccionActual.setBackground(new Color(220, 220, 220));
                panelSeccionActual.setBorder(BorderFactory.createTitledBorder(
                        "Sección " + item.getNumeroSeccion() + ": " + item.getTituloSeccion()));

                checklistPanelEq.add(panelSeccionActual);
                checklistPanelEq.add(Box.createVerticalStrut(5));
                numeroSeccionAnterior = item.getNumeroSeccion();
            }

            // Crear item del checklist
            JPanel itemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
            itemPanel.setOpaque(false);

            JCheckBox checkbox = new JCheckBox(item.getDescripcion());
            checkbox.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            if (item.esCritico()) {
                checkbox.setText(checkbox.getText() + " ⚠️");
            }

            JTextField observacionField = new JTextField(30);
            observacionField.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            observacionField.setToolTipText("Observación opcional para este item");

            checkbox.addActionListener(e -> {
                itemEstado.setCompletado(checkbox.isSelected());
                actualizarProgresoChecklist();
            });

            observacionField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                public void insertUpdate(javax.swing.event.DocumentEvent e) { itemEstado.setObservacion(observacionField.getText()); }
                public void removeUpdate(javax.swing.event.DocumentEvent e) { itemEstado.setObservacion(observacionField.getText()); }
                public void changedUpdate(javax.swing.event.DocumentEvent e) { itemEstado.setObservacion(observacionField.getText()); }
            });

            itemPanel.add(checkbox);
            itemPanel.add(new JLabel("Obs:"));
            itemPanel.add(observacionField);

            panelSeccionActual.add(itemPanel);
        }

        checklistScrollEq.revalidate();
        checklistScrollEq.repaint();
        actualizarProgresoChecklist();
    }

    private List<ChecklistItem> obtenerItemsChecklistPorTipo(String tipoEquipo) {
        List<ChecklistItem> items = new ArrayList<>();
        String sql = "SELECT id, numero_seccion, titulo_seccion, numero_item, descripcion, es_critico, orden " +
                "FROM checklist_items " +
                "WHERE tipo_equipo = ? " +
                "ORDER BY numero_seccion, orden";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tipoEquipo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(new ChecklistItem(
                            rs.getInt("id"),
                            rs.getString("descripcion"),
                            rs.getInt("numero_seccion"),
                            rs.getString("titulo_seccion"),
                            rs.getBoolean("es_critico"),
                            rs.getInt("orden")
                    ));
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar checklist:\n" + ex.getMessage(),
                    "Error BD",
                    JOptionPane.ERROR_MESSAGE);
        }

        return items;
    }

    private void actualizarProgresoChecklist() {
        if (checklistActualEq == null || checklistActualEq.isEmpty()) {
            progresoBarra.setText("Progreso: 0/0 items [░░░░░░░░░░]");
            itemsCriticosLabel.setText("");
            return;
        }

        int total = checklistActualEq.size();
        int completados = (int) checklistActualEq.stream().filter(ChecklistItemEstado::isCompletado).count();
        int criticos = (int) checklistActualEq.stream().filter(c -> c.getItem().esCritico()).count();
        int criticosPendientes = (int) checklistActualEq.stream()
                .filter(c -> c.getItem().esCritico() && !c.isCompletado()).count();

        double porcentaje = (100.0 * completados) / total;
        int barras = (int) (porcentaje / 10);
        String barra = "█".repeat(barras) + "░".repeat(10 - barras);

        progresoBarra.setText(String.format("Progreso: %d/%d items [%s] %.0f%%", completados, total, barra, porcentaje));

        if (criticosPendientes > 0) {
            itemsCriticosLabel.setText("⚠️ " + criticosPendientes + " items críticos sin completar");
            itemsCriticosLabel.setForeground(new Color(200, 50, 50));
        } else {
            itemsCriticosLabel.setText("✓ Todos los items críticos completados");
            itemsCriticosLabel.setForeground(new Color(50, 150, 50));
        }
    }

    private void guardarMantenimientoEquipo() {
        if (equipoActualEq == null) {
            JOptionPane.showMessageDialog(this,
                    "Busca un equipo primero.",
                    "Equipo requerido",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (checklistActualEq == null || checklistActualEq.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No hay items de checklist cargados.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int completados = (int) checklistActualEq.stream().filter(ChecklistItemEstado::isCompletado).count();
        if (completados == 0) {
            JOptionPane.showMessageDialog(this,
                    "Debes marcar al menos un item del checklist.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int criticosPendientes = (int) checklistActualEq.stream()
                .filter(c -> c.getItem().esCritico() && !c.isCompletado()).count();
        if (criticosPendientes > 0) {
            int confirmar = JOptionPane.showConfirmDialog(this,
                    "Hay " + criticosPendientes + " items críticos sin completar.\n¿Deseas continuar?",
                    "Items críticos pendientes",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (confirmar != JOptionPane.YES_OPTION) {
                return;
            }
        }

        String tipoMant = mantPreventivoBtnEq.isSelected() ? "preventivo" : "correctivo";

        String sql = "INSERT INTO mantenimiento_equipo (equipo_id, fecha_programada, tipo, estado, realizado_por_usuario_id) " +
                "VALUES (?, ?, ?, 'completado', ?)";

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement psEquipo = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                psEquipo.setInt(1, equipoActualEq.getId());
                psEquipo.setDate(2, java.sql.Date.valueOf(java.time.LocalDate.now()));
                psEquipo.setString(3, tipoMant);
                psEquipo.setInt(4, usuarioIdSesion);

                int filasEquipo = psEquipo.executeUpdate();
                if (filasEquipo == 0) {
                    throw new SQLException("No se pudo insertar mantención.");
                }

                int idMantenimiento;
                try (ResultSet generatedKeys = psEquipo.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        idMantenimiento = generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("No se obtuvo el ID de mantención.");
                    }
                }

                String sqlChecklist = "INSERT INTO mantenimiento_checklist (mantenimiento_equipo_id, checklist_item_id, completado, observacion, completado_en) " +
                        "VALUES (?, ?, ?, ?, ?)";

                try (PreparedStatement psChecklist = conn.prepareStatement(sqlChecklist)) {
                    for (ChecklistItemEstado itemEstado : checklistActualEq) {
                        psChecklist.setInt(1, idMantenimiento);
                        psChecklist.setInt(2, itemEstado.getItem().getId());
                        psChecklist.setBoolean(3, itemEstado.isCompletado());
                        psChecklist.setString(4, itemEstado.getObservacion().isEmpty() ? null : itemEstado.getObservacion());
                        psChecklist.setTimestamp(5, itemEstado.isCompletado() ? new java.sql.Timestamp(System.currentTimeMillis()) : null);
                        psChecklist.addBatch();
                    }
                    psChecklist.executeBatch();
                }

                conn.commit();

                JOptionPane.showMessageDialog(this,
                        "✓ Mantención guardada exitosamente.\n\n" +
                        "Equipo: " + equipoActualEq.getCodigo() + "\n" +
                        "Tipo: " + tipoMant + "\n" +
                        "Items completados: " + completados + "/" + checklistActualEq.size(),
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);

                limpiarFormularioEquipo();

            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al guardar mantención:\n" + ex.getMessage(),
                    "Error BD",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarFormularioEquipo() {
        equipoCodigoField.setText("");
        equipoInfoLabel.setText("Ingresa código y busca...");
        equipoInfoLabel.setForeground(new Color(100, 100, 100));
        equipoTipoLabel.setText("Tipo: -");
        equipoModeloLabel.setText("Modelo: -");
        equipoUbicacionLabel.setText("Ubicación: -");
        equipoEstadoLabel.setText("Estado: -");
        equipoResponsableLabel.setText("Responsable: -");

        mantPreventivoBtnEq.setSelected(true);
        mantCorrectivoBtnEq.setSelected(false);

        checklistPanelEq.removeAll();
        checklistScrollEq.revalidate();
        checklistScrollEq.repaint();

        progresoBarra.setText("Progreso: 0/0 items [░░░░░░░░░░]");
        itemsCriticosLabel.setText("");

        equipoActualEq = null;
        checklistActualEq = null;
    }

    private void buscarMantencionesPorEquipoCodigo() {
        String codigo = consultaEquipoCodigoFieldEq.getText().trim().toUpperCase();

        if (codigo.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Ingresa un código de equipo válido (ej: PC-001, IMP-001, PROY-001).",
                    "Código inválido",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        EquipoItem equipo = obtenerEquipoPorCodigo(codigo);
        if (equipo == null) {
            consultaEquipoInfoLabelEq.setText("No se encontró el equipo con código " + codigo + ".");
            consultaEquipoInfoLabelEq.setForeground(new Color(180, 60, 60));
            consultaFechaMantEqCombo.removeAllItems();
            consultaFechaMantEqCombo.setEnabled(false);
            consultaDetalleMantEqArea.setText("Sin resultados para el código indicado.");
            equipoConsultaActualEq = null;
            return;
        }

        equipoConsultaActualEq = equipo;
        consultaEquipoInfoLabelEq.setText("Equipo encontrado: " + equipo.getCodigo() + " | " + equipo.getTipo() + " | " + equipo.getModelo());
        consultaEquipoInfoLabelEq.setForeground(new Color(50, 130, 50));
        cargarFechasMantencionesEquipo(equipo.getId());
    }

    private void cargarFechasMantencionesEquipo(int equipoId) {
        consultaFechaMantEqCombo.removeAllItems();
        consultaFechaMantEqCombo.setEnabled(false);

        String sql = "SELECT id, COALESCE(fecha_realizada, fecha_programada) AS fecha_mant " +
                "FROM mantenimiento_equipo " +
                "WHERE equipo_id = ? " +
                "ORDER BY fecha_mant DESC, id DESC";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, equipoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int mantenimientoId = rs.getInt("id");
                    Date fecha = rs.getDate("fecha_mant");
                    String fechaTexto = (fecha != null ? fecha.toString() : "Sin fecha") + " (ID mant. " + mantenimientoId + ")";
                    consultaFechaMantEqCombo.addItem(new MantencionFechaItem(mantenimientoId, fechaTexto));
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar fechas de mantenciones:\n" + ex.getMessage(),
                    "Error BD",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (consultaFechaMantEqCombo.getItemCount() == 0) {
            consultaDetalleMantEqArea.setText("El equipo no tiene mantenciones registradas.");
            return;
        }

        consultaFechaMantEqCombo.setEnabled(true);
        consultaFechaMantEqCombo.setSelectedIndex(0);
        mostrarDetalleMantencionEquipoSeleccionada();
    }

    private void mostrarDetalleMantencionEquipoSeleccionada() {
        MantencionFechaItem seleccion = (MantencionFechaItem) consultaFechaMantEqCombo.getSelectedItem();
        if (seleccion == null || equipoConsultaActualEq == null) {
            return;
        }

        String sqlResumen = "SELECT me.id, me.fecha_programada, me.fecha_realizada, me.tipo, me.estado, " +
                "COALESCE(u.nombre, '-') AS realizado_por, COALESCE(me.detalles, '-') AS detalles, " +
                "SUM(CASE WHEN mc.completado = 1 THEN 1 ELSE 0 END) AS completados, " +
                "COUNT(mc.id) AS total_items " +
                "FROM mantenimiento_equipo me " +
                "LEFT JOIN usuarios u ON u.id = me.realizado_por_usuario_id " +
                "LEFT JOIN mantenimiento_checklist mc ON mc.mantenimiento_equipo_id = me.id " +
                "WHERE me.id = ? " +
                "GROUP BY me.id, me.fecha_programada, me.fecha_realizada, me.tipo, me.estado, u.nombre, me.detalles";

        StringBuilder detalle = new StringBuilder();

        try (Connection conn = getConnection();
             PreparedStatement psResumen = conn.prepareStatement(sqlResumen)) {

            psResumen.setInt(1, seleccion.getMantenimientoId());
            try (ResultSet rs = psResumen.executeQuery()) {
                if (rs.next()) {
                    detalle.append("Equipo: ").append(equipoConsultaActualEq.getCodigo()).append("\n");
                    detalle.append("Tipo equipo: ").append(equipoConsultaActualEq.getTipo()).append("\n");
                    detalle.append("Modelo: ").append(equipoConsultaActualEq.getModelo()).append("\n");
                    detalle.append("Ubicación: ").append(equipoConsultaActualEq.getUbicacion()).append("\n");
                    detalle.append("--------------------------------------------------\n");
                    detalle.append("ID mantención: ").append(rs.getInt("id")).append("\n");
                    detalle.append("Fecha programada: ").append(rs.getDate("fecha_programada")).append("\n");
                    detalle.append("Fecha realizada: ").append(rs.getDate("fecha_realizada") != null ? rs.getDate("fecha_realizada") : "-").append("\n");
                    detalle.append("Tipo mantención: ").append(rs.getString("tipo")).append("\n");
                    detalle.append("Estado: ").append(rs.getString("estado")).append("\n");
                    detalle.append("Realizado por: ").append(rs.getString("realizado_por")).append("\n");
                    detalle.append("Progreso checklist: ").append(rs.getInt("completados")).append("/").append(rs.getInt("total_items")).append("\n");
                    detalle.append("Detalles generales: ").append(rs.getString("detalles")).append("\n");
                    detalle.append("--------------------------------------------------\n");
                }
            }

            String sqlChecklist = "SELECT ci.numero_seccion, ci.descripcion, mc.completado, COALESCE(mc.observacion, '') AS observacion " +
                    "FROM mantenimiento_checklist mc " +
                    "INNER JOIN checklist_items ci ON ci.id = mc.checklist_item_id " +
                    "WHERE mc.mantenimiento_equipo_id = ? " +
                    "ORDER BY ci.numero_seccion, ci.orden";

            try (PreparedStatement psChecklist = conn.prepareStatement(sqlChecklist)) {
                psChecklist.setInt(1, seleccion.getMantenimientoId());
                try (ResultSet rc = psChecklist.executeQuery()) {
                    int seccionActual = -1;
                    while (rc.next()) {
                        int seccion = rc.getInt("numero_seccion");
                        if (seccion != seccionActual) {
                            detalle.append("\nSección ").append(seccion).append(":\n");
                            seccionActual = seccion;
                        }

                        String estado = rc.getBoolean("completado") ? "[OK]" : "[ ]";
                        detalle.append(" ").append(estado).append(" ").append(rc.getString("descripcion")).append("\n");

                        String observacion = rc.getString("observacion");
                        if (observacion != null && !observacion.isBlank()) {
                            detalle.append("     Obs: ").append(observacion).append("\n");
                        }
                    }
                }
            }

            consultaDetalleMantEqArea.setText(detalle.toString());
            consultaDetalleMantEqArea.setCaretPosition(0);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar detalle de mantención:\n" + ex.getMessage(),
                    "Error BD",
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