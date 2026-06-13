package pe.nuevasonrisa.dao.impl;

import pe.nuevasonrisa.config.DatabaseConnection;
import pe.nuevasonrisa.dao.CitaDAO;
import pe.nuevasonrisa.model.Cita;
import pe.nuevasonrisa.model.CitaTabla;

import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class CitaDAOImpl implements CitaDAO {

    @Override
    public List<CitaTabla> listarCitas(){
        List<CitaTabla> lista = new ArrayList<>();

        String actualizarNoAsistio = """
                                    UPDATE citas
                                    SET estado = 'No asistió'
                                    WHERE estado = 'Pendiente'
                                    AND fecha = CURRENT_DATE
                                    AND (
                                        hora + INTERVAL '2 hour'
                                    ) <= CURRENT_TIME
                                    """;

                                    try (
                                            Connection conn =
                                                    DatabaseConnection.getConnection()
                                    ) {

                                        PreparedStatement psUpdate =
                                                conn.prepareStatement(
                                                        actualizarNoAsistio
                                                );

                                        psUpdate.executeUpdate();

                                    } catch (Exception e) {
                                        e.printStackTrace();
        }

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
                ){
                    while (rs.next()){
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

                }catch (Exception e){
                    e.printStackTrace();
                }

                return lista;
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
    public boolean doctorDisponible(int doctorId, LocalDate fecha, LocalTime hora) {
        String sql = """
            SELECT COUNT(*) AS total
            FROM citas
            WHERE doctor_id = ?
              AND fecha = ?
              AND hora = ?
              AND estado NOT IN ('Cancelado', 'No asistió')
        """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, doctorId);
            ps.setDate(2, Date.valueOf(fecha));
            ps.setTime(3, Time.valueOf(hora));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total") == 0;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean pacienteDisponible(int pacienteId, LocalDate fecha, LocalTime hora) {
        String sql = """
            SELECT COUNT(*) AS total
            FROM citas
            WHERE paciente_id = ?
              AND fecha = ?
              AND hora = ?
              AND estado NOT IN ('Cancelado', 'No asistió')
        """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, pacienteId);
            ps.setDate(2, Date.valueOf(fecha));
            ps.setTime(3, Time.valueOf(hora));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total") == 0;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean dentroHorarioDoctor(int doctorId, LocalDate fecha, LocalTime hora) {
        int diaSemana = convertirDiaSemana(fecha.getDayOfWeek());

        String sql = """
            SELECT COUNT(*) AS total
            FROM horarios_doctor
            WHERE doctor_id = ?
              AND dia_semana = ?
              AND ? >= hora_inicio
              AND ? < hora_fin
        """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, doctorId);
            ps.setInt(2, diaSemana);
            ps.setTime(3, Time.valueOf(hora));
            ps.setTime(4, Time.valueOf(hora));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total") > 0;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
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
            e.printStackTrace();
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
            e.printStackTrace();
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