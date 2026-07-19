package pe.nuevasonrisa.dao.impl;

import pe.nuevasonrisa.config.DatabaseConnection;
import pe.nuevasonrisa.dao.UsuarioGestionDAO;
import pe.nuevasonrisa.model.UsuarioTabla;
import pe.nuevasonrisa.model.Usuario;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class UsuarioGestionDAOImpl implements UsuarioGestionDAO {

    @Override
    public List<UsuarioTabla> listarUsuarios() {

        List<UsuarioTabla> lista = new ArrayList<>();

        String sql = """
            SELECT
                u.id,
                u.usuario,
                u.nombre,
                u.apellido,
                r.nombre as rol,
                u.estado,
                u.dni,
                u.celular,
                u.email
            FROM usuarios u
            INNER JOIN roles r
            ON r.id = u.rol_id
            ORDER BY u.id
        """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                lista.add(
                        new UsuarioTabla(
                                rs.getInt("id"),
                                rs.getString("usuario"),
                                rs.getString("nombre"),
                                rs.getString("apellido"),
                                rs.getString("rol"),
                                rs.getString("estado"),
                                rs.getString("dni"),
                                rs.getString("celular"),
                                rs.getString("email")
                        )
                );
            }

        } catch (Exception e) {
            pe.nuevasonrisa.util.AppLogger.error(getClass(), "Unhandled error while completing the operation.", e);
        }

        return lista;
    }

    public boolean crearUsuario(Usuario usuario) {

        String sql = """
        INSERT INTO usuarios
        (
            usuario,
            password,
            nombre,
            apellido,
            dni,
            celular,
            email,
            estado,
            rol_id,
            servicio_id
        )
        VALUES
        (
            ?,
            ?,
            ?,
            ?,
            ?,
            ?,
            ?,
            'Activo',
            (SELECT id FROM roles WHERE nombre = ?),
            NULL
        )
    """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            String hash =
                    BCrypt.hashpw(
                            usuario.getPassword(),
                            BCrypt.gensalt()
                    );

            ps.setString(1, usuario.getUsuario());
            ps.setString(2, hash);
            ps.setString(3, usuario.getNombre());
            ps.setString(4, usuario.getApellido());
            ps.setString(5, usuario.getDni());
            ps.setString(6, usuario.getCelular());
            ps.setString(7, usuario.getEmail());
            ps.setString(8, usuario.getRol());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            pe.nuevasonrisa.util.AppLogger.error(getClass(), "Unhandled error while completing the operation.", e);
            return false;
        }
    }

    @Override
    public boolean actualizarUsuario(Usuario usuario) {
        String sql = """
        UPDATE usuarios
        SET
            usuario = ?,
            nombre = ?,
            apellido = ?,
            dni = ?,
            celular = ?,
            email = ?,
            rol_id = (SELECT id FROM roles WHERE nombre = ?)
        WHERE id = ?
    """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, usuario.getUsuario());
            ps.setString(2, usuario.getNombre());
            ps.setString(3, usuario.getApellido());
            ps.setString(4, usuario.getDni());
            ps.setString(5, usuario.getCelular());
            ps.setString(6, usuario.getEmail());
            ps.setString(7, usuario.getRol());
            ps.setInt(8, usuario.getId());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {

            if (e.getMessage() != null && e.getMessage().contains("usuarios_dni_key")) {
                pe.nuevasonrisa.util.AppLogger.getLogger(getClass()).warn("No se pudo crear el usuario porque el DNI ya está registrado.");
            } else if (e.getMessage() != null && e.getMessage().contains("usuarios_usuario_key")) {
                pe.nuevasonrisa.util.AppLogger.getLogger(getClass()).warn("No se pudo crear el usuario porque el nombre de usuario ya está registrado.");
            } else {
                pe.nuevasonrisa.util.AppLogger.error(getClass(), "Unhandled error while completing the operation.", e);
            }

            return false;
        }
    }

    public boolean cambiarEstadoUsuario(int idUsuario, String nuevoEstado) {
        String sql = """
        UPDATE usuarios
        SET estado = ?
        WHERE id = ?
    """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, nuevoEstado);
            ps.setInt(2, idUsuario);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            pe.nuevasonrisa.util.AppLogger.error(getClass(), "Unhandled error while completing the operation.", e);
            return false;
        }
    }
}
