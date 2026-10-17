import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MenuPrincipalView extends JFrame {

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

        // 2. Panel Central (Fondo de Bienvenida)
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(new Color(236, 233, 216));
        
        JLabel lblBienvenida = new JLabel("<html><center><b style='color:#002b66; font-size:24px;'>BANCO UNICATOLICA</b><br><span style='color:#555; font-size:14px;'>Sistema Integrado de Gestión Bancaria</span></center></html>");
        centerPanel.add(lblBienvenida);
        add(centerPanel, BorderLayout.CENTER);

        // 3. Barra de Estado
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY));
        
        JLabel lblReloj = new JLabel();
        Timer timer = new Timer(1000, e -> {
            String horaStr = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a").format(new Date());
            lblReloj.setText("Usuario: operador_caja1 | Terminal: WS-SUC-042 | Conectado | " + horaStr);
        });
        timer.start();
        statusBar.add(lblReloj);
        add(statusBar, BorderLayout.SOUTH);

        // =========================================================
        // 4. EVENTOS DE NAVEGACIÓN (Enlazando las ventanas)
        // =========================================================

        // Sistema
        itemCerrarSesion.addActionListener(e -> {
            new LoginView().setVisible(true);
            this.dispose(); // Cierra el menú principal
        });
        itemSalir.addActionListener(e -> System.exit(0));

        // Clientes
        itemNuevoCliente.addActionListener(e -> new GestionClientesView().setVisible(true));
        itemConsultaClientes.addActionListener(e -> new ConsultaClientesView().setVisible(true));

        // Cuentas
        itemNuevaCuenta.addActionListener(e -> new GestionCuentasView().setVisible(true));
        itemConsultaCuentas.addActionListener(e -> new ConsultaCuentasView().setVisible(true));

        // Operaciones
        itemVentanilla.addActionListener(e -> new VentanillaCajaView().setVisible(true));

        // Reportes
        itemTransacciones.addActionListener(e -> new ConsultaTransaccionesView().setVisible(true));
        itemEstadoCuenta.addActionListener(e -> new EstadoCuentaView().setVisible(true));
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
        SwingUtilities.invokeLater(() -> new MenuPrincipalView().setVisible(true));
    }
}