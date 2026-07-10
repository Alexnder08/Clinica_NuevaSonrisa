package pe.nuevasonrisa.service;

import pe.nuevasonrisa.dao.CitaDAO;
import pe.nuevasonrisa.dao.impl.ServicioDAOImpl;
import pe.nuevasonrisa.model.Cita;
import pe.nuevasonrisa.model.CitaTabla;
import pe.nuevasonrisa.util.FechaSistema;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.time.LocalDate;
import java.time.LocalTime;

public class CitaService {

    private final CitaDAO dao;
    private final ServicioService servicioService;

    public CitaService(CitaDAO dao) {
        this(dao, new ServicioService(new ServicioDAOImpl()));
    }

    public CitaService(CitaDAO dao, ServicioService servicioService) {
        this.dao = dao;
        this.servicioService = servicioService;
    }

    public List<CitaTabla> obtenerCitas() {
        return dao.listarCitas();
    }

    public int marcarPendientesVencidasComoNoAsistio() {
        return dao.marcarPendientesVencidasComoNoAsistio();
    }

    public boolean crearCita(Cita cita) {
        return dao.crearCita(cita);
    }

    public boolean cancelarCita(int citaId, String motivo) {
        return dao.cambiarEstado(citaId, "Cancelado", motivo);
    }

    public boolean cambiarEstado(int citaId, String estado) {
        return dao.cambiarEstado(citaId, estado, null);
    }

    public ResultadoOperacion cambiarEstadoConResultado(
            int citaId,
            String estadoActual,
            String estadoNuevo,
            LocalDate fecha,
            String notas
    ) {
        String validacion = validarTransicionEstado(estadoActual, estadoNuevo, fecha, notas);
        if (validacion != null) {
            return ResultadoOperacion.error(validacion);
        }

        boolean actualizado = dao.cambiarEstado(citaId, estadoNuevo, null);
        if (!actualizado) {
            return ResultadoOperacion.error("No se pudo cambiar el estado de la cita. Verifique que la cita exista y vuelva a intentar.");
        }

        return ResultadoOperacion.exito("El estado de la cita se actualizo correctamente.");
    }

    public ResultadoOperacion cancelarCitaConResultado(int citaId, String estadoActual, String motivo) {
        String motivoLimpio = motivo == null ? "" : motivo.trim();
        if (motivoLimpio.isBlank()) {
            return ResultadoOperacion.error("El motivo de cancelacion es obligatorio.");
        }

        String actualClave = claveEstado(estadoActual);
        if (!"pendiente".equals(actualClave) && !"en espera".equals(actualClave)) {
            return ResultadoOperacion.error("Solo se puede cancelar una cita pendiente o en espera.");
        }

        boolean cancelado = dao.cambiarEstado(citaId, "Cancelado", motivoLimpio);
        if (!cancelado) {
            return ResultadoOperacion.error("No se pudo cancelar la cita. Verifique que la cita exista y vuelva a intentar.");
        }

        return ResultadoOperacion.exito("La cita fue cancelada correctamente.");
    }

    public String validarTransicionEstado(String estadoActual, String estadoNuevo) {
        return validarTransicionEstado(estadoActual, estadoNuevo, null, null);
    }

    public String validarTransicionEstado(
            String estadoActual,
            String estadoNuevo,
            LocalDate fecha,
            String notas
    ) {
        String actual = normalizarEstado(estadoActual);
        String nuevo = normalizarEstado(estadoNuevo);
        String actualClave = claveEstado(actual);
        String nuevoClave = claveEstado(nuevo);

        if (actualClave.isBlank()) {
            return "El estado actual de la cita no es valido.";
        }

        if (!esEstadoPermitido(actualClave)) {
            return "El estado actual de la cita no es valido.";
        }

        if (nuevo.isBlank()) {
            return "Seleccione un estado valido.";
        }

        if (!esEstadoPermitido(nuevoClave)) {
            return "Seleccione un estado valido.";
        }

        if (actualClave.equals(nuevoClave)) {
            return null;
        }

        if ("pendiente".equals(actualClave) && "en espera".equals(nuevoClave)) {
            LocalDate hoy = FechaSistema.hoy();
            if (fecha != null && !fecha.equals(hoy)) {
                return "Solo se puede pasar a En espera una cita pendiente del dia de hoy.";
            }
            return null;
        }

        if ("en espera".equals(actualClave) && "realizado".equals(nuevoClave)) {
            if (notas == null || notas.trim().isBlank()) {
                return "Registre una nota clinica antes de finalizar la cita.";
            }
            return null;
        }

        return "No se permite cambiar el estado de " + actual + " a " + nuevo + ".";
    }

    public String validarCita(Cita cita) {
        try {
            return validarCitaInterna(cita, null);
        } catch (IllegalStateException e) {
            return e.getMessage();
        }
    }

    public String validarEdicionCita(Cita cita) {
        if ("Realizado".equalsIgnoreCase(cita.getEstado())
                || "Cancelado".equalsIgnoreCase(cita.getEstado())
                || "No asistió".equalsIgnoreCase(cita.getEstado())) {
            return null;
        }

        try {
            return validarCitaInterna(cita, cita.getId());
        } catch (IllegalStateException e) {
            return e.getMessage();
        }
    }

    private String validarCitaInterna(Cita cita, Integer citaIdExcluir) {
        if (!servicioService.servicioAsignadoADoctor(cita.getDoctorId(), cita.getServicioId())) {
            return "El servicio seleccionado no está asignado al odontólogo.";
        }

        if (!dao.dentroHorarioDoctor(
                cita.getDoctorId(),
                cita.getFecha(),
                cita.getHora(),
                cita.getDuracion()
        )) {
            return "El odontólogo no atiende en la fecha u hora seleccionada.";
        }

        if (!dao.doctorDisponible(
                cita.getDoctorId(),
                cita.getFecha(),
                cita.getHora(),
                cita.getDuracion(),
                citaIdExcluir
        )) {
            return "El odontólogo ya tiene una cita registrada en ese horario.";
        }

        if (!dao.pacienteDisponible(
                cita.getPacienteId(),
                cita.getFecha(),
                cita.getHora(),
                cita.getDuracion(),
                citaIdExcluir
        )) {
            return "El paciente ya tiene una cita registrada en ese horario.";
        }

        return null;
    }

    public boolean actualizarCita(Cita cita) {
        return dao.actualizarCita(cita);
    }

    public ResultadoOperacion actualizarCitaConResultado(Cita cita) {
        String validacion = validarEdicionCita(cita);
        if (validacion != null) {
            return ResultadoOperacion.error(validacion);
        }

        boolean actualizado = dao.actualizarCita(cita);
        if (!actualizado) {
            return ResultadoOperacion.error("No se pudo actualizar la cita. Verifique la disponibilidad del odontologo y del paciente.");
        }

        return ResultadoOperacion.exito("La cita fue actualizada correctamente.");
    }

    public List<String> estadosPermitidosDesde(String estadoActual) {
        String actualClave = claveEstado(estadoActual);
        List<String> estados = new ArrayList<>();
        String actual = normalizarEstado(estadoActual);
        if (!actual.isBlank()) {
            estados.add(actual);
        }

        if ("pendiente".equals(actualClave)) {
            estados.add("En espera");
        } else if ("en espera".equals(actualClave)) {
            estados.add("Realizado");
        }

        return estados.stream().distinct().toList();
    }

    private String normalizarEstado(String estado) {
        return estado == null ? "" : estado.trim();
    }

    private String claveEstado(String estado) {
        String sinAcentos = Normalizer.normalize(normalizarEstado(estado), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return sinAcentos.toLowerCase(Locale.ROOT);
    }

    private boolean esEstadoPermitido(String estado) {
        return "pendiente".equals(estado)
                || "en espera".equals(estado)
                || "realizado".equals(estado)
                || "cancelado".equals(estado)
                || "no asistio".equals(estado);
    }

    public List<LocalTime> obtenerHorasDisponibles(
            int pacienteId,
            int doctorId,
            LocalDate fecha,
            int duracionMinutos
    ) {
        return dao.listarHorasDisponibles(pacienteId, doctorId, fecha, duracionMinutos);
    }
}
