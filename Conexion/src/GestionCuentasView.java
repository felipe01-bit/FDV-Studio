import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GestionCuentasView extends JFrame {

    private JComboBox<String> cmbCliente;
    private JTextField txtNumCuenta;
    private JRadioButton rbAhorros, rbCorriente, rbActiva, rbInactiva;
    private JButton btnGuardar, btnCancelar;

    public GestionCuentasView() {
        // 1. Configuración de la Ventana Principal
        setTitle("Banco Unicatolica - Registro y Modificación de Cuentas");
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
                "</tr><tr><td style='color:#444444; font-size:11px; padding-top:2px;'>Módulo de Cuentas — Registro Maestro y Actualización</td></tr></table></html>");

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
                "<span style='font-size:10px; color:#555;'>Hora oficial Colombia (UTC-5): " + horaStr + " COT | Oracle 19c Activo</span>" +
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
        JLabel lblMode = new JLabel("Ficha de cuenta seleccionada para actualización de parámetros.");
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
        
        String[] clientes = {
            "1144089231 - Carlos Alberto Gómez Ramírez (C.C.)",
            "1144567890 - María Fernanda Ortiz Castro (C.C.)",
            "1144901234 - Jorge Andrés Caicedo Mina (C.C.)"
        };
        cmbCliente = new JComboBox<>(clientes);
        cmbCliente.setEnabled(false);
        cmbCliente.setPreferredSize(new Dimension(460, 26)); 
        
        // Wrapper para evitar deformación
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

        txtNumCuenta = new JTextField("4102-8849-1029");
        txtNumCuenta.setFont(new Font("Monospaced", Font.PLAIN, 13));
        txtNumCuenta.setEnabled(false);
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
        JLabel lblEstado = new JLabel("<html><nobr><font color='#cc0000'>*</font> Estado de la Cuenta:</nobr></html>");
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

        // --- Fila 5: Banner informativo ---
        JPanel infoPanel = new JPanel(new BorderLayout(8, 0));
        infoPanel.setBackground(new Color(232, 240, 254));
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(148, 184, 232)),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        JLabel lblIconInfo = new JLabel("<html><b style='color:#1a4f8b; font-size:14px; margin-left:5px;'>i</b></html>");
        JLabel lblTextInfo = new JLabel("<html><span style='color:#1a4f8b; font-size:11.5px;'>" +
                "<b>Restricción:</b> En modo edición, los campos <u>\"Número de Cuenta\"</u> y <u>\"Cliente\"</u> permanecen deshabilitados " +
                "y grisados por integridad referencial y auditoría bancaria. Solo se permite actualizar el tipo de producto y su estado operativo.</span></html>");
        infoPanel.add(lblIconInfo, BorderLayout.WEST);
        infoPanel.add(lblTextInfo, BorderLayout.CENTER);
        
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 10, 10, 10);
        formPanel.add(infoPanel, gbc);

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
        
        JLabel lblShortcuts = new JLabel("<html><span style='color:#555; font-size:11px;'>[Enter] Procesar Guardar/Actualizar &nbsp;|&nbsp; [Esc] Cancelar operación</span></html>");
        
        JPanel pnlAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlAcciones.setOpaque(false);
        btnCancelar = new JButton("✕ Cancelar");
        btnGuardar = new JButton("✓ Actualizar (Guardar)");
        btnGuardar.setFont(new Font("Arial", Font.BOLD, 12));
        btnGuardar.setForeground(new Color(0, 43, 102));
        pnlAcciones.add(btnCancelar);
        pnlAcciones.add(btnGuardar);
        
        pnlBotones.add(lblShortcuts, BorderLayout.WEST);
        pnlBotones.add(pnlAcciones, BorderLayout.EAST);
        
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(153, 148, 136)));
        statusBar.add(new JLabel("Listo. Modo edición activo para la cuenta 4102-8849-1029. | Oracle DB: 192.168.1.104:1521/UNICADB"));

        bottomContainer.add(pnlBotones, BorderLayout.NORTH);
        bottomContainer.add(statusBar, BorderLayout.SOUTH);
        add(bottomContainer, BorderLayout.SOUTH);

        // 6. Eventos de botones
        btnCancelar.addActionListener(e -> dispose());
        btnGuardar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String tipo = rbAhorros.isSelected() ? "Ahorros" : "Corriente";
                String estado = rbActiva.isSelected() ? "Activa" : "Inactiva";
                
                JOptionPane.showMessageDialog(GestionCuentasView.this, 
                        "La cuenta ha sido actualizada exitosamente.\n\n" +
                        "Nuevo Tipo: " + tipo + "\n" +
                        "Nuevo Estado: " + estado, 
                        "Actualización Exitosa", JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
        SwingUtilities.invokeLater(() -> new GestionCuentasView().setVisible(true));
    }
}