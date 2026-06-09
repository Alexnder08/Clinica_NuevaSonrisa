package pe.nuevasonrisa.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import pe.nuevasonrisa.dao.impl.CitaDAOImpl;
import pe.nuevasonrisa.dao.impl.OdontologoDAOImpl;
import pe.nuevasonrisa.dao.impl.PacienteDAOImpl;
import pe.nuevasonrisa.dao.impl.ServicioDAOImpl;
import pe.nuevasonrisa.model.Cita;
import pe.nuevasonrisa.model.OdontologoTabla;
import pe.nuevasonrisa.model.PacienteTabla;
import pe.nuevasonrisa.model.ServicioTabla;
import pe.nuevasonrisa.service.CitaService;
import pe.nuevasonrisa.service.OdontologoService;
import pe.nuevasonrisa.service.PacienteService;
import pe.nuevasonrisa.service.ServicioService;
import pe.nuevasonrisa.service.AuditoriaService;

import java.time.LocalDate;
import java.time.LocalTime;

public class NuevaCitaController {

    @FXML private ComboBox<PacienteTabla> cbPaciente;
    @FXML private ComboBox<OdontologoTabla> cbDoctor;
    @FXML private ComboBox<ServicioTabla> cbServicio;
    @FXML private DatePicker dpFecha;
    @FXML private ComboBox<String> cbHora;
    @FXML private TextArea txtMotivo;
    @FXML private TextArea txtNotas;

    private final PacienteService pacienteService =
            new PacienteService(new PacienteDAOImpl());

    private final OdontologoService odontologoService =
            new OdontologoService(new OdontologoDAOImpl());

    private final ServicioService servicioService =
            new ServicioService(new ServicioDAOImpl());

    private final CitaService citaService =
            new CitaService(new CitaDAOImpl());

    private final AuditoriaService auditoriaService =
            new AuditoriaService();

    @FXML
    public void initialize() {
        cbPaciente.setItems(
                FXCollections.observableArrayList(
                        pacienteService.obtenerPacientes()
                )
        );

        cbDoctor.setItems(
                FXCollections.observableArrayList(
                        odontologoService.obtenerOdontologos()
                )
        );

        cbServicio.setItems(
                FXCollections.observableArrayList(
                        servicioService.obtenerServicios()
                )
        );

        cbHora.setItems(FXCollections.observableArrayList(
                "08:00", "09:00", "10:00", "11:00",
                "12:00", "13:00", "14:00", "15:00",
                "16:00", "17:00", "18:00"
        ));

        configurarVistaCombos();
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
                        item.getNombre() + " (" + item.getDuracion() + " h)");
            }
        });

        cbServicio.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(ServicioTabla item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null :
                        item.getNombre() + " (" + item.getDuracion() + " h)");
            }
        });
    }

    @FXML
    private void guardar() {
        PacienteTabla paciente = cbPaciente.getValue();
        OdontologoTabla doctor = cbDoctor.getValue();
        ServicioTabla servicio = cbServicio.getValue();
        LocalDate fecha = dpFecha.getValue();
        String horaTexto = cbHora.getValue();
        String motivo = txtMotivo.getText().trim();
        String notas = txtNotas.getText().trim();

        if (paciente == null || doctor == null || servicio == null || fecha == null || horaTexto == null) {
            mostrarError("Paciente, doctor, servicio, fecha y hora son obligatorios.");
            return;
        }

        LocalTime hora;

        try {
            hora = LocalTime.parse(horaTexto);
        } catch (Exception e) {
            mostrarError("La hora debe tener formato HH:mm. Ejemplo: 09:30");
            return;
        }

        if (fecha.isBefore(LocalDate.now())) {
            mostrarError("No se puede registrar una cita en una fecha pasada.");
            return;
        }

        Cita cita = new Cita();
        cita.setPacienteId(paciente.getId());
        cita.setDoctorId(doctor.getId());
        cita.setServicioId(servicio.getId());
        cita.setFecha(fecha);
        cita.setHora(hora);
        cita.setDuracion(servicio.getDuracion());
        cita.setMotivoConsulta(motivo);
        cita.setNotas(notas);

        String resultado = citaService.validarCita(cita);

        if (resultado != null) {
            mostrarError(resultado);
            return;
        }

        boolean creado = citaService.crearCita(cita);

        if (creado) {

            auditoriaService.registrar(
                    "CREAR",
                    "CITAS",
                    "Cita creada para " +
                            paciente.getNombre() + " " +
                            paciente.getApellido() +
                            " el " + fecha +
                            " a las " + horaTexto
            );
            cerrar();
        } else {
            mostrarError("No se pudo crear la cita por un error en base de datos.");
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
}