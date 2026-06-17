package pe.nuevasonrisa.dao;

import pe.nuevasonrisa.model.PasswordResetToken;
import pe.nuevasonrisa.model.Usuario;

import java.time.LocalDateTime;
import java.util.Optional;

public interface RecuperacionPasswordDAO {

    Optional<Usuario> buscarUsuarioPorEmail(String email);

    boolean guardarToken(int usuarioId, String tokenHash, LocalDateTime expiresAt);

    Optional<PasswordResetToken> obtenerTokenActivo(int usuarioId);

    boolean marcarTokenUsado(int tokenId, LocalDateTime usedAt);

    boolean actualizarPassword(int usuarioId, String passwordHash);

    int limpiarTokensExpirados(LocalDateTime ahora);
}
