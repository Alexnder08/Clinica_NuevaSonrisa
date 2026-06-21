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
    private final NotificacionCitaService notificacionService;

    public RecordatorioService(CitaDAO citaDAO, CorreoService correoService) {
        this.citaDAO = citaDAO;
        this.notificacionService = new NotificacionCitaService(correoService);
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
            boolean ok = notificacionService.enviarRecordatorio(cita);

            if (ok && citaDAO.marcarRecordatorioEnviado(cita.getId(), LocalDateTime.now())) {
                enviados.add(cita);
            }
        }

        return enviados.size();
    }

}
