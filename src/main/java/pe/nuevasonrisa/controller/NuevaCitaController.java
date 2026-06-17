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
import pe.nuevasonrisa.model.OdontologoTabla;
import pe.nuevasonrisa.model.PacienteTabla;
import pe.nuevasonrisa.model.ServicioTabla;
import pe.nuevasonrisa.service.AuditoriaService;
import pe.nuevasonrisa.service.CitaService;
import pe.nuevasonrisa.service.OdontologoService;
import pe.nuevasonrisa.service.PacienteService;
import pe.nuevasonrisa.service.ServicioService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

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
        cbPaciente.setItems(FXCollections.observableArrayList(pacienteService.obtenerPacientes()));
        cbDoctor.setItems(FXCollections.observableArrayList(odontologoService.obtenerOdontologos()));

        cbHora.setItems(FXCollections.observableArrayList(
                "08:00", "09:00", "10:00", "11:00",
                "12:00", "13:00", "14:00", "15:00",
                "16:00", "17:00", "18:00"
        ));

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

    @FXML
    private void guardar() {
        PacienteTabla paciente = cbPaciente.getValue();
        OdontologoTabla doctor = cbDoctor.getValue();
        ServicioTabla servicio = cbServicio.getValue();
        LocalDate fecha = dpFecha.getValue();
        String horaTexto = cbHora.getValue();
        String motivo = txtMotivo.getText().trim();
        String notas = txtNotas.getText().trim();

        String camposFaltantes = camposFaltantes(
                paciente == null ? "paciente" : null,
                doctor == null ? "doctor" : null,
                servicio == null ? "servicio" : null,
                fecha == null ? "fecha" : null,
                campoFaltante(horaTexto, "hora"),
                campoFaltante(motivo, "motivo de consulta"),
                campoFaltante(notas, "notas")
        );

        if (camposFaltantes != null) {
            mostrarError("Complete los campos obligatorios: " + camposFaltantes + ".");
            return;
        }

        LocalTime hora = parsearHora(horaTexto);
        if (hora == null) {
            return;
        }

        LocalDate hoy = LocalDate.now(ZoneId.of("America/Lima"));
        LocalTime ahora = LocalTime.now(ZoneId.of("America/Lima"))
                .withSecond(0)
                .withNano(0);

        if (fecha.isBefore(hoy)) {
            mostrarError("La fecha de la cita no puede ser anterior a hoy.");
            return;
        }

        if (fecha.isEqual(hoy) && !hora.isAfter(ahora)) {
            mostrarError("La hora de la cita debe ser posterior a la hora actual.");
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
            mostrarError("No se pudo crear la cita. Verifique la disponibilidad del doctor y vuelva a intentar.");
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

    private String camposFaltantes(String... campos) {
        List<String> faltantes = new ArrayList<>();
        for (String campo : campos) {
            if (campo != null) {
                faltantes.add(campo);
            }
        }
        return faltantes.isEmpty() ? null : String.join(", ", faltantes);
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
