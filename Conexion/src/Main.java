import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        Connection conexionMySQL = ConexionDB.conectar();
        
        if (conexionMySQL != null) {
            CrearBaseDatos creadorBD = new CrearBaseDatos();
            creadorBD.crear(conexionMySQL);

            try {
                conexionMySQL.close();
                System.out.println("Conexión cerrada.");
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            }
        } else {
            System.out.println("No se pudo proceder porque falló la conexión.");
        }
    }
} 