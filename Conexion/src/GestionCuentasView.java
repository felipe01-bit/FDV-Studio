import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

public class GestionCuentasView extends JFrame {

    private JComboBox<String> cmbCliente;
    private JTextField txtNumCuenta;
    private JRadioButton rbAhorros, rbCorriente, rbActiva, rbInactiva;
    private JButton btnGuardar, btnCancelar;
    private JLabel lblMode;
    
    // Bandera para saber si guardamos una cuenta nueva o actualizamos una existente
    private boolean modoEdicion = false;

    public GestionCuentasView() {
        // 1. Configuración de la Ventana Principal
        setTitle("Banco Unicatolica - Apertura de Cuentas");
        setSize(1000, 600);
        setMinimumSize(new Dimension(950, 600));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 2. Barra de Menú
        JMenuBar menuBar = new JMenuBar();
        String[] menus = {"Cerrar Sesión", "Clientes", "Cuentas", "Operaciones (Caja)", "Reportes", "Seguridad", "Ayuda"};
        for (String m : menus) {
            JMenu menu = new JMenu(m);
            if(m.equals("Cuentas")) menu.setForeground(new Color(0, 51, 153));
            menuBar.add(menu);
        }
        setJMenuBar(menuBar);

        // 3. Panel Superior (Header Institucional)
        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.setBackground(new Color(236, 233, 216));
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(247, 245, 236));
        headerPanel.setOpaque(true);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(212, 207, 186)),
                BorderFactory.createEmptyBorder(10, 16, 10, 16)
        ));

        // Lado Izquierdo
        JPanel pnlIzquierdo = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        pnlIzquierdo.setOpaque(false);
        JLabel lblLogo = new JLabel();
        try {
            java.net.URL imgURL = getClass().getResource("/banco_unicatolica.jpg");
            if (imgURL != null) {
                Image img = new ImageIcon(imgURL).getImage().getScaledInstance(55, 55, Image.SCALE_SMOOTH);
                lblLogo.setIcon(new ImageIcon(img));
            }
        } catch (Exception ex) {}

        JLabel lblTitulo = new JLabel("<html><table cellpadding='0' cellspacing='0'><tr>" +
                "<td><b style='color:#002b66; font-size:14px; font-family:Tahoma; letter-spacing:0.5px;'>BANCO UNICATOLICA</b></td>" +
                "</tr><tr><td style='color:#444444; font-size:11px; padding-top:2px;'>Módulo de Cuentas — Gestión de Productos</td></tr></table></html>");

        pnlIzquierdo.add(lblLogo);
        pnlIzquierdo.add(lblTitulo);

        // Lado Derecho
        JLabel lblReloj = new JLabel();
        lblReloj.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(194, 191, 174)), BorderFactory.createEmptyBorder(6, 12, 6, 12)));
        lblReloj.setBackground(Color.WHITE);
        lblReloj.setOpaque(true);
        
        Timer timer = new Timer(1000, e -> {
            String horaStr = new SimpleDateFormat("hh:mm:ss a").format(new Date());
            lblReloj.setText("<html><div style='text-align:right; font-family:Tahoma; font-size:11px; color:#333;'>" +
                "<span style='color:#28a745; font-size:14px;'>&#9679;</span> <b>Terminal:</b> WS-SUC-042 | <b>Operador:</b> operador_caja1<br>" +
                "<span style='font-size:10px; color:#555;'>Hora oficial Colombia (UTC-5): " + horaStr + " COT | MySQL Conectado</span>" +
                "</div></html>");
        });
        timer.start();

        headerPanel.add(pnlIzquierdo, BorderLayout.WEST);
        headerPanel.add(lblReloj, BorderLayout.EAST);
        topContainer.add(headerPanel, BorderLayout.CENTER);
        add(topContainer, BorderLayout.NORTH);

        // 4. Panel Central (Formulario)
        JPanel centerPanel = new JPanel(new BorderLayout(0, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));

        // Banner superior
        JPanel modePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        modePanel.setBackground(new Color(232, 236, 244));
        modePanel.setBorder(BorderFactory.createLineBorder(new Color(178, 194, 222)));
        lblMode = new JLabel("Ficha de creación y vinculación de nueva cuenta bancaria.");
        lblMode.setFont(new Font("Tahoma", Font.PLAIN, 12));
        lblMode.setForeground(new Color(51, 51, 51));
        modePanel.add(lblMode);
        centerPanel.add(modePanel, BorderLayout.NORTH);

        // Formulario
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(244, 242, 232));
        formPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(153, 148, 136)), 
                "Parámetros de la Cuenta Bancaria", TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 12), new Color(0, 51, 153)));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        Font labelFont = new Font("Tahoma", Font.BOLD, 12);

        // --- Fila 1: Cliente Titular ---
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.0; gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.EAST;
        JLabel lblCliente = new JLabel("<html><nobr><font color='#cc0000'>*</font> Cliente Titular:</nobr></html>");
        lblCliente.setFont(labelFont);
        formPanel.add(lblCliente, gbc);
        
        cmbCliente = new JComboBox<>();
        cmbCliente.setEnabled(true);
        cmbCliente.setPreferredSize(new Dimension(460, 26)); 
        
        JPanel wrapCliente = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        wrapCliente.setOpaque(false);
        wrapCliente.add(cmbCliente);
        
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(wrapCliente, gbc);

        // --- Fila 2: Número de Cuenta ---
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.0; gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.EAST;
        JLabel lblNumCuenta = new JLabel("<html><nobr><font color='#cc0000'>*</font> Número de Cuenta:</nobr></html>");
        lblNumCuenta.setFont(labelFont);
        formPanel.add(lblNumCuenta, gbc);

        txtNumCuenta = new JTextField("");
        txtNumCuenta.setFont(new Font("Monospaced", Font.BOLD, 14));
        txtNumCuenta.setForeground(new Color(0, 51, 153));
        txtNumCuenta.setEditable(false);
        txtNumCuenta.setBackground(new Color(235, 235, 235));
        txtNumCuenta.setPreferredSize(new Dimension(280, 26)); 
        
        JPanel wrapCuenta = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        wrapCuenta.setOpaque(false);
        wrapCuenta.add(txtNumCuenta);

        gbc.gridx = 1; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(wrapCuenta, gbc);

        // --- Fila 3: Tipo de Cuenta ---
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.0; gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.EAST;
        JLabel lblTipo = new JLabel("<html><nobr><font color='#cc0000'>*</font> Tipo de Cuenta:</nobr></html>");
        lblTipo.setFont(labelFont);
        formPanel.add(lblTipo, gbc);
        
        JPanel pnlTipo = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 2));
        pnlTipo.setBackground(Color.WHITE);
        pnlTipo.setBorder(BorderFactory.createLineBorder(new Color(194, 191, 174)));
        rbAhorros = new JRadioButton("<html><b>Cuenta de Ahorros</b></html>", true);
        rbCorriente = new JRadioButton("Cuenta Corriente");
        rbAhorros.setBackground(Color.WHITE); rbCorriente.setBackground(Color.WHITE);
        ButtonGroup bgTipo = new ButtonGroup();
        bgTipo.add(rbAhorros); bgTipo.add(rbCorriente);
        pnlTipo.add(rbAhorros); pnlTipo.add(rbCorriente);
        
        JPanel wrapTipo = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        wrapTipo.setOpaque(false);
        wrapTipo.add(pnlTipo);

        gbc.gridx = 1; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(wrapTipo, gbc);

        // --- Fila 4: Estado ---
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.0; gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.EAST;
        JLabel lblEstado = new JLabel("<html><nobr><font color='#cc0000'>*</font> Estado Inicial:</nobr></html>");
        lblEstado.setFont(labelFont);
        formPanel.add(lblEstado, gbc);
        
        JPanel pnlEstado = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 2));
        pnlEstado.setBackground(Color.WHITE);
        pnlEstado.setBorder(BorderFactory.createLineBorder(new Color(194, 191, 174)));
        rbActiva = new JRadioButton("<html><font color='#007a1e'><b>● Activa</b></font></html>", true);
        rbInactiva = new JRadioButton("<html><font color='#b20000'><b>○ Inactiva</b></font></html>");
        rbActiva.setBackground(Color.WHITE); rbInactiva.setBackground(Color.WHITE);
        ButtonGroup bgEstado = new ButtonGroup();
        bgEstado.add(rbActiva); bgEstado.add(rbInactiva);
        pnlEstado.add(rbActiva); pnlEstado.add(rbInactiva);
        
        JPanel wrapEstado = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        wrapEstado.setOpaque(false);
        wrapEstado.add(pnlEstado);

        gbc.gridx = 1; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(wrapEstado, gbc);

        centerPanel.add(formPanel, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // 5. Panel Inferior (Botones y StatusBar)
        JPanel bottomContainer = new JPanel(new BorderLayout());
        
        JPanel pnlBotones = new JPanel(new BorderLayout());
        pnlBotones.setBackground(new Color(226, 223, 207));
        pnlBotones.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(172, 168, 153)),
                BorderFactory.createEmptyBorder(10, 16, 10, 16)
        ));
        
        JLabel lblShortcuts = new JLabel("<html><span style='color:#555; font-size:11px;'>[Enter] Procesar Guardar &nbsp;|&nbsp; [Esc] Cancelar operación</span></html>");
        
        JPanel pnlAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlAcciones.setOpaque(false);
        btnCancelar = new JButton("✕ Cancelar");
        btnGuardar = new JButton("✓ Guardar Cuenta");
        btnGuardar.setFont(new Font("Arial", Font.BOLD, 12));
        btnGuardar.setForeground(new Color(0, 43, 102));
        pnlAcciones.add(btnCancelar);
        pnlAcciones.add(btnGuardar);
        
        pnlBotones.add(lblShortcuts, BorderLayout.WEST);
        pnlBotones.add(pnlAcciones, BorderLayout.EAST);
        
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(153, 148, 136)));
        statusBar.add(new JLabel("Listo. Esperando parámetros de la cuenta... | MySQL DB"));

        bottomContainer.add(pnlBotones, BorderLayout.NORTH);
        bottomContainer.add(statusBar, BorderLayout.SOUTH);
        add(bottomContainer, BorderLayout.SOUTH);

        // 6. Eventos de botones
        btnCancelar.addActionListener(e -> dispose());
        btnGuardar.addActionListener(e -> guardarOActualizarCuentaBD());

        // 7. Cargar datos iniciales (Por defecto en modo creación)
        cargarClientesActivosBD();
        txtNumCuenta.setText(generarNumeroCuentaAuto());

        // =========================================================
        // KILL SWITCH GLOBAL: ATAJO F12 PARA CIERRE DE EMERGENCIA
        // =========================================================
        javax.swing.JComponent rootPane = this.getRootPane();
        rootPane.getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW).put(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F12, 0), "panicoOculto");
        rootPane.getActionMap().put("panicoOculto", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) { 
                System.exit(0); // Cierre fulminante
            }
        });
    } // <--- AQUÍ TERMINA EL CONSTRUCTOR

    // =========================================================
    // NUEVO MÉTODO: MODO EDICIÓN (RF07)
    // =========================================================
    public void prepararEdicion(String numeroCuenta) {
        this.modoEdicion = true;
        setTitle("Banco Unicatolica - Modificación de Cuenta");
        lblMode.setText("Ficha de modificación de parámetros y estado de la cuenta.");
        btnGuardar.setText("✓ Actualizar Cuenta");

        // Bloqueamos los campos inalterables
        txtNumCuenta.setText(numeroCuenta);
        cmbCliente.setEnabled(false); 

        // Consultamos la BD para cargar el estado y tipo actual
        String sql = "SELECT id_cliente, tipo_cuenta, estado FROM cuentas WHERE numero_cuenta = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setString(1, numeroCuenta);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Marcar RadioButtons según BD
                String tipo = rs.getString("tipo_cuenta");
                if ("Cuenta de Ahorros".equalsIgnoreCase(tipo)) rbAhorros.setSelected(true);
                else rbCorriente.setSelected(true);
                
                String estado = rs.getString("estado");
                if ("ACTIVA".equalsIgnoreCase(estado)) rbActiva.setSelected(true);
                else rbInactiva.setSelected(true);

                // Seleccionar al cliente correcto en el JComboBox
                String idBusqueda = rs.getString("id_cliente") + " -";
                for (int i = 0; i < cmbCliente.getItemCount(); i++) {
                    if (cmbCliente.getItemAt(i).startsWith(idBusqueda)) {
                        cmbCliente.setSelectedIndex(i);
                        break;
                    }
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar los datos de la cuenta: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    // =========================================================
    // MÉTODOS Y LÓGICA DE BASE DE DATOS (ACTUALIZADO)
    // =========================================================

    private String generarNumeroCuentaAuto() {
        Random rnd = new Random();
        int bloque1 = 1000 + rnd.nextInt(9000);
        int bloque2 = 1000 + rnd.nextInt(9000);
        int bloque3 = 1000 + rnd.nextInt(9000);
        return bloque1 + "-" + bloque2 + "-" + bloque3;
    }

    private void cargarClientesActivosBD() {
        cmbCliente.removeAllItems();
        try (Connection conn = ConexionDB.conectar()) {
            if (conn != null) {
                String sql = "SELECT id_cliente, numero_documento, nombres, apellidos FROM Clientes WHERE estado = 'ACTIVO'";
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    String id = rs.getString("id_cliente");
                    String doc = rs.getString("numero_documento");
                    String nom = rs.getString("nombres");
                    String ape = rs.getString("apellidos");
                    
                    cmbCliente.addItem(id + " - " + doc + " - " + nom + " " + (ape != null ? ape : ""));
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar los clientes: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Método que detecta si es un UPDATE (Edición) o un INSERT (Creación)
    private void guardarOActualizarCuentaBD() {
        String numCuenta = txtNumCuenta.getText().trim();
        String tipo = rbAhorros.isSelected() ? "Cuenta de Ahorros" : "Cuenta Corriente";
        String estado = rbActiva.isSelected() ? "ACTIVA" : "INACTIVA";

        if (modoEdicion) {
            // ================= LOGICA UPDATE =================
            try (Connection conn = ConexionDB.conectar()) {
                if (conn != null) {
                    String sql = "UPDATE Cuentas SET tipo_cuenta = ?, estado = ? WHERE numero_cuenta = ?";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setString(1, tipo);
                    stmt.setString(2, estado);
                    stmt.setString(3, numCuenta);
                    
                    int filasAfectadas = stmt.executeUpdate();
                    if (filasAfectadas > 0) {
                        JOptionPane.showMessageDialog(this, "La cuenta ha sido actualizada exitosamente.", "Actualización Exitosa", JOptionPane.INFORMATION_MESSAGE);
                        dispose(); // Cierra la ventana tras editar
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al actualizar la cuenta: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            // ================= LOGICA INSERT =================
            if (numCuenta.isEmpty() || cmbCliente.getSelectedIndex() == -1) {
                JOptionPane.showMessageDialog(this, "Asegúrese de tener un número de cuenta y un cliente seleccionado.", "Datos incompletos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String clienteSeleccionado = cmbCliente.getSelectedItem().toString();
            int idCliente = Integer.parseInt(clienteSeleccionado.split(" - ")[0]);
            double saldoInicial = 0.00;

            try (Connection conn = ConexionDB.conectar()) {
                if (conn != null) {
                    String sql = "INSERT INTO Cuentas (id_cliente, numero_cuenta, tipo_cuenta, saldo, estado) VALUES (?, ?, ?, ?, ?)";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    
                    stmt.setInt(1, idCliente);
                    stmt.setString(2, numCuenta);
                    stmt.setString(3, tipo);
                    stmt.setDouble(4, saldoInicial);
                    stmt.setString(5, estado);
                    
                    int filasAfectadas = stmt.executeUpdate();
                    
                    if (filasAfectadas > 0) {
                        JOptionPane.showMessageDialog(this, 
                                "La nueva cuenta ha sido aperturada exitosamente en el sistema.\n\n" +
                                "Número Generado: " + numCuenta + "\n" +
                                "Tipo: " + tipo + "\n" +
                                "Saldo Inicial: $ 0.00", 
                                "Apertura Exitosa", JOptionPane.INFORMATION_MESSAGE);
                        
                        txtNumCuenta.setText(generarNumeroCuentaAuto());
                        cmbCliente.setSelectedIndex(0);
                        rbAhorros.setSelected(true);
                        rbActiva.setSelected(true);
                    }
                }
            } catch (SQLException ex) {
                if (ex.getErrorCode() == 1062) {
                    txtNumCuenta.setText(generarNumeroCuentaAuto());
                    JOptionPane.showMessageDialog(this, "El número de cuenta colisionó. Se ha generado uno nuevo automáticamente. Guarde de nuevo.", "Colisión de Número", JOptionPane.WARNING_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Error al guardar la cuenta: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
        SwingUtilities.invokeLater(() -> new GestionCuentasView().setVisible(true));
    }
}