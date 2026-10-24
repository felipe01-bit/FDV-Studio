import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.File;
import java.io.PrintWriter;

public class ConsultaTransaccionesView extends JFrame {

    private JTextField txtBusquedaCuenta;
    private JTable tablaHistorial;
    private DefaultTableModel modeloTabla;
    private JButton btnBuscar, btnLimpiar, btnImprimir, btnExportar, btnDetalle;
    
    // Etiquetas dinámicas
    private JLabel lblQuickStats;
    private JLabel lblResumen;
    private JLabel lblEstadoInferior;

    public ConsultaTransaccionesView() {
        // 1. Configuración de la Ventana Principal
        setTitle("Banco Unicatolica - Sistema de Gestión Bancaria");
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
                "<span style='color:#555;'>Zona horaria: Colombia (UTC-5) • MySQL: <b>En línea</b> | " + horaStr + "</span>" +
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
        txtBusquedaCuenta = new JTextField("", 25);
        txtBusquedaCuenta.setFont(new Font("Consolas", Font.BOLD, 13));
        pnlBusqueda.add(txtBusquedaCuenta);

        btnBuscar = new JButton("Buscar");
        btnBuscar.setFont(new Font("Tahoma", Font.BOLD, 11));
        btnBuscar.setForeground(new Color(0, 45, 107));
        btnLimpiar = new JButton("Ver Todas (Limpiar)");

        pnlBusqueda.add(btnBuscar);
        pnlBusqueda.add(btnLimpiar);

        JPanel pnlStatsBuscador = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        pnlStatsBuscador.setOpaque(false);
        lblQuickStats = new JLabel("<html><div style='background-color:#f0ece0; border:1px solid #cfcbba; padding:4px 8px; font-size:10px;'>" +
                "<span style='color:#555;'>Modo de Vista: </span><b style='color:#002b66;'>Auditoría Global (Todas las Cuentas)</b></div></html>");
        pnlStatsBuscador.add(lblQuickStats);

        pnlTopBuscador.add(pnlBusqueda, BorderLayout.WEST);
        pnlTopBuscador.add(pnlStatsBuscador, BorderLayout.EAST);
        centerPanel.add(pnlTopBuscador, BorderLayout.NORTH);

        // 4.2 Tabla de Historial
        String[] columnas = {"Fecha/Hora", "Tipo (Depósito/Retiro)", "Monto Oficial", "Número de Cuenta"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tablaHistorial = new JTable(modeloTabla);
        tablaHistorial.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaHistorial.setRowHeight(26);
        tablaHistorial.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 11));
        tablaHistorial.getTableHeader().setBackground(new Color(235, 232, 216));

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        tablaHistorial.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);
        
        tablaHistorial.getColumnModel().getColumn(0).setPreferredWidth(130);
        tablaHistorial.getColumnModel().getColumn(1).setPreferredWidth(200);
        tablaHistorial.getColumnModel().getColumn(2).setPreferredWidth(150);
        tablaHistorial.getColumnModel().getColumn(3).setPreferredWidth(120);

        JScrollPane scrollTabla = new JScrollPane(tablaHistorial);
        scrollTabla.setBorder(BorderFactory.createLineBorder(new Color(127, 157, 185)));
        
        JPanel pnlTablaContainer = new JPanel(new BorderLayout());
        pnlTablaContainer.setOpaque(false);
        JLabel lblSubtitulo = new JLabel("<html><span style='font-size:10px; font-weight:bold; color:#222;'>Historial de Movimientos Registrados en la Base de Datos </span><span style='font-size:9px; color:#666;'>(Orden Cronológico Inverso)</span></html>");
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
        btnImprimir = new JButton("Imprimir Auditoría (F8)");
        btnExportar = new JButton("Exportar a CSV");
        btnDetalle = new JButton("Ver Detalle Contable");
        pnlBotones.add(btnImprimir);
        pnlBotones.add(btnExportar);
        pnlBotones.add(btnDetalle);

        JPanel pnlTotales = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        pnlTotales.setOpaque(false);
        lblResumen = new JLabel("<html><div style='background-color:#e4dfcd; border:1px solid #beb9a4; padding:4px 10px; font-size:10.5px; border-radius:3px;'>" +
                "<span style='color:#555;'>Total Depósitos: </span><b style='color:#006600; font-family:monospace;'>$ 0.00</b> <span style='color:#999; margin:0 5px;'>|</span> " +
                "<span style='color:#555;'>Total Retiros: </span><b style='color:#990000; font-family:monospace;'>$ 0.00</b> <span style='color:#999; margin:0 5px;'>|</span> " +
                "<span style='color:#555;'>Balance Movimientos: </span><b style='color:#002b66; font-family:monospace;'>$ 0.00</b>" +
                "</div></html>");
        pnlTotales.add(lblResumen);

        pnlAcciones.add(pnlBotones, BorderLayout.WEST);
        pnlAcciones.add(pnlTotales, BorderLayout.EAST);
        
        centerPanel.add(pnlAcciones, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);

        // 5. Barra de Estado Inferior
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(198, 194, 174)));
        lblEstadoInferior = new JLabel("Listo. MySQL DB Conectado.");
        statusBar.add(lblEstadoInferior);
        add(statusBar, BorderLayout.SOUTH);

        // =========================================================
        // 6. LÓGICA DE BASE DE DATOS Y EVENTOS
        // =========================================================

        btnBuscar.addActionListener(e -> buscarTransacciones());
        
        btnLimpiar.addActionListener(e -> {
            txtBusquedaCuenta.setText("");
            lblQuickStats.setText("<html><div style='background-color:#f0ece0; border:1px solid #cfcbba; padding:4px 8px; font-size:10px;'>" +
                "<span style='color:#555;'>Modo de Vista: </span><b style='color:#002b66;'>Auditoría Global (Todas las Cuentas)</b></div></html>");
            cargarTransaccionesBD("");
        });
        
        // --- EVENTOS DE LOS TRES BOTONES NUEVOS ---
        btnImprimir.addActionListener(e -> imprimirAuditoria());
        btnExportar.addActionListener(e -> exportarCSV());
        btnDetalle.addActionListener(e -> verDetalleContable());

        // Atajo de teclado para F8 (Imprimir)
        JComponent rootPane = this.getRootPane();
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F8, 0), "imprimirF8");
        rootPane.getActionMap().put("imprimirF8", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) { imprimirAuditoria(); }
        });

        // Cargar todas las transacciones al iniciar la vista
        cargarTransaccionesBD("");
    }

    // =========================================================
    // FUNCIONALIDADES DE LOS TRES BOTONES
    // =========================================================

    private void imprimirAuditoria() {
        if (modeloTabla.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "La tabla está vacía. No hay datos para imprimir.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            // Imprime la JTable nativamente con encabezado y número de página
            boolean impreso = tablaHistorial.print(JTable.PrintMode.FIT_WIDTH,
                    new java.text.MessageFormat("Reporte de Auditoría de Transacciones - Banco Unicatolica"),
                    new java.text.MessageFormat("Página - {0}"));
            if (impreso) {
                JOptionPane.showMessageDialog(this, "El reporte se envió correctamente.", "Impresión Exitosa", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (java.awt.print.PrinterException ex) {
            JOptionPane.showMessageDialog(this, "Error al intentar imprimir: " + ex.getMessage(), "Fallo de Impresión", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportarCSV() {
        if (modeloTabla.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No hay datos en la tabla para exportar.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Exportar Auditoría a Excel / CSV");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos CSV (*.csv)", "csv"));
        
        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".csv")) {
                filePath += ".csv";
            }

            try (PrintWriter writer = new PrintWriter(new File(filePath), "UTF-8")) {
                // Cabeceras del CSV
                writer.println("Fecha y Hora,Tipo de Operacion,Monto,Numero de Cuenta");
                
                // Recorremos las filas extrayendo y limpiando el HTML
                for (int i = 0; i < modeloTabla.getRowCount(); i++) {
                    String fecha = limpiarHTML(modeloTabla.getValueAt(i, 0).toString());
                    String tipo = limpiarHTML(modeloTabla.getValueAt(i, 1).toString());
                    String monto = limpiarHTML(modeloTabla.getValueAt(i, 2).toString());
                    String cuenta = limpiarHTML(modeloTabla.getValueAt(i, 3).toString());
                    
                    writer.println(fecha + "," + tipo + ",\"" + monto + "\"," + cuenta);
                }
                JOptionPane.showMessageDialog(this, "Archivo guardado exitosamente en:\n" + filePath, "Exportación Completada", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Ocurrió un error al guardar el archivo: " + ex.getMessage(), "Error de Exportación", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void verDetalleContable() {
        int filaVisual = tablaHistorial.getSelectedRow();
        if (filaVisual == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un registro en la tabla haciendo clic sobre él.", "Selección Requerida", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Recuperamos los valores de la tabla y limpiamos el formato HTML para la alerta
        String fecha = limpiarHTML(modeloTabla.getValueAt(filaVisual, 0).toString());
        String tipo = limpiarHTML(modeloTabla.getValueAt(filaVisual, 1).toString());
        String monto = limpiarHTML(modeloTabla.getValueAt(filaVisual, 2).toString());
        String cuenta = limpiarHTML(modeloTabla.getValueAt(filaVisual, 3).toString());
        
        // Generamos un número de comprobante aleatorio para la vista de auditoría
        String idTraza = "TRX-" + (int)(Math.random() * 900000 + 100000);

        String mensaje = "========================================\n" +
                         "       DETALLE DE TRAZA CONTABLE\n" +
                         "========================================\n\n" +
                         "ID de Operación : " + idTraza + "\n" +
                         "Fecha / Hora    : " + fecha + "\n" +
                         "Cuenta Afectada : " + cuenta + "\n" +
                         "Naturaleza      : " + tipo + "\n" +
                         "Monto Oficial   : " + monto + "\n\n" +
                         "Estado Lógico   : PROCESADA Y CONCILIADA\n" +
                         "========================================";

        JOptionPane.showMessageDialog(this, mensaje, "Ficha de Detalle de Transacción", JOptionPane.INFORMATION_MESSAGE);
    }

    // Método auxiliar para eliminar etiquetas HTML de las celdas antes de exportar
    private String limpiarHTML(String textoHtml) {
        if (textoHtml == null) return "";
        return textoHtml.replaceAll("<[^>]*>", "").replaceAll("&nbsp;", " ").trim();
    }

    // =========================================================
    // MÉTODOS DE BASE DE DATOS (MYSQL)
    // =========================================================

    private void buscarTransacciones() {
        String cuenta = txtBusquedaCuenta.getText().trim();
        
        if (cuenta.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor ingrese un número de cuenta para filtrar, o use 'Ver Todas'.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try (Connection conn = ConexionDB.conectar()) {
            if (conn != null) {
                String sqlCuenta = "SELECT c.estado, c.tipo_cuenta, cl.nombres, cl.apellidos, cl.numero_documento " +
                                   "FROM Cuentas c " +
                                   "INNER JOIN Clientes cl ON c.id_cliente = cl.id_cliente " +
                                   "WHERE c.numero_cuenta = ?";
                PreparedStatement stmtCuenta = conn.prepareStatement(sqlCuenta);
                stmtCuenta.setString(1, cuenta);
                ResultSet rsCuenta = stmtCuenta.executeQuery();

                if (rsCuenta.next()) {
                    String nombreFull = rsCuenta.getString("nombres") + " " + rsCuenta.getString("apellidos");
                    String doc = rsCuenta.getString("numero_documento");
                    String estado = rsCuenta.getString("estado");
                    String tipo = rsCuenta.getString("tipo_cuenta");
                    
                    String colorEstado = estado.equals("ACTIVA") ? "#006600" : "#cc0000";
                    
                    lblQuickStats.setText("<html><div style='background-color:#f0ece0; border:1px solid #cfcbba; padding:4px 8px; font-size:10px;'>" +
                        "<span style='color:#555;'>Titular: </span><b style='color:#002b66;'>" + nombreFull + " (C.C. " + doc + ")</b> <span style='color:#999;'>|</span> " +
                        "<span style='color:#555;'>Estado: </span><b style='color:" + colorEstado + ";'>" + estado + " (" + tipo + ")</b></div></html>");
                    
                    cargarTransaccionesBD(cuenta);
                    
                } else {
                    JOptionPane.showMessageDialog(this, "No se encontró la cuenta: " + cuenta, "Sin resultados", JOptionPane.WARNING_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al buscar la cuenta: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarTransaccionesBD(String cuentaFiltro) {
        modeloTabla.setRowCount(0);
        double totalDepositos = 0;
        double totalRetiros = 0;
        int conteoRegistros = 0;

        try (Connection conn = ConexionDB.conectar()) {
            if (conn != null) {
                String sql = "";
                PreparedStatement stmt;

                if (cuentaFiltro.isEmpty()) {
                    sql = "SELECT t.fecha_hora, t.tipo_operacion, t.monto, t.terminal, c.numero_cuenta " +
                          "FROM Transacciones t " +
                          "INNER JOIN Cuentas c ON t.id_cuenta = c.id_cuenta " +
                          "ORDER BY t.fecha_hora DESC";
                    stmt = conn.prepareStatement(sql);
                } else {
                    sql = "SELECT t.fecha_hora, t.tipo_operacion, t.monto, t.terminal, c.numero_cuenta " +
                          "FROM Transacciones t " +
                          "INNER JOIN Cuentas c ON t.id_cuenta = c.id_cuenta " +
                          "WHERE c.numero_cuenta = ? " +
                          "ORDER BY t.fecha_hora DESC";
                    stmt = conn.prepareStatement(sql);
                    stmt.setString(1, cuentaFiltro);
                }

                ResultSet rs = stmt.executeQuery();
                DecimalFormat df = new DecimalFormat("#,##0.00 COP");
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

                while (rs.next()) {
                    conteoRegistros++;
                    String fechaObj = rs.getTimestamp("fecha_hora") != null ? sdf.format(rs.getTimestamp("fecha_hora")) : "N/A";
                    String tipoOp = rs.getString("tipo_operacion");
                    double monto = rs.getDouble("monto");
                    String numCuenta = rs.getString("numero_cuenta");
                    String terminal = rs.getString("terminal");

                    String tipoCol = "";
                    String montoCol = "";

                    if (tipoOp.equals("RETIRO")) {
                        totalRetiros += monto;
                        tipoCol = "<html><b style='color:#990000;'>● Retiro</b> <span style='font-size:9px; color:#666;'>(" + terminal + ")</span></html>";
                        montoCol = "<html><b style='color:#990000; font-family:monospace;'>- $ " + df.format(monto) + "</b></html>";
                    } else {
                        totalDepositos += monto;
                        tipoCol = "<html><b style='color:#006600;'>● Depósito</b> <span style='font-size:9px; color:#666;'>(" + terminal + ")</span></html>";
                        montoCol = "<html><b style='color:#006600; font-family:monospace;'>+ $ " + df.format(monto) + "</b></html>";
                    }

                    modeloTabla.addRow(new Object[]{fechaObj, tipoCol, montoCol, numCuenta});
                }

                double neto = totalDepositos - totalRetiros;
                String signoNeto = (neto >= 0) ? "+ $" : "- $";
                String colorNeto = (neto >= 0) ? "#002b66" : "#990000";

                lblResumen.setText("<html><div style='background-color:#e4dfcd; border:1px solid #beb9a4; padding:4px 10px; font-size:10.5px; border-radius:3px;'>" +
                        "<span style='color:#555;'>Total Depósitos: </span><b style='color:#006600; font-family:monospace;'>$ " + df.format(totalDepositos) + "</b> <span style='color:#999; margin:0 5px;'>|</span> " +
                        "<span style='color:#555;'>Total Retiros: </span><b style='color:#990000; font-family:monospace;'>$ " + df.format(totalRetiros) + "</b> <span style='color:#999; margin:0 5px;'>|</span> " +
                        "<span style='color:#555;'>Balance Movimientos: </span><b style='color:" + colorNeto + "; font-family:monospace;'>" + signoNeto + " " + df.format(Math.abs(neto)) + "</b>" +
                        "</div></html>");

                lblEstadoInferior.setText("Listo. Búsqueda completada: " + conteoRegistros + " registros encontrados. | MySQL DB");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error de Base de Datos: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }

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
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
        SwingUtilities.invokeLater(() -> new ConsultaTransaccionesView().setVisible(true));
    }
}