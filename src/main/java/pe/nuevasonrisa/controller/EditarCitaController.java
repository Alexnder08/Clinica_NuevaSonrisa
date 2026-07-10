package pe.nuevasonrisa.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import pe.nuevasonrisa.dao.impl.CitaDAOImpl;
import pe.nuevasonrisa.dao.impl.OdontologoDAOImpl;
import pe.nuevasonrisa.dao.impl.PacienteDAOImpl;
import pe.nuevasonrisa.dao.impl.ServicioDAOImpl;
import pe.nuevasonrisa.model.Cita;
import pe.nuevasonrisa.model.CitaTabla;
import pe.nuevasonrisa.model.OdontologoTabla;
import pe.nuevasonrisa.model.PacienteTabla;
import pe.nuevasonrisa.model.ServicioTabla;
import pe.nuevasonrisa.service.AuditoriaService;
import pe.nuevasonrisa.service.CitaService;
import pe.nuevasonrisa.service.CorreoService;
import pe.nuevasonrisa.service.NotificacionCitaService;
import pe.nuevasonrisa.service.OdontologoService;
import pe.nuevasonrisa.service.PacienteService;
import pe.nuevasonrisa.service.ResultadoOperacion;
import pe.nuevasonrisa.service.ServicioService;
import pe.nuevasonrisa.util.FechaSistema;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class EditarCitaController {

    @FXML private ComboBox<PacienteTabla> cbPaciente;
    @FXML private ComboBox<OdontologoTabla> cbDoctor;
    @FXML private ComboBox<ServicioTabla> cbServicio;
    @FXML private ComboBox<String> cbHora;
    @FXML private ComboBox<String> cbEstado;
    @FXML private DatePicker dpFecha;
    @FXML private TextArea txtMotivo;
    @FXML private TextArea txtNotas;

    private CitaTabla citaActual;

    private final PacienteService pacienteService = new PacienteService(new PacienteDAOImpl());
    private final OdontologoService odontologoService = new OdontologoService(new OdontologoDAOImpl());
    private final ServicioService servicioService = new ServicioService(new ServicioDAOImpl());
    private final CitaService citaService = new CitaService(new CitaDAOImpl());
    private final AuditoriaService auditoriaService = new AuditoriaService();
    private final NotificacionCitaService notificacionService =
            new NotificacionCitaService(new CorreoService());

    @FXML
    public void initialize() {
        cbEstado.setItems(FXCollections.observableArrayList("Pendiente"));

        configurarVistaCombos();

        cbDoctor.setOnAction(event -> {
            OdontologoTabla doctor = cbDoctor.getValue();

            if (doctor == null) {
                cbServicio.getItems().clear();
                return;
            }

            cbServicio.setItems(FXCollections.observableArrayList(
                    servicioService.obtenerServiciosPorDoctor(doctor.getId())
            ));

            cbServicio.getSelectionModel().clearSelection();
        });
    }

    public void cargarCita(CitaTabla cita) {
        this.citaActual = cita;
        cargarCombos();

        cbPaciente.getItems().stream()
                .filter(p -> p.getId() == cita.getPacienteId())
                .findFirst()
                .ifPresent(p -> cbPaciente.getSelectionModel().select(p));

        cbDoctor.getItems().stream()
                .filter(d -> d.getId() == cita.getDoctorId())
                .findFirst()
                .ifPresent(d -> cbDoctor.getSelectionModel().select(d));

        cbServicio.setItems(FXCollections.observableArrayList(
                servicioService.obtenerServiciosPorDoctor(cita.getDoctorId())
        ));

        cbServicio.getItems().stream()
                .filter(s -> s.getId() == cita.getServicioId())
                .findFirst()
                .ifPresent(s -> cbServicio.getSelectionModel().select(s));

        dpFecha.setValue(cita.getFecha());
        cbHora.setValue(cita.getHora().toString().substring(0, 5));
        cbEstado.setItems(FXCollections.observableArrayList(
                citaService.estadosPermitidosDesde(cita.getEstado())
        ));
        cbEstado.setValue(cita.getEstado());
        txtMotivo.setText(cita.getMotivoConsulta() == null ? "" : cita.getMotivoConsulta());
        txtNotas.setText(cita.getNotas() == null ? "" : cita.getNotas());
    }

    private void cargarCombos() {
        cbPaciente.setItems(FXCollections.observableArrayList(pacienteService.obtenerPacientes()));
        cbDoctor.setItems(FXCollections.observableArrayList(odontologoService.obtenerOdontologos()));
        cbServicio.setItems(FXCollections.observableArrayList());

        cbHora.setItems(FXCollections.observableArrayList(
                "08:00", "09:00", "10:00", "11:00",
                "12:00", "13:00", "14:00", "15:00",
                "16:00", "17:00", "18:00"
        ));
    }

    @FXML
    private void guardar() {
        if (citaActual == null) {
            return;
        }

        if ("Realizado".equalsIgnoreCase(citaActual.getEstado())
                || "Cancelado".equalsIgnoreCase(citaActual.getEstado())
                || "No asistió".equalsIgnoreCase(citaActual.getEstado())) {
            mostrarError("No se puede modificar una cita realizada, cancelada o marcada como no asistida.");
            return;
        }

        String motivo = texto(txtMotivo.getText());
        String notas = texto(txtNotas.getText());

        String camposFaltantes = camposFaltantes(
                cbPaciente.getValue() == null ? "paciente" : null,
                cbDoctor.getValue() == null ? "doctor" : null,
                cbServicio.getValue() == null ? "servicio" : null,
                dpFecha.getValue() == null ? "fecha" : null,
                campoFaltante(cbHora.getValue(), "hora"),
                cbEstado.getValue() == null ? "estado" : null,
                campoFaltante(motivo, "motivo de consulta")
        );

        if (camposFaltantes != null) {
            mostrarError("Complete los campos obligatorios: " + camposFaltantes + ".");
            return;
        }

        LocalTime hora = parsearHora(cbHora.getValue());
        if (hora == null) {
            return;
        }

        LocalDate fecha = dpFecha.getValue();
        String estadoAnterior = citaActual.getEstado();
        String estadoNuevo = cbEstado.getValue();

        if (!estadoAnterior.equalsIgnoreCase(estadoNuevo)) {
            String validacionEstado = citaService.validarTransicionEstado(
                    estadoAnterior,
                    estadoNuevo,
                    fecha,
                    notas
            );
            if (validacionEstado != null) {
                mostrarError(validacionEstado);
                return;
            }

            if (soloCambioEstado(fecha, hora, motivo, notas)) {
                ResultadoOperacion resultado = citaService.cambiarEstadoConResultado(
                        citaActual.getId(),
                        estadoAnterior,
                        estadoNuevo,
                        fecha,
                        notas
                );
                if (resultado.exitoso()) {
                    auditoriaService.registrar(
                            estadoNuevo.toUpperCase(),
                            "CITAS",
                            "Cita #" + citaActual.getId() +
                                    " cambio de estado: " +
                                    estadoAnterior + " -> " +
                                    estadoNuevo
                    );
                    cerrar();
                } else {
                    mostrarError(resultado.mensaje());
                }
                return;
            }
        }

        LocalDate hoy = FechaSistema.hoy();
        LocalTime ahora = FechaSistema.ahoraSinSegundos();

        if (fecha.isBefore(hoy)) {
            mostrarError("La fecha de la cita no puede ser anterior a hoy.");
            return;
        }

        if (fecha.isEqual(hoy) && !hora.isAfter(ahora)) {
            mostrarError("La hora de la cita debe ser posterior a la hora actual.");
            return;
        }

        Cita cita = new Cita();
        cita.setId(citaActual.getId());
        cita.setPacienteId(cbPaciente.getValue().getId());
        cita.setDoctorId(cbDoctor.getValue().getId());
        cita.setServicioId(cbServicio.getValue().getId());
        cita.setFecha(fecha);
        cita.setHora(hora);
        cita.setDuracion(cbServicio.getValue().getDuracion());
        cita.setEstado(estadoNuevo);
        cita.setMotivoConsulta(motivo);
        cita.setNotas(notas.isBlank() ? null : notas);

        ResultadoOperacion resultado = citaService.actualizarCitaConResultado(cita);
        if (resultado.exitoso()) {
            if (!estadoAnterior.equalsIgnoreCase(cita.getEstado())) {
                auditoriaService.registrar(
                        cita.getEstado().toUpperCase(),
                        "CITAS",
                        "Cita #" + cita.getId() +
                                " cambió de estado: " +
                                estadoAnterior + " -> " +
                                cita.getEstado()
                );
            } else {
                auditoriaService.registrar(
                        "EDITAR",
                        "CITAS",
                        "Cita #" + cita.getId() + " actualizada"
                );
            }

            notificacionService.enviarModificacion(
                    cita.getId(),
                    cbPaciente.getValue(),
                    cbDoctor.getValue(),
                    cbServicio.getValue(),
                    cita.getFecha(),
                    cita.getHora()
            );

            cerrar();
        } else {
            mostrarError(resultado.mensaje());
        }
    }

    private LocalTime parsearHora(String horaTexto) {
        try {
            return LocalTime.parse(horaTexto);
        } catch (Exception e) {
            mostrarError("La hora debe tener formato HH:mm. Ejemplo: 09:30");
            return null;
        }
    }

    @FXML
    private void cancelar() {
        cerrar();
    }

    private void cerrar() {
        Stage stage = (Stage) cbPaciente.getScene().getWindow();
        stage.close();
    }

    private String campoFaltante(String valor, String nombreCampo) {
        return valor == null || valor.isBlank() ? nombreCampo : null;
    }

    private String texto(String valor) {
        return valor == null ? "" : valor.trim();
    }

    private String camposFaltantes(String... campos) {
        List<String> faltantes = new ArrayList<>();
        for (String campo : campos) {
            if (campo != null) {
                faltantes.add(campo);
            }
        }
        return faltantes.isEmpty() ? null : String.join(", ", faltantes);
    }

    private boolean soloCambioEstado(LocalDate fecha, LocalTime hora, String motivo, String notas) {
        return cbPaciente.getValue().getId() == citaActual.getPacienteId()
                && cbDoctor.getValue().getId() == citaActual.getDoctorId()
                && cbServicio.getValue().getId() == citaActual.getServicioId()
                && fecha.equals(citaActual.getFecha())
                && hora.equals(citaActual.getHora())
                && mismoTexto(motivo, citaActual.getMotivoConsulta())
                && mismoTexto(notas, citaActual.getNotas());
    }

    private boolean mismoTexto(String valorFormulario, String valorActual) {
        String formulario = valorFormulario == null ? "" : valorFormulario.trim();
        String actual = valorActual == null ? "" : valorActual.trim();
        return formulario.equals(actual);
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void configurarVistaCombos() {
        cbPaciente.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(PacienteTabla item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null :
                        item.getDni() + " - " + item.getNombre() + " " + item.getApellido());
            }
        });

        cbPaciente.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(PacienteTabla item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null :
                        item.getDni() + " - " + item.getNombre() + " " + item.getApellido());
            }
        });

        cbDoctor.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(OdontologoTabla item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null :
                        item.getNombre() + " " + item.getApellido() + " - " + item.getServicio());
            }
        });

        cbDoctor.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(OdontologoTabla item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null :
                        item.getNombre() + " " + item.getApellido() + " - " + item.getServicio());
            }
        });

        cbServicio.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(ServicioTabla item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null :
                        item.getNombre() + " (" + item.getDuracion() + " min)");
            }
        });

        cbServicio.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(ServicioTabla item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null :
                        item.getNombre() + " (" + item.getDuracion() + " min)");
            }
        });
    }
}
