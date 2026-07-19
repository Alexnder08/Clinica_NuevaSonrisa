package pe.nuevasonrisa.dao.impl;

import pe.nuevasonrisa.config.DatabaseConnection;
import pe.nuevasonrisa.dao.DoctorServicioDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

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
            pe.nuevasonrisa.util.AppLogger.error(getClass(), "Unhandled error while completing the operation.", e);
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
                pe.nuevasonrisa.util.AppLogger.error(getClass(), "Unhandled error while completing the operation.", e);
                return false;
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (Exception e) {
            pe.nuevasonrisa.util.AppLogger.error(getClass(), "Unhandled error while completing the operation.", e);
            return false;
        }
    }

    @Override
    public boolean existeOtroDoctorConMismosServicios(int doctorId, List<Integer> serviciosIds) {
        if (serviciosIds == null || serviciosIds.isEmpty()) {
            return false;
        }

        List<Integer> objetivo = normalizar(serviciosIds);
        Map<Integer, List<Integer>> serviciosPorDoctor = new HashMap<>();

        String sql = """
            SELECT doctor_id, servicio_id
            FROM doctor_servicios
            ORDER BY doctor_id, servicio_id
        """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                int actualDoctorId = rs.getInt("doctor_id");
                int servicioId = rs.getInt("servicio_id");
                serviciosPorDoctor
                        .computeIfAbsent(actualDoctorId, k -> new ArrayList<>())
                        .add(servicioId);
            }
        } catch (Exception e) {
            pe.nuevasonrisa.util.AppLogger.error(getClass(), "Unhandled error while completing the operation.", e);
            return false;
        }

        for (Map.Entry<Integer, List<Integer>> entry : serviciosPorDoctor.entrySet()) {
            int otroDoctorId = entry.getKey();
            if (otroDoctorId == doctorId) {
                continue;
            }

            List<Integer> actuales = normalizar(entry.getValue());
            if (actuales.equals(objetivo)) {
                return true;
            }
        }

        return false;
    }

    private List<Integer> normalizar(List<Integer> serviciosIds) {
        return new ArrayList<>(new TreeSet<>(serviciosIds));
    }
}
