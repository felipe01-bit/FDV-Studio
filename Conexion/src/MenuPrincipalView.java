import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MenuPrincipalView extends JFrame {

    // Variables de instancia para controlar ventanas únicas
    private GestionClientesView ventanaNuevoCliente = null;
    private ConsultaClientesView ventanaConsultaClientes = null;
    private GestionCuentasView ventanaNuevaCuenta = null;
    private ConsultaCuentasView ventanaConsultaCuentas = null;
    private VentanillaCajaView ventanaCaja = null;
    private ConsultaTransaccionesView ventanaTransacciones = null;
    private EstadoCuentaView ventanaEstadoCuenta = null;

    public MenuPrincipalView() {
        setTitle("Banco Unicatolica - Menú Principal");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 1. Crear la Barra de Menú (JMenuBar)
        JMenuBar menuBar = new JMenuBar();

        // --- Menú Sistema ---
        JMenu menuSistema = new JMenu("Sistema");
        JMenuItem itemCerrarSesion = new JMenuItem("Cerrar Sesión");
        JMenuItem itemSalir = new JMenuItem("Salir del Sistema");
        menuSistema.add(itemCerrarSesion);
        menuSistema.addSeparator();
        menuSistema.add(itemSalir);

        // --- Menú Clientes ---
        JMenu menuClientes = new JMenu("Clientes");
        JMenuItem itemNuevoCliente = new JMenuItem("Registrar Nuevo Cliente");
        JMenuItem itemConsultaClientes = new JMenuItem("Consultar y Gestionar Clientes");
        menuClientes.add(itemNuevoCliente);
        menuClientes.add(itemConsultaClientes);

        // --- Menú Cuentas ---
        JMenu menuCuentas = new JMenu("Cuentas");
        JMenuItem itemNuevaCuenta = new JMenuItem("Registrar Nueva Cuenta");
        JMenuItem itemConsultaCuentas = new JMenuItem("Consultar y Baja de Cuentas");
        menuCuentas.add(itemNuevaCuenta);
        menuCuentas.add(itemConsultaCuentas);

        // --- Menú Operaciones ---
        JMenu menuOperaciones = new JMenu("Operaciones");
        JMenuItem itemVentanilla = new JMenuItem("Ventanilla de Caja (Depósitos/Retiros)");
        menuOperaciones.add(itemVentanilla);

        // --- Menú Reportes ---
        JMenu menuReportes = new JMenu("Reportes");
        JMenuItem itemTransacciones = new JMenuItem("Historial de Transacciones");
        JMenuItem itemEstadoCuenta = new JMenuItem("Estado de Cuenta / Recibo");
        menuReportes.add(itemTransacciones);
        menuReportes.add(itemEstadoCuenta);

        // Agregar menús a la barra
        menuBar.add(menuSistema);
        menuBar.add(menuClientes);
        menuBar.add(menuCuentas);
        menuBar.add(menuOperaciones);
        menuBar.add(menuReportes);
        setJMenuBar(menuBar);

        // 2. Panel Central (Fondo de Bienvenida con Logo)
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(new Color(236, 233, 216));
        
        JPanel pnlBienvenida = new JPanel();
        pnlBienvenida.setLayout(new BoxLayout(pnlBienvenida, BoxLayout.Y_AXIS));
        pnlBienvenida.setOpaque(false);

        // Cargar el Logo
        JLabel lblLogo = new JLabel();
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
        try {
            java.net.URL imgURL = getClass().getResource("/banco_unicatolica.jpg");
            if (imgURL != null) {
                // Redimensionamos la imagen para que se vea grande y nítida en el menú
                Image img = new ImageIcon(imgURL).getImage().getScaledInstance(160, 160, Image.SCALE_SMOOTH);
                lblLogo.setIcon(new ImageIcon(img));
            }
        } catch (Exception ex) {
            System.err.println("No se pudo cargar la imagen en el menú principal.");
        }

        // Título debajo del logo
        JLabel lblTitulo = new JLabel("<html><center><b style='color:#002b66; font-size:26px; font-family:Tahoma;'>BANCO UNICATOLICA</b><br><span style='color:#555; font-size:15px; font-family:Tahoma;'>Sistema Integrado de Gestión Bancaria</span></center></html>");
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        pnlBienvenida.add(lblLogo);
        pnlBienvenida.add(Box.createRigidArea(new Dimension(0, 20))); // Espacio entre el logo y el texto
        pnlBienvenida.add(lblTitulo);

        centerPanel.add(pnlBienvenida);
        add(centerPanel, BorderLayout.CENTER);

        // 3. Barra de Estado Inferior (Dividida en Izquierda y Derecha)
        JPanel bottomContainer = new JPanel(new BorderLayout());
        bottomContainer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY));
        
        // Reloj a la izquierda
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblReloj = new JLabel();
        Timer timer = new Timer(1000, e -> {
            String horaStr = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a").format(new Date());
            lblReloj.setText("Usuario: operador_caja1 | Terminal: WS-SUC-042 | Conectado | " + horaStr);
        });
        timer.start();
        statusBar.add(lblReloj);

        // Firma del desarrollador a la derecha
        JPanel pnlFirma = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel lblFirma = new JLabel("<html><span style='color:#555; font-size:11px;'>Desarrollado por: <b style='color:#002b66;'>FDV_Studio</b></span></html>");
        pnlFirma.add(lblFirma);

        bottomContainer.add(statusBar, BorderLayout.WEST);
        bottomContainer.add(pnlFirma, BorderLayout.EAST);
        
        add(bottomContainer, BorderLayout.SOUTH);

        // =========================================================
        // 4. EVENTOS DE NAVEGACIÓN (Enlazando las ventanas de forma única)
        // =========================================================

        // Sistema
        itemCerrarSesion.addActionListener(e -> {
            new LoginView().setVisible(true);
            this.dispose(); // Cierra el menú principal
        });
        itemSalir.addActionListener(e -> System.exit(0));

        // Clientes
        itemNuevoCliente.addActionListener(e -> {
            if (ventanaNuevoCliente == null || !ventanaNuevoCliente.isVisible()) {
                ventanaNuevoCliente = new GestionClientesView();
                ventanaNuevoCliente.setVisible(true);
            } else {
                ventanaNuevoCliente.requestFocus();
            }
        });
        
        itemConsultaClientes.addActionListener(e -> {
            if (ventanaConsultaClientes == null || !ventanaConsultaClientes.isVisible()) {
                ventanaConsultaClientes = new ConsultaClientesView();
                ventanaConsultaClientes.setVisible(true);
            } else {
                ventanaConsultaClientes.requestFocus();
            }
        });

        // Cuentas
        itemNuevaCuenta.addActionListener(e -> {
            if (ventanaNuevaCuenta == null || !ventanaNuevaCuenta.isVisible()) {
                ventanaNuevaCuenta = new GestionCuentasView();
                ventanaNuevaCuenta.setVisible(true);
            } else {
                ventanaNuevaCuenta.requestFocus();
            }
        });
        
        itemConsultaCuentas.addActionListener(e -> {
            if (ventanaConsultaCuentas == null || !ventanaConsultaCuentas.isVisible()) {
                ventanaConsultaCuentas = new ConsultaCuentasView();
                ventanaConsultaCuentas.setVisible(true);
            } else {
                ventanaConsultaCuentas.requestFocus();
            }
        });

        // Operaciones
        itemVentanilla.addActionListener(e -> {
            if (ventanaCaja == null || !ventanaCaja.isVisible()) {
                ventanaCaja = new VentanillaCajaView();
                ventanaCaja.setVisible(true);
            } else {
                ventanaCaja.requestFocus();
            }
        });

        // Reportes
        itemTransacciones.addActionListener(e -> {
            if (ventanaTransacciones == null || !ventanaTransacciones.isVisible()) {
                ventanaTransacciones = new ConsultaTransaccionesView();
                ventanaTransacciones.setVisible(true);
            } else {
                ventanaTransacciones.requestFocus();
            }
        });
        
        itemEstadoCuenta.addActionListener(e -> {
            if (ventanaEstadoCuenta == null || !ventanaEstadoCuenta.isVisible()) {
                ventanaEstadoCuenta = new EstadoCuentaView();
                ventanaEstadoCuenta.setVisible(true);
            } else {
                ventanaEstadoCuenta.requestFocus();
            }
        });

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
        
    } // <-- Aquí termina el constructor

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
        SwingUtilities.invokeLater(() -> new MenuPrincipalView().setVisible(true));
    }
}