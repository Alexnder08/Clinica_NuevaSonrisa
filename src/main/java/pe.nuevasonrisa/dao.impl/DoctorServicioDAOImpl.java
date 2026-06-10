package pe.nuevasonrisa.dao.impl;

import pe.nuevasonrisa.config.DatabaseConnection;
import pe.nuevasonrisa.dao.DoctorServicioDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DoctorServicioDAOImpl implements DoctorServicioDAO {

    @Override
    public List<Integer> obtenerServiciosDoctor(int doctorId) {

        List<Integer> lista = new ArrayList<>();

        String sql = """
            SELECT servicio_id
            FROM doctor_servicios
            WHERE doctor_id = ?
        """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, doctorId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(rs.getInt("servicio_id"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

    @Override
    public boolean guardarServiciosDoctor(
            int doctorId,
            List<Integer> serviciosIds
    ) {

        String eliminarSql = """
            DELETE FROM doctor_servicios
            WHERE doctor_id = ?
        """;

        String insertarSql = """
            INSERT INTO doctor_servicios
            (doctor_id, servicio_id)
            VALUES (?, ?)
        """;

        try (
                Connection conn = DatabaseConnection.getConnection()
        ) {
            conn.setAutoCommit(false);

            try (
                    PreparedStatement psEliminar =
                            conn.prepareStatement(eliminarSql);

                    PreparedStatement psInsertar =
                            conn.prepareStatement(insertarSql)
            ) {
                psEliminar.setInt(1, doctorId);
                psEliminar.executeUpdate();

                for (Integer servicioId : serviciosIds) {
                    psInsertar.setInt(1, doctorId);
                    psInsertar.setInt(2, servicioId);
                    psInsertar.addBatch();
                }

                psInsertar.executeBatch();

                conn.commit();
                return true;

            } catch (Exception e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}