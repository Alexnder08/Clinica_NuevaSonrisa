package pe.nuevasonrisa.service;

import org.mindrot.jbcrypt.BCrypt;
import pe.nuevasonrisa.dao.RecuperacionPasswordDAO;
import pe.nuevasonrisa.model.PasswordResetToken;
import pe.nuevasonrisa.model.Usuario;

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


    public PasswordRecoveryService(RecuperacionPasswordDAO dao,
                                   CorreoService correoService) {
        this.dao = dao;
        this.correoService = correoService;
    }


    public String solicitarRestablecimiento(String email) {

        if (email == null || email.isBlank()) {
            return "Ingrese el correo registrado.";
        }


        Optional<Usuario> usuarioOpt =
                dao.buscarUsuarioPorEmail(email.trim());


        if (usuarioOpt.isEmpty()) {
            return "No existe un usuario con ese correo.";
        }


        Usuario usuario = usuarioOpt.get();


        String token = generarToken();


        String tokenHash =
                BCrypt.hashpw(token, BCrypt.gensalt());


        LocalDateTime expira =
                LocalDateTime.now().plusMinutes(30);



        if (!dao.guardarToken(
                usuario.getId(),
                tokenHash,
                expira)) {

            return "No se pudo generar el código de recuperación.";
        }



        String asunto =
                "Recuperación de contraseña - Nueva Sonrisa";


        String html =
                construirCorreoRecuperacion(
                        usuario,
                        token,
                        expira);



        boolean enviado =
                correoService.enviarCorreoHtml(
                        usuario.getEmail(),
                        asunto,
                        html,
                        null);



        if (!enviado) {

            String detalle =
                    correoService.getUltimoError();


            String sufijo =
                    detalle == null || detalle.isBlank()
                            ? ""
                            : " Detalle: " + detalle;


            return "Se generó el código, pero "
                    + correoService.getProveedorUltimoIntento()
                    + " no pudo enviar el correo."
                    + sufijo;
        }



        return "Se envió el código de recuperación al correo registrado.";
    }





    public String restablecerConCodigo(
            String email,
            String codigo,
            String nuevaContrasena,
            String confirmarContrasena) {



        if (email == null || email.isBlank()) {
            return "Ingrese el correo registrado.";
        }


        if (codigo == null || codigo.isBlank()) {
            return "Ingrese el código recibido por correo.";
        }


        if (nuevaContrasena == null || nuevaContrasena.isBlank()) {
            return "Ingrese la nueva contraseña.";
        }


        if (confirmarContrasena == null ||
                confirmarContrasena.isBlank()) {

            return "Confirme la nueva contraseña.";
        }



        if (nuevaContrasena.length() < 6) {
            return "La nueva contraseña debe tener mínimo 6 caracteres.";
        }



        if (!nuevaContrasena.equals(confirmarContrasena)) {

            return "La nueva contraseña y su confirmación no coinciden.";
        }




        Optional<Usuario> usuarioOpt =
                dao.buscarUsuarioPorEmail(email.trim());



        if (usuarioOpt.isEmpty()) {

            return "No existe un usuario con ese correo.";
        }



        Usuario usuario =
                usuarioOpt.get();




        Optional<PasswordResetToken> tokenOpt =
                dao.obtenerTokenActivo(usuario.getId());



        if (tokenOpt.isEmpty()) {

            return "No hay un código de recuperación activo para este usuario.";
        }




        PasswordResetToken token =
                tokenOpt.get();




        if (token.getExpiresAt()
                .isBefore(LocalDateTime.now())) {


            return "El código de recuperación expiró.";
        }





        if (!BCrypt.checkpw(
                codigo,
                token.getTokenHash())) {


            return "El código de recuperación es incorrecto.";
        }




        String nuevoHash =
                BCrypt.hashpw(
                        nuevaContrasena,
                        BCrypt.gensalt());




        if (!dao.actualizarPassword(
                usuario.getId(),
                nuevoHash)) {


            return "No se pudo actualizar la contraseña.";
        }




        dao.marcarTokenUsado(
                token.getId(),
                LocalDateTime.now());


        dao.limpiarTokensExpirados(
                LocalDateTime.now());



        return "Contraseña actualizada correctamente.";
    }





    private String generarToken() {

        return "%06d".formatted(
                secureRandom.nextInt(1_000_000));
    }





    static String construirCorreoRecuperacion(
            Usuario usuario,
            String token,
            LocalDateTime expira) {


        return """
                <!doctype html>
                <html lang="es">
                <body style="font-family:Arial;background:#f1f5f9;padding:20px">

                <div style="max-width:600px;
                            margin:auto;
                            background:white;
                            padding:30px;
                            border-radius:15px">

                    <h2 style="color:#1f4e78">
                        Nueva Sonrisa
                    </h2>

                    <h3>
                        Recuperación de contraseña
                    </h3>


                    <p>
                    Hola <b>%s</b>
                    </p>


                    <p>
                    Tu código de recuperación es:
                    </p>


                    <h1 style="
                    text-align:center;
                    color:#1f4e78;
                    letter-spacing:5px">

                    %s

                    </h1>


                    <p>
                    Código válido hasta:
                    <b>%s</b>
                    </p>


                    <p>
                    Si no solicitaste este cambio,
                    ignora este mensaje.
                    </p>


                </div>

                </body>
                </html>
                """
                .formatted(
                        CorreoService.escaparHtml(
                                usuario.getNombreCompleto()),

                        CorreoService.escaparHtml(token),

                        CorreoService.escaparHtml(
                                FORMATO_VENCIMIENTO.format(expira))
                );
    }
}