package pe.nuevasonrisa.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import pe.nuevasonrisa.dao.impl.ServicioDAOImpl;
import pe.nuevasonrisa.model.Servicio;
import pe.nuevasonrisa.model.ServicioTabla;
import pe.nuevasonrisa.service.ServicioService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
        String nombre = txtNombre.getText().trim();
        String duracionTexto = txtDuracion.getText().trim();
        String costoTexto = txtCosto.getText().trim();

        String camposFaltantes = camposFaltantes(
                campoFaltante(nombre, "nombre"),
                campoFaltante(duracionTexto, "duración"),
                campoFaltante(costoTexto, "costo")
        );

        if (camposFaltantes != null) {
            mostrarError("Complete los campos obligatorios: " + camposFaltantes + ".");
            return;
        }

        Integer duracion = parsearDuracion(duracionTexto);
        if (duracion == null) {
            return;
        }

        BigDecimal costo = parsearCosto(costoTexto);
        if (costo == null) {
            return;
        }

        if (duracion <= 0) {
            mostrarError("La duración debe ser mayor a 0 minutos.");
            return;
        }

        if (costo.compareTo(BigDecimal.ZERO) < 0) {
            mostrarError("El costo no puede ser negativo.");
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
            mostrarError("No se pudo actualizar el servicio. Verifique que el nombre no esté repetido.");
        }
    }

    private Integer parsearDuracion(String duracionTexto) {
        try {
            return Integer.parseInt(duracionTexto);
        } catch (NumberFormatException e) {
            mostrarError("La duración debe ser un número entero en minutos. Ejemplo: 30");
            return null;
        }
    }

    private BigDecimal parsearCosto(String costoTexto) {
        try {
            return new BigDecimal(costoTexto);
        } catch (NumberFormatException e) {
            mostrarError("El costo debe ser un número válido. Ejemplo: 80.00");
            return null;
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
}
