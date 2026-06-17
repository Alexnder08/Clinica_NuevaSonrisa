package pe.nuevasonrisa.dao.impl;

import pe.nuevasonrisa.config.DatabaseConnection;
import pe.nuevasonrisa.dao.DashboardInicioDAO;
import pe.nuevasonrisa.model.Auditoria;
import pe.nuevasonrisa.model.DashboardResumen;
import pe.nuevasonrisa.model.HistorialAcceso;
import pe.nuevasonrisa.model.CitaHoy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DashboardInicioDAOImpl
        implements DashboardInicioDAO {

    @Override
    public DashboardResumen obtenerResumen() {

        int pacientes = 0;
        int pendientes = 0;
        int hoy = 0;
        int odontologos = 0;

        try (
                Connection conn =
                        DatabaseConnection.getConnection()
        ) {

            PreparedStatement ps1 =
                    conn.prepareStatement(
                            "SELECT COUNT(*) total FROM pacientes"
                    );

            ResultSet rs1 = ps1.executeQuery();

            if (rs1.next()) {
                pacientes = rs1.getInt("total");
            }

            PreparedStatement ps2 =
                    conn.prepareStatement(
                            """
                            SELECT COUNT(*) total
                            FROM citas
                            WHERE estado='Pendiente'
                            """
                    );

            ResultSet rs2 = ps2.executeQuery();

            if (rs2.next()) {
                pendientes = rs2.getInt("total");
            }

            PreparedStatement ps3 =
                    conn.prepareStatement(
                            """
                            SELECT COUNT(*) total
                            FROM citas
                            WHERE fecha = CURRENT_DATE
                            """
                    );

            ResultSet rs3 = ps3.executeQuery();

            if (rs3.next()) {
                hoy = rs3.getInt("total");
            }

            PreparedStatement ps4 =
                    conn.prepareStatement(
                            """
                            SELECT COUNT(*) total
                            FROM usuarios
                            WHERE rol_id =
                            (
                                SELECT id
                                FROM roles
                                WHERE nombre='Doctor'
                            )
                            """
                    );

            ResultSet rs4 = ps4.executeQuery();

            if (rs4.next()) {
                odontologos = rs4.getInt("total");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new DashboardResumen(
                pacientes,
                pendientes,
                hoy,
                odontologos
        );
    }

    @Override
    public List<Auditoria> obtenerUltimasAcciones() {

        List<Auditoria> lista =
                new ArrayList<>();

        String sql = """
        SELECT *
        FROM auditoria
        ORDER BY fecha DESC
        LIMIT 10
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
                        new Auditoria(
                                rs.getInt("id"),
                                rs.getString("usuario"),
                                rs.getString("accion"),
                                rs.getString("modulo"),
                                rs.getString("detalle"),
                                rs.getTimestamp("fecha")
                                        .toLocalDateTime()
                        )
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

    @Override
    public List<HistorialAcceso> obtenerUltimosAccesos() {

        List<HistorialAcceso> lista =
                new ArrayList<>();

        String sql = """
        SELECT *
        FROM historial_accesos
        ORDER BY fecha_acceso DESC
        LIMIT 10
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
            e.printStackTrace();
        }

        return lista;
    }


    @Override
    public List<CitaHoy> obtenerCitaHoy() {

        List<CitaHoy> lista = new ArrayList<>();

        String sql = """
        SELECT
            p.nombre || ' ' || p.apellido AS paciente,
            u.nombre || ' ' || u.apellido AS doctor,
            c.estado,
            c.hora
        FROM citas c
        INNER JOIN pacientes p ON p.id = c.paciente_id
        INNER JOIN usuarios u ON u.id = c.doctor_id
        WHERE c.fecha = CURRENT_DATE
        ORDER BY c.hora
        LIMIT 10
    """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                lista.add(
                        new CitaHoy(
                                rs.getString("paciente"),
                                rs.getString("doctor"),
                                rs.getString("estado"),
                                rs.getTime("hora").toLocalTime()
                        )
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

    @Override
    public int contarCitasHoy() {
        return contar("""
        SELECT COUNT(*) total
        FROM citas
        WHERE fecha = CURRENT_DATE
    """);
    }

    @Override
    public int contarPendientesHoy() {
        return contar("""
        SELECT COUNT(*) total
        FROM citas
        WHERE fecha = CURRENT_DATE
          AND estado = 'Pendiente'
    """);
    }

    @Override
    public int contarRealizadasHoy() {
        return contar("""
        SELECT COUNT(*) total
        FROM citas
        WHERE fecha = CURRENT_DATE
          AND estado = 'Realizado'
    """);
    }

    @Override
    public int contarCanceladasHoy() {
        return contar("""
        SELECT COUNT(*) total
        FROM citas
        WHERE fecha = CURRENT_DATE
          AND estado = 'Cancelado'
    """);
    }

    private int contar(String sql) {
        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {
            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

}