import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class LoginView extends JFrame {

    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JButton btnIngresar, btnCancelar;
    private JLabel lblIntentos, lblEstado;
    
    private int intentosFallidos = 0;
    private final int MAX_INTENTOS = 3;

    public LoginView() {
        // 1. Configuración de la Ventana Principal
        setTitle("Banco Unicatolica - Sistema de Gestión");
        setSize(420, 360); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());

        // 2. Barra de Menú Simple
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(new JMenu("Seguridad"));
        menuBar.add(new JMenu("Ayuda"));
        setJMenuBar(menuBar);

        // 3. Panel Central
        JPanel centerPanel = new JPanel(new BorderLayout(0, 15));
        centerPanel.setBackground(new Color(236, 233, 216));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // Header Institucional
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(208, 208, 208)));
        
        JLabel lblLogo = new JLabel("<html><div style='background-color:#0A246A; color:white; padding:8px 10px; font-weight:bold; border:1px solid #7F9DB9;'>BU</div></html>");
        JLabel lblTitulo = new JLabel("<html><b style='color:#0A246A; font-size:13px;'>BANCO UNICATOLICA</b><br><span style='color:#444; font-size:10px;'>Módulo de Autenticación</span></html>");
        
        headerPanel.add(lblLogo);
        headerPanel.add(lblTitulo);
        centerPanel.add(headerPanel, BorderLayout.NORTH);

        // Formulario de Login
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(5, 0, 5, 0);

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Usuario:"), gbc);
        
        gbc.gridy = 1;
        txtUsuario = new JTextField("operador_caja1");
        txtUsuario.setPreferredSize(new Dimension(200, 25));
        formPanel.add(txtUsuario, gbc);

        gbc.gridy = 2;
        formPanel.add(new JLabel("Contraseña:"), gbc);
        
        gbc.gridy = 3;
        txtPassword = new JPasswordField();
        txtPassword.setPreferredSize(new Dimension(200, 25));
        formPanel.add(txtPassword, gbc);

        // Cuadro de Intentos
        gbc.gridy = 4;
        gbc.insets = new Insets(15, 0, 5, 0);
        
        JPanel pnlIntentos = new JPanel(new BorderLayout());
        pnlIntentos.setBackground(new Color(227, 224, 208));
        pnlIntentos.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(197, 194, 180)),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        
        lblIntentos = new JLabel("<html><span style='font-size:10px; color:#555;'>Intentos fallidos: <b style='color:#AA0000;'>" + intentosFallidos + "</b> / " + MAX_INTENTOS + "</span></html>");
        lblEstado = new JLabel("<html><span style='font-size:9px; background-color:#28a745; color:white; padding:2px 4px;'>ESTADO: Activo</span></html>");
        
        pnlIntentos.add(lblIntentos, BorderLayout.WEST);
        pnlIntentos.add(lblEstado, BorderLayout.EAST);
        
        formPanel.add(pnlIntentos, gbc);
        centerPanel.add(formPanel, BorderLayout.CENTER);

        // Botones
        JPanel pnlBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlBotones.setOpaque(false);
        pnlBotones.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(208, 208, 208)));
        
        btnCancelar = new JButton("Cancelar");
        btnIngresar = new JButton("Ingresar");
        btnIngresar.setFont(new Font("Tahoma", Font.BOLD, 11));
        
        pnlBotones.add(btnCancelar);
        pnlBotones.add(btnIngresar);
        pnlBotones.setBorder(BorderFactory.createCompoundBorder(pnlBotones.getBorder(), BorderFactory.createEmptyBorder(10, 0, 0, 0)));
        
        centerPanel.add(pnlBotones, BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);

        // 4. Barra de Estado Inferior
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(153, 153, 153)));
        statusBar.add(new JLabel("<html><span style='color:#00aa00;'>&#9679;</span> Listo. Servidor: 192.168.1.104:1521 (Oracle DB) [Conectado] | Modo: Local</html>"));
        add(statusBar, BorderLayout.SOUTH);

        // =========================================================
        // 5. EVENTOS
        // =========================================================

        btnCancelar.addActionListener(e -> {
            txtUsuario.setText("");
            txtPassword.setText("");
            txtUsuario.requestFocus();
        });

        btnIngresar.addActionListener(e -> validarLogin());
        txtPassword.addActionListener(e -> validarLogin());
    }

    // =========================================================
    // 6. LÓGICA DE BASE DE DATOS Y SEGURIDAD
    // =========================================================
    private void validarLogin() {
        String usr = txtUsuario.getText().trim();
        String pwd = new String(txtPassword.getPassword());

        if (usr.isEmpty() || pwd.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor ingrese usuario y contraseña.", "Campos vacíos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = ConexionDB.conectar()) {
            if (conn != null) {
                String sql = "SELECT * FROM Usuarios WHERE username = ? AND password = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, usr);
                stmt.setString(2, pwd);
                
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, "Autenticación exitosa. Bienvenido al sistema.", "Acceso Permitido", JOptionPane.INFORMATION_MESSAGE);
                    intentosFallidos = 0;
                    
                    MenuPrincipalView menu = new MenuPrincipalView();
                    menu.setVisible(true);
                    this.dispose(); 
                } else {
                    intentosFallidos++;
                    actualizarContador();

                    if (intentosFallidos >= MAX_INTENTOS) {
                        bloquearCuenta();
                    } else {
                        JOptionPane.showMessageDialog(this, "Credenciales incorrectas. Intento " + intentosFallidos + " de " + MAX_INTENTOS, "Error de Autenticación", JOptionPane.WARNING_MESSAGE);
                        txtPassword.setText("");
                        txtPassword.requestFocus();
                    }
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al consultar la base de datos: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarContador() {
        lblIntentos.setText("<html><span style='font-size:10px; color:#555;'>Intentos fallidos: <b style='color:#AA0000;'>" + intentosFallidos + "</b> / " + MAX_INTENTOS + "</span></html>");
    }

    private void bloquearCuenta() {
        lblEstado.setText("<html><span style='font-size:9px; background-color:#C9302C; color:white; padding:2px 4px;'>ESTADO: Bloqueado</span></html>");
        txtUsuario.setEnabled(false);
        txtPassword.setEnabled(false);
        btnIngresar.setEnabled(false);

        String mensajeError = "Cuenta bloqueada por seguridad\n\n" +
                "Se ha superado el límite de 3 intentos fallidos consecutivos según la directiva de seguridad del Banco Unicatolica.\n\n" +
                "Código: SEC_ERR_003_LOCKED\n" +
                "Terminal: WS-SUC-042";

        JOptionPane.showMessageDialog(this, mensajeError, "Error de Autenticación", JOptionPane.ERROR_MESSAGE);
    }

    // =========================================================
    // 7. MÉTODO MAIN
    // =========================================================
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
        SwingUtilities.invokeLater(() -> new LoginView().setVisible(true));
    }
}