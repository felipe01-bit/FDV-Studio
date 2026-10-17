import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GestionClientesView extends JFrame {

    private JTextField txtIdentificacion, txtNombre, txtTelefono, txtCorreo, txtDireccion;
    private JRadioButton rbActivo, rbInactivo;
    private JButton btnGuardar, btnCancelar;

    public GestionClientesView() {
        // 1. Configuración de la Ventana Principal
        setTitle("Banco Unicatolica - Gestión de Clientes");
        setSize(850, 520); // Un poco más ancha para que quepa bien el header
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 2. Barra de Menú
        JMenuBar menuBar = new JMenuBar();
        String[] menus = {"Cerrar Sesion", "Clientes", "Cuentas", "Operaciones", "Reportes", "Ayuda"};
        for (String m : menus) {
            menuBar.add(new JMenu(m));
        }
        setJMenuBar(menuBar);

        // 3. Panel Superior (Header estilo Web / Beige Claro)
        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.setBackground(new Color(236, 233, 216)); // Color base ventana Swing clásica
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(245, 243, 233)); // Fondo beige claro del cuadro
        headerPanel.setOpaque(true);
        // Doble borde: Margen exterior + Borde gris delgado
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 15, 5, 15),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(180, 180, 180)),
                        BorderFactory.createEmptyBorder(8, 10, 8, 10)
                )
        ));

        // --- Lado Izquierdo: Logo y Títulos ---
      // --- Lado Izquierdo: Logo y Títulos ---
        JPanel pnlIzquierdo = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlIzquierdo.setOpaque(false);
        
        JLabel lblLogo = new JLabel();
        try {
            // getResource busca el archivo sin importar las carpetas del sistema
            java.net.URL imgURL = getClass().getResource("/banco_unicatolica.jpg");
            if (imgURL != null) {
                ImageIcon icon = new ImageIcon(imgURL);
                Image img = icon.getImage().getScaledInstance(55, 55, Image.SCALE_SMOOTH);
                lblLogo.setIcon(new ImageIcon(img));
            } else {
                System.out.println("Aviso: No se encontró la imagen en el Classpath.");
            }
        } catch (Exception ex) {
            System.err.println("Error al cargar imagen: " + ex.getMessage());
        }

        // Uso de HTML y Tablas para alinear el título y el "botón/etiqueta" azul
        JLabel lblTitulo = new JLabel("<html>" +
                "<table cellpadding='0' cellspacing='0'>" +
                "<tr>" +
                "<td valign='middle'><b style='color:#002b66; font-size:15px; font-family:Tahoma;'>BANCO UNICATOLICA</b></td>" +
                "<td width='10'></td>" +
                "<td valign='middle'><div style='font-size:9px; color:#003882; background-color:#e2ecf8; border:1px solid #9bbbe1; padding:2px 5px;'>Módulo de Gestión de Clientes</div></td>" +
                "</tr>" +
                "<tr>" +
                "<td colspan='3' style='color:#555555; font-size:10px; padding-top:3px; font-family:Tahoma;'>Registro, actualización y administración de datos maestros</td>" +
                "</tr>" +
                "</table>" +
                "</html>");

        pnlIzquierdo.add(lblLogo);
        pnlIzquierdo.add(lblTitulo);

        // --- Lado Derecho: Reloj en vivo y Estado ---
        JLabel lblReloj = new JLabel();
        // Borde izquierdo para crear la línea divisoria (Separator)
        lblReloj.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 1, 0, 0, new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(0, 15, 0, 5)
        ));

        // Timer para hacer que los segundos avancen en vivo
        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String horaStr = new SimpleDateFormat("hh:mm:ss a").format(new Date());
                String fechaStr = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
                
                lblReloj.setText("<html><div style='text-align:right; font-family:Tahoma;'>" +
                    "<span style='color:#004080; font-size:13px;'>&#128338;</span> " + 
                    "<b style='font-family:monospace; font-size:11px; background-color:white; border:1px solid #808080; padding:1px 5px; color:#000;'>" + horaStr + " COT</b><br>" +
                    "<span style='font-size:9px; color:#555;'>Zona horaria: <b style='color:black;'>Colombia (UTC-5)</b> | " + fechaStr + "</span><br>" +
                    "<span style='font-size:9px; color:#00aa00; font-weight:bold;'>&#9679; Servidor Sede Central: Conectado</span>" +
                    "</div></html>");
            }
        });
        timer.start();

        headerPanel.add(pnlIzquierdo, BorderLayout.WEST);
        headerPanel.add(lblReloj, BorderLayout.EAST);
        topContainer.add(headerPanel, BorderLayout.CENTER);
        add(topContainer, BorderLayout.NORTH);

        // 4. Panel Central (Formulario)
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JPanel formPanel = new JPanel(new GridBagLayout());
        // Ajuste del título del panel según el boceto
        formPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), 
                "Datos Generales del Cliente (Formulario de Edición / Registro)", 
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 12), new Color(0, 51, 153)));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("<html><font color='red'>*</font> Identificación:</html>"), gbc);
        txtIdentificacion = new JTextField("", 15);
        gbc.gridx = 1;
        formPanel.add(txtIdentificacion, gbc);

        gbc.gridx = 2;
        formPanel.add(new JLabel("<html><font color='red'>*</font> Estado:</html>"), gbc);
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        rbActivo = new JRadioButton("Activo", true);
        rbInactivo = new JRadioButton("Inactivo");
        ButtonGroup bgEstado = new ButtonGroup();
        bgEstado.add(rbActivo); bgEstado.add(rbInactivo);
        statusPanel.add(rbActivo); statusPanel.add(rbInactivo);
        gbc.gridx = 3;
        formPanel.add(statusPanel, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("<html><font color='red'>*</font> Nombre Completo:</html>"), gbc);
        txtNombre = new JTextField("", 25);
        gbc.gridx = 1; gbc.gridwidth = 3;
        formPanel.add(txtNombre, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("<html><font color='red'>*</font> Teléfono:</html>"), gbc);
        txtTelefono = new JTextField("", 15);
        gbc.gridx = 1; gbc.gridwidth = 3;
        formPanel.add(txtTelefono, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("<html><font color='red'>*</font> Correo electrónico:</html>"), gbc);
        txtCorreo = new JTextField("", 25);
        gbc.gridx = 1; gbc.gridwidth = 3;
        formPanel.add(txtCorreo, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("<html><font color='red'>*</font> Dirección:</html>"), gbc);
        txtDireccion = new JTextField("", 25);
        gbc.gridx = 1; gbc.gridwidth = 3;
        formPanel.add(txtDireccion, gbc);

        centerPanel.add(formPanel, BorderLayout.CENTER);

        // 5. Panel Inferior
        JPanel bottomPanel = new JPanel(new BorderLayout());
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnGuardar = new JButton("Guardar Cliente");
        btnCancelar = new JButton("Cancelar");
        btnPanel.add(btnGuardar);
        btnPanel.add(btnCancelar);
        
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY));
        statusBar.add(new JLabel("Listo. Esperando entrada de datos... | Terminal: WS-SUC-042"));

        bottomPanel.add(btnPanel, BorderLayout.NORTH);
        bottomPanel.add(statusBar, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // 6. Eventos
        btnGuardar.addActionListener(e -> validarFormulario());
        btnCancelar.addActionListener(e -> dispose());
    }

    private void validarFormulario() {
        if (txtIdentificacion.getText().trim().isEmpty() || txtNombre.getText().trim().isEmpty() ||
            txtTelefono.getText().trim().isEmpty() || txtCorreo.getText().trim().isEmpty() ||
            txtDireccion.getText().trim().isEmpty()) {
            
            if(txtCorreo.getText().trim().isEmpty()) txtCorreo.setBackground(new Color(255, 240, 240));

            JOptionPane.showMessageDialog(this,
                    "Debe diligenciar todos los campos obligatorios marcados con asterisco rojo.\n\n" +
                    "Por favor verifique los datos ingresados.",
                    "Error de Validación", JOptionPane.ERROR_MESSAGE);
        } else {
            txtCorreo.setBackground(Color.WHITE);
            JOptionPane.showMessageDialog(this, "Datos del cliente guardados correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
        SwingUtilities.invokeLater(() -> new GestionClientesView().setVisible(true));
    }
}