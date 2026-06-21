package pe.nuevasonrisa.service;

import pe.nuevasonrisa.model.OdontologoTabla;
import pe.nuevasonrisa.model.PacienteTabla;
import pe.nuevasonrisa.model.ServicioTabla;
import pe.nuevasonrisa.util.FeatureFlags;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class ConfirmacionReservaService {

    private static final Locale LOCALE_ES = Locale.forLanguageTag("es-PE");
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern(
            "EEEE d 'de' MMMM 'de' yyyy",
            LOCALE_ES
    );
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");

    private final CorreoService correoService;

    public ConfirmacionReservaService(CorreoService correoService) {
        this.correoService = correoService;
    }

    public Resultado enviar(
            PacienteTabla paciente,
            OdontologoTabla doctor,
            ServicioTabla servicio,
            LocalDate fecha,
            LocalTime hora
    ) {
        if (!FeatureFlags.emailFeaturesEnabled()) {
            return new Resultado(false, "La cita fue registrada, pero el envio de correos esta desactivado.");
        }
        if (paciente.getCorreo() == null || paciente.getCorreo().isBlank()) {
            return new Resultado(false, "La cita fue registrada, pero el paciente no tiene un correo valido.");
        }

        String pacienteNombre = paciente.getNombre() + " " + paciente.getApellido();
        String doctorNombre = doctor.getNombre() + " " + doctor.getApellido();
        String fechaTexto = FORMATO_FECHA.format(fecha);
        String horaTexto = FORMATO_HORA.format(hora);
        String html = construirHtml(pacienteNombre, doctorNombre, servicio.getNombre(), fechaTexto, horaTexto);
        String idempotencia = "confirmacion-cita-%d-%d-%s-%s".formatted(
                paciente.getId(),
                doctor.getId(),
                fecha,
                horaTexto.replace(":", "")
        );

        boolean enviado = correoService.enviarCorreoHtml(
                paciente.getCorreo().trim(),
                "Confirmacion de tu cita - Nueva Sonrisa",
                html,
                idempotencia
        );

        String mensaje = enviado
                ? "La cita fue registrada y enviamos la confirmacion a " + paciente.getCorreo() + "."
                : "La cita fue registrada, pero no se pudo enviar la confirmacion por correo.";
        return new Resultado(enviado, mensaje);
    }

    private String construirHtml(
            String paciente,
            String doctor,
            String servicio,
            String fecha,
            String hora
    ) {
        return """
                <!doctype html>
                <html lang="es">
                  <body style="margin:0;background:#f1f5f9;font-family:Arial,sans-serif;color:#0f172a">
                    <div style="max-width:600px;margin:0 auto;padding:32px 16px">
                      <div style="background:#ffffff;border-radius:18px;overflow:hidden;border:1px solid #e2e8f0">
                        <div style="background:#0f766e;padding:28px 32px;color:#ffffff">
                          <div style="font-size:13px;letter-spacing:1.4px;text-transform:uppercase;opacity:.85">Nueva Sonrisa</div>
                          <h1 style="margin:8px 0 0;font-size:26px">Tu cita esta confirmada</h1>
                        </div>
                        <div style="padding:30px 32px">
                          <p style="margin-top:0;font-size:16px">Hola <strong>%s</strong>, hemos reservado tu atencion odontologica.</p>
                          <div style="background:#f0fdfa;border:1px solid #99f6e4;border-radius:12px;padding:20px;margin:24px 0">
                            <p style="margin:0 0 12px"><strong>Fecha:</strong> %s</p>
                            <p style="margin:0 0 12px"><strong>Hora:</strong> %s</p>
                            <p style="margin:0 0 12px"><strong>Doctor:</strong> %s</p>
                            <p style="margin:0"><strong>Servicio:</strong> %s</p>
                          </div>
                          <p style="font-size:14px;color:#475569;line-height:1.6">Te recomendamos llegar 10 minutos antes. Si necesitas reprogramar, comunicate con la clinica.</p>
                        </div>
                      </div>
                      <p style="text-align:center;color:#64748b;font-size:12px">Este es un mensaje automatico de Nueva Sonrisa.</p>
                    </div>
                  </body>
                </html>
                """.formatted(
                CorreoService.escaparHtml(paciente),
                CorreoService.escaparHtml(fecha),
                CorreoService.escaparHtml(hora),
                CorreoService.escaparHtml(doctor),
                CorreoService.escaparHtml(servicio)
        );
    }

    public record Resultado(boolean enviado, String mensaje) {
    }
}
