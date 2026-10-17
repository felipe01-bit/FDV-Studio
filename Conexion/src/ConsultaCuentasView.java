import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ConsultaCuentasView extends JFrame {

    private JTextField txtBusqueda;
    private JTable tablaCuentas;
    private DefaultTableModel modeloTabla;
    private JButton btnBuscar, btnLimpiar, btnModificar, btnNueva, btnExtracto, btnEliminar;

    public ConsultaCuentasView() {
        // 1. Configuración de la Ventana Principal
        setTitle("Banco Unicatolica - Consulta de Cuentas y Baja Lógica (RF06 / RF08)");
        setSize(980, 620);
        setMinimumSize(new Dimension(930, 600));
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
                "</tr><tr><td style='color:#555555; font-size:11px; padding-top:2px;'>Módulo de Cuentas — Búsqueda de Cuentas y Baja Lógica</td></tr></table></html>");

        pnlIzquierdo.add(lblLogo);
        pnlIzquierdo.add(lblTitulo);

        // Lado Derecho
        JLabel lblReloj = new JLabel();
        lblReloj.setOpaque(false);
        Timer timer = new Timer(1000, e -> {
            String horaStr = new SimpleDateFormat("hh:mm:ss a").format(new Date());
            lblReloj.setText("<html><div style='text-align:right; font-family:Tahoma; font-size:10px; color:#444;'>" +
                "<span style='color:#00aa00;'>&#9679;</span> <b>Terminal:</b> WS-SUC-042 | <b>Operador:</b> operador_caja1 | <b>Hora:</b> " + horaStr + " COT<br>" +
                "<span>Zona horaria: Colombia (UTC-5) • Oracle 19c Enterprise: <b>En línea</b></span>" +
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

        // 4.1 Buscador (RF06)
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

        // Cargar datos de prueba
        modeloTabla.addRow(new Object[]{"1002-3341-9844", "María Fernanda Ortiz Castro", "Cuenta Corriente", "Activa", "$ 4.250.000,00 COP"});
        modeloTabla.addRow(new Object[]{"4102-8849-1029", "Carlos Alberto Gómez Ramírez", "Cuenta de Ahorros", "Activa", "$ 350.000,00 COP"});
        modeloTabla.addRow(new Object[]{"4102-9901-5523", "Jorge Andrés Caicedo Mina", "Cuenta de Ahorros", "Inactiva", "$ 0,00 COP"});
        modeloTabla.addRow(new Object[]{"5201-1194-7730", "Diana Marcela Rengifo Pérez", "Cuenta de Ahorros", "Activa", "$ 1.820.500,00 COP"});

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

        JPanel pnlDerecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnEliminar = new JButton("Eliminar Cuenta");
        btnEliminar.setForeground(new Color(170, 34, 34));
        btnEliminar.setFont(new Font("Tahoma", Font.BOLD, 11));
        pnlDerecha.add(new JLabel("<html><span style='color:#aa2222; font-size:11px;'>Acción de Auditoría:</span></html>"));
        pnlDerecha.add(btnEliminar);

        pnlAcciones.add(pnlIzquierda, BorderLayout.WEST);
        pnlAcciones.add(pnlDerecha, BorderLayout.EAST);
        centerPanel.add(pnlAcciones, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);

        // 5. Barra de Estado
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(172, 168, 153)));
        statusBar.add(new JLabel("Listo. Módulo cargado correctamente. | Total cuentas en BD: 3.240"));
        add(statusBar, BorderLayout.SOUTH);

        // =========================================================
        // 6. LÓGICA Y EVENTOS
        // =========================================================

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(modeloTabla);
        tablaCuentas.setRowSorter(sorter);

        // Método auxiliar para quitar tildes
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
        txtBusqueda.addActionListener(ejecutarBusqueda); // Permitir buscar al presionar Enter

        // Evento Limpiar
        btnLimpiar.addActionListener(e -> {
            txtBusqueda.setText("");
            sorter.setRowFilter(null);
        });

        // Evento Eliminar Cuenta (RF08 - Eliminar fila)
        btnEliminar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int filaVisual = tablaCuentas.getSelectedRow();
                if (filaVisual == -1) {
                    JOptionPane.showMessageDialog(ConsultaCuentasView.this, 
                            "Por favor, seleccione una cuenta de la tabla para eliminar.", 
                            "Atención", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int filaModelo = tablaCuentas.convertRowIndexToModel(filaVisual);
                String numCuenta = modeloTabla.getValueAt(filaModelo, 0).toString();
                String titular = modeloTabla.getValueAt(filaModelo, 1).toString();
                String tipo = modeloTabla.getValueAt(filaModelo, 2).toString();
                String saldo = modeloTabla.getValueAt(filaModelo, 4).toString();

                String mensaje = "¿Está seguro que desea eliminar esta cuenta?\n" +
                        "Esta operación aplicará una baja lógica de la cuenta bancaria en el sistema central del Banco Unicatolica según la especificación del requerimiento.\n\n" +
                        "Número de Cuenta: " + numCuenta + "\n" +
                        "Titular: " + titular + "\n" +
                        "Tipo de Producto: " + tipo + "\n" +
                        "Saldo Remanente: " + saldo + "\n" +
                        "Transición de Estado: ACTIVA -> INACTIVA / CANCELADA (BAJA)";

                int confirmacion = JOptionPane.showConfirmDialog(ConsultaCuentasView.this,
                        mensaje, "Confirmación de Eliminación",
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

                if (confirmacion == JOptionPane.YES_OPTION) {
                    modeloTabla.removeRow(filaModelo); // Elimina efectivamente la fila seleccionada
                    JOptionPane.showMessageDialog(ConsultaCuentasView.this,
                            "La cuenta ha sido dada de baja y removida exitosamente de la vista.",
                            "Baja Exitosa", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
    }

    // Función que elimina diacríticos/tildes
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