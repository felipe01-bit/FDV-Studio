import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class CrearBaseDatos {
    public void crear(Connection conexion) {
        String sql = "CREATE DATABASE IF NOT EXISTS banco_unicatolica;";
        
        try (Statement stmt = conexion.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("Base de datos 'banco_unicatolica' creada correctamente.");
        } catch (SQLException e) {
            System.err.println("Error al crear la base de datos: " + e.getMessage());
        }
    }
}