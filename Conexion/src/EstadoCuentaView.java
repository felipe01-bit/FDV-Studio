import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.print.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EstadoCuentaView extends JFrame {

    private JTextField txtBusquedaCuenta;
    private JTable tablaHistorial;
    private DefaultTableModel modeloTabla;
    private JButton btnBuscar, btnImprimir, btnExportarPDF, btnCerrar;
    
    // Panel que agrupa el contenido que se va a convertir en PDF
    private JPanel pnlImprimible;
    
    // Etiquetas Dinámicas
    private JLabel lblTitular, lblDocumento, lblNumeroCuenta, lblTipoProducto, lblEstadoOperativo, lblPeriodo;
    private JLabel lblCajaSaldo, lblResumenDepositos, lblResumenRetiros, lblMovimientoNeto;

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

        JLabel lblReloj = new JLabel();
        lblReloj.setOpaque(false);
        Timer timer = new Timer(1000, e -> {
            String horaStr = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a").format(new Date());
            lblReloj.setText("<html><div style='text-align:right; font-family:Tahoma; font-size:10px; color:#333;'>" +
                "<span style='color:#00aa00;'>&#9679;</span> <b>Terminal:</b> WS-SUC-042 | <b>Operador:</b> operador_caja1<br>" +
                "<span style='color:#666;'>Fecha y Hora de Emisión: " + horaStr + " COT | MySQL (En línea)</span>" +
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
        pnlBusqueda.add(new JLabel("<html><b style='color:#002b49;'>Cuenta Consultada:</b></html>"));
        
        txtBusquedaCuenta = new JTextField("---", 15);
        txtBusquedaCuenta.setFont(new Font("Consolas", Font.BOLD, 14));
        txtBusquedaCuenta.setForeground(new Color(0, 34, 68));
        txtBusquedaCuenta.setEditable(false);
        pnlBusqueda.add(txtBusquedaCuenta);
        
        btnBuscar = new JButton("Buscar Cuenta / Actualizar");
        pnlBusqueda.add(btnBuscar);
        
        pnlBusqueda.add(new JLabel("<html><span style='font-size:10px; color:#666; margin-left:100px;'>Estado del Comprobante: <b style='color:#006600;'>VIGENTE / CERTIFICADO OFICIAL</b></span></html>"));
        centerPanel.add(pnlBusqueda);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // ==============================================================
        // --- CONTENEDOR DEL ÁREA IMPRIMIBLE (RECIBO + TABLA) ---
        // ==============================================================
        pnlImprimible = new JPanel();
        pnlImprimible.setLayout(new BoxLayout(pnlImprimible, BoxLayout.Y_AXIS));
        pnlImprimible.setBackground(new Color(236, 233, 216));

        // --- 4.2 Formato de Recibo (RF12) ---
        JPanel pnlReciboBorder = new JPanel(new BorderLayout());
        pnlReciboBorder.setBackground(new Color(255, 253, 245));
        pnlReciboBorder.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createDashedBorder(new Color(102, 102, 102), 2.0f, 5.0f, 2.0f, false),
            BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));

        JPanel pnlTituloRecibo = new JPanel(new BorderLayout());
        pnlTituloRecibo.setOpaque(false);
        pnlTituloRecibo.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(170, 170, 170)));
        JLabel lblTituloDoc = new JLabel("<html><b>COMPROBANTE OFICIAL DE ESTADO DE CUENTA BANCARIO</b><br><span style='font-size:9px; color:#666;'>NIT: 890.301.442-1 — Vigilado Superintendencia Financiera de Colombia</span></html>");
        String numeroReciboRandom = "EC-2026-" + (int)(Math.random()*9000 + 1000);
        JLabel lblReciboNo = new JLabel("<html><span style='font-family:monospace; font-size:11px; background-color:#e0e0e0; border:1px solid #999; padding:2px 4px;'><b>RECIBO No. " + numeroReciboRandom + "</b></span></html>");
        pnlTituloRecibo.add(lblTituloDoc, BorderLayout.WEST);
        pnlTituloRecibo.add(lblReciboNo, BorderLayout.EAST);
        pnlReciboBorder.add(pnlTituloRecibo, BorderLayout.NORTH);

        JPanel pnlGridRecibo = new JPanel(new GridBagLayout());
        pnlGridRecibo.setOpaque(false);
        pnlGridRecibo.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        
        JPanel pnlDatosCliente = new JPanel(new GridLayout(4, 2, 10, 5));
        pnlDatosCliente.setOpaque(false);
        pnlDatosCliente.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(200, 200, 200)));
        
        lblTitular = new JLabel("<html><span style='font-size:10px; color:#666;'>Titular de la Cuenta:</span><br><b style='color:#002244; font-size:11px;'>-</b></html>");
        lblDocumento = new JLabel("<html><span style='font-size:10px; color:#666;'>Documento de Identidad:</span><br><b style='font-family:monospace;'>-</b></html>");
        lblNumeroCuenta = new JLabel("<html><span style='font-size:10px; color:#666;'>Número de Cuenta:</span><br><b style='color:#003366; font-family:monospace; background-color:white; border:1px solid #ccc; padding:1px 3px;'>-</b></html>");
        lblTipoProducto = new JLabel("<html><span style='font-size:10px; color:#666;'>Tipo de Producto:</span><br><b>-</b></html>");
        lblEstadoOperativo = new JLabel("<html><span style='font-size:10px; color:#666;'>Estado Operativo:</span><br><b>-</b></html>");
        
        String fechaHoy = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        lblPeriodo = new JLabel("<html><span style='font-size:10px; color:#666;'>Corte al:</span><br><b>" + fechaHoy + "</b></html>");
        
        pnlDatosCliente.add(lblTitular);
        pnlDatosCliente.add(lblDocumento);
        pnlDatosCliente.add(lblNumeroCuenta);
        pnlDatosCliente.add(lblTipoProducto);
        pnlDatosCliente.add(lblEstadoOperativo);
        pnlDatosCliente.add(lblPeriodo);
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.6;
        pnlGridRecibo.add(pnlDatosCliente, gbc);

        JPanel pnlSaldoGigante = new JPanel(new BorderLayout());
        pnlSaldoGigante.setOpaque(false);
        pnlSaldoGigante.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));
        
        lblCajaSaldo = new JLabel("<html><div style='text-align:center; background-color:#eaf2fc; border:2px solid #004080; padding:10px 20px;'>" +
                "<span style='color:#002b49; font-size:18px; font-weight:bold; font-family:Tahoma;'>Saldo Disponible: <span style='font-family:monospace;'>$ 0,00</span></span><br>" +
                "<span style='font-size:10px; color:#666;'>Total Retenido: <b>$ 0,00</b> | Saldo Contable: <b>$ 0,00</b></span></div></html>");
        lblCajaSaldo.setHorizontalAlignment(SwingConstants.CENTER);
        
        pnlSaldoGigante.add(lblCajaSaldo, BorderLayout.CENTER);
        pnlSaldoGigante.add(new JLabel("<html><center><span style='font-size:9px; color:#888;'>* Saldo certificado en línea por el core bancario MySQL</span></center></html>"), BorderLayout.SOUTH);

        gbc.gridx = 1; gbc.weightx = 0.4;
        pnlGridRecibo.add(pnlSaldoGigante, gbc);
        pnlReciboBorder.add(pnlGridRecibo, BorderLayout.CENTER);

        // Agregamos el recibo al contenedor Imprimible
        pnlImprimible.add(pnlReciboBorder);
        pnlImprimible.add(Box.createRigidArea(new Dimension(0, 10)));

        // --- 4.3 Tabla de Movimientos Reales ---
        JPanel pnlTabla = new JPanel(new BorderLayout());
        pnlTabla.setBackground(new Color(247, 246, 240));
        pnlTabla.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(153, 153, 153)), 
                "Historial Oficial de Transacciones y Movimientos", TitledBorder.LEFT, TitledBorder.TOP, new Font("Tahoma", Font.BOLD, 11), new Color(0, 51, 102)));

        String[] columnas = {"Fecha/Hora ▲", "Detalle de Transacción", "Monto Oficial"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tablaHistorial = new JTable(modeloTabla);
        tablaHistorial.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaHistorial.setRowHeight(24);
        
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        tablaHistorial.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);
        tablaHistorial.getColumnModel().getColumn(0).setPreferredWidth(130);
        tablaHistorial.getColumnModel().getColumn(1).setPreferredWidth(250);
        tablaHistorial.getColumnModel().getColumn(2).setPreferredWidth(150);

        JScrollPane scrollTabla = new JScrollPane(tablaHistorial);
        scrollTabla.setPreferredSize(new Dimension(800, 150));
        pnlTabla.add(scrollTabla, BorderLayout.CENTER);

        JPanel pnlResumen = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        pnlResumen.setBackground(new Color(240, 238, 224));
        pnlResumen.setBorder(BorderFactory.createLineBorder(new Color(184, 182, 168)));
        
        lblResumenDepositos = new JLabel("<html><span style='font-size:11px;'>Total Depósitos (+): <b style='color:#006600; font-family:monospace;'>$ 0.00</b></span></html>");
        lblResumenRetiros = new JLabel("<html><span style='font-size:11px;'>Total Retiros (-): <b style='color:#cc0000; font-family:monospace;'>$ 0.00</b></span></html>");
        lblMovimientoNeto = new JLabel("<html><span style='font-size:11px;'>Movimiento Neto: <b style='color:#002b66; font-family:monospace;'>$ 0.00</b></span></html>");
        
        pnlResumen.add(lblResumenDepositos);
        pnlResumen.add(lblResumenRetiros);
        pnlResumen.add(lblMovimientoNeto);
        pnlTabla.add(pnlResumen, BorderLayout.SOUTH);

        // Agregamos la tabla al contenedor Imprimible
        pnlImprimible.add(pnlTabla);

        // Agregamos todo el bloque al centro de la ventana
        centerPanel.add(pnlImprimible);

        // --- 4.4 Botones de Acción Finales ---
        JPanel pnlBotones = new JPanel(new BorderLayout());
        pnlBotones.setOpaque(false);
        pnlBotones.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JPanel pnlBotonesIzq = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pnlBotonesIzq.setOpaque(false);
        btnImprimir = new JButton("🖶 Imprimir Recibo (Ctrl+P)");
        btnExportarPDF = new JButton("💾 Exportar a PDF (Certificado)");
        pnlBotonesIzq.add(btnImprimir);
        pnlBotonesIzq.add(btnExportarPDF);

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
        statusBar.add(new JLabel("Listo. Esperando solicitud de cuenta. | Base de Datos MySQL Conectada"));
        add(statusBar, BorderLayout.SOUTH);

        // =========================================================
        // 6. EVENTOS PRINCIPALES
        // =========================================================

        btnBuscar.addActionListener(e -> {
            String inputCuenta = JOptionPane.showInputDialog(this, "Ingrese el número de cuenta para generar su extracto oficial:", "Buscar Cuenta", JOptionPane.QUESTION_MESSAGE);
            if(inputCuenta != null && !inputCuenta.trim().isEmpty()) {
                verificarYcargarCuenta(inputCuenta.trim());
            }
        });
        
        // Conectamos ambos botones al mismo método nativo de impresión/PDF
        btnExportarPDF.addActionListener(e -> generarDocumentoPDF());
        btnImprimir.addActionListener(e -> generarDocumentoPDF());
        
        btnCerrar.addActionListener(e -> dispose());

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
    // GENERADOR NATIVO DE PDF / IMPRESIÓN
    // =========================================================
    private void generarDocumentoPDF() {
        if (txtBusquedaCuenta.getText().equals("---")) {
            JOptionPane.showMessageDialog(this, "Por favor, busque y cargue una cuenta bancaria primero.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        PrinterJob job = PrinterJob.getPrinterJob();
        job.setJobName("Extracto_" + txtBusquedaCuenta.getText());

        job.setPrintable(new Printable() {
            @Override
            public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
                if (pageIndex > 0) {
                    return Printable.NO_SUCH_PAGE;
                }
                Graphics2D g2d = (Graphics2D) graphics;
                
                // Alineamos y ajustamos la escala para que todo el panel quepa perfecto en la hoja (Carta/A4)
                g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
                double scaleX = pageFormat.getImageableWidth() / pnlImprimible.getWidth();
                double scaleY = pageFormat.getImageableHeight() / pnlImprimible.getHeight();
                double scale = Math.min(scaleX, scaleY);
                g2d.scale(scale, scale);

                // Capturamos el panel exacto como se ve en pantalla
                pnlImprimible.printAll(graphics);
                return Printable.PAGE_EXISTS;
            }
        });

        // Abre la ventana de impresión de tu sistema operativo
        boolean doPrint = job.printDialog();
        if (doPrint) {
            try {
                job.print();
                JOptionPane.showMessageDialog(this, "El documento se procesó correctamente.\nSi elegiste 'Microsoft Print to PDF', busca el archivo en la carpeta donde lo guardaste.", "Certificado Generado", JOptionPane.INFORMATION_MESSAGE);
            } catch (PrinterException ex) {
                JOptionPane.showMessageDialog(this, "Error al generar el PDF: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // =========================================================
    // LÓGICA DE BASE DE DATOS (MYSQL)
    // =========================================================
    private void verificarYcargarCuenta(String numeroCuenta) {
        try (Connection conn = ConexionDB.conectar()) {
            if (conn != null) {
                String sql = "SELECT c.id_cuenta, c.saldo, c.estado, c.tipo_cuenta, cl.nombres, cl.apellidos, cl.numero_documento " +
                             "FROM Cuentas c " +
                             "INNER JOIN Clientes cl ON c.id_cliente = cl.id_cliente " +
                             "WHERE c.numero_cuenta = ?";
                
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, numeroCuenta);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    int idCuenta = rs.getInt("id_cuenta");
                    double saldo = rs.getDouble("saldo");
                    String estado = rs.getString("estado");
                    
                    String titular = rs.getString("nombres") + " " + rs.getString("apellidos");
                    String documento = rs.getString("numero_documento");
                    String tipo = rs.getString("tipo_cuenta");
                    
                    txtBusquedaCuenta.setText(numeroCuenta);
                    lblTitular.setText("<html><span style='font-size:10px; color:#666;'>Titular de la Cuenta:</span><br><b style='color:#002244; font-size:11px;'>" + titular + "</b></html>");
                    lblDocumento.setText("<html><span style='font-size:10px; color:#666;'>Documento de Identidad:</span><br><b style='font-family:monospace;'>" + documento + "</b></html>");
                    lblNumeroCuenta.setText("<html><span style='font-size:10px; color:#666;'>Número de Cuenta:</span><br><b style='color:#003366; font-family:monospace; background-color:white; border:1px solid #ccc; padding:1px 3px;'>" + numeroCuenta + "</b></html>");
                    lblTipoProducto.setText("<html><span style='font-size:10px; color:#666;'>Tipo de Producto:</span><br><b>" + tipo + "</b></html>");
                    
                    String colorEstado = estado.equals("ACTIVA") ? "#006600" : "#cc0000";
                    lblEstadoOperativo.setText("<html><span style='font-size:10px; color:#666;'>Estado Operativo:</span><br><b style='color:" + colorEstado + ";'>● " + estado + "</b></html>");
                    
                    DecimalFormat df = new DecimalFormat("$ #,##0.00");
                    lblCajaSaldo.setText("<html><div style='text-align:center; background-color:#eaf2fc; border:2px solid #004080; padding:10px 20px;'>" +
                            "<span style='color:#002b49; font-size:18px; font-weight:bold; font-family:Tahoma;'>Saldo Disponible: <span style='font-family:monospace;'>" + df.format(saldo) + " COP</span></span><br>" +
                            "<span style='font-size:10px; color:#666;'>Total Retenido: <b>$ 0,00</b> | Saldo Contable: <b>" + df.format(saldo) + " COP</b></span></div></html>");
                    
                    cargarHistorialTransacciones(idCuenta, conn);
                    
                } else {
                    JOptionPane.showMessageDialog(this, "El número de cuenta ingresado no existe en la base de datos.", "Error de Búsqueda", JOptionPane.WARNING_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al buscar la cuenta: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarHistorialTransacciones(int idCuenta, Connection conn) {
        modeloTabla.setRowCount(0); 
        double totalDepositos = 0;
        double totalRetiros = 0;

        try {
            String sql = "SELECT tipo_operacion, monto, fecha_hora, terminal FROM Transacciones WHERE id_cuenta = ? ORDER BY fecha_hora DESC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idCuenta);
            ResultSet rs = stmt.executeQuery();

            DecimalFormat df = new DecimalFormat("#,##0.00 COP");
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

            while (rs.next()) {
                String tipoOp = rs.getString("tipo_operacion");
                double monto = rs.getDouble("monto");
                String terminal = rs.getString("terminal");
                
                String fechaObj = rs.getTimestamp("fecha_hora") != null ? sdf.format(rs.getTimestamp("fecha_hora")) : "N/A";
                String fechaCol = "<html><span style='font-family:monospace;'>" + fechaObj + "</span></html>";
                
                String tipoCol = "";
                String montoCol = "";

                if (tipoOp.equals("RETIRO")) {
                    totalRetiros += monto;
                    tipoCol = "<html><b style='color:#cc0000;'>● Retiro</b> <span style='color:#666;'>(" + terminal + ")</span></html>";
                    montoCol = "<html><b style='color:#cc0000; font-family:monospace;'>- $ " + df.format(monto) + "</b></html>";
                } else { 
                    totalDepositos += monto;
                    tipoCol = "<html><b style='color:#006600;'>● Depósito</b> <span style='color:#666;'>(" + terminal + ")</span></html>";
                    montoCol = "<html><b style='color:#006600; font-family:monospace;'>+ $ " + df.format(monto) + "</b></html>";
                }

                modeloTabla.addRow(new Object[]{fechaCol, tipoCol, montoCol});
            }

            lblResumenDepositos.setText("<html><span style='font-size:11px;'>Total Depósitos (+): <b style='color:#006600; font-family:monospace;'>$ " + df.format(totalDepositos) + "</b></span></html>");
            lblResumenRetiros.setText("<html><span style='font-size:11px;'>Total Retiros (-): <b style='color:#cc0000; font-family:monospace;'>$ " + df.format(totalRetiros) + "</b></span></html>");
            
            double neto = totalDepositos - totalRetiros;
            String signoNeto = (neto >= 0) ? "+ $" : "- $";
            String colorNeto = (neto >= 0) ? "#002b66" : "#cc0000";
            lblMovimientoNeto.setText("<html><span style='font-size:11px;'>Movimiento Neto: <b style='color:" + colorNeto + "; font-family:monospace;'>" + signoNeto + " " + df.format(Math.abs(neto)) + "</b></span></html>");

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar historial: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
        SwingUtilities.invokeLater(() -> new EstadoCuentaView().setVisible(true));
    }
}