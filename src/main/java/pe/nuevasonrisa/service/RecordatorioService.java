package pe.nuevasonrisa.service;

import pe.nuevasonrisa.dao.CitaDAO;
import pe.nuevasonrisa.model.RecordatorioCita;
import pe.nuevasonrisa.util.FeatureFlags;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RecordatorioService {

    private final CitaDAO citaDAO;
    private final CorreoService correoService;

    public RecordatorioService(CitaDAO citaDAO, CorreoService correoService) {
        this.citaDAO = citaDAO;
        this.correoService = correoService;
    }

    public List<RecordatorioCita> obtenerPendientes() {
        LocalDate desde = LocalDate.now();
        LocalDate hasta = desde.plusDays(1);
        return citaDAO.listarCitasPendientesParaRecordatorio(desde, hasta);
    }

    public int enviarRecordatoriosProximos() {
        if (!FeatureFlags.emailFeaturesEnabled()) {
            return 0;
        }

        List<RecordatorioCita> pendientes = obtenerPendientes();
        List<RecordatorioCita> enviados = new ArrayList<>();

        for (RecordatorioCita cita : pendientes) {
            String asunto = "Recordatorio de cita - Nueva Sonrisa";
            String cuerpo = construirMensaje(cita);

            boolean ok = correoService.enviarCorreo(
                    cita.getCorreo(),
                    asunto,
                    cuerpo
            );

            if (ok && citaDAO.marcarRecordatorioEnviado(cita.getId(), LocalDateTime.now())) {
                enviados.add(cita);
            }
        }

        return enviados.size();
    }

    private String construirMensaje(RecordatorioCita cita) {
        return """
                Hola %s,

                Te recordamos tu cita en Nueva Sonrisa:
                - Doctor: %s
                - Servicio: %s
                - Fecha: %s
                - Hora: %s

                Si necesitas reprogramar, contacta a la clinica.
                """.formatted(
                cita.getPaciente(),
                cita.getDoctor(),
                cita.getServicio(),
                cita.getFecha(),
                cita.getHora()
        );
    }
}
