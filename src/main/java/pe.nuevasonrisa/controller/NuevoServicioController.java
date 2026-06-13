package pe.nuevasonrisa.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import pe.nuevasonrisa.dao.impl.ServicioDAOImpl;
import pe.nuevasonrisa.model.Servicio;
import pe.nuevasonrisa.service.ServicioService;

import java.math.BigDecimal;

public class NuevoServicioController {

    @FXML private TextField txtNombre;
    @FXML private TextField txtDuracion;
    @FXML private TextField txtCosto;

    private final ServicioService service =
            new ServicioService(new ServicioDAOImpl());

    @FXML
    private void guardar() {
        try {
            String nombre = txtNombre.getText().trim();
            String duracionTexto = txtDuracion.getText().trim();
            String costoTexto = txtCosto.getText().trim();

            if (nombre.isBlank() || duracionTexto.isBlank() || costoTexto.isBlank()) {
                mostrarError("Todos los campos son obligatorios.");
                return;
            }

            int duracion = Integer.parseInt(duracionTexto);
            BigDecimal costo = new BigDecimal(costoTexto);

            if (duracion <= 0) {
                mostrarError("La duración debe ser mayor a 0.");
                return;
            }

            if (costo.compareTo(BigDecimal.ZERO) < 0) {
                mostrarError("El costo no puede ser negativo.");
                return;
            }

            Servicio servicio = new Servicio();
            servicio.setNombre(nombre);
            servicio.setDuracion(duracion);
            servicio.setCosto(costo);

            boolean creado = service.crearServicio(servicio);

            if (creado) {
                cerrar();
            } else {
                mostrarError("No se pudo crear el servicio. Verifique si el nombre ya existe.");
            }

        } catch (NumberFormatException e) {
            mostrarError("Duración y costo deben ser valores numericos.");
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