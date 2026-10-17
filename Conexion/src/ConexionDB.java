import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class ConexionDB {
    
    // Cambia "banco_unicatolica" por el nombre exacto de tu base de datos
    private static final String URL = "jdbc:mysql://localhost:3306/banco_unicatolica?useSSL=false&serverTimezone=UTC";
    private static final String USUARIO = "root"; // Tu usuario de MySQL
    private static final String PASSWORD = "12345kfc*";    // Tu contraseña de MySQL (déjalo vacío si no tiene)

    public static Connection conectar() {
        Connection conexion = null;
        try {
            // Cargar el driver (Opcional en versiones nuevas, pero buena práctica)
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Establecer la conexión
            conexion = DriverManager.getConnection(URL, USUARIO, PASSWORD);
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Error: Falta el driver JDBC (mysql-connector-j).", "Error de Conexión", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al conectar con la base de datos:\n" + e.getMessage(), "Error de Conexión", JOptionPane.ERROR_MESSAGE);
        }
        return conexion;
    }
}