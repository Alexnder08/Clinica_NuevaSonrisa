package pe.nuevasonrisa.controller;

import javafx.collections.FXCollections;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
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
import pe.nuevasonrisa.service.ConfirmacionReservaService;
import pe.nuevasonrisa.service.CorreoService;
import pe.nuevasonrisa.service.OdontologoService;
import pe.nuevasonrisa.service.PacienteService;
import pe.nuevasonrisa.service.ServicioService;
import pe.nuevasonrisa.util.FechaSistema;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class NuevaCitaController {

    @FXML private ComboBox<PacienteTabla> cbPaciente;
    @FXML private ComboBox<OdontologoTabla> cbDoctor;
    @FXML private ComboBox<ServicioTabla> cbServicio;
    @FXML private DatePicker dpFecha;
    @FXML private ComboBox<String> cbHora;
    @FXML private TextArea txtMotivo;
    @FXML private TextArea txtNotas;
    @FXML private Button btnGuardar;
    @FXML private Label lblEstado;

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

    private final ConfirmacionReservaService confirmacionReservaService =
            new ConfirmacionReservaService(new CorreoService());

    @FXML
    public void initialize() {
        cbPaciente.setItems(FXCollections.observableArrayList(
                pacienteService.obtenerPacientes().stream()
                        .filter(paciente -> "Activo".equalsIgnoreCase(paciente.getEstado()))
                        .toList()
        ));
        cbDoctor.setItems(FXCollections.observableArrayList(odontologoService.obtenerDisponiblesParaCita()));

        cbHora.setDisable(true);

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
            actualizarHorasDisponibles();
        });

        cbPaciente.setOnAction(event -> actualizarHorasDisponibles());
        cbServicio.setOnAction(event -> actualizarHorasDisponibles());
        dpFecha.setOnAction(event -> actualizarHorasDisponibles());
    }

    private void actualizarHorasDisponibles() {
        PacienteTabla paciente = cbPaciente.getValue();
        OdontologoTabla doctor = cbDoctor.getValue();
        ServicioTabla servicio = cbServicio.getValue();
        LocalDate fecha = dpFecha.getValue();

        cbHora.getItems().clear();
        cbHora.getSelectionModel().clearSelection();
        if (paciente == null || doctor == null || servicio == null || fecha == null) {
            cbHora.setDisable(true);
            return;
        }

        LocalDate hoy = FechaSistema.hoy();
        LocalTime ahora = FechaSistema.ahora();
        List<String> horas = citaService.obtenerHorasDisponibles(
                        paciente.getId(), doctor.getId(), fecha, servicio.getDuracion())
                .stream()
                .filter(hora -> !fecha.isEqual(hoy) || hora.isAfter(ahora))
                .map(LocalTime::toString)
                .toList();

        cbHora.setItems(FXCollections.observableArrayList(horas));
        cbHora.setDisable(horas.isEmpty());
        if (horas.isEmpty()) {
            lblEstado.setText("No hay horas disponibles para la seleccion actual.");
        } else {
            lblEstado.setText("Selecciona una de las " + horas.size() + " horas disponibles.");
        }
    }

    @FXML
    private void guardar() {
        PacienteTabla paciente = cbPaciente.getValue();
        OdontologoTabla doctor = cbDoctor.getValue();
        ServicioTabla servicio = cbServicio.getValue();
        LocalDate fecha = dpFecha.getValue();
        String horaTexto = cbHora.getValue();
        String motivo = texto(txtMotivo.getText());
        String notas = texto(txtNotas.getText());

        String camposFaltantes = camposFaltantes(
                paciente == null ? "paciente" : null,
                doctor == null ? "doctor" : null,
                servicio == null ? "servicio" : null,
                fecha == null ? "fecha" : null,
                campoFaltante(horaTexto, "hora"),
                campoFaltante(motivo, "motivo de consulta")
        );

        if (camposFaltantes != null) {
            mostrarError("Complete los campos obligatorios: " + camposFaltantes + ".");
            return;
        }

        LocalTime hora = parsearHora(horaTexto);
        if (hora == null) {
            return;
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
        cita.setPacienteId(paciente.getId());
        cita.setDoctorId(doctor.getId());
        cita.setServicioId(servicio.getId());
        cita.setFecha(fecha);
        cita.setHora(hora);
        cita.setDuracion(servicio.getDuracion());
        cita.setMotivoConsulta(motivo);
        cita.setNotas(notas.isBlank() ? null : notas);

        String resultado = citaService.validarCita(cita);

        if (resultado != null) {
            mostrarError(resultado);
            return;
        }

        boolean creado;
        try {
            creado = citaService.crearCita(cita);
        } catch (RuntimeException e) {
            String mensaje = e.getMessage() == null || e.getMessage().isBlank()
                    ? "No se pudo registrar la cita."
                    : e.getMessage();
            mostrarError(mensaje);
            return;
        }

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
            enviarConfirmacion(paciente, doctor, servicio, fecha, hora);
        } else {
            mostrarError("No se pudo crear la cita. Verifique la disponibilidad del odontologo y del paciente.");
        }
    }

    private void enviarConfirmacion(
            PacienteTabla paciente,
            OdontologoTabla doctor,
            ServicioTabla servicio,
            LocalDate fecha,
            LocalTime hora
    ) {
        btnGuardar.setDisable(true);
        lblEstado.setText("Reserva registrada. Enviando confirmacion por correo...");
        lblEstado.getStyleClass().setAll("form-status", "form-status-progress");

        CompletableFuture
                .supplyAsync(() -> confirmacionReservaService.enviar(paciente, doctor, servicio, fecha, hora))
                .exceptionally(error -> new ConfirmacionReservaService.Resultado(
                        false,
                        "La cita fue registrada, pero ocurrio un error al enviar la confirmacion."
                ))
                .thenAccept(resultado -> Platform.runLater(() -> {
                    Alert.AlertType tipo = resultado.enviado()
                            ? Alert.AlertType.INFORMATION
                            : Alert.AlertType.WARNING;
                    Alert alert = new Alert(tipo);
                    alert.setTitle(resultado.enviado() ? "Reserva confirmada" : "Reserva registrada");
                    alert.setHeaderText(resultado.enviado()
                            ? "Todo listo para la cita"
                            : "La cita se guardo correctamente");
                    alert.setContentText(resultado.mensaje());
                    alert.showAndWait();
                    cerrar();
                }));
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
                        item.getNombre() + " " + item.getApellido());
            }
        });

        cbDoctor.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(OdontologoTabla item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null :
                        item.getNombre() + " " + item.getApellido());
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
