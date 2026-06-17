package pe.nuevasonrisa.dao.impl;

import pe.nuevasonrisa.config.DatabaseConnection;
import pe.nuevasonrisa.dao.OdontologoDAO;
import pe.nuevasonrisa.model.OdontologoTabla;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class OdontologoDAOImpl implements OdontologoDAO {

    @Override
    public List<OdontologoTabla> listarOdontologos() {

        List<OdontologoTabla> lista = new ArrayList<>();

        String sql = """
            SELECT
                u.id,
                u.usuario,
                u.nombre,
                u.apellido,
                u.dni,
                u.celular,
                COALESCE(s.nombre,'Sin asignar') servicio,
                u.estado
            FROM usuarios u
            INNER JOIN roles r
                ON r.id = u.rol_id
            LEFT JOIN servicios s
                ON s.id = u.servicio_id
            WHERE r.nombre = 'Doctor'
            ORDER BY u.id
        """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                lista.add(
                        new OdontologoTabla(
                                rs.getInt("id"),
                                rs.getString("usuario"),
                                rs.getString("nombre"),
                                rs.getString("apellido"),
                                rs.getString("dni"),
                                rs.getString("celular"),
                                rs.getString("servicio"),
                                rs.getString("estado")
                        )
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }
}