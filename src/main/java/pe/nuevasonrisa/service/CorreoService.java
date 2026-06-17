package pe.nuevasonrisa.service;

import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

public class CorreoService {

    public boolean enviarCorreo(String destinatario, String asunto, String cuerpo) {
        String host = System.getenv("SMTP_HOST");
        String puerto = System.getenv("SMTP_PORT");
        String usuario = System.getenv("SMTP_USER");
        String password = System.getenv("SMTP_PASS");
        String remitente = System.getenv().getOrDefault("SMTP_FROM", usuario);

        if (host == null || host.isBlank()
                || puerto == null || puerto.isBlank()
                || usuario == null || usuario.isBlank()
                || password == null || password.isBlank()
                || remitente == null || remitente.isBlank()) {
            System.out.println("SMTP no configurado. No se enviara correo a " + destinatario);
            return false;
        }

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", puerto);

        Session session = Session.getInstance(props, new jakarta.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(usuario, password);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(remitente));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            message.setSubject(asunto, "UTF-8");
            message.setText(cuerpo, "UTF-8");

            Transport.send(message);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
