package pe.nuevasonrisa.service;

import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import pe.nuevasonrisa.config.CorreoConfig;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Properties;

public class CorreoService {

    private static final String RESEND_ENDPOINT = "https://api.resend.com/emails";
    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private String ultimoError;
    private String proveedorUltimoIntento = "Sin configurar";

    public boolean enviarCorreo(String destinatario, String asunto, String cuerpo) {
        String html = "<div style=\"font-family:Arial,sans-serif;white-space:pre-line;color:#334155\">"
                + escaparHtml(cuerpo)
                + "</div>";
        return enviarCorreoHtml(destinatario, asunto, html, null);
    }

    public boolean enviarCorreoHtml(String destinatario, String asunto, String html, String claveIdempotencia) {
        ultimoError = null;
        String apiKey = CorreoConfig.resendApiKey();
        if (apiKey != null && !apiKey.isBlank()) {
            proveedorUltimoIntento = "Resend";
        } else {
            proveedorUltimoIntento = "SMTP";
        }

        String destinatarioNormalizado = normalizarDestinatario(destinatario);
        if (destinatarioNormalizado == null) {
            ultimoError = "El destinatario no contiene una dirección de correo válida.";
            return false;
        }

        if (apiKey != null && !apiKey.isBlank()) {
            return enviarConResend(apiKey, destinatarioNormalizado, asunto, html, claveIdempotencia);
        }

        return enviarConSmtp(destinatarioNormalizado, asunto, html);
    }

    public String getProveedorUltimoIntento() {
        return proveedorUltimoIntento;
    }

    public String getUltimoError() {
        return ultimoError;
    }

    static String normalizarDestinatario(String destinatario) {
        if (destinatario == null) {
            return null;
        }

        String correo = destinatario.trim();
        return correo.matches("^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,}$")
                ? correo
                : null;
    }

    private boolean enviarConResend(
            String apiKey,
            String destinatario,
            String asunto,
            String html,
            String claveIdempotencia
    ) {
        String remitente = CorreoConfig.resendFrom();

        String json = """
                {"from":"%s","to":["%s"],"subject":"%s","html":"%s"}
                """.formatted(
                escaparJson(remitente),
                escaparJson(destinatario),
                escaparJson(asunto),
                escaparJson(html)
        );

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(RESEND_ENDPOINT))
                .timeout(Duration.ofSeconds(20))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json));

        if (claveIdempotencia != null && !claveIdempotencia.isBlank()) {
            requestBuilder.header("Idempotency-Key", claveIdempotencia);
        }

        try {
            HttpResponse<String> response = HTTP_CLIENT.send(
                    requestBuilder.build(),
                    HttpResponse.BodyHandlers.ofString()
            );
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return true;
            }

            ultimoError = "HTTP " + response.statusCode() + ": " + response.body();
            System.err.println("Resend rechazo el correo (" + ultimoError + ")");
        } catch (Exception e) {
            ultimoError = e.getMessage();
            System.err.println("No se pudo conectar con Resend: " + ultimoError);
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
        }
        return false;
    }

    private boolean enviarConSmtp(String destinatario, String asunto, String html) {
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
            ultimoError = "No se encontraron credenciales SMTP.";
            System.err.println("Correo no configurado. Defina RESEND_API_KEY o las variables SMTP.");
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
            message.setContent(html, "text/html; charset=UTF-8");
            Transport.send(message);
            return true;
        } catch (Exception e) {
            ultimoError = e.getMessage();
            System.err.println("No se pudo enviar el correo por SMTP: " + ultimoError);
            return false;
        }
    }

    private static String escaparJson(String texto) {
        if (texto == null) {
            return "";
        }
        return texto.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\n", "\\n")
                .replace("\t", "\\t");
    }

    public static String escaparHtml(String texto) {
        if (texto == null) {
            return "";
        }
        return texto.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
