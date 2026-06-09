package pe.nuevasonrisa.dao.impl;

import pe.nuevasonrisa.config.DatabaseConnection;
import pe.nuevasonrisa.dao.AuditoriaDAO;
import pe.nuevasonrisa.model.Auditoria;

import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.ArrayList;

public class AuditoriaDAOImpl implements AuditoriaDAO {

    @Override
    public void registrar(String usuario, String accion, String modulo, String detalle) {

        String sql = """
            INSERT INTO auditoria
            (
                usuario,
                accion,
                modulo,
                detalle
            )
            VALUES (?, ?, ?, ?)
        """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, usuario);
            ps.setString(2, accion);
            ps.setString(3, modulo);
            ps.setString(4, detalle);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public List<Auditoria> listar() {

        List<Auditoria> lista =
                new ArrayList<>();

        String sql = """
        SELECT *
        FROM auditoria
        ORDER BY fecha DESC
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
}