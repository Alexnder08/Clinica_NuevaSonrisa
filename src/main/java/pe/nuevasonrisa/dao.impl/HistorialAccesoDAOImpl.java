package pe.nuevasonrisa.dao.impl;

import pe.nuevasonrisa.config.DatabaseConnection;
import pe.nuevasonrisa.dao.HistorialAccesoDAO;
import pe.nuevasonrisa.model.HistorialAcceso;

import java.sql.ResultSet;
import java.util.List;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class HistorialAccesoDAOImpl
        implements HistorialAccesoDAO {

    @Override
    public void registrar(
            String usuario,
            String rol,
            String estado
    ) {

        String sql = """
            INSERT INTO historial_accesos
            (
                usuario,
                rol,
                estado
            )
            VALUES (?, ?, ?)
        """;

        try (
                Connection conn =
                        DatabaseConnection.getConnection();

                PreparedStatement ps =
                        conn.prepareStatement(sql)
        ) {

            ps.setString(1, usuario);
            ps.setString(2, rol);
            ps.setString(3, estado);

            ps.executeUpdate();

        } catch (Exception e) {
            pe.nuevasonrisa.util.AppLogger.error(getClass(), "Unhandled error while completing the operation.", e);
        }
    }

    @Override
    public List<HistorialAcceso> listar() {

        List<HistorialAcceso> lista =
                new ArrayList<>();

        String sql = """
        SELECT *
        FROM historial_accesos
        ORDER BY fecha_acceso DESC
    """;

        try (
                Connection conn =
                        DatabaseConnection.getConnection();

                PreparedStatement ps =
                        conn.prepareStatement(sql);

                ResultSet rs =
                        ps.executeQuery()
        ) {

            while (rs.next()) {

                lista.add(
                        new HistorialAcceso(
                                rs.getInt("id"),
                                rs.getString("usuario"),
                                rs.getString("rol"),
                                rs.getString("estado"),
                                rs.getTimestamp("fecha_acceso")
                                        .toLocalDateTime()
                        )
                );
            }

        } catch (Exception e) {
            pe.nuevasonrisa.util.AppLogger.error(getClass(), "Unhandled error while completing the operation.", e);
        }

        return lista;
    }

}