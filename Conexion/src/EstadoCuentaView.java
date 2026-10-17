import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EstadoCuentaView extends JFrame {

    private JTextField txtBusquedaCuenta;
    private JTable tablaHistorial;
    private DefaultTableModel modeloTabla;
    private JButton btnBuscar, btnImprimir, btnExportarPDF, btnCerrar;

    public EstadoCuentaView() {
        // 1. Configuración de la Ventana Principal
        setTitle("Banco Unicatolica - Sistema de Gestión Bancaria :: [RF12 - Estado de Cuenta / Recibo]");
        setSize(950, 700);
        setMinimumSize(new Dimension(900, 650));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 2. Barra de Menú
        JMenuBar menuBar = new JMenuBar();
        String[] menus = {"Cerrar Sesión", "Clientes", "Cuentas", "Reportes", "Operaciones", "Seguridad", "Ayuda"};
        for (String m : menus) {
            JMenu menu = new JMenu(m);
            if (m.equals("Reportes")) menu.setForeground(new Color(0, 51, 153));
            menuBar.add(menu);
        }
        setJMenuBar(menuBar);

        // 3. Header Institucional
        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.setBackground(new Color(236, 233, 216));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(153, 153, 153)),
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
                "<td><b style='color:#002244; font-size:15px; font-family:Tahoma;'>BANCO UNICATOLICA S.A.</b></td>" +
                "</tr><tr><td style='color:#555555; font-size:11px; padding-top:2px;'>Módulo de Extractos y Certificaciones Bancarias — Generación de Estado de Cuenta</td></tr></table></html>");

        pnlIzquierdo.add(lblLogo);
        pnlIzquierdo.add(lblTitulo);

        // Lado Derecho
        JLabel lblReloj = new JLabel();
        lblReloj.setOpaque(false);
        Timer timer = new Timer(1000, e -> {
            String horaStr = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a").format(new Date());
            lblReloj.setText("<html><div style='text-align:right; font-family:Tahoma; font-size:10px; color:#333;'>" +
                "<span style='color:#00aa00;'>&#9679;</span> <b>Terminal:</b> WS-SUC-042 | <b>Operador:</b> operador_caja1<br>" +
                "<span style='color:#666;'>Fecha y Hora de Emisión: " + horaStr + " COT | Oracle 19c (En línea)</span>" +
                "</div></html>");
        });
        timer.start();

        headerPanel.add(pnlIzquierdo, BorderLayout.WEST);
        headerPanel.add(lblReloj, BorderLayout.EAST);
        topContainer.add(headerPanel, BorderLayout.CENTER);
        add(topContainer, BorderLayout.NORTH);

        // 4. Panel Central
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        centerPanel.setBackground(new Color(236, 233, 216));

        // --- 4.1 Buscador Superior ---
        JPanel pnlBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        pnlBusqueda.setBackground(new Color(244, 242, 232));
        pnlBusqueda.setBorder(BorderFactory.createLineBorder(new Color(160, 160, 160)));
        pnlBusqueda.add(new JLabel("<html><b style='color:#002b49;'>Buscar / Seleccionar Cuenta:</b></html>"));
        
        txtBusquedaCuenta = new JTextField("4102-8849-1029", 15);
        txtBusquedaCuenta.setFont(new Font("Consolas", Font.BOLD, 14));
        txtBusquedaCuenta.setForeground(new Color(0, 34, 68));
        txtBusquedaCuenta.setEditable(false);
        pnlBusqueda.add(txtBusquedaCuenta);
        
        btnBuscar = new JButton("Buscar Otra Cuenta");
        pnlBusqueda.add(btnBuscar);
        
        pnlBusqueda.add(new JLabel("<html><span style='font-size:10px; color:#666; margin-left:100px;'>Estado del Comprobante: <b style='color:#006600;'>VIGENTE / CERTIFICADO OFICIAL</b></span></html>"));
        centerPanel.add(pnlBusqueda);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // --- 4.2 Formato de Recibo (RF12) ---
        JPanel pnlReciboBorder = new JPanel(new BorderLayout());
        pnlReciboBorder.setBackground(new Color(255, 253, 245));
        // Borde "Dashed" (punteado) para simular recibo
        pnlReciboBorder.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createDashedBorder(new Color(102, 102, 102), 2.0f, 5.0f, 2.0f, false),
            BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));

        // Título del Recibo
        JPanel pnlTituloRecibo = new JPanel(new BorderLayout());
        pnlTituloRecibo.setOpaque(false);
        pnlTituloRecibo.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(170, 170, 170)));
        JLabel lblTituloDoc = new JLabel("<html><b>COMPROBANTE OFICIAL DE ESTADO DE CUENTA BANCARIO</b><br><span style='font-size:9px; color:#666;'>NIT: 890.301.442-1 — Vigilado Superintendencia Financiera de Colombia</span></html>");
        JLabel lblReciboNo = new JLabel("<html><span style='font-family:monospace; font-size:11px; background-color:#e0e0e0; border:1px solid #999; padding:2px 4px;'><b>RECIBO No. EC-2026-11-0429</b></span></html>");
        pnlTituloRecibo.add(lblTituloDoc, BorderLayout.WEST);
        pnlTituloRecibo.add(lblReciboNo, BorderLayout.EAST);
        pnlReciboBorder.add(pnlTituloRecibo, BorderLayout.NORTH);

        // Grid del recibo
        JPanel pnlGridRecibo = new JPanel(new GridBagLayout());
        pnlGridRecibo.setOpaque(false);
        pnlGridRecibo.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        
        // Columna Izquierda (Datos Cliente)
        JPanel pnlDatosCliente = new JPanel(new GridLayout(4, 2, 10, 5));
        pnlDatosCliente.setOpaque(false);
        pnlDatosCliente.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(200, 200, 200)));
        pnlDatosCliente.add(new JLabel("<html><span style='font-size:10px; color:#666;'>Titular de la Cuenta:</span><br><b style='color:#002244; font-size:11px;'>Carlos Alberto Gómez Ramírez</b></html>"));
        pnlDatosCliente.add(new JLabel("<html><span style='font-size:10px; color:#666;'>Documento de Identidad:</span><br><b style='font-family:monospace;'>C.C. 1144089231</b></html>"));
        pnlDatosCliente.add(new JLabel("<html><span style='font-size:10px; color:#666;'>Número de Cuenta:</span><br><b style='color:#003366; font-family:monospace; background-color:white; border:1px solid #ccc; padding:1px 3px;'>4102-8849-1029</b></html>"));
        pnlDatosCliente.add(new JLabel("<html><span style='font-size:10px; color:#666;'>Tipo de Producto:</span><br><b>Cuenta de Ahorros Tradicional</b></html>"));
        pnlDatosCliente.add(new JLabel("<html><span style='font-size:10px; color:#666;'>Estado Operativo:</span><br><b style='color:#006600;'>● ACTIVA</b></html>"));
        pnlDatosCliente.add(new JLabel("<html><span style='font-size:10px; color:#666;'>Período Consultado:</span><br><b>01/10/2026 al 10/11/2026</b></html>"));
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.6;
        pnlGridRecibo.add(pnlDatosCliente, gbc);

        // Columna Derecha (Saldo Gigante)
        JPanel pnlSaldoGigante = new JPanel(new BorderLayout());
        pnlSaldoGigante.setOpaque(false);
        pnlSaldoGigante.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));
        
        JLabel lblCajaSaldo = new JLabel("<html><div style='text-align:center; background-color:#eaf2fc; border:2px solid #004080; padding:10px 20px;'>" +
                "<span style='color:#002b49; font-size:18px; font-weight:bold; font-family:Tahoma;'>Saldo Disponible: <span style='font-family:monospace;'>$ 350.000,00</span></span><br>" +
                "<span style='font-size:10px; color:#666;'>Total Retenido: <b>$ 0,00</b> | Saldo Contable: <b>$ 350.000,00</b></span></div></html>");
        lblCajaSaldo.setHorizontalAlignment(SwingConstants.CENTER);
        
        pnlSaldoGigante.add(lblCajaSaldo, BorderLayout.CENTER);
        pnlSaldoGigante.add(new JLabel("<html><center><span style='font-size:9px; color:#888;'>* Saldo certificado en línea por el core bancario Oracle 19c</span></center></html>"), BorderLayout.SOUTH);

        gbc.gridx = 1; gbc.weightx = 0.4;
        pnlGridRecibo.add(pnlSaldoGigante, gbc);

        pnlReciboBorder.add(pnlGridRecibo, BorderLayout.CENTER);
        centerPanel.add(pnlReciboBorder);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // --- 4.3 Tabla de Movimientos Condensada ---
        JPanel pnlTabla = new JPanel(new BorderLayout());
        pnlTabla.setBackground(new Color(247, 246, 240));
        pnlTabla.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(153, 153, 153)), 
                "Historial de Movimientos de la Cuenta", TitledBorder.LEFT, TitledBorder.TOP, new Font("Tahoma", Font.BOLD, 11), new Color(0, 51, 102)));

        String[] columnas = {"Fecha/Hora ▲", "Tipo", "Monto"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tablaHistorial = new JTable(modeloTabla);
        tablaHistorial.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaHistorial.setRowHeight(22);
        
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        tablaHistorial.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);
        tablaHistorial.getColumnModel().getColumn(0).setPreferredWidth(120);
        tablaHistorial.getColumnModel().getColumn(1).setPreferredWidth(250);
        tablaHistorial.getColumnModel().getColumn(2).setPreferredWidth(150);

        // Filas HTML para replicar los colores verdes (depósito) y rojos (retiro) exactos del boceto
        modeloTabla.addRow(new Object[]{"<html><span style='font-family:monospace;'>10/11/2026 14:15:22</span></html>", "<html><b>● Depósito</b> <span style='color:#666;'>(Ventanilla Sucursal Principal)</span></html>", "<html><b style='color:#006600; font-family:monospace;'>+ $ 1.200.000,00 COP</b></html>"});
        modeloTabla.addRow(new Object[]{"<html><span style='font-family:monospace;'>08/11/2026 09:42:10</span></html>", "<html><b style='color:#cc0000;'>● Retiro</b> <span style='color:#666;'>(Cajero Automático ATH 12)</span></html>", "<html><b style='color:#cc0000; font-family:monospace;'>- $ 350.000,00 COP</b></html>"});
        modeloTabla.addRow(new Object[]{"<html><span style='font-family:monospace;'>05/11/2026 16:30:45</span></html>", "<html><b>● Depósito</b> <span style='color:#666;'>(Transferencia ACH Bancolombia)</span></html>", "<html><b style='color:#006600; font-family:monospace;'>+ $ 850.000,00 COP</b></html>"});
        modeloTabla.addRow(new Object[]{"<html><span style='font-family:monospace;'>01/11/2026 11:05:18</span></html>", "<html><b style='color:#cc0000;'>● Retiro</b> <span style='color:#666;'>(Ventanilla Caja 03)</span></html>", "<html><b style='color:#cc0000; font-family:monospace;'>- $ 500.000,00 COP</b></html>"});
        modeloTabla.addRow(new Object[]{"<html><span style='font-family:monospace;'>28/10/2026 15:20:00</span></html>", "<html><b>● Depósito</b> <span style='color:#666;'>(Abono Nómina Empresa)</span></html>", "<html><b style='color:#006600; font-family:monospace;'>+ $ 2.450.000,00 COP</b></html>"});
        modeloTabla.addRow(new Object[]{"<html><span style='font-family:monospace;'>22/10/2026 10:11:34</span></html>", "<html><b style='color:#cc0000;'>● Retiro</b> <span style='color:#666;'>(Pago Servicios Públicos)</span></html>", "<html><b style='color:#cc0000; font-family:monospace;'>- $ 145.500,00 COP</b></html>"});

        JScrollPane scrollTabla = new JScrollPane(tablaHistorial);
        scrollTabla.setPreferredSize(new Dimension(800, 150));
        pnlTabla.add(scrollTabla, BorderLayout.CENTER);

        // Resumen inferior de la tabla
        JPanel pnlResumen = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        pnlResumen.setBackground(new Color(240, 238, 224));
        pnlResumen.setBorder(BorderFactory.createLineBorder(new Color(184, 182, 168)));
        pnlResumen.add(new JLabel("<html><span style='font-size:11px;'>Total Depósitos (+): <b style='color:#006600; font-family:monospace;'>$ 4.500.000,00</b></span></html>"));
        pnlResumen.add(new JLabel("<html><span style='font-size:11px;'>Total Retiros (-): <b style='color:#cc0000; font-family:monospace;'>$ 995.500,00</b></span></html>"));
        pnlResumen.add(new JLabel("<html><span style='font-size:11px;'>Movimiento Neto: <b style='color:#002b66; font-family:monospace;'>+ $ 3.504.500,00</b></span></html>"));
        pnlTabla.add(pnlResumen, BorderLayout.SOUTH);

        centerPanel.add(pnlTabla);

        // --- 4.4 Botones de Acción Finales ---
        JPanel pnlBotones = new JPanel(new BorderLayout());
        pnlBotones.setOpaque(false);
        pnlBotones.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JPanel pnlBotonesIzq = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pnlBotonesIzq.setOpaque(false);
        btnImprimir = new JButton("🖶 Imprimir Recibo / Extracto (Ctrl+P)");
        btnImprimir.setFont(new Font("Tahoma", Font.BOLD, 11));
        btnExportarPDF = new JButton("💾 Exportar a PDF (Certificado)");
        pnlBotonesIzq.add(btnImprimir);
        pnlBotonesIzq.add(btnExportarPDF);
        pnlBotonesIzq.add(new JButton("📋 Copiar al Portapapeles"));

        JPanel pnlBotonesDer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pnlBotonesDer.setOpaque(false);
        btnCerrar = new JButton("✖ Cerrar Vista");
        pnlBotonesDer.add(btnCerrar);

        pnlBotones.add(pnlBotonesIzq, BorderLayout.WEST);
        pnlBotones.add(pnlBotonesDer, BorderLayout.EAST);
        
        centerPanel.add(pnlBotones);

        add(centerPanel, BorderLayout.CENTER);

        // 5. Barra de Estado
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(153, 153, 153)));
        statusBar.add(new JLabel("Listo. Estado de cuenta generado exitosamente. | Oracle 19c: 192.168.1.104:1521/UNICADB"));
        add(statusBar, BorderLayout.SOUTH);

        // =========================================================
        // 6. EVENTOS
        // =========================================================

        btnBuscar.addActionListener(e -> JOptionPane.showMessageDialog(this, "Abre ventana de búsqueda de clientes/cuentas...", "Búsqueda", JOptionPane.INFORMATION_MESSAGE));
        
        btnImprimir.addActionListener(e -> JOptionPane.showMessageDialog(this, "Enviando formato de recibo a la impresora predeterminada...", "Impresión en curso", JOptionPane.INFORMATION_MESSAGE));
        
        btnExportarPDF.addActionListener(e -> JOptionPane.showMessageDialog(this, "El Estado de Cuenta se ha guardado como 'EC_410288491029_10112026.pdf' en su carpeta local.", "Exportación PDF Exitosa", JOptionPane.INFORMATION_MESSAGE));

        btnCerrar.addActionListener(e -> dispose());
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
        SwingUtilities.invokeLater(() -> new EstadoCuentaView().setVisible(true));
    }
}