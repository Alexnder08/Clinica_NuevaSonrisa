package pe.nuevasonrisa.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import pe.nuevasonrisa.dao.impl.ServicioDAOImpl;
import pe.nuevasonrisa.model.Servicio;
import pe.nuevasonrisa.model.ServicioTabla;
import pe.nuevasonrisa.service.ServicioService;

import java.math.BigDecimal;

public class EditarServicioController {

    @FXML private TextField txtNombre;
    @FXML private TextField txtDuracion;
    @FXML private TextField txtCosto;

    private int idServicio;

    private final ServicioService service =
            new ServicioService(new ServicioDAOImpl());

    public void cargarServicio(ServicioTabla servicio) {
        idServicio = servicio.getId();
        txtNombre.setText(servicio.getNombre());
        txtDuracion.setText(String.valueOf(servicio.getDuracion()));
        txtCosto.setText(servicio.getCosto().toString());
    }

    @FXML
    private void guardar() {
        try {
            String nombre = txtNombre.getText().trim();
            int duracion = Integer.parseInt(txtDuracion.getText().trim());
            BigDecimal costo = new BigDecimal(txtCosto.getText().trim());

            if (nombre.isBlank()) {
                mostrarError("El nombre es obligatorio.");
                return;
            }

            if (duracion <= 0) {
                mostrarError("La duración debe ser mayor a 0.");
                return;
            }

            Servicio servicio = new Servicio();
            servicio.setId(idServicio);
            servicio.setNombre(nombre);
            servicio.setDuracion(duracion);
            servicio.setCosto(costo);

            boolean actualizado = service.actualizarServicio(servicio);

            if (actualizado) {
                cerrar();
            } else {
                mostrarError("No se pudo actualizar el servicio.");
            }

        } catch (NumberFormatException e) {
            mostrarError("Duración y costo deben ser numericos.");
        }
    }

    @FXML
    private void cancelar() {
        cerrar();
    }

    private void cerrar() {
        Stage stage = (Stage) txtNombre.getScene().getWindow();
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