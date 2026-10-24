import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ConsultaCuentasView extends JFrame {

    private JTextField txtBusqueda;
    private JTable tablaCuentas;
    private DefaultTableModel modeloTabla;
    private JButton btnBuscar, btnLimpiar, btnModificar, btnNueva, btnExtracto, btnEliminar;
    private JButton btnEmergencia, btnDesbloquear; 

    public ConsultaCuentasView() {
        // 1. Configuración de la Ventana Principal
        setTitle("Banco Unicatolica - Consulta de Cuentas y Baja Lógica");
        setSize(1100, 620); 
        setMinimumSize(new Dimension(1000, 600));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 2. Barra de Menú
        JMenuBar menuBar = new JMenuBar();
        String[] menus = {"Cerrar Sesión", "Clientes", "Cuentas", "Operaciones (Caja)", "Reportes", "Seguridad", "Ayuda"};
        for (String m : menus) {
            JMenu menu = new JMenu(m);
            if (m.equals("Cuentas")) menu.setForeground(new Color(0, 51, 153));
            menuBar.add(menu);
        }
        setJMenuBar(menuBar);

        // 3. Header Institucional
        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.setBackground(new Color(236, 233, 216));
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(243, 240, 226));
        headerPanel.setOpaque(true);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 195, 176)),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));

        // Lado Izquierdo
        JPanel pnlIzquierdo = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
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
                "</tr><tr><td style='color:#555555; font-size:11px; padding-top:2px;'>Módulo de Cuentas — Búsqueda de Cuentas y Gestión</td></tr></table></html>");

        pnlIzquierdo.add(lblLogo);
        pnlIzquierdo.add(lblTitulo);

        // Lado Derecho
        JLabel lblReloj = new JLabel();
        lblReloj.setOpaque(false);
        Timer timer = new Timer(1000, e -> {
            String horaStr = new SimpleDateFormat("hh:mm:ss a").format(new Date());
            lblReloj.setText("<html><div style='text-align:right; font-family:Tahoma; font-size:10px; color:#444;'>" +
                "<span style='color:#00aa00;'>&#9679;</span> <b>Terminal:</b> WS-SUC-042 | <b>Operador:</b> operador_caja1 | <b>Hora:</b> " + horaStr + " COT<br>" +
                "<span>Zona horaria: Colombia (UTC-5) • MySQL: <b>En línea</b></span>" +
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

        // 4.1 Buscador
        JPanel pnlBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        pnlBusqueda.setBackground(new Color(247, 246, 240));
        pnlBusqueda.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(172, 168, 153)), 
                "► CRITERIOS DE BÚSQUEDA Y FILTRADO:", TitledBorder.LEFT, TitledBorder.TOP, new Font("Tahoma", Font.BOLD, 11), new Color(0, 51, 153)));

        pnlBusqueda.add(new JLabel("<html><b>Buscar por Número de Cuenta o Titular:</b></html>"));
        txtBusqueda = new JTextField(22);
        pnlBusqueda.add(txtBusqueda);

        btnBuscar = new JButton("Buscar");
        btnBuscar.setFont(new Font("Tahoma", Font.BOLD, 11));
        btnBuscar.setForeground(new Color(0, 51, 102));

        btnLimpiar = new JButton("Limpiar Filtro");

        pnlBusqueda.add(btnBuscar);
        pnlBusqueda.add(btnLimpiar);

        centerPanel.add(pnlBusqueda, BorderLayout.NORTH);

        // 4.2 Tabla JTable
        String[] columnas = {"Número de Cuenta", "Titular", "Tipo", "Estado", "Saldo Actual"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tablaCuentas = new JTable(modeloTabla);
        tablaCuentas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaCuentas.setRowHeight(24);
        tablaCuentas.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 11));

        // Alineación a la derecha para el Saldo
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        tablaCuentas.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);

        // CARGAMOS LOS DATOS DIRECTAMENTE DE LA BD AL ABRIR LA VENTANA
        cargarDatosTabla();

        JScrollPane scrollTabla = new JScrollPane(tablaCuentas);
        scrollTabla.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(127, 157, 185)), "Registro de Cuentas Bancarias Activas e Históricas"));
        centerPanel.add(scrollTabla, BorderLayout.CENTER);

        // 4.3 Botones de Acción
        JPanel pnlAcciones = new JPanel(new BorderLayout());
        
        JPanel pnlIzquierda = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnModificar = new JButton("✎ Modificar Parámetros");
        btnNueva = new JButton("+ Nueva Cuenta");
        btnExtracto = new JButton("📄 Extracto de Cuenta");
        pnlIzquierda.add(btnModificar);
        pnlIzquierda.add(btnNueva);
        pnlIzquierda.add(btnExtracto);

        // Botones de seguridad en el panel derecho
        JPanel pnlDerecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        
        btnDesbloquear = new JButton("🔓 Desbloquear");
        btnDesbloquear.setFont(new Font("Tahoma", Font.BOLD, 11));
        
        btnEmergencia = new JButton("🚨 BLOQUEO (F12)");
        btnEmergencia.setBackground(new Color(204, 0, 0));
        btnEmergencia.setForeground(Color.WHITE);
        btnEmergencia.setFont(new Font("Tahoma", Font.BOLD, 11));

        btnEliminar = new JButton("Eliminar Cuenta");
        btnEliminar.setForeground(new Color(170, 34, 34));
        btnEliminar.setFont(new Font("Tahoma", Font.BOLD, 11));
        
        pnlDerecha.add(btnDesbloquear);
        pnlDerecha.add(btnEmergencia);
        pnlDerecha.add(new JLabel(" | "));
        pnlDerecha.add(btnEliminar);

        pnlAcciones.add(pnlIzquierda, BorderLayout.WEST);
        pnlAcciones.add(pnlDerecha, BorderLayout.EAST);
        centerPanel.add(pnlAcciones, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);

        // 5. Barra de Estado
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(172, 168, 153)));
        statusBar.add(new JLabel("Listo. Módulo cargado correctamente con conexión a BD."));
        add(statusBar, BorderLayout.SOUTH);

        // 6. LÓGICA Y EVENTOS
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(modeloTabla);
        tablaCuentas.setRowSorter(sorter);

        // Evento Buscar
        ActionListener ejecutarBusqueda = e -> {
            String busqueda = txtBusqueda.getText().trim();
            if (busqueda.isEmpty()) {
                sorter.setRowFilter(null);
            } else {
                String busquedaSinTildes = quitarTildes(busqueda).toLowerCase();
                sorter.setRowFilter(new RowFilter<DefaultTableModel, Integer>() {
                    @Override
                    public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                        for (int i = 0; i < entry.getValueCount(); i++) {
                            String valorColumna = entry.getStringValue(i);
                            if (valorColumna != null) {
                                String valorSinTildes = quitarTildes(valorColumna).toLowerCase();
                                if (valorSinTildes.contains(busquedaSinTildes)) {
                                    return true;
                                }
                            }
                        }
                        return false;
                    }
                });
            }
        };

        btnBuscar.addActionListener(ejecutarBusqueda);
        txtBusqueda.addActionListener(ejecutarBusqueda); 

        // Evento Limpiar
        btnLimpiar.addActionListener(e -> {
            txtBusqueda.setText("");
            sorter.setRowFilter(null);
        });

        // Evento Modificar Cuenta (RF07)
        btnModificar.addActionListener(e -> {
            int filaVisual = tablaCuentas.getSelectedRow();
            if (filaVisual == -1) {
                JOptionPane.showMessageDialog(this, "Por favor, seleccione una cuenta de la tabla para modificar sus parámetros.", "Atención", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int filaModelo = tablaCuentas.convertRowIndexToModel(filaVisual);
            String numCuenta = modeloTabla.getValueAt(filaModelo, 0).toString();

            GestionCuentasView vistaEdicion = new GestionCuentasView();
            vistaEdicion.prepararEdicion(numCuenta); 
            
            vistaEdicion.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                    cargarDatosTabla(); 
                }
            });
            vistaEdicion.setVisible(true);
        });

        // Evento Nueva Cuenta
        btnNueva.addActionListener(e -> {
            GestionCuentasView vistaNueva = new GestionCuentasView();
            vistaNueva.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                    cargarDatosTabla(); 
                }
            });
            vistaNueva.setVisible(true);
        });

        // Evento Generar Extracto (RF12)
        btnExtracto.addActionListener(e -> {
            int filaVisual = tablaCuentas.getSelectedRow();
            if (filaVisual == -1) {
                JOptionPane.showMessageDialog(this, "Por favor, seleccione una cuenta para generar su extracto.", "Atención", JOptionPane.WARNING_MESSAGE);
                return;
            }
            EstadoCuentaView vistaExtracto = new EstadoCuentaView();
            vistaExtracto.setVisible(true);
        });

        // =========================================================
        // EVENTOS DE SEGURIDAD (RF15 / HU12) - KILL SWITCH
        // =========================================================
        
        // 1. Cierre mediante botón visual
        btnEmergencia.addActionListener(e -> {
            System.exit(0); // Apaga el sistema inmediatamente
        });

        // 2. Cierre silencioso mediante tecla F12
        JComponent rootPane = this.getRootPane();
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F12, 0), "panicoOculto");
        rootPane.getActionMap().put("panicoOculto", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) { 
                System.exit(0); // Cierre de emergencia silencioso
            }
        });

        btnDesbloquear.addActionListener(e -> {
            int filaVisual = tablaCuentas.getSelectedRow();
            if (filaVisual == -1) {
                JOptionPane.showMessageDialog(this, "Seleccione una cuenta de la tabla para desbloquear.", "Atención", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int filaModelo = tablaCuentas.convertRowIndexToModel(filaVisual);
            String estadoActual = modeloTabla.getValueAt(filaModelo, 3).toString();
            
            if (!estadoActual.equals("BLOQUEO PREVENTIVO")) {
                JOptionPane.showMessageDialog(this, "Esta cuenta no se encuentra bajo Bloqueo Preventivo.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            JPasswordField pf = new JPasswordField();
            int ok = JOptionPane.showConfirmDialog(this, pf, "Credenciales de Supervisor Requeridas", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            
            if (ok == JOptionPane.OK_OPTION) {
                String clave = new String(pf.getPassword());
                if ("admin123".equals(clave)) {
                    String numCuenta = modeloTabla.getValueAt(filaModelo, 0).toString();
                    cambiarEstadoCuentaBD(numCuenta, "ACTIVA");
                    JOptionPane.showMessageDialog(this, "Cuenta reactivada exitosamente a estado ACTIVA.", "Desbloqueo Autorizado", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Contraseña de supervisor incorrecta.", "Acceso Denegado", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Evento Eliminar Cuenta
        btnEliminar.addActionListener(e -> {
            int filaVisual = tablaCuentas.getSelectedRow();
            if (filaVisual == -1) {
                JOptionPane.showMessageDialog(this, "Por favor, seleccione una cuenta de la tabla para eliminar.", "Atención", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int filaModelo = tablaCuentas.convertRowIndexToModel(filaVisual);
            String numCuenta = modeloTabla.getValueAt(filaModelo, 0).toString();
            String titular = modeloTabla.getValueAt(filaModelo, 1).toString();

            String mensaje = "¿Está seguro que desea eliminar DEFINITIVAMENTE esta cuenta?\n" +
                    "Esta operación borrará el registro de la base de datos y no se podrá deshacer.\n\n" +
                    "Número de Cuenta: " + numCuenta + "\n" +
                    "Titular: " + titular;

            int confirmacion = JOptionPane.showConfirmDialog(this, mensaje, "Confirmación de Eliminación Física", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (confirmacion == JOptionPane.YES_OPTION) {
                String sql = "DELETE FROM cuentas WHERE numero_cuenta = ?";
                try (Connection con = ConexionDB.conectar(); PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setString(1, numCuenta);
                    int filas = ps.executeUpdate();
                    if(filas > 0) {
                        modeloTabla.removeRow(filaModelo);
                        JOptionPane.showMessageDialog(this, "La cuenta ha sido eliminada permanentemente de la base de datos.", "Eliminación Exitosa", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (SQLException ex) {
                    if(ex.getErrorCode() == 1451) {
                        JOptionPane.showMessageDialog(this, "No se puede eliminar la cuenta porque ya tiene transacciones o historial asociado.\nDebe eliminar primero las transacciones o aplicar una baja lógica.", "Error de Integridad Referencial", JOptionPane.ERROR_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "Error al conectar con la base de datos:\n" + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
    }

    // =========================================================
    // MÉTODOS DE BASE DE DATOS Y UTILIDADES
    // =========================================================
    
    private void cargarDatosTabla() {
        modeloTabla.setRowCount(0); 
        String sql = "SELECT c.numero_cuenta, CONCAT(cl.nombres, ' ', IFNULL(cl.apellidos, '')) AS titular, " +
                     "c.tipo_cuenta, c.estado, c.saldo " +
                     "FROM cuentas c " +
                     "INNER JOIN clientes cl ON c.id_cliente = cl.id_cliente";
                     
        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
             
            while (rs.next()) {
                String numeroCuenta = rs.getString("numero_cuenta");
                String titular = rs.getString("titular").trim();
                String tipo = rs.getString("tipo_cuenta");
                String estado = rs.getString("estado");
                String saldo = String.format("$ %,.2f COP", rs.getDouble("saldo")); 
                
                modeloTabla.addRow(new Object[]{numeroCuenta, titular, tipo, estado, saldo});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar las cuentas desde la base de datos:\n" + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cambiarEstadoCuentaBD(String numCuenta, String nuevoEstado) {
        String sql = "UPDATE cuentas SET estado = ? WHERE numero_cuenta = ?";
        try (Connection con = ConexionDB.conectar(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nuevoEstado);
            ps.setString(2, numCuenta);
            ps.executeUpdate();
            cargarDatosTabla(); // Refrescar la tabla para ver el cambio inmediato
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cambiar el estado de la cuenta: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String quitarTildes(String texto) {
        if (texto == null) return "";
        String normalizado = Normalizer.normalize(texto, Normalizer.Form.NFD);
        return normalizado.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
        SwingUtilities.invokeLater(() -> new ConsultaCuentasView().setVisible(true));
    }
}