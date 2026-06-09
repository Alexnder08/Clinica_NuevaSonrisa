package pe.nuevasonrisa.dao.impl;

import pe.nuevasonrisa.config.DatabaseConnection;
import pe.nuevasonrisa.dao.ServicioDAO;
import pe.nuevasonrisa.model.Servicio;
import pe.nuevasonrisa.model.ServicioTabla;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServicioDAOImpl implements ServicioDAO {

    @Override
    public List<ServicioTabla> listarServicios() {

        List<ServicioTabla> lista = new ArrayList<>();

        String sql = """
            SELECT
                id,
                nombre,
                duracion,
                costo
            FROM servicios
            ORDER BY id
        """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                lista.add(
                        new ServicioTabla(
                                rs.getInt("id"),
                                rs.getString("nombre"),
                                rs.getInt("duracion"),
                                rs.getBigDecimal("costo")
                        )
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

    @Override
    public boolean crearServicio(Servicio servicio) {
        String sql = """
        INSERT INTO servicios(nombre, duracion, costo)
        VALUES (?, ?, ?)
    """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, servicio.getNombre());
            ps.setInt(2, servicio.getDuracion());
            ps.setBigDecimal(3, servicio.getCosto());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean actualizarServicio(Servicio servicio) {
        String sql = """
        UPDATE servicios
        SET nombre = ?,
            duracion = ?,
            costo = ?
        WHERE id = ?
    """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, servicio.getNombre());
            ps.setInt(2, servicio.getDuracion());
            ps.setBigDecimal(3, servicio.getCosto());
            ps.setInt(4, servicio.getId());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    }
