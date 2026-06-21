package pe.nuevasonrisa.service;

import pe.nuevasonrisa.model.CitaTabla;
import pe.nuevasonrisa.model.OdontologoTabla;
import pe.nuevasonrisa.model.PacienteTabla;
import pe.nuevasonrisa.model.RecordatorioCita;
import pe.nuevasonrisa.model.ServicioTabla;
import pe.nuevasonrisa.util.FeatureFlags;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class NotificacionCitaService {

    private static final Locale LOCALE_ES = Locale.forLanguageTag("es-PE");
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern(
            "EEEE d 'de' MMMM 'de' yyyy", LOCALE_ES
    );
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");

    private final CorreoService correoService;

    public NotificacionCitaService(CorreoService correoService) {
        this.correoService = correoService;
    }

    public boolean enviarModificacion(
            int citaId,
            PacienteTabla paciente,
            OdontologoTabla doctor,
            ServicioTabla servicio,
            LocalDate fecha,
            LocalTime hora
    ) {
        if (!puedeEnviar(paciente)) {
            return false;
        }

        String html = construirHtml(
                "Cita actualizada",
                "Tu cita fue modificada",
                "#2563eb",
                "#eff6ff",
                "#bfdbfe",
                nombreCompleto(paciente),
                nombreCompleto(doctor),
                servicio.getNombre(),
                fecha,
                hora,
                "Estos son los nuevos datos de tu reserva. Revisa especialmente la fecha y la hora.",
                null
        );

        return correoService.enviarCorreoHtml(
                paciente.getCorreo().trim(),
                "Tu cita fue modificada - Nueva Sonrisa",
                html,
                "modificacion-cita-%d-%s-%s".formatted(citaId, fecha, FORMATO_HORA.format(hora))
        );
    }

    public boolean enviarCancelacion(CitaTabla cita, PacienteTabla paciente, String motivo) {
        if (!puedeEnviar(paciente)) {
            return false;
        }

        String html = construirHtml(
                "Cita cancelada",
                "Tu cita ha sido cancelada",
                "#dc2626",
                "#fef2f2",
                "#fecaca",
                nombreCompleto(paciente),
                cita.getDoctor(),
                cita.getServicio(),
                cita.getFecha(),
                cita.getHora(),
                "La reserva indicada ya no se encuentra activa.",
                "Motivo de cancelacion: " + motivo
        );

        return correoService.enviarCorreoHtml(
                paciente.getCorreo().trim(),
                "Cancelacion de cita - Nueva Sonrisa",
                html,
                "cancelacion-cita-" + cita.getId()
        );
    }

    public boolean enviarRecordatorio(RecordatorioCita cita) {
        if (!FeatureFlags.emailFeaturesEnabled()
                || cita.getCorreo() == null
                || cita.getCorreo().isBlank()) {
            return false;
        }

        String html = construirHtml(
                "Recordatorio",
                "Tu cita se acerca",
                "#0f766e",
                "#f0fdfa",
                "#99f6e4",
                cita.getPaciente(),
                cita.getDoctor(),
                cita.getServicio(),
                cita.getFecha(),
                cita.getHora(),
                "Te esperamos en Nueva Sonrisa. Te recomendamos llegar 10 minutos antes.",
                "Si necesitas reprogramar, comunicate con la clinica antes de la hora reservada."
        );

        return correoService.enviarCorreoHtml(
                cita.getCorreo().trim(),
                "Recordatorio de tu cita - Nueva Sonrisa",
                html,
                "recordatorio-cita-" + cita.getId()
        );
    }

    static String construirHtml(
            String etiqueta,
            String titulo,
            String color,
            String fondoDetalle,
            String bordeDetalle,
            String paciente,
            String doctor,
            String servicio,
            LocalDate fecha,
            LocalTime hora,
            String introduccion,
            String aviso
    ) {
        String bloqueAviso = aviso == null || aviso.isBlank() ? "" : """
                <div style="margin-top:22px;padding:14px 16px;border-left:4px solid %s;background:#f8fafc;color:#475569;font-size:14px;line-height:1.6">
                  %s
                </div>
                """.formatted(color, CorreoService.escaparHtml(aviso));

        return """
                <!doctype html>
                <html lang="es">
                  <body style="margin:0;padding:0;background:#f1f5f9;font-family:Arial,Helvetica,sans-serif;color:#0f172a">
                    <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="background:#f1f5f9">
                      <tr><td align="center" style="padding:32px 14px">
                        <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="max-width:620px;background:#ffffff;border:1px solid #e2e8f0;border-radius:18px;overflow:hidden">
                          <tr><td style="padding:25px 30px;background:%s;color:#ffffff">
                            <table role="presentation" width="100%%"><tr>
                              <td style="font-size:20px;font-weight:700">Nueva Sonrisa</td>
                              <td align="right"><span style="display:inline-block;padding:7px 11px;border:1px solid rgba(255,255,255,.55);border-radius:999px;font-size:12px;font-weight:700">%s</span></td>
                            </tr></table>
                          </td></tr>
                          <tr><td style="padding:32px 30px">
                            <h1 style="margin:0 0 14px;font-size:25px;line-height:1.25;color:#0f172a">%s</h1>
                            <p style="margin:0 0 9px;font-size:16px;line-height:1.6">Hola <strong>%s</strong>,</p>
                            <p style="margin:0 0 24px;color:#475569;font-size:15px;line-height:1.7">%s</p>
                            <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="background:%s;border:1px solid %s;border-radius:13px">
                              <tr><td style="padding:20px 22px">
                                <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="font-size:15px;line-height:1.5">
                                  <tr><td style="padding:0 0 12px;color:#64748b;width:105px">Fecha</td><td style="padding:0 0 12px;font-weight:700">%s</td></tr>
                                  <tr><td style="padding:0 0 12px;color:#64748b">Hora</td><td style="padding:0 0 12px;font-weight:700">%s</td></tr>
                                  <tr><td style="padding:0 0 12px;color:#64748b">Odontologo</td><td style="padding:0 0 12px;font-weight:700">%s</td></tr>
                                  <tr><td style="color:#64748b">Servicio</td><td style="font-weight:700">%s</td></tr>
                                </table>
                              </td></tr>
                            </table>
                            %s
                          </td></tr>
                          <tr><td style="padding:18px 30px;background:#f8fafc;border-top:1px solid #e2e8f0;color:#64748b;font-size:12px;line-height:1.6;text-align:center">
                            Este es un mensaje automatico de Nueva Sonrisa. No respondas a este correo.
                          </td></tr>
                        </table>
                      </td></tr>
                    </table>
                  </body>
                </html>
                """.formatted(
                color,
                CorreoService.escaparHtml(etiqueta),
                CorreoService.escaparHtml(titulo),
                CorreoService.escaparHtml(paciente),
                CorreoService.escaparHtml(introduccion),
                fondoDetalle,
                bordeDetalle,
                CorreoService.escaparHtml(FORMATO_FECHA.format(fecha)),
                CorreoService.escaparHtml(FORMATO_HORA.format(hora)),
                CorreoService.escaparHtml(doctor),
                CorreoService.escaparHtml(servicio),
                bloqueAviso
        );
    }

    private boolean puedeEnviar(PacienteTabla paciente) {
        return FeatureFlags.emailFeaturesEnabled()
                && paciente != null
                && paciente.getCorreo() != null
                && !paciente.getCorreo().isBlank();
    }

    private String nombreCompleto(PacienteTabla paciente) {
        return paciente.getNombre() + " " + paciente.getApellido();
    }

    private String nombreCompleto(OdontologoTabla doctor) {
        return doctor.getNombre() + " " + doctor.getApellido();
    }
}
