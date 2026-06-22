package pe.nuevasonrisa.service;

import org.mindrot.jbcrypt.BCrypt;
import pe.nuevasonrisa.dao.RecuperacionPasswordDAO;
import pe.nuevasonrisa.model.PasswordResetToken;
import pe.nuevasonrisa.model.Usuario;
import pe.nuevasonrisa.util.FeatureFlags;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class PasswordRecoveryService {

    private static final DateTimeFormatter FORMATO_VENCIMIENTO =
            DateTimeFormatter.ofPattern("dd/MM/yyyy 'a las' HH:mm");

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
        String html = construirCorreoRecuperacion(usuario, token, expira);

        if (!correoService.enviarCorreoHtml(usuario.getEmail(), asunto, html, null)) {
            String detalle = correoService.getUltimoError();
            String sufijo = detalle == null || detalle.isBlank() ? "" : " Detalle: " + detalle;
            return "Se generó el código, pero " + correoService.getProveedorUltimoIntento()
                    + " no pudo enviar el correo." + sufijo;
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
        return "%06d".formatted(secureRandom.nextInt(1_000_000));
    }

    static String construirCorreoRecuperacion(Usuario usuario, String token, LocalDateTime expira) {
        return """
                <!doctype html>
                <html lang="es">
                  <body style="margin:0;padding:0;background:#f1f5f9;font-family:Arial,Helvetica,sans-serif;color:#0f172a">
                    <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="background:#f1f5f9">
                      <tr><td align="center" style="padding:32px 14px">
                        <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="max-width:600px;background:#ffffff;border:1px solid #e2e8f0;border-radius:18px;overflow:hidden">
                          <tr><td style="padding:26px 30px;background:#1f4e78;color:#ffffff">
                            <div style="font-size:20px;font-weight:700">Nueva Sonrisa</div>
                            <div style="margin-top:7px;font-size:13px;opacity:.9">Seguridad de tu cuenta</div>
                          </td></tr>
                          <tr><td style="padding:32px 30px">
                            <h1 style="margin:0 0 16px;font-size:25px">Recupera tu contraseña</h1>
                            <p style="margin:0 0 10px;font-size:16px;line-height:1.6">Hola <strong>%s</strong>,</p>
                            <p style="margin:0 0 22px;color:#475569;font-size:15px;line-height:1.7">Recibimos una solicitud para restablecer la contraseña del usuario <strong>%s</strong>.</p>
                            <div style="padding:22px;text-align:center;background:#eff6ff;border:1px solid #bfdbfe;border-radius:13px">
                              <div style="color:#64748b;font-size:12px;font-weight:700;letter-spacing:1px;text-transform:uppercase">Código de recuperación</div>
                              <div style="margin:12px 0;font-family:Consolas,monospace;font-size:30px;font-weight:700;letter-spacing:4px;color:#1f4e78">%s</div>
                              <div style="color:#475569;font-size:13px">Válido hasta el %s</div>
                            </div>
                            <p style="margin:22px 0 0;color:#475569;font-size:14px;line-height:1.7">Ingresa este código en la aplicación. Si no solicitaste el cambio, ignora este mensaje y tu contraseña permanecerá igual.</p>
                          </td></tr>
                          <tr><td style="padding:18px 30px;background:#f8fafc;border-top:1px solid #e2e8f0;text-align:center;color:#64748b;font-size:12px">Este es un mensaje automático. No compartas este código.</td></tr>
                        </table>
                      </td></tr>
                    </table>
                  </body>
                </html>
                """.formatted(
                CorreoService.escaparHtml(usuario.getNombreCompleto()),
                CorreoService.escaparHtml(usuario.getUsuario()),
                CorreoService.escaparHtml(token),
                CorreoService.escaparHtml(FORMATO_VENCIMIENTO.format(expira))
        );
    }
}
