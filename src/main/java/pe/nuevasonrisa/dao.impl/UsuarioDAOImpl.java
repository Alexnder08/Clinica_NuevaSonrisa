package pe.nuevasonrisa.dao.impl;

import pe.nuevasonrisa.config.DatabaseConnection;
import pe.nuevasonrisa.dao.UsuarioDAO;
import pe.nuevasonrisa.model.Usuario;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;

public class UsuarioDAOImpl implements UsuarioDAO {

    @Override
    public Optional<Usuario> buscarPorUsuario(String usuario) {
        String sql = """
            SELECT 
                u.id,
                u.usuario,
                u.password,
                u.nombre,
                u.apellido,
                r.nombre AS rol,
                u.estado,
                u.dni,
                u.celular,
                COALESCE(s.nombre, 'Ninguno') AS servicio
            FROM usuarios u
            INNER JOIN roles r ON r.id = u.rol_id
            LEFT JOIN servicios s ON s.id = u.servicio_id
            WHERE u.usuario = ?
            LIMIT 1
        """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, usuario);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Usuario user = new Usuario(
                            rs.getInt("id"),
                            rs.getString("usuario"),
                            rs.getString("password"),
                            rs.getString("nombre"),
                            rs.getString("apellido"),
                            rs.getString("rol"),
                            rs.getString("estado"),
                            rs.getString("dni"),
                            rs.getString("celular"),
                            rs.getString("servicio")
                    );

                    return Optional.of(user);
                }
            }

        } catch (Exception e) {
            System.out.println("Error al buscar usuario: " + e.getMessage());
        }

        return Optional.empty();
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        String sql = """
            SELECT 
                u.id,
                u.usuario,
                u.password,
                u.nombre,
                u.apellido,
                r.nombre AS rol,
                u.estado,
                u.dni,
                u.celular,
                COALESCE(s.nombre, 'Ninguno') AS servicio
            FROM usuarios u
            INNER JOIN roles r ON r.id = u.rol_id
            LEFT JOIN servicios s ON s.id = u.servicio_id
            WHERE lower(u.email) = lower(?)
            LIMIT 1
        """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Usuario user = new Usuario(
                            rs.getInt("id"),
                            rs.getString("usuario"),
                            rs.getString("password"),
                            rs.getString("nombre"),
                            rs.getString("apellido"),
                            rs.getString("rol"),
                            rs.getString("estado"),
                            rs.getString("dni"),
                            rs.getString("celular"),
                            rs.getString("servicio")
                    );
                    return Optional.of(user);
                }
            }
        } catch (Exception e) {
            System.out.println("Error al buscar usuario por email: " + e.getMessage());
        }

        return Optional.empty();
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
            ps.setString(7, usuario.getRol());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean cambiarPassword(
            String usuario,
            String passwordActual,
            String passwordNueva
    ) {

        Optional<Usuario> encontrado =
                buscarPorUsuario(usuario);

        if (encontrado.isEmpty()) {
            return false;
        }

        Usuario user = encontrado.get();

        String hashActual = user.getPassword();

        if (hashActual.startsWith("$2b$") || hashActual.startsWith("$2y$")) {
            hashActual = "$2a$" + hashActual.substring(4);
        }

        boolean valida =
                BCrypt.checkpw(
                        passwordActual,
                        hashActual
                );

        if (!valida) {
            return false;
        }

        String nuevoHash =
                BCrypt.hashpw(
                        passwordNueva,
                        BCrypt.gensalt()
                );

        String sql = """
        UPDATE usuarios
        SET password = ?
        WHERE usuario = ?
    """;

        try (
                Connection conn =
                        DatabaseConnection.getConnection();

                PreparedStatement ps =
                        conn.prepareStatement(sql)
        ) {

            ps.setString(1, nuevoHash);
            ps.setString(2, usuario);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean actualizarPasswordPorId(int usuarioId, String passwordHash) {
        String sql = """
        UPDATE usuarios
        SET password = ?
        WHERE id = ?
    """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, passwordHash);
            ps.setInt(2, usuarioId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
