package pe.nuevasonrisa.dao.impl;

import pe.nuevasonrisa.config.DatabaseConnection;
import pe.nuevasonrisa.dao.ReporteDAO;
import pe.nuevasonrisa.model.ReporteCitasDoctor;
import pe.nuevasonrisa.model.ReporteEstado;
import pe.nuevasonrisa.model.ReporteServicio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ReporteDAOImpl implements ReporteDAO {

    @Override
    public List<ReporteCitasDoctor> reporteCitasDoctor() {

        List<ReporteCitasDoctor> lista = new ArrayList<>();

        String sql = """
            SELECT *
            FROM vw_reporte_citas_doctor
            ORDER BY total_citas DESC
        """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                lista.add(
                        new ReporteCitasDoctor(
                                rs.getInt("doctor_id"),
                                rs.getString("doctor"),
                                rs.getInt("total_citas"),
                                rs.getInt("realizadas"),
                                rs.getInt("canceladas"),
                                rs.getInt("no_asistio")
                        )
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

    @Override
    public List<ReporteServicio> reporteServicios() {

        List<ReporteServicio> lista = new ArrayList<>();

        String sql = """
            SELECT *
            FROM vw_reporte_servicios
            ORDER BY total_citas DESC
        """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                lista.add(
                        new ReporteServicio(
                                rs.getInt("servicio_id"),
                                rs.getString("servicio"),
                                rs.getInt("total_citas"),
                                rs.getInt("realizadas"),
                                rs.getInt("canceladas")
                        )
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

    @Override
    public List<ReporteEstado> reporteEstados() {

        List<ReporteEstado> lista = new ArrayList<>();

        String sql = """
            SELECT *
            FROM vw_reporte_estados
            ORDER BY total DESC
        """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                lista.add(
                        new ReporteEstado(
                                rs.getString("estado"),
                                rs.getInt("total")
                        )
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }
}