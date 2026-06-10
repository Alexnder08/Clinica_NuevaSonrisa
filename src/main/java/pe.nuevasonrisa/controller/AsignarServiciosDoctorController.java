package pe.nuevasonrisa.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import pe.nuevasonrisa.dao.impl.DoctorServicioDAOImpl;
import pe.nuevasonrisa.dao.impl.ServicioDAOImpl;
import pe.nuevasonrisa.model.ServicioTabla;
import pe.nuevasonrisa.service.DoctorServicioService;
import pe.nuevasonrisa.service.ServicioService;

import java.util.ArrayList;
import java.util.List;

public class AsignarServiciosDoctorController {

    @FXML
    private Label lblDoctor;

    @FXML
    private VBox contenedorServicios;

    private int doctorId;

    private final ServicioService servicioService =
            new ServicioService(
                    new ServicioDAOImpl()
            );

    private final DoctorServicioService doctorServicioService =
            new DoctorServicioService(
                    new DoctorServicioDAOImpl()
            );

    private final List<CheckBox> checkBoxes =
            new ArrayList<>();

    public void cargarDoctor(
            int doctorId,
            String nombreDoctor
    ) {

        this.doctorId = doctorId;

        lblDoctor.setText(
                "Servicios de " + nombreDoctor
        );

        cargarServicios();
    }

    private void cargarServicios() {

        contenedorServicios.getChildren().clear();
        checkBoxes.clear();

        List<ServicioTabla> servicios =
                servicioService.obtenerServicios();

        List<Integer> asignados =
                doctorServicioService
                        .obtenerServiciosDoctor(
                                doctorId
                        );

        for (ServicioTabla servicio : servicios) {

            CheckBox cb =
                    new CheckBox(
                            servicio.getNombre()
                                    + " ("
                                    + servicio.getDuracion()
                                    + " min)"
                    );

            cb.setUserData(
                    servicio.getId()
            );

            cb.setSelected(
                    asignados.contains(
                            servicio.getId()
                    )
            );

            checkBoxes.add(cb);

            contenedorServicios
                    .getChildren()
                    .add(cb);
        }
    }

    @FXML
    private void guardar() {

        List<Integer> seleccionados =
                new ArrayList<>();

        for (CheckBox cb : checkBoxes) {

            if (cb.isSelected()) {

                seleccionados.add(
                        (Integer) cb.getUserData()
                );
            }
        }

        if (seleccionados.isEmpty()) {
            mostrarError("Debe asignar al menos un servicio al odontÃ³logo.");
            return;
        }

        boolean ok =
                doctorServicioService
                        .guardarServiciosDoctor(
                                doctorId,
                                seleccionados
                        );

        if (ok) {

            Alert alert =
                    new Alert(
                            Alert.AlertType.INFORMATION
                    );

            alert.setHeaderText(null);
            alert.setContentText(
                    "Servicios actualizados correctamente."
            );

            alert.showAndWait();

            cerrar();

        } else {

            Alert alert =
                    new Alert(
                            Alert.AlertType.ERROR
                    );

            alert.setHeaderText(null);
            alert.setContentText(
                    "No se pudieron guardar los cambios."
            );

            alert.showAndWait();
        }
    }

    @FXML
    private void cancelar() {
        cerrar();
    }

    private void cerrar() {

        Stage stage =
                (Stage) lblDoctor
                        .getScene()
                        .getWindow();

        stage.close();
    }

    @FXML
    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}