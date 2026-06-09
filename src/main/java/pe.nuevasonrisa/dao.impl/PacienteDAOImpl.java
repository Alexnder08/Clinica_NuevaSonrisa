package pe.nuevasonrisa.dao.impl;

import pe.nuevasonrisa.config.DatabaseConnection;
import pe.nuevasonrisa.dao.PacienteDAO;
import pe.nuevasonrisa.model.Paciente;
import pe.nuevasonrisa.model.PacienteTabla;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PacienteDAOImpl implements PacienteDAO {

    @Override
    public List<PacienteTabla> listarPacientes() {

        List<PacienteTabla> lista = new ArrayList<>();

        String sql = """
            SELECT
                id,
                dni,
                nombre,
                apellido,
                telefono,
                correo
            FROM pacientes
            ORDER BY id
        """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                lista.add(
                        new PacienteTabla(
                                rs.getInt("id"),
                                rs.getString("dni"),
                                rs.getString("nombre"),
                                rs.getString("apellido"),
                                rs.getString("telefono"),
                                rs.getString("correo")
                        )
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

    public boolean crearPaciente(Paciente paciente) {

        String sql = """
        INSERT INTO pacientes
        (
            dni,
            nombre,
            apellido,
            telefono,
            correo
        )
        VALUES (?, ?, ?, ?, ?)
    """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, paciente.getDni());
            ps.setString(2, paciente.getNombre());
            ps.setString(3, paciente.getApellido());
            ps.setString(4, paciente.getTelefono());
            ps.setString(5, paciente.getCorreo());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean actualizarPaciente(Paciente paciente) {

        String sql = """
        UPDATE pacientes
        SET
            dni = ?,
            nombre = ?,
            apellido = ?,
            telefono = ?,
            correo = ?
        WHERE id = ?
    """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, paciente.getDni());
            ps.setString(2, paciente.getNombre());
            ps.setString(3, paciente.getApellido());
            ps.setString(4, paciente.getTelefono());
            ps.setString(5, paciente.getCorreo());
            ps.setInt(6, paciente.getId());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}