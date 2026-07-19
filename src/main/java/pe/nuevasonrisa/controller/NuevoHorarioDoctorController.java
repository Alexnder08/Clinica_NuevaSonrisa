package pe.nuevasonrisa.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import pe.nuevasonrisa.dao.impl.HorarioDoctorDAOImpl;
import pe.nuevasonrisa.dao.impl.OdontologoDAOImpl;
import pe.nuevasonrisa.model.OdontologoTabla;
import pe.nuevasonrisa.service.HorarioDoctorService;
import pe.nuevasonrisa.service.OdontologoService;
import pe.nuevasonrisa.service.AuditoriaService;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class NuevoHorarioDoctorController {

    @FXML private ComboBox<OdontologoTabla> cbDoctor;
    @FXML private ComboBox<String> cbDia;
    @FXML private TextField txtHoraInicio;
    @FXML private TextField txtHoraFin;

    private final OdontologoService odontologoService =
            new OdontologoService(new OdontologoDAOImpl());

    private final HorarioDoctorService horarioService =
            new HorarioDoctorService(new HorarioDoctorDAOImpl());
    private final AuditoriaService auditoriaService = new AuditoriaService();

    @FXML
    public void initialize() {
        cbDoctor.setItems(FXCollections.observableArrayList(odontologoService.obtenerOdontologos()));
        cbDia.setItems(FXCollections.observableArrayList(
                "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"
        ));
        configurarVistaDoctor();
    }

    @FXML
    private void guardar() {
        OdontologoTabla doctor = cbDoctor.getValue();
        String dia = cbDia.getValue();
        String horaInicio = txtHoraInicio.getText().trim();
        String horaFin = txtHoraFin.getText().trim();

        String camposFaltantes = camposFaltantes(
                doctor == null ? "doctor" : null,
                campoFaltante(dia, "día"),
                campoFaltante(horaInicio, "hora de inicio"),
                campoFaltante(horaFin, "hora de fin")
        );

        if (camposFaltantes != null) {
            mostrarError("Complete los campos obligatorios: " + camposFaltantes + ".");
            return;
        }

        if (!horasValidas(horaInicio, horaFin)) {
            return;
        }

        String validacion = horarioService.validarNuevoHorario(
                doctor.getId(),
                convertirDiaANumero(dia),
                horaInicio,
                horaFin
        );

        if (validacion != null) {
            mostrarError(validacion);
            return;
        }

        boolean creado = horarioService.crearHorario(
                doctor.getId(),
                convertirDiaANumero(dia),
                horaInicio,
                horaFin
        );

        if (creado) {
            auditoriaService.registrar("CREAR", "HORARIOS", "Horario creado para odontologo ID " + doctor.getId() + ".");
            cerrar();
        } else {
            mostrarError("No se pudo crear el horario. Verifique los datos e inténtelo nuevamente.");
        }
    }

    @FXML
    private void cancelar() {
        cerrar();
    }

    private boolean horasValidas(String horaInicio, String horaFin) {
        try {
            LocalTime inicio = LocalTime.parse(horaInicio);
            LocalTime fin = LocalTime.parse(horaFin);

            if (!inicio.isBefore(fin)) {
                mostrarError("La hora de inicio debe ser menor que la hora de fin.");
                return false;
            }

            return true;
        } catch (Exception e) {
            mostrarError("Las horas deben tener formato HH:mm. Ejemplo: 09:00");
            return false;
        }
    }

    private int convertirDiaANumero(String dia) {
        return switch (dia) {
            case "Lunes" -> 1;
            case "Martes" -> 2;
            case "Miércoles" -> 3;
            case "Jueves" -> 4;
            case "Viernes" -> 5;
            case "Sábado" -> 6;
            case "Domingo" -> 7;
            default -> 1;
        };
    }

    private void configurarVistaDoctor() {
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

    private void cerrar() {
        Stage stage = (Stage) cbDoctor.getScene().getWindow();
        stage.close();
    }
}
