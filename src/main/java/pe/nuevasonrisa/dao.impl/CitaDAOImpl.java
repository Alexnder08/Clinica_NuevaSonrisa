package pe.nuevasonrisa.dao.impl;

import pe.nuevasonrisa.config.DatabaseConnection;
import pe.nuevasonrisa.dao.CitaDAO;
import pe.nuevasonrisa.model.Cita;
import pe.nuevasonrisa.model.CitaTabla;
import pe.nuevasonrisa.model.RecordatorioCita;

import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class CitaDAOImpl implements CitaDAO {

    @Override
    public List<CitaTabla> listarCitas() {
        List<CitaTabla> lista = new ArrayList<>();
        marcarPendientesVencidasComoNoAsistio();

        String sql = """
            SELECT
                c.id,
                c.paciente_id,
                c.doctor_id,
                c.servicio_id,
                p.nombre || ' ' || p.apellido AS paciente,
                u.nombre || ' ' || u.apellido AS doctor,
                s.nombre AS servicio,
                c.fecha,
                c.hora,
                c.estado,
                c.motivo_consulta,
                c.notas
            FROM citas c
            INNER JOIN pacientes p ON p.id = c.paciente_id
            INNER JOIN usuarios u ON u.id = c.doctor_id
            INNER JOIN servicios s ON s.id = c.servicio_id
            ORDER BY c.fecha DESC, c.hora DESC
        """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                lista.add(new CitaTabla(
                        rs.getInt("id"),
                        rs.getInt("paciente_id"),
                        rs.getInt("doctor_id"),
                        rs.getInt("servicio_id"),
                        rs.getString("paciente"),
                        rs.getString("doctor"),
                        rs.getString("servicio"),
                        rs.getDate("fecha").toLocalDate(),
                        rs.getTime("hora").toLocalTime(),
                        rs.getString("estado"),
                        rs.getString("motivo_consulta"),
                        rs.getString("notas")
                ));
            }
        } catch (Exception e) {
            pe.nuevasonrisa.util.AppLogger.error(getClass(), "Unhandled error while completing the operation.", e);
        }

        return lista;
    }

    @Override
    public int marcarPendientesVencidasComoNoAsistio() {
        String sql = """
            UPDATE citas
            SET estado = 'No asistió'
            WHERE estado = 'Pendiente'
              AND (
                    fecha < CURRENT_DATE
                    OR (
                        fecha = CURRENT_DATE
                        AND (hora + INTERVAL '2 hour') <= CURRENT_TIME
                    )
              )
        """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            return ps.executeUpdate();
        } catch (Exception e) {
            pe.nuevasonrisa.util.AppLogger.error(getClass(), "Unhandled error while completing the operation.", e);
            return 0;
        }
    }

    @Override
    public boolean crearCita(Cita cita) {
        String sql = """
        INSERT INTO citas (
            paciente_id,
            doctor_id,
            servicio_id,
            fecha,
            hora,
            duracion,
            estado,
            motivo_consulta,
            notas
        )
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
    """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, cita.getPacienteId());
            ps.setInt(2, cita.getDoctorId());
            ps.setInt(3, cita.getServicioId());
            ps.setDate(4, Date.valueOf(cita.getFecha()));
            ps.setTime(5, Time.valueOf(cita.getHora()));
            ps.setInt(6, cita.getDuracion());
            ps.setString(7, "Pendiente");
            ps.setString(8, cita.getMotivoConsulta());
            ps.setString(9, cita.getNotas());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {

            if ("23505".equals(e.getSQLState())) {
                throw new RuntimeException(
                        "El paciente ya tiene una cita registrada para esa fecha y hora."
                );
            }

            throw new RuntimeException(e);
        }
    }
    @Override
    public boolean doctorDisponible(int doctorId, LocalDate fecha, LocalTime hora, int duracionMinutos, Integer citaIdExcluir) {
        String sql = """
            SELECT COUNT(*) AS total
            FROM citas
            WHERE doctor_id = ?
              AND fecha = ?
              AND estado NOT IN ('Cancelado', 'No asistió')
              AND (? IS NULL OR id <> ?)
              AND hora < (CAST(? AS time) + make_interval(mins => ?))
              AND (hora + make_interval(mins => COALESCE(duracion, 1))) > CAST(? AS time)
        """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, doctorId);
            ps.setDate(2, Date.valueOf(fecha));
            if (citaIdExcluir == null) {
                ps.setNull(3, Types.INTEGER);
                ps.setNull(4, Types.INTEGER);
            } else {
                ps.setInt(3, citaIdExcluir);
                ps.setInt(4, citaIdExcluir);
            }
            ps.setObject(5, hora, Types.TIME);
            ps.setInt(6, duracionMinutos);
            ps.setObject(7, hora, Types.TIME);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total") == 0;
                }
            }

        } catch (Exception e) {
            throw new IllegalStateException("No se pudo verificar la disponibilidad del odontologo.", e);
        }
        throw new IllegalStateException("La consulta de disponibilidad del odontologo no devolvio resultados.");
    }

    @Override
    public boolean pacienteDisponible(int pacienteId, LocalDate fecha, LocalTime hora, int duracionMinutos, Integer citaIdExcluir) {
        String sql = """
            SELECT COUNT(*) AS total
            FROM citas
            WHERE paciente_id = ?
              AND fecha = ?
              AND estado NOT IN ('Cancelado', 'No asistió')
              AND (? IS NULL OR id <> ?)
              AND hora < (CAST(? AS time) + make_interval(mins => ?))
              AND (hora + make_interval(mins => COALESCE(duracion, 1))) > CAST(? AS time)
        """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, pacienteId);
            ps.setDate(2, Date.valueOf(fecha));
            if (citaIdExcluir == null) {
                ps.setNull(3, Types.INTEGER);
                ps.setNull(4, Types.INTEGER);
            } else {
                ps.setInt(3, citaIdExcluir);
                ps.setInt(4, citaIdExcluir);
            }
            ps.setObject(5, hora, Types.TIME);
            ps.setInt(6, duracionMinutos);
            ps.setObject(7, hora, Types.TIME);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total") == 0;
                }
            }

        } catch (Exception e) {
            throw new IllegalStateException("No se pudo verificar la disponibilidad del paciente.", e);
        }
        throw new IllegalStateException("La consulta de disponibilidad del paciente no devolvio resultados.");
    }

    @Override
    public boolean dentroHorarioDoctor(int doctorId, LocalDate fecha, LocalTime hora, int duracionMinutos) {
        int diaSemana = convertirDiaSemana(fecha.getDayOfWeek());
        LocalTime horaFin = hora.plusMinutes(duracionMinutos);

        String sql = """
            SELECT COUNT(*) AS total
            FROM horarios_doctor
            WHERE doctor_id = ?
              AND dia_semana = ?
              AND ? >= hora_inicio
              AND ? <= hora_fin
        """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, doctorId);
            ps.setInt(2, diaSemana);
            ps.setObject(3, hora, Types.TIME);
            ps.setObject(4, horaFin, Types.TIME);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total") > 0;
                }
            }

        } catch (Exception e) {
            throw new IllegalStateException("No se pudo verificar el horario del odontologo.", e);
        }
        throw new IllegalStateException("La consulta de horario del odontologo no devolvio resultados.");
    }

    @Override
    public List<LocalTime> listarHorasDisponibles(
            int pacienteId,
            int doctorId,
            LocalDate fecha,
            int duracionMinutos
    ) {
        List<LocalTime> horas = new ArrayList<>();
        int diaSemana = convertirDiaSemana(fecha.getDayOfWeek());
        String sql = """
            WITH slots AS (
                SELECT generate_series(
                    ?::date + time '08:00',
                    ?::date + time '18:00' - make_interval(mins => ?),
                    interval '1 hour'
                ) AS inicio
            )
            SELECT inicio::time AS hora
            FROM slots
            WHERE EXISTS (
                SELECT 1
                FROM horarios_doctor h
                WHERE h.doctor_id = ?
                  AND h.dia_semana = ?
                  AND inicio::time >= h.hora_inicio
                  AND (inicio + make_interval(mins => ?))::time <= h.hora_fin
            )
              AND NOT EXISTS (
                SELECT 1
                FROM citas c
                WHERE c.doctor_id = ?
                  AND c.fecha = ?
                  AND lower(c.estado) <> lower('Cancelado')
                  AND lower(c.estado) NOT LIKE 'no asist%'
                  AND c.hora < (inicio + make_interval(mins => ?))::time
                  AND (c.hora + make_interval(mins => COALESCE(c.duracion, 1))) > inicio::time
            )
              AND NOT EXISTS (
                SELECT 1
                FROM citas c
                WHERE c.paciente_id = ?
                  AND c.fecha = ?
                  AND lower(c.estado) <> lower('Cancelado')
                  AND lower(c.estado) NOT LIKE 'no asist%'
                  AND c.hora < (inicio + make_interval(mins => ?))::time
                  AND (c.hora + make_interval(mins => COALESCE(c.duracion, 1))) > inicio::time
            )
            ORDER BY inicio
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fecha));
            ps.setDate(2, Date.valueOf(fecha));
            ps.setInt(3, duracionMinutos);
            ps.setInt(4, doctorId);
            ps.setInt(5, diaSemana);
            ps.setInt(6, duracionMinutos);
            ps.setInt(7, doctorId);
            ps.setDate(8, Date.valueOf(fecha));
            ps.setInt(9, duracionMinutos);
            ps.setInt(10, pacienteId);
            ps.setDate(11, Date.valueOf(fecha));
            ps.setInt(12, duracionMinutos);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    horas.add(rs.getTime("hora").toLocalTime());
                }
            }
        } catch (Exception e) {
            pe.nuevasonrisa.util.AppLogger.error(getClass(), "Could not calculate available appointment times.", e);
        }
        return horas;
    }

    @Override
    public boolean cambiarEstado(
            int citaId,
            String estado,
            String motivoCancelacion
    ) {

        String sql = """
        UPDATE citas
        SET estado = ?,
            motivo_cancelacion = ?
        WHERE id = ?
    """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, estado);
            ps.setString(2, motivoCancelacion);
            ps.setInt(3, citaId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            pe.nuevasonrisa.util.AppLogger.error(getClass(), "Unhandled error while completing the operation.", e);
            return false;
        }
    }

    @Override
    public boolean actualizarCita(Cita cita) {

        String sql = """
        UPDATE citas
        SET
            paciente_id = ?,
            doctor_id = ?,
            servicio_id = ?,
            fecha = ?,
            hora = ?,
            duracion = ?,
            estado = ?,
            motivo_consulta = ?,
            notas = ?
        WHERE id = ?
    """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, cita.getPacienteId());
            ps.setInt(2, cita.getDoctorId());
            ps.setInt(3, cita.getServicioId());
            ps.setDate(4, Date.valueOf(cita.getFecha()));
            ps.setTime(5, Time.valueOf(cita.getHora()));
            ps.setInt(6, cita.getDuracion());
            ps.setString(7, cita.getEstado());
            ps.setString(8, cita.getMotivoConsulta());
            ps.setString(9, cita.getNotas());
            ps.setInt(10, cita.getId());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            pe.nuevasonrisa.util.AppLogger.error(getClass(), "Unhandled error while completing the operation.", e);
            return false;
        }
    }

    @Override
    public List<RecordatorioCita> listarCitasPendientesParaRecordatorio(LocalDate desde, LocalDate hasta) {
        List<RecordatorioCita> lista = new ArrayList<>();

        String sql = """
            SELECT
                c.id,
                p.nombre || ' ' || p.apellido AS paciente,
                p.correo,
                u.nombre || ' ' || u.apellido AS doctor,
                COALESCE(s.nombre, 'Sin servicio') AS servicio,
                c.fecha,
                c.hora
            FROM citas c
            INNER JOIN pacientes p ON p.id = c.paciente_id
            INNER JOIN usuarios u ON u.id = c.doctor_id
            LEFT JOIN servicios s ON s.id = c.servicio_id
            WHERE c.estado = 'Pendiente'
              AND c.recordatorio_enviado_at IS NULL
              AND c.fecha BETWEEN ? AND ?
              AND p.correo IS NOT NULL
              AND btrim(p.correo) <> ''
            ORDER BY c.fecha, c.hora
        """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setDate(1, Date.valueOf(desde));
            ps.setDate(2, Date.valueOf(hasta));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new RecordatorioCita(
                            rs.getInt("id"),
                            rs.getString("paciente"),
                            rs.getString("correo"),
                            rs.getString("doctor"),
                            rs.getString("servicio"),
                            rs.getDate("fecha").toLocalDate(),
                            rs.getTime("hora").toLocalTime()
                    ));
                }
            }
        } catch (Exception e) {
            pe.nuevasonrisa.util.AppLogger.error(getClass(), "Unhandled error while completing the operation.", e);
        }

        return lista;
    }

    @Override
    public boolean marcarRecordatorioEnviado(int citaId, LocalDateTime enviadoEn) {
        String sql = """
            UPDATE citas
            SET recordatorio_enviado_at = ?
            WHERE id = ?
        """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setTimestamp(1, Timestamp.valueOf(enviadoEn));
            ps.setInt(2, citaId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            pe.nuevasonrisa.util.AppLogger.error(getClass(), "Unhandled error while completing the operation.", e);
            return false;
        }
    }


    private int convertirDiaSemana(DayOfWeek day) {
        return switch (day) {
            case MONDAY -> 1;
            case TUESDAY -> 2;
            case WEDNESDAY -> 3;
            case THURSDAY -> 4;
            case FRIDAY -> 5;
            case SATURDAY -> 6;
            case SUNDAY -> 7;
        };
    }
}
