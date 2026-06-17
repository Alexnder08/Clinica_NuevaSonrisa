package pe.nuevasonrisa.service;

import org.mindrot.jbcrypt.BCrypt;
import pe.nuevasonrisa.dao.RecuperacionPasswordDAO;
import pe.nuevasonrisa.model.PasswordResetToken;
import pe.nuevasonrisa.model.Usuario;
import pe.nuevasonrisa.util.FeatureFlags;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

public class PasswordRecoveryService {

    private final RecuperacionPasswordDAO dao;
    private final CorreoService correoService;
    private final SecureRandom secureRandom = new SecureRandom();

    public PasswordRecoveryService(RecuperacionPasswordDAO dao, CorreoService correoService) {
        this.dao = dao;
        this.correoService = correoService;
    }

    public String solicitarRestablecimiento(String email) {
        if (!FeatureFlags.emailFeaturesEnabled()) {
            return "La recuperación por correo está preparada en código, pero aún no está activada.";
        }

        if (email == null || email.isBlank()) {
            return "Ingrese el correo registrado.";
        }

        Optional<Usuario> usuarioOpt = dao.buscarUsuarioPorEmail(email.trim());
        if (usuarioOpt.isEmpty()) {
            return "No existe un usuario con ese correo.";
        }

        Usuario usuario = usuarioOpt.get();
        String token = generarToken();
        String tokenHash = BCrypt.hashpw(token, BCrypt.gensalt());
        LocalDateTime expira = LocalDateTime.now().plusMinutes(30);

        if (!dao.guardarToken(usuario.getId(), tokenHash, expira)) {
            return "No se pudo generar el código de recuperación.";
        }

        String asunto = "Recuperación de contraseña - Nueva Sonrisa";
        String cuerpo = """
                Hola %s,

                Se solicitó un restablecimiento de contraseña para tu usuario %s.

                Código de recuperación: %s
                Vence: %s

                Si no solicitaste este cambio, ignora este mensaje.
                """.formatted(
                usuario.getNombreCompleto(),
                usuario.getUsuario(),
                token,
                expira
        );

        if (!correoService.enviarCorreo(usuario.getEmail(), asunto, cuerpo)) {
            return "Se generó el código, pero no se pudo enviar el correo. Revise la configuración SMTP.";
        }

        return "Se envió el código de recuperación al correo registrado.";
    }

    public String restablecerConCodigo(String email, String codigo, String nuevaContrasena, String confirmarContrasena) {
        if (!FeatureFlags.emailFeaturesEnabled()) {
            return "La recuperación por correo está preparada en código, pero aún no está activada.";
        }

        if (email == null || email.isBlank()) {
            return "Ingrese el correo registrado.";
        }

        if (codigo == null || codigo.isBlank()) {
            return "Ingrese el código recibido por correo.";
        }

        if (nuevaContrasena == null || nuevaContrasena.isBlank()) {
            return "Ingrese la nueva contraseña.";
        }

        if (confirmarContrasena == null || confirmarContrasena.isBlank()) {
            return "Confirme la nueva contraseña.";
        }

        if (nuevaContrasena.length() < 6) {
            return "La nueva contraseña debe tener mínimo 6 caracteres.";
        }

        if (!nuevaContrasena.equals(confirmarContrasena)) {
            return "La nueva contraseña y su confirmación no coinciden.";
        }

        Optional<Usuario> usuarioOpt = dao.buscarUsuarioPorEmail(email.trim());
        if (usuarioOpt.isEmpty()) {
            return "No existe un usuario con ese correo.";
        }

        Usuario usuario = usuarioOpt.get();
        Optional<PasswordResetToken> tokenOpt = dao.obtenerTokenActivo(usuario.getId());
        if (tokenOpt.isEmpty()) {
            return "No hay un código de recuperación activo para este usuario.";
        }

        PasswordResetToken token = tokenOpt.get();
        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            return "El código de recuperación expiró.";
        }

        if (!BCrypt.checkpw(codigo, token.getTokenHash())) {
            return "El código de recuperación es incorrecto.";
        }

        String nuevoHash = BCrypt.hashpw(nuevaContrasena, BCrypt.gensalt());
        if (!dao.actualizarPassword(usuario.getId(), nuevoHash)) {
            return "No se pudo actualizar la contraseña.";
        }

        dao.marcarTokenUsado(token.getId(), LocalDateTime.now());
        dao.limpiarTokensExpirados(LocalDateTime.now());
        return "Contraseña actualizada correctamente.";
    }

    private String generarToken() {
        byte[] buffer = new byte[9];
        secureRandom.nextBytes(buffer);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(buffer);
    }
}
