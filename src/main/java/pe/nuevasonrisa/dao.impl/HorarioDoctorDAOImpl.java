package pe.nuevasonrisa.dao.impl;

import pe.nuevasonrisa.config.DatabaseConnection;
import pe.nuevasonrisa.dao.HorarioDoctorDAO;
import pe.nuevasonrisa.model.HorarioDoctorTabla;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class HorarioDoctorDAOImpl implements HorarioDoctorDAO {

    @Override
    public List<HorarioDoctorTabla> listarPorDoctor(int doctorId) {
        List<HorarioDoctorTabla> lista = new ArrayList<>();

        String sql = """
            SELECT
                h.id,
                h.doctor_id,
                u.nombre,
                u.apellido,
                h.dia_semana,
                h.hora_inicio,
                h.hora_fin
            FROM horarios_doctor h
            INNER JOIN usuarios u
                ON u.id = h.doctor_id
            WHERE h.doctor_id = ?
            ORDER BY h.dia_semana
        """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, doctorId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(
                            new HorarioDoctorTabla(
                                    rs.getInt("id"),
                                    rs.getInt("doctor_id"),
                                    rs.getString("nombre") + " " + rs.getString("apellido"),
                                    obtenerDia(rs.getInt("dia_semana")),
                                    rs.getTime("hora_inicio").toLocalTime(),
                                    rs.getTime("hora_fin").toLocalTime()
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
    public boolean actualizarHorario(int id, int diaSemana, String horaInicio, String horaFin) {
        String sql = """
        UPDATE horarios_doctor
        SET dia_semana = ?,
            hora_inicio = ?::time,
            hora_fin = ?::time
        WHERE id = ?
    """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, diaSemana);
            ps.setString(2, horaInicio);
            ps.setString(3, horaFin);
            ps.setInt(4, id);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            pe.nuevasonrisa.util.AppLogger.error(getClass(), "Unhandled error while completing the operation.", e);
            return false;
        }
    }

    @Override
    public boolean eliminarHorario(int id) {
        String sql = "DELETE FROM horarios_doctor WHERE id = ?";

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            pe.nuevasonrisa.util.AppLogger.error(getClass(), "Unhandled error while completing the operation.", e);
            return false;
        }
    }

    @Override
    public boolean crearHorario(int doctorId, int diaSemana, String horaInicio, String horaFin) {
        String sql = """
        INSERT INTO horarios_doctor
        (
            doctor_id,
            dia_semana,
            hora_inicio,
            hora_fin
        )
        VALUES (?, ?, ?::time, ?::time)
    """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, doctorId);
            ps.setInt(2, diaSemana);
            ps.setString(3, horaInicio);
            ps.setString(4, horaFin);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            pe.nuevasonrisa.util.AppLogger.error(getClass(), "Unhandled error while completing the operation.", e);
            return false;
        }
    }

    @Override
    public boolean existeCruceHorario(int doctorId, int diaSemana, String horaInicio, String horaFin) {
        String sql = """
        SELECT COUNT(*) AS total
        FROM horarios_doctor
        WHERE doctor_id = ?
          AND dia_semana = ?
          AND (?::time < hora_fin)
          AND (?::time > hora_inicio)
    """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, doctorId);
            ps.setInt(2, diaSemana);
            ps.setString(3, horaInicio);
            ps.setString(4, horaFin);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total") > 0;
                }
            }

        } catch (Exception e) {
            pe.nuevasonrisa.util.AppLogger.error(getClass(), "Unhandled error while completing the operation.", e);
        }

        return true;
    }

    private String obtenerDia(int dia) {
        return switch (dia) {
            case 1 -> "Lunes";
            case 2 -> "Martes";
            case 3 -> "Miércoles";
            case 4 -> "Jueves";
            case 5 -> "Viernes";
            case 6 -> "Sábado";
            case 7 -> "Domingo";
            default -> "N/A";
        };
    }
}
