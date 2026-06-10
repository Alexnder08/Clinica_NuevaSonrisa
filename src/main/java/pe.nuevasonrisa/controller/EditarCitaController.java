package pe.nuevasonrisa.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.time.LocalTime;
import java.time.LocalDate;
import java.time.ZoneId;

import pe.nuevasonrisa.dao.impl.CitaDAOImpl;
import pe.nuevasonrisa.dao.impl.OdontologoDAOImpl;
import pe.nuevasonrisa.dao.impl.PacienteDAOImpl;
import pe.nuevasonrisa.dao.impl.ServicioDAOImpl;
import pe.nuevasonrisa.model.Cita;
import pe.nuevasonrisa.model.CitaTabla;
import pe.nuevasonrisa.model.OdontologoTabla;
import pe.nuevasonrisa.model.PacienteTabla;
import pe.nuevasonrisa.model.ServicioTabla;
import pe.nuevasonrisa.service.CitaService;
import pe.nuevasonrisa.service.OdontologoService;
import pe.nuevasonrisa.service.PacienteService;
import pe.nuevasonrisa.service.ServicioService;
import pe.nuevasonrisa.service.AuditoriaService;

import java.time.LocalTime;

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

    @FXML
    public void initialize() {
        cbEstado.setItems(FXCollections.observableArrayList(
                "Pendiente",
                "Realizado"
        ));

        configurarVistaCombos();

        cbDoctor.setOnAction(event -> {

            OdontologoTabla doctor =
                    cbDoctor.getValue();

            if (doctor == null) {
                return;
            }

            cbServicio.setItems(
                    FXCollections.observableArrayList(
                            servicioService.obtenerServiciosPorDoctor(
                                    doctor.getId()
                            )
                    )
            );

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

        cbServicio.getItems().stream()
                .filter(s -> s.getId() == cita.getServicioId())
                .findFirst()
                .ifPresent(s -> cbServicio.getSelectionModel().select(s));

        dpFecha.setValue(cita.getFecha());
        cbHora.setValue(cita.getHora().toString().substring(0, 5));
        cbEstado.setValue(cita.getEstado());
        txtMotivo.setText(cita.getMotivoConsulta() == null ? "" : cita.getMotivoConsulta());
        txtNotas.setText(cita.getNotas() == null ? "" : cita.getNotas());
    }

    private void cargarCombos() {
        cbPaciente.setItems(FXCollections.observableArrayList(pacienteService.obtenerPacientes()));
        cbDoctor.setItems(FXCollections.observableArrayList(odontologoService.obtenerOdontologos()));
        cbServicio.setItems(FXCollections.observableArrayList(servicioService.obtenerServicios()));

        cbHora.setItems(FXCollections.observableArrayList(
                "08:00", "09:00", "10:00", "11:00",
                "12:00", "13:00", "14:00", "15:00",
                "16:00", "17:00", "18:00"
        ));
    }

    @FXML
    private void guardar() {
        if (citaActual == null) return;

        if ("Realizado".equalsIgnoreCase(citaActual.getEstado())
                || "Cancelado".equalsIgnoreCase(citaActual.getEstado())) {
            mostrarError("No se puede modificar una cita realizada o cancelada.");
            return;
        }

        if (cbPaciente.getValue() == null || cbDoctor.getValue() == null ||
                cbServicio.getValue() == null || dpFecha.getValue() == null ||
                cbHora.getValue() == null || cbEstado.getValue() == null) {
            mostrarError("Complete paciente, doctor, servicio, fecha, hora y estado.");
            return;
        }


        LocalDate fecha = dpFecha.getValue();
        LocalTime hora = LocalTime.parse(cbHora.getValue());

        LocalDate hoy = LocalDate.now(ZoneId.of("America/Lima"));
        LocalTime ahora = LocalTime.now(ZoneId.of("America/Lima"))
                .withSecond(0)
                .withNano(0);

        if (fecha.isBefore(hoy)) {
            mostrarError("No puede registrar una cita en una fecha anterior a hoy.");
            return;
        }

        if (fecha.isEqual(hoy) && !hora.isAfter(ahora)) {
            mostrarError("No puede registrar una cita en una hora anterior o igual a la actual.");
            return;
        }

        if ("Realizado".equalsIgnoreCase(citaActual.getEstado())) {
            mostrarError("No se puede modificar una cita ya realizada.");
            return;
        }

        String estadoAnterior = citaActual.getEstado();

        Cita cita = new Cita();
        cita.setId(citaActual.getId());
        cita.setPacienteId(cbPaciente.getValue().getId());
        cita.setDoctorId(cbDoctor.getValue().getId());
        cita.setServicioId(cbServicio.getValue().getId());
        cita.setFecha(dpFecha.getValue());
        cita.setHora(LocalTime.parse(cbHora.getValue()));
        cita.setDuracion(cbServicio.getValue().getDuracion());
        cita.setEstado(cbEstado.getValue());
        cita.setMotivoConsulta(txtMotivo.getText().trim());
        cita.setNotas(txtNotas.getText().trim());

        String validacion = citaService.validarEdicionCita(cita);

        if (validacion != null) {
            mostrarError(validacion);
            return;
        }
        if (citaService.actualizarCita(cita)) {

            if (!estadoAnterior.equalsIgnoreCase(cita.getEstado())) {

                auditoriaService.registrar(
                        cita.getEstado().toUpperCase(),
                        "CITAS",
                        "Cita #" + cita.getId() +
                                " cambiÃ³ de estado: " +
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

            cerrar();

        } else {
            mostrarError("No se pudo actualizar la cita.");
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