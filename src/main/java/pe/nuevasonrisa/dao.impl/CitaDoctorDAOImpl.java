package pe.nuevasonrisa.dao.impl;

import pe.nuevasonrisa.config.DatabaseConnection;
import pe.nuevasonrisa.dao.CitaDoctorDAO;
import pe.nuevasonrisa.model.CitaDoctorTabla;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CitaDoctorDAOImpl implements CitaDoctorDAO {

    @Override
    public List<CitaDoctorTabla> obtenerMisCitas(int doctorId) {

        List<CitaDoctorTabla> lista = new ArrayList<>();

        String sql = """
            SELECT
                c.id,
                p.nombre || ' ' || p.apellido AS paciente,
                c.fecha,
                c.hora,
                c.estado,
                COALESCE(s.nombre, 'Sin servicio') AS servicio,
                c.motivo_consulta,
                c.notas
            FROM citas c
            INNER JOIN pacientes p ON p.id = c.paciente_id
            LEFT JOIN servicios s ON s.id = c.servicio_id
            WHERE c.doctor_id = ?
            ORDER BY c.fecha DESC, c.hora DESC
        """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, doctorId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(
                            new CitaDoctorTabla(
                                    rs.getInt("id"),
                                    rs.getString("paciente"),
                                    rs.getDate("fecha").toLocalDate(),
                                    rs.getTime("hora").toLocalTime(),
                                    rs.getString("estado"),
                                    rs.getString("servicio"),
                                    rs.getString("motivo_consulta"),
                                    rs.getString("notas")
                            )
                    );
                }
            }

        } catch (Exception e) {
            pe.nuevasonrisa.util.AppLogger.error(getClass(), "Unhandled error while completing the operation.", e);
        }

        return lista;
    }

    @Override
    public List<CitaDoctorTabla> obtenerCitasHoy(int doctorId) {

        List<CitaDoctorTabla> lista = new ArrayList<>();

        String sql = """
            SELECT
                c.id,
                p.nombre || ' ' || p.apellido AS paciente,
                c.fecha,
                c.hora,
                c.estado,
                COALESCE(s.nombre, 'Sin servicio') AS servicio,
                c.motivo_consulta,
                c.notas
            FROM citas c
            INNER JOIN pacientes p ON p.id = c.paciente_id
            LEFT JOIN servicios s ON s.id = c.servicio_id
            WHERE c.doctor_id = ?
              AND c.fecha = CURRENT_DATE
            ORDER BY c.hora
        """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, doctorId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(
                            new CitaDoctorTabla(
                                    rs.getInt("id"),
                                    rs.getString("paciente"),
                                    rs.getDate("fecha").toLocalDate(),
                                    rs.getTime("hora").toLocalTime(),
                                    rs.getString("estado"),
                                    rs.getString("servicio"),
                                    rs.getString("motivo_consulta"),
                                    rs.getString("notas")
                            )
                    );
                }
            }

        } catch (Exception e) {
            pe.nuevasonrisa.util.AppLogger.error(getClass(), "Unhandled error while completing the operation.", e);
        }

        return lista;
    }

    @Override
    public boolean actualizarNotas(int citaId, int doctorId, String notas) {

        String sql = """
            UPDATE citas
            SET notas = ?
            WHERE id = ?
              AND doctor_id = ?
        """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, notas);
            ps.setInt(2, citaId);
            ps.setInt(3, doctorId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            pe.nuevasonrisa.util.AppLogger.error(getClass(), "Unhandled error while completing the operation.", e);
            return false;
        }
    }

    @Override
    public boolean finalizarCita(int citaId, int doctorId) {
        String sql = """
            UPDATE citas
            SET estado = 'Realizado'
            WHERE id = ?
              AND doctor_id = ?
              AND estado = 'En espera'
              AND btrim(COALESCE(notas, '')) <> ''
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, citaId);
            ps.setInt(2, doctorId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            pe.nuevasonrisa.util.AppLogger.error(getClass(), "Unhandled error while completing the operation.", e);
            return false;
        }
    }
}
