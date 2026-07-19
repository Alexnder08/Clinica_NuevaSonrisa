package pe.nuevasonrisa;

import pe.nuevasonrisa.config.DatabaseConnection;
import java.sql.Connection;

public class TestConexion {

    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            System.out.println("Conexion exitosa");
            System.out.println(conn.getMetaData().getDatabaseProductName());
        } catch (Exception e) {
            pe.nuevasonrisa.util.AppLogger.error(TestConexion.class, "Database connection test failed.", e);
        }
    }
}
