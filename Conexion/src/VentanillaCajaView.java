import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class VentanillaCajaView extends JFrame {

    private JTextField txtNumCuenta, txtMonto;
    private JLabel lblTitular, lblIdentificacion, lblTipoCuenta, lblSaldo, lblAdvertencia;
    private JRadioButton rbDeposito, rbRetiro;
    private JButton btnVerificar, btnProcesar, btnLimpiar, btnEmergencia;
    
    // Variables de estado real cargadas desde la Base de Datos
    private int idCuentaActual = -1;
    private double saldoActual = 0.00;
    private String estadoCuentaActual = "";

    public VentanillaCajaView() {
        // 1. Configuración de la Ventana Principal
        setTitle("Banco Unicatolica - Ventanilla de Caja y Transacciones (RF09 / RF15)");
        setSize(1000, 680);
        setMinimumSize(new Dimension(950, 620));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setExtendedState(JFrame.MAXIMIZED_BOTH);



        // 3. Header Institucional
        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.setBackground(new Color(236, 233, 216));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(172, 168, 153)),
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
                "<td><b style='color:#002b66; font-size:15px; font-family:Tahoma;'>BANCO UNICATOLICA</b></td>" +
                "</tr><tr><td style='color:#444444; font-size:11px; padding-top:2px;'>Módulo de Transacciones Rápidas de Caja — Depósitos y Retiros</td></tr></table></html>");

        pnlIzquierdo.add(lblLogo);
        pnlIzquierdo.add(lblTitulo);

        // Lado Derecho
        JLabel lblInfoCaja = new JLabel();
        lblInfoCaja.setOpaque(true);
        lblInfoCaja.setBackground(new Color(242, 240, 230));
        lblInfoCaja.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(187, 187, 187)),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));

        Timer timer = new Timer(1000, e -> {
            String horaStr = new SimpleDateFormat("hh:mm:ss a").format(new Date());
            lblInfoCaja.setText("<html><div style='text-align:right; font-family:Tahoma; font-size:10.5px; color:#333;'>" +
                "<span style='color:#00aa00;'>&#9679;</span> <b>Terminal:</b> WS-CAJA-03 | <b>Operador:</b> operador_caja1<br>" +
                "<span>Hora oficial Colombia (UTC-5): <b>" + horaStr + " COT</b> | MySQL: En línea</span>" +
                "</div></html>");
        });
        timer.start();

        headerPanel.add(pnlIzquierdo, BorderLayout.WEST);
        headerPanel.add(lblInfoCaja, BorderLayout.EAST);
        topContainer.add(headerPanel, BorderLayout.CENTER);
        add(topContainer, BorderLayout.NORTH);

        // 4. Panel Central Formulario (Pasos 1 y 2)
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        // --- PASO 1: Búsqueda y Validación de Cuenta ---
        JPanel pnlPaso1 = new JPanel(new BorderLayout(0, 8));
        pnlPaso1.setBackground(new Color(247, 246, 240));
        pnlPaso1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(160, 160, 160)),
                "1. Consulta y Selección de Cuenta Destino / Origen", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Tahoma", Font.BOLD, 11), new Color(0, 43, 102)));

        JPanel pnlBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        pnlBusqueda.setOpaque(false);
        pnlBusqueda.add(new JLabel("<html><b>* Número de Cuenta:</b></html>"));

        txtNumCuenta = new JTextField("", 18);
        txtNumCuenta.setFont(new Font("Consolas", Font.BOLD, 14));
        txtNumCuenta.setForeground(new Color(0, 43, 102));
        pnlBusqueda.add(txtNumCuenta);

        btnVerificar = new JButton("Verificar Cuenta");
        btnVerificar.setFont(new Font("Tahoma", Font.BOLD, 11));
        pnlBusqueda.add(btnVerificar);

        JLabel lblHint = new JLabel("<html><span style='color:#002b66; font-weight:bold;'>[F2]</span> Búsqueda rápida</span></html>");
        pnlBusqueda.add(lblHint);
        
        // BOTÓN DE PÁNICO (RF15)
        JPanel pnlSeguridad = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        pnlSeguridad.setOpaque(false);
        
        btnEmergencia = new JButton("🚨 EMERGENCIA (F12)");
        btnEmergencia.setBackground(new Color(204, 0, 0));
        btnEmergencia.setForeground(Color.WHITE);
        btnEmergencia.setFont(new Font("Tahoma", Font.BOLD, 11));
        
        pnlSeguridad.add(btnEmergencia);
        
        JPanel pnlTopPaso1 = new JPanel(new BorderLayout());
        pnlTopPaso1.setOpaque(false);
        pnlTopPaso1.add(pnlBusqueda, BorderLayout.WEST);
        pnlTopPaso1.add(pnlSeguridad, BorderLayout.EAST);

        pnlPaso1.add(pnlTopPaso1, BorderLayout.NORTH);

        // Ficha de Datos
        JPanel pnlFicha = new JPanel(new GridLayout(1, 4, 10, 0));
        pnlFicha.setBackground(new Color(234, 231, 214));
        pnlFicha.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(196, 190, 170)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        lblTitular = new JLabel("<html><span style='color:#666;'>Titular:</span><br><b>-</b></html>");
        lblIdentificacion = new JLabel("<html><span style='color:#666;'>Identificación (C.C.):</span><br><b style='font-family:monospace;'>-</b></html>");
        lblTipoCuenta = new JLabel("<html><span style='color:#666;'>Estado de Cuenta:</span><br><b style='color:#002b66;'>-</b></html>");
        lblSaldo = new JLabel("<html><span style='color:#666;'>Saldo Disponible Actual:</span><br><b style='color:#006600; font-family:monospace; background-color:#e6ffe6; padding:2px;'>$ 0.00 COP</b></html>");

        pnlFicha.add(lblTitular);
        pnlFicha.add(lblIdentificacion);
        pnlFicha.add(lblTipoCuenta);
        pnlFicha.add(lblSaldo);

        pnlPaso1.add(pnlFicha, BorderLayout.CENTER);
        centerPanel.add(pnlPaso1);

        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // --- PASO 2: Tipo de Transacción y Monto ---
        JPanel pnlPaso2 = new JPanel(new GridBagLayout());
        pnlPaso2.setBackground(new Color(247, 246, 240));
        pnlPaso2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(160, 160, 160)),
                "2. Parámetros de la Transacción Ágil", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Tahoma", Font.BOLD, 11), new Color(0, 43, 102)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        Font labelFont = new Font("Tahoma", Font.BOLD, 12);

        // Fila 1: RadioButtons
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
        JLabel lblTipoTrans = new JLabel("<html><nobr>* Tipo de Transacción:</nobr></html>");
        lblTipoTrans.setFont(labelFont);
        pnlPaso2.add(lblTipoTrans, gbc);

        JPanel pnlRadioGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        pnlRadioGroup.setOpaque(false);
        rbDeposito = new JRadioButton("<html><b style='color:#006600;'>Depósitos en Efectivo (+)</b></html>");
        rbRetiro = new JRadioButton("<html><b style='color:#b20000;'>Retiro por Ventanilla (-)</b></html>", true);
        rbDeposito.setBackground(Color.WHITE);
        rbRetiro.setBackground(Color.WHITE);
        ButtonGroup bgTrans = new ButtonGroup();
        bgTrans.add(rbDeposito);
        bgTrans.add(rbRetiro);
        pnlRadioGroup.add(rbDeposito);
        pnlRadioGroup.add(rbRetiro);

        gbc.gridx = 1; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.WEST;
        pnlPaso2.add(pnlRadioGroup, gbc);

        // Fila 2: Monto
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST;
        JLabel lblMontoLabel = new JLabel("<html><nobr>* Monto a Transar ($):</nobr></html>");
        lblMontoLabel.setFont(labelFont);
        pnlPaso2.add(lblMontoLabel, gbc);

        txtMonto = new JTextField("", 15);
        txtMonto.setFont(new Font("Consolas", Font.BOLD, 18));
        txtMonto.setForeground(new Color(0, 0, 0));
        txtMonto.setBackground(new Color(255, 255, 255));

        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        pnlPaso2.add(txtMonto, gbc);

        JLabel lblSpecs = new JLabel("<html><div style='background-color:#f0f0f0; border:1px solid #ccc; padding:4px 8px; font-size:10px; color:#444;'>" +
                "<b>Formato numérico:</b> Escriba sin puntos<br>" +
                "<b>Mínimo:</b> $ 10.000 COP | <b>Tope Caja:</b> $ 10.000.000 COP</div></html>");
        gbc.gridx = 2; gbc.anchor = GridBagConstraints.WEST;
        pnlPaso2.add(lblSpecs, gbc);

        // Fila 3: Advertencia en tiempo real
        lblAdvertencia = new JLabel("");
        lblAdvertencia.setFont(new Font("Tahoma", Font.BOLD, 11));
        lblAdvertencia.setForeground(new Color(178, 34, 34));

        gbc.gridx = 1; gbc.gridy = 2; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.WEST;
        pnlPaso2.add(lblAdvertencia, gbc);

        centerPanel.add(pnlPaso2);
        add(centerPanel, BorderLayout.CENTER);

        // 5. Panel Inferior (Botones y StatusBar)
        JPanel bottomContainer = new JPanel(new BorderLayout());

        JPanel pnlAcciones = new JPanel(new BorderLayout());
        pnlAcciones.setBackground(new Color(223, 220, 206));
        pnlAcciones.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(172, 168, 153)),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));

        JLabel lblShortcuts = new JLabel("<html><span style='color:#333; font-size:11px;'><b>[Enter]</b> Procesar &nbsp;|&nbsp; <b>[Esc]</b> Cancelar &nbsp;|&nbsp; <b>[F2]</b> Buscar cédula</span></html>");

        JPanel pnlBtnDerecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlBtnDerecha.setOpaque(false);

        btnLimpiar = new JButton("Limpiar Formulario (F5)");
        btnProcesar = new JButton("Procesar Transacción");
        btnProcesar.setFont(new Font("Tahoma", Font.BOLD, 12));
        btnProcesar.setForeground(new Color(0, 43, 102));

        pnlBtnDerecha.add(btnLimpiar);
        pnlBtnDerecha.add(btnProcesar);

        pnlAcciones.add(lblShortcuts, BorderLayout.WEST);
        pnlAcciones.add(pnlBtnDerecha, BorderLayout.EAST);

        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(172, 168, 153)));
        statusBar.add(new JLabel("Estado: Listo | Conexión MySQL Establecida | Caja: #03"));

        bottomContainer.add(pnlAcciones, BorderLayout.NORTH);
        bottomContainer.add(statusBar, BorderLayout.SOUTH);
        add(bottomContainer, BorderLayout.SOUTH);

        // =========================================================
        // 6. EVENTOS Y LÓGICA
        // =========================================================

        btnVerificar.addActionListener(e -> verificarCuenta());
        btnProcesar.addActionListener(e -> procesarTransaccion());
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        rbDeposito.addActionListener(e -> evaluarExcesoMonto());
        rbRetiro.addActionListener(e -> evaluarExcesoMonto());
        
        // --- KILL SWITCH: CIERRE DE EMERGENCIA ---
        btnEmergencia.addActionListener(e -> System.exit(0));

        txtMonto.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { evaluarExcesoMonto(); }
            @Override
            public void removeUpdate(DocumentEvent e) { evaluarExcesoMonto(); }
            @Override
            public void changedUpdate(DocumentEvent e) { evaluarExcesoMonto(); }
        });

        configurarAtajosTeclado();
    }

    // =========================================================
    // MÉTODOS DE BASE DE DATOS Y TRANSACCIONES
    // =========================================================

    private void verificarCuenta() {
        String numCuenta = txtNumCuenta.getText().trim();
        if(numCuenta.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese un número de cuenta válido para buscar.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = ConexionDB.conectar()) {
            if (conn != null) {
                String sql = "SELECT c.id_cuenta, c.saldo, c.estado, c.tipo_cuenta, cl.nombres, cl.apellidos, cl.numero_documento " +
                             "FROM Cuentas c " +
                             "INNER JOIN Clientes cl ON c.id_cliente = cl.id_cliente " +
                             "WHERE c.numero_cuenta = ?";
                
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, numCuenta);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    idCuentaActual = rs.getInt("id_cuenta");
                    saldoActual = rs.getDouble("saldo");
                    estadoCuentaActual = rs.getString("estado");
                    
                    String titular = rs.getString("nombres") + " " + rs.getString("apellidos");
                    String documento = rs.getString("numero_documento");
                    
                    String colorEstado = estadoCuentaActual.equals("ACTIVA") ? "#006600" : "#cc0000";
                    
                    lblTitular.setText("<html><span style='color:#666;'>Titular:</span><br><b>" + titular + "</b></html>");
                    lblIdentificacion.setText("<html><span style='color:#666;'>Identificación (C.C.):</span><br><b style='font-family:monospace;'>" + documento + "</b></html>");
                    lblTipoCuenta.setText("<html><span style='color:#666;'>Estado de Cuenta:</span><br><b style='color:" + colorEstado + ";'>● " + estadoCuentaActual + "</b></html>");
                    
                    DecimalFormat df = new DecimalFormat("$ #,##0.00 COP");
                    lblSaldo.setText("<html><span style='color:#666;'>Saldo Disponible Actual:</span><br><b style='color:#006600; font-family:monospace; background-color:#e6ffe6; padding:2px;'>" + df.format(saldoActual) + "</b></html>");
                    
                    evaluarExcesoMonto();
                } else {
                    limpiarDatosCuenta();
                    JOptionPane.showMessageDialog(this, "Número de cuenta no encontrado en la base de datos.", "Verificación Fallida", JOptionPane.WARNING_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al verificar la cuenta: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void procesarTransaccion() {
        if (idCuentaActual == -1) {
            JOptionPane.showMessageDialog(this, "Primero debe verificar una cuenta válida pulsando [Verificar Cuenta].", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (estadoCuentaActual.equalsIgnoreCase("INACTIVA")) {
            JOptionPane.showMessageDialog(this, "Operación rechazada: La cuenta se encuentra INACTIVA.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String montoTexto = txtMonto.getText().trim().replace(".", "").replace(",", ".");
        if (montoTexto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor ingrese el monto a transar.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double monto;
        try {
            monto = Double.parseDouble(montoTexto);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Monto numérico inválido. Ingrese solo números.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (monto < 10000) {
            JOptionPane.showMessageDialog(this, "El monto mínimo por transacción es de $10.000 COP.", "Monto Inválido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        DecimalFormat df = new DecimalFormat("$ #,##0.00 COP");
        String tipoOperacion = rbRetiro.isSelected() ? "RETIRO" : "CONSIGNACION";

        if (tipoOperacion.equals("RETIRO") && monto > saldoActual) {
            double deficit = monto - saldoActual;
            String detalleError = "El sistema ha rechazado el retiro por fondos insuficientes.\n\n" +
                    "• Saldo actual: " + df.format(saldoActual) + "\n" +
                    "• Monto solicitado: " + df.format(monto) + "\n" +
                    "• Déficit no autorizado: -" + df.format(deficit);
            JOptionPane.showMessageDialog(this, detalleError, "Saldo Insuficiente", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // --- INICIO DE LA TRANSACCIÓN SQL (ACID) ---
        try (Connection conn = ConexionDB.conectar()) {
            if (conn != null) {
                conn.setAutoCommit(false); 
                try {
                    double nuevoSaldo = tipoOperacion.equals("RETIRO") ? (saldoActual - monto) : (saldoActual + monto);
                    
                    String sqlUpdate = "UPDATE Cuentas SET saldo = ? WHERE id_cuenta = ?";
                    PreparedStatement stmtUpdate = conn.prepareStatement(sqlUpdate);
                    stmtUpdate.setDouble(1, nuevoSaldo);
                    stmtUpdate.setInt(2, idCuentaActual);
                    stmtUpdate.executeUpdate();

                    String sqlInsert = "INSERT INTO Transacciones (id_cuenta, tipo_operacion, monto, terminal) VALUES (?, ?, ?, 'WS-CAJA-03')";
                    PreparedStatement stmtInsert = conn.prepareStatement(sqlInsert);
                    stmtInsert.setInt(1, idCuentaActual);
                    stmtInsert.setString(2, tipoOperacion);
                    stmtInsert.setDouble(3, monto);
                    stmtInsert.executeUpdate();

                    conn.commit();
                    
                    saldoActual = nuevoSaldo;
                    lblSaldo.setText("<html><span style='color:#666;'>Saldo Disponible Actual:</span><br><b style='color:#006600; font-family:monospace; background-color:#e6ffe6; padding:2px;'>" + df.format(saldoActual) + "</b></html>");
                    txtMonto.setText("");
                    evaluarExcesoMonto();
                    
                    JOptionPane.showMessageDialog(this, "Transacción de " + tipoOperacion + " aprobada y guardada.\nNuevo saldo: " + df.format(saldoActual), "Transacción Exitosa", JOptionPane.INFORMATION_MESSAGE);

                } catch (SQLException ex) {
                    conn.rollback(); 
                    throw ex; 
                } finally {
                    conn.setAutoCommit(true); 
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Ocurrió un error en la transacción y fue revertida: " + ex.getMessage(), "Fallo Crítico SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    // =========================================================
    // UTILIDADES VISUALES Y ATAJOS
    // =========================================================

    private void limpiarDatosCuenta() {
        lblTitular.setText("<html><span style='color:#666;'>Titular:</span><br><b>-</b></html>");
        lblIdentificacion.setText("<html><span style='color:#666;'>Identificación (C.C.):</span><br><b style='font-family:monospace;'>-</b></html>");
        lblTipoCuenta.setText("<html><span style='color:#666;'>Estado de Cuenta:</span><br><b style='color:#002b66;'>-</b></html>");
        lblSaldo.setText("<html><span style='color:#666;'>Saldo Disponible Actual:</span><br><b style='color:#006600; font-family:monospace; background-color:#e6ffe6; padding:2px;'>$ 0.00 COP</b></html>");
        
        idCuentaActual = -1;
        saldoActual = 0.00;
        estadoCuentaActual = "";
        evaluarExcesoMonto();
    }

    private void limpiarFormulario() {
        txtNumCuenta.setText("");
        limpiarDatosCuenta();
        txtMonto.setText("");
        rbRetiro.setSelected(true);
        lblAdvertencia.setText("");
        txtNumCuenta.requestFocus();
    }

    private void evaluarExcesoMonto() {
        if (!rbRetiro.isSelected() || idCuentaActual == -1) {
            lblAdvertencia.setText("");
            return;
        }

        String texto = txtMonto.getText().trim().replace(".", "").replace(",", ".");
        if (texto.isEmpty()) {
            lblAdvertencia.setText("");
            return;
        }

        try {
            double monto = Double.parseDouble(texto);
            if (monto > saldoActual) {
                DecimalFormat df = new DecimalFormat("#,##0.00 COP");
                lblAdvertencia.setText("<html>⚠ El monto ingresado ($ " + df.format(monto) + ") excede el saldo disponible actual.</html>");
            } else {
                lblAdvertencia.setText("");
            }
        } catch (NumberFormatException ex) {
            lblAdvertencia.setText("");
        }
    }

    private void buscarPorCedula() {
        String cedula = JOptionPane.showInputDialog(this, 
                "Ingrese el número de Cédula (C.C.) del Titular para buscar su cuenta:", 
                "Búsqueda Rápida de Cuenta [F2]", 
                JOptionPane.QUESTION_MESSAGE);
        
        if (cedula != null && !cedula.trim().isEmpty()) {
            try (Connection conn = ConexionDB.conectar()) {
                if (conn != null) {
                    String sql = "SELECT c.numero_cuenta FROM Cuentas c " +
                                 "INNER JOIN Clientes cl ON c.id_cliente = cl.id_cliente " +
                                 "WHERE cl.numero_documento = ? AND c.estado = 'ACTIVA' LIMIT 1";
                                 
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setString(1, cedula.trim());
                    ResultSet rs = stmt.executeQuery();

                    if (rs.next()) {
                        txtNumCuenta.setText(rs.getString("numero_cuenta"));
                        verificarCuenta(); 
                    } else {
                        JOptionPane.showMessageDialog(this, "No se encontraron cuentas activas para la cédula: " + cedula, "Sin Resultados", JOptionPane.WARNING_MESSAGE);
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error en la búsqueda por cédula: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void configurarAtajosTeclado() {
        JComponent rootPane = this.getRootPane();

        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0), "buscarF2");
        rootPane.getActionMap().put("buscarF2", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) { buscarPorCedula(); }
        });

        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), "limpiarForm");
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "limpiarForm");
        rootPane.getActionMap().put("limpiarForm", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) { limpiarFormulario(); }
        });

        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "procesarEnter");
        rootPane.getActionMap().put("procesarEnter", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) { procesarTransaccion(); }
        });

        // --- KILL SWITCH: ATAJO F12 ---
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F12, 0), "panicoOculto");
        rootPane.getActionMap().put("panicoOculto", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) { 
                System.exit(0); // Cierre de emergencia silencioso
            }
        });

        
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
        SwingUtilities.invokeLater(() -> new VentanillaCajaView().setVisible(true));
    }
}