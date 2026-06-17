package pe.nuevasonrisa.dao.impl;

import pe.nuevasonrisa.config.DatabaseConnection;
import pe.nuevasonrisa.dao.RecuperacionPasswordDAO;
import pe.nuevasonrisa.model.PasswordResetToken;
import pe.nuevasonrisa.model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

public class RecuperacionPasswordDAOImpl implements RecuperacionPasswordDAO {

    private final UsuarioDAOImpl usuarioDAO = new UsuarioDAOImpl();

    @Override
    public Optional<Usuario> buscarUsuarioPorEmail(String email) {
        return usuarioDAO.buscarPorEmail(email);
    }

    @Override
    public boolean guardarToken(int usuarioId, String tokenHash, LocalDateTime expiresAt) {
        String sql = """
            INSERT INTO password_reset_tokens (
                usuario_id,
                token_hash,
                expires_at
            )
            VALUES (?, ?, ?)
        """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, usuarioId);
            ps.setString(2, tokenHash);
            ps.setTimestamp(3, Timestamp.valueOf(expiresAt));
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Optional<PasswordResetToken> obtenerTokenActivo(int usuarioId) {
        String sql = """
            SELECT id, usuario_id, token_hash, expires_at, used_at, created_at
            FROM password_reset_tokens
            WHERE usuario_id = ?
              AND used_at IS NULL
            ORDER BY created_at DESC
            LIMIT 1
        """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, usuarioId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new PasswordResetToken(
                            rs.getInt("id"),
                            rs.getInt("usuario_id"),
                            rs.getString("token_hash"),
                            rs.getTimestamp("expires_at").toLocalDateTime(),
                            rs.getTimestamp("used_at") == null ? null : rs.getTimestamp("used_at").toLocalDateTime(),
                            rs.getTimestamp("created_at").toLocalDateTime()
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public boolean marcarTokenUsado(int tokenId, LocalDateTime usedAt) {
        String sql = """
            UPDATE password_reset_tokens
            SET used_at = ?
            WHERE id = ?
        """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setTimestamp(1, Timestamp.valueOf(usedAt));
            ps.setInt(2, tokenId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean actualizarPassword(int usuarioId, String passwordHash) {
        return usuarioDAO.actualizarPasswordPorId(usuarioId, passwordHash);
    }

    @Override
    public int limpiarTokensExpirados(LocalDateTime ahora) {
        String sql = """
            DELETE FROM password_reset_tokens
            WHERE expires_at < ?
               OR used_at IS NOT NULL
        """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setTimestamp(1, Timestamp.valueOf(ahora));
            return ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
