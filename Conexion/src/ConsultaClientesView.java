import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConsultaClientesView extends JFrame {

    private JTextField txtBusqueda;
    private JTable tablaClientes;
    private DefaultTableModel modeloTabla;
    private JButton btnBuscar, btnLimpiar, btnEditar, btnNuevo, btnEliminar;

    public ConsultaClientesView() {
        // 1. Configuración de la Ventana Principal
        setTitle("Banco Unicatolica - Consulta de Clientes (RF02 / RF04)");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 2. Barra de Menú
        JMenuBar menuBar = new JMenuBar();
        String[] menus = {"Cerrar Sesion", "Clientes", "Cuentas", "Operaciones", "Reportes", "Ayuda"};
        for (String m : menus) {
            menuBar.add(new JMenu(m));
        }
        setJMenuBar(menuBar);

        // 3. Panel Superior (Header Institucional)
        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.setBackground(new Color(236, 233, 216));
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(245, 243, 233));
        headerPanel.setOpaque(true);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 15, 5, 15),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(180, 180, 180)),
                        BorderFactory.createEmptyBorder(8, 10, 8, 10)
                )
        ));

        // Lado Izquierdo: Logo y Títulos
        JPanel pnlIzquierdo = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
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
                "<td valign='middle'><b style='color:#002b66; font-size:15px; font-family:Tahoma;'>BANCO UNICATOLICA</b></td>" +
                "<td width='10'></td><td valign='middle'><div style='font-size:9px; color:#003882; background-color:#e2ecf8; border:1px solid #9bbbe1; padding:2px 5px;'>Módulo de Clientes</div></td>" +
                "</tr><tr><td colspan='3' style='color:#555555; font-size:10px; padding-top:3px;'>Búsqueda de registros y Borrado Físico</td></tr></table></html>");

        pnlIzquierdo.add(lblLogo);
        pnlIzquierdo.add(lblTitulo);

        // Lado Derecho: Reloj
        JLabel lblReloj = new JLabel();
        lblReloj.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, new Color(200, 200, 200)), BorderFactory.createEmptyBorder(0, 15, 0, 5)));
        Timer timer = new Timer(1000, e -> {
            String horaStr = new SimpleDateFormat("hh:mm:ss a").format(new Date());
            lblReloj.setText("<html><div style='text-align:right; font-family:Tahoma;'>" +
                "<span style='color:#004080; font-size:13px;'>&#128338;</span> " + 
                "<b style='font-family:monospace; font-size:11px; background-color:white; border:1px solid #808080; padding:1px 5px; color:#000;'>" + horaStr + " COT</b><br>" +
                "<span style='font-size:9px; color:#555;'>Zona horaria: Colombia (UTC-5)</span><br>" +
                "<span style='font-size:9px; color:#00aa00; font-weight:bold;'>&#9679; MySQL DB: Conectado</span></div></html>");
        });
        timer.start();

        headerPanel.add(pnlIzquierdo, BorderLayout.WEST);
        headerPanel.add(lblReloj, BorderLayout.EAST);
        topContainer.add(headerPanel, BorderLayout.CENTER);
        add(topContainer, BorderLayout.NORTH);

        // 4. Panel Central (Buscador y Tabla)
        JPanel centerPanel = new JPanel(new BorderLayout(0, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // 4.1 Panel de Búsqueda
        JPanel pnlBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        pnlBusqueda.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Criterios de Consulta"));
        pnlBusqueda.add(new JLabel("Buscar por Documento o Nombre:"));
        txtBusqueda = new JTextField(20);
        pnlBusqueda.add(txtBusqueda);
        
        btnBuscar = new JButton("Buscar");
        btnLimpiar = new JButton("Limpiar Filtro");
        pnlBusqueda.add(btnBuscar);
        pnlBusqueda.add(btnLimpiar);
        
        centerPanel.add(pnlBusqueda, BorderLayout.NORTH);

        // 4.2 Tabla de Resultados
        String[] columnas = {"Documento", "Nombre Completo", "Teléfono", "Correo", "Dirección", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaClientes = new JTable(modeloTabla);
        tablaClientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaClientes.setRowHeight(22);
        tablaClientes.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        JScrollPane scrollTabla = new JScrollPane(tablaClientes);
        scrollTabla.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Registros de Clientes en Base de Datos"));
        centerPanel.add(scrollTabla, BorderLayout.CENTER);

        // 4.3 Botones de Acción Inferiores
        JPanel pnlAcciones = new JPanel(new BorderLayout());
        
        JPanel pnlIzquierdaAcciones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnEditar = new JButton("Ver Detalle / Editar");
        btnNuevo = new JButton("Nuevo Cliente");
        pnlIzquierdaAcciones.add(btnEditar);
        pnlIzquierdaAcciones.add(btnNuevo);

        JPanel pnlDerechaAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnEliminar = new JButton("Eliminar seleccionado");
        btnEliminar.setForeground(Color.RED);
        btnEliminar.setFont(new Font("Arial", Font.BOLD, 12));
        pnlDerechaAcciones.add(new JLabel("<html><span style='color:red;'>Acción destructiva:</span></html>"));
        pnlDerechaAcciones.add(btnEliminar);

        pnlAcciones.add(pnlIzquierdaAcciones, BorderLayout.WEST);
        pnlAcciones.add(pnlDerechaAcciones, BorderLayout.EAST);
        
        centerPanel.add(pnlAcciones, BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);

        // 5. Barra de Estado Inferior
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY));
        statusBar.add(new JLabel("Listo. Módulo conectado a MySQL."));
        add(statusBar, BorderLayout.SOUTH);

        // =========================================================
        // 6. EVENTOS Y BÚSQUEDA
        // =========================================================
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(modeloTabla);
        tablaClientes.setRowSorter(sorter);

        ActionListener buscarAction = e -> {
            String textoBusqueda = txtBusqueda.getText().trim();
            if (textoBusqueda.isEmpty()) {
                sorter.setRowFilter(null);
            } else {
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + textoBusqueda));
            }
        };

        btnBuscar.addActionListener(buscarAction);
        txtBusqueda.addActionListener(buscarAction);

        btnLimpiar.addActionListener(e -> {
            txtBusqueda.setText("");
            sorter.setRowFilter(null);
        });

        // Evento de ELIMINACIÓN FÍSICA en MySQL (DELETE)
        btnEliminar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int filaVisual = tablaClientes.getSelectedRow();
                
                if (filaVisual == -1) {
                    JOptionPane.showMessageDialog(ConsultaClientesView.this, 
                            "Por favor, seleccione un cliente de la tabla para eliminar.", 
                            "Atención", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int filaModelo = tablaClientes.convertRowIndexToModel(filaVisual);
                String docCliente = modeloTabla.getValueAt(filaModelo, 0).toString().trim();
                String nombreCliente = modeloTabla.getValueAt(filaModelo, 1).toString();

                String mensaje = "¿Está seguro que desea BORRAR DEFINITIVAMENTE este cliente?\n" +
                                 "Esta acción eliminará la fila física de la base de datos MySQL.\n\n" +
                                 "Documento: " + docCliente + "\n" +
                                 "Titular: " + nombreCliente;

                int confirmacion = JOptionPane.showConfirmDialog(ConsultaClientesView.this, 
                        mensaje, "Confirmación de Eliminación", 
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

                if (confirmacion == JOptionPane.YES_OPTION) {
                    try (Connection conn = ConexionDB.conectar()) {
                        if (conn != null) {
                            // Instrucción DELETE para borrar de verdad
                            String sql = "DELETE FROM clientes WHERE numero_documento = ?";
                            PreparedStatement stmt = conn.prepareStatement(sql);
                            stmt.setString(1, docCliente);
                            
                            int filas = stmt.executeUpdate();
                            if (filas > 0) {
                                JOptionPane.showMessageDialog(ConsultaClientesView.this, 
                                        "El cliente ha sido eliminado completamente de la base de datos.", 
                                        "Borrado Exitoso", JOptionPane.INFORMATION_MESSAGE);
                                // Recargamos para que desaparezca de la pantalla
                                cargarClientesBD(); 
                            } else {
                                JOptionPane.showMessageDialog(ConsultaClientesView.this, 
                                        "No se encontró el registro con el documento " + docCliente + ".", 
                                        "Aviso", JOptionPane.WARNING_MESSAGE);
                            }
                        }
                    } catch (SQLException ex) {
                        // Error 1451: MySQL bloquea el borrado por la Llave Foránea (el cliente tiene cuentas)
                        if (ex.getErrorCode() == 1451) {
                            JOptionPane.showMessageDialog(ConsultaClientesView.this, 
                                "No se puede borrar este cliente porque tiene cuentas bancarias asignadas.\n\n" +
                                "La base de datos protege su información (Restricción de Llave Foránea).\n" +
                                "Debe borrar primero sus cuentas antes de borrar al cliente.", 
                                "Protección de MySQL", JOptionPane.ERROR_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(ConsultaClientesView.this, 
                                    "Error al intentar borrar el cliente: " + ex.getMessage(), 
                                    "Error SQL", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        });

        // Cargar los datos al iniciar la ventana
        cargarClientesBD();
    }

    // =========================================================
    // LÓGICA DE CONSULTA BASE DE DATOS
    // =========================================================
    private void cargarClientesBD() {
        modeloTabla.setRowCount(0); // Limpiar filas de la vista
        try (Connection conn = ConexionDB.conectar()) {
            if (conn != null) {
                String sql = "SELECT numero_documento, CONCAT(nombres, ' ', apellidos) AS nombre_completo, telefono, correo, direccion, estado FROM clientes";
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    String doc = rs.getString("numero_documento");
                    String nombre = rs.getString("nombre_completo");
                    String tel = rs.getString("telefono");
                    String correo = rs.getString("correo");
                    String dir = rs.getString("direccion");
                    String estado = rs.getString("estado");

                    modeloTabla.addRow(new Object[]{doc, nombre, tel, correo, dir, estado});
                }
            }
        } catch (SQLException ex) {
            System.err.println("Error al cargar la lista de clientes: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
        SwingUtilities.invokeLater(() -> new ConsultaClientesView().setVisible(true));
    }
}