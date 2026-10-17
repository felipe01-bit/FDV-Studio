import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ConsultaTransaccionesView extends JFrame {

    private JTextField txtBusquedaCuenta;
    private JTable tablaHistorial;
    private DefaultTableModel modeloTabla;
    private JButton btnBuscar, btnLimpiar, btnImprimir, btnExportar, btnDetalle;

    public ConsultaTransaccionesView() {
        // 1. Configuración de la Ventana Principal
        setTitle("Banco Unicatolica - Sistema de Gestión Bancaria :: [RF10 - Consulta de Transacciones e Historial]");
        setSize(1000, 680);
        setMinimumSize(new Dimension(950, 600));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 2. Barra de Menú
        JMenuBar menuBar = new JMenuBar();
        String[] menus = {"Cerrar Sesión", "Clientes", "Cuentas", "Operaciones", "Reportes", "Seguridad", "Ayuda"};
        for (String m : menus) {
            JMenu menu = new JMenu(m);
            if (m.equals("Operaciones")) menu.setForeground(new Color(0, 51, 153));
            menuBar.add(menu);
        }
        setJMenuBar(menuBar);

        // 3. Header Institucional
        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.setBackground(new Color(236, 233, 216));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(244, 242, 230));
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(216, 211, 190)),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));

        // Lado Izquierdo
        JPanel pnlIzquierdo = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        pnlIzquierdo.setOpaque(false);
        JLabel lblLogo = new JLabel();
        try {
            java.net.URL imgURL = getClass().getResource("/banco_unicatolica.jpg");
            if (imgURL != null) {
                Image img = new ImageIcon(imgURL).getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
                lblLogo.setIcon(new ImageIcon(img));
            }
        } catch (Exception ex) {}

        JLabel lblTitulo = new JLabel("<html><table cellpadding='0' cellspacing='0'><tr>" +
                "<td><b style='color:#002b66; font-size:14px; font-family:Tahoma;'>BANCO UNICATOLICA</b></td>" +
                "</tr><tr><td style='color:#4a473a; font-size:11px; padding-top:2px;'>Módulo de Consulta de Transacciones — Auditoría y Libro Diario de Movimientos</td></tr></table></html>");

        pnlIzquierdo.add(lblLogo);
        pnlIzquierdo.add(lblTitulo);

        // Lado Derecho
        JLabel lblReloj = new JLabel();
        lblReloj.setOpaque(false);
        Timer timer = new Timer(1000, e -> {
            String horaStr = new SimpleDateFormat("hh:mm:ss a").format(new Date());
            lblReloj.setText("<html><div style='text-align:right; font-family:Tahoma; font-size:10px; color:#333;'>" +
                "<span style='color:#00aa00;'>&#9679;</span> <b>Terminal:</b> WS-SUC-042 | <b>Operador:</b> operador_caja1<br>" +
                "<span style='color:#555;'>Zona horaria: Colombia (UTC-5) • Oracle 19c Enterprise: <b>En línea</b> | " + horaStr + "</span>" +
                "</div></html>");
        });
        timer.start();

        headerPanel.add(pnlIzquierdo, BorderLayout.WEST);
        headerPanel.add(lblReloj, BorderLayout.EAST);
        topContainer.add(headerPanel, BorderLayout.CENTER);
        add(topContainer, BorderLayout.NORTH);

        // 4. Panel Central
        JPanel centerPanel = new JPanel(new BorderLayout(0, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        centerPanel.setBackground(new Color(236, 233, 216));

        // 4.1 Buscador y Filtro (RF10)
        JPanel pnlTopBuscador = new JPanel(new BorderLayout());
        pnlTopBuscador.setOpaque(false);

        JPanel pnlBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        pnlBusqueda.setBackground(new Color(251, 250, 246));
        pnlBusqueda.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(194, 189, 168)), 
                "► FORMULARIO DE BÚSQUEDA DE TRANSACCIONES:", TitledBorder.LEFT, TitledBorder.TOP, new Font("Tahoma", Font.BOLD, 11), new Color(12, 54, 119)));

        pnlBusqueda.add(new JLabel("<html><b>Número de Cuenta:</b></html>"));
        txtBusquedaCuenta = new JTextField("4102-8849-1029", 25);
        txtBusquedaCuenta.setFont(new Font("Consolas", Font.BOLD, 13));
        pnlBusqueda.add(txtBusquedaCuenta);

        btnBuscar = new JButton("Buscar");
        btnBuscar.setFont(new Font("Tahoma", Font.BOLD, 11));
        btnBuscar.setForeground(new Color(0, 45, 107));
        btnLimpiar = new JButton("Limpiar Filtro");

        pnlBusqueda.add(btnBuscar);
        pnlBusqueda.add(btnLimpiar);

        // Stats Rápidas a la derecha del buscador
        JPanel pnlStatsBuscador = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        pnlStatsBuscador.setOpaque(false);
        JLabel lblQuickStats = new JLabel("<html><div style='background-color:#f0ece0; border:1px solid #cfcbba; padding:4px 8px; font-size:10px;'>" +
                "<span style='color:#555;'>Titular: </span><b style='color:#002b66;'>Carlos Alberto Gómez Ramírez (C.C. 1144089231)</b> <span style='color:#999;'>|</span> " +
                "<span style='color:#555;'>Estado: </span><b style='color:#006600;'>Activa (Ahorros)</b></div></html>");
        pnlStatsBuscador.add(lblQuickStats);

        pnlTopBuscador.add(pnlBusqueda, BorderLayout.WEST);
        pnlTopBuscador.add(pnlStatsBuscador, BorderLayout.EAST);
        centerPanel.add(pnlTopBuscador, BorderLayout.NORTH);

        // 4.2 Tabla de Historial
        String[] columnas = {"Fecha/Hora", "Tipo (Depósito/Retiro)", "Monto", "Número de Cuenta"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tablaHistorial = new JTable(modeloTabla);
        tablaHistorial.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaHistorial.setRowHeight(26);
        tablaHistorial.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 11));
        tablaHistorial.getTableHeader().setBackground(new Color(235, 232, 216));

        // Alineación y colores en la tabla
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        tablaHistorial.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);

        // Llenar datos de prueba usando HTML para aplicar los colores verde/rojo según el boceto
        modeloTabla.addRow(new Object[]{"10/11/2026 14:15:22", "<html><b style='color:#006600;'>● Depósito</b> <span style='font-size:9px; color:#666;'>(Ventanilla)</span></html>", "<html><b style='color:#006600; font-family:monospace;'>+ $ 1.200.000,00 COP</b></html>", "4102-8849-1029"});
        modeloTabla.addRow(new Object[]{"08/11/2026 09:42:10", "<html><b style='color:#990000;'>● Retiro</b> <span style='font-size:9px; color:#666;'>(Cajero)</span></html>", "<html><b style='color:#990000; font-family:monospace;'>- $ 350.000,00 COP</b></html>", "4102-8849-1029"});
        modeloTabla.addRow(new Object[]{"05/11/2026 16:30:45", "<html><b style='color:#006600;'>● Depósito</b> <span style='font-size:9px; color:#666;'>(Transferencia ACH)</span></html>", "<html><b style='color:#006600; font-family:monospace;'>+ $ 850.000,00 COP</b></html>", "4102-8849-1029"});
        modeloTabla.addRow(new Object[]{"01/11/2026 11:05:18", "<html><b style='color:#990000;'>● Retiro</b> <span style='font-size:9px; color:#666;'>(Ventanilla)</span></html>", "<html><b style='color:#990000; font-family:monospace;'>- $ 500.000,00 COP</b></html>", "4102-8849-1029"});
        modeloTabla.addRow(new Object[]{"28/10/2026 15:20:00", "<html><b style='color:#006600;'>● Depósito</b> <span style='font-size:9px; color:#666;'>(Abono Nómina)</span></html>", "<html><b style='color:#006600; font-family:monospace;'>+ $ 2.450.000,00 COP</b></html>", "4102-8849-1029"});
        modeloTabla.addRow(new Object[]{"22/10/2026 10:11:34", "<html><b style='color:#990000;'>● Retiro</b> <span style='font-size:9px; color:#666;'>(Pago Servicios)</span></html>", "<html><b style='color:#990000; font-family:monospace;'>- $ 145.500,00 COP</b></html>", "4102-8849-1029"});

        JScrollPane scrollTabla = new JScrollPane(tablaHistorial);
        scrollTabla.setBorder(BorderFactory.createLineBorder(new Color(127, 157, 185)));
        
        JPanel pnlTablaContainer = new JPanel(new BorderLayout());
        pnlTablaContainer.setOpaque(false);
        JLabel lblSubtitulo = new JLabel("<html><span style='font-size:10px; font-weight:bold; color:#222;'>Historial de Movimientos Registrados en el Sistema </span><span style='font-size:9px; color:#666;'>(Filtro por cuenta)</span></html>");
        lblSubtitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        pnlTablaContainer.add(lblSubtitulo, BorderLayout.NORTH);
        pnlTablaContainer.add(scrollTabla, BorderLayout.CENTER);

        centerPanel.add(pnlTablaContainer, BorderLayout.CENTER);

        // 4.3 Botones de Acción y Totales Resumen
        JPanel pnlAcciones = new JPanel(new BorderLayout());
        pnlAcciones.setOpaque(false);
        pnlAcciones.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        
        JPanel pnlBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pnlBotones.setOpaque(false);
        btnImprimir = new JButton("Imprimir Comprobante (F8)");
        btnExportar = new JButton("Exportar a Excel / CSV");
        btnDetalle = new JButton("Ver Detalle Contable");
        pnlBotones.add(btnImprimir);
        pnlBotones.add(btnExportar);
        pnlBotones.add(btnDetalle);

        JPanel pnlTotales = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        pnlTotales.setOpaque(false);
        JLabel lblResumen = new JLabel("<html><div style='background-color:#e4dfcd; border:1px solid #beb9a4; padding:4px 10px; font-size:10.5px; border-radius:3px;'>" +
                "<span style='color:#555;'>Total Depósitos: </span><b style='color:#006600; font-family:monospace;'>$ 4.500.000,00</b> <span style='color:#999; margin:0 5px;'>|</span> " +
                "<span style='color:#555;'>Total Retiros: </span><b style='color:#990000; font-family:monospace;'>$ 995.500,00</b> <span style='color:#999; margin:0 5px;'>|</span> " +
                "<span style='color:#555;'>Balance Movimientos: </span><b style='color:#002b66; font-family:monospace;'>+ $ 3.504.500,00</b>" +
                "</div></html>");
        pnlTotales.add(lblResumen);

        pnlAcciones.add(pnlBotones, BorderLayout.WEST);
        pnlAcciones.add(pnlTotales, BorderLayout.EAST);
        
        centerPanel.add(pnlAcciones, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);

        // 5. Barra de Estado Inferior
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(198, 194, 174)));
        statusBar.add(new JLabel("Listo. Búsqueda de cuenta completada: 6 registros. | Oracle 19c: 192.168.1.104:1521/UNICADB"));
        add(statusBar, BorderLayout.SOUTH);

        // =========================================================
        // 6. LÓGICA Y EVENTOS
        // =========================================================

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(modeloTabla);
        tablaHistorial.setRowSorter(sorter);

        btnBuscar.addActionListener(e -> {
            String busqueda = txtBusquedaCuenta.getText().trim();
            if (busqueda.isEmpty()) {
                sorter.setRowFilter(null);
            } else {
                // Filtra específicamente por la columna 3 (Número de Cuenta)
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + busqueda, 3));
            }
        });

        btnLimpiar.addActionListener(e -> {
            txtBusquedaCuenta.setText("");
            sorter.setRowFilter(null);
        });
        
        btnImprimir.addActionListener(e -> JOptionPane.showMessageDialog(this, "Generando comprobante de impresión para auditoría...", "Imprimir", JOptionPane.INFORMATION_MESSAGE));
        btnExportar.addActionListener(e -> JOptionPane.showMessageDialog(this, "Exportando movimientos a archivo CSV...", "Exportación", JOptionPane.INFORMATION_MESSAGE));
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
        SwingUtilities.invokeLater(() -> new ConsultaTransaccionesView().setVisible(true));
    }
}