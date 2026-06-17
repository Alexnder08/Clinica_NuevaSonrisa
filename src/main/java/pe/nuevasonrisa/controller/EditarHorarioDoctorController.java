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
import pe.nuevasonrisa.model.HorarioDoctorTabla;
import pe.nuevasonrisa.model.OdontologoTabla;
import pe.nuevasonrisa.service.HorarioDoctorService;
import pe.nuevasonrisa.service.OdontologoService;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class EditarHorarioDoctorController {

    private static final String MIERCOLES_LEGACY = "Mi" + (char) 195 + (char) 169 + "rcoles";

    @FXML private ComboBox<OdontologoTabla> cbDoctor;
    @FXML private ComboBox<String> cbDia;
    @FXML private TextField txtHoraInicio;
    @FXML private TextField txtHoraFin;

    private HorarioDoctorTabla horarioActual;

    private final OdontologoService odontologoService =
            new OdontologoService(new OdontologoDAOImpl());

    private final HorarioDoctorService horarioService =
            new HorarioDoctorService(new HorarioDoctorDAOImpl());

    @FXML
    public void initialize() {
        cbDoctor.setItems(FXCollections.observableArrayList(odontologoService.obtenerOdontologos()));
        cbDia.setItems(FXCollections.observableArrayList(
                "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"
        ));
        configurarVistaDoctor();
    }

    public void cargarHorario(HorarioDoctorTabla horario) {
        this.horarioActual = horario;

        cbDoctor.getItems().stream()
                .filter(d -> d.getId() == horario.getDoctorId())
                .findFirst()
                .ifPresent(d -> cbDoctor.getSelectionModel().select(d));

        cbDia.setValue(normalizarDia(horario.getDia()));
        txtHoraInicio.setText(horario.getHoraInicio().toString().substring(0, 5));
        txtHoraFin.setText(horario.getHoraFin().toString().substring(0, 5));
    }

    @FXML
    private void guardar() {
        if (horarioActual == null) {
            mostrarError("No se encontró el horario seleccionado.");
            return;
        }

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

        boolean actualizado = horarioService.actualizarHorario(
                horarioActual.getId(),
                convertirDiaANumero(dia),
                horaInicio,
                horaFin
        );

        if (actualizado) {
            cerrar();
        } else {
            mostrarError("No se pudo actualizar el horario. Verifique los datos e inténtelo nuevamente.");
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

    private String normalizarDia(String dia) {
        if (MIERCOLES_LEGACY.equalsIgnoreCase(dia)) {
            return "Miércoles";
        }
        if ("Sabado".equalsIgnoreCase(dia)) {
            return "Sábado";
        }
        return dia;
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
