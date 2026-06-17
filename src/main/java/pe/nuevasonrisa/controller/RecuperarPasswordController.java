package pe.nuevasonrisa.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import pe.nuevasonrisa.dao.impl.RecuperacionPasswordDAOImpl;
import pe.nuevasonrisa.service.CorreoService;
import pe.nuevasonrisa.service.PasswordRecoveryService;

import java.util.ArrayList;
import java.util.List;

public class RecuperarPasswordController {

    @FXML private TextField txtCorreo;
    @FXML private TextField txtCodigo;
    @FXML private PasswordField txtNueva;
    @FXML private PasswordField txtConfirmar;
    @FXML private Label lblMensaje;

    private final PasswordRecoveryService service =
            new PasswordRecoveryService(
                    new RecuperacionPasswordDAOImpl(),
                    new CorreoService()
            );

    @FXML
    private void enviarCodigo() {
        String correo = txtCorreo.getText().trim();

        if (correo.isBlank()) {
            mostrarMensaje("Complete el campo obligatorio: correo.", false);
            return;
        }

        if (!correo.matches("^[A-Za-z][A-Za-z0-9._%+-]{2,}@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            mostrarMensaje("Ingrese un correo válido. Ejemplo: usuario@correo.com", false);
            return;
        }

        String mensaje = service.solicitarRestablecimiento(correo);
        mostrarMensaje(mensaje, mensaje.startsWith("Se envió") || mensaje.startsWith("Se generó"));
    }

    @FXML
    private void restablecer() {
        String correo = txtCorreo.getText().trim();
        String codigo = txtCodigo.getText().trim();
        String nueva = txtNueva.getText().trim();
        String confirmar = txtConfirmar.getText().trim();

        String camposFaltantes = camposFaltantes(
                campoFaltante(correo, "correo"),
                campoFaltante(codigo, "código"),
                campoFaltante(nueva, "nueva contraseña"),
                campoFaltante(confirmar, "confirmación de contraseña")
        );

        if (camposFaltantes != null) {
            mostrarMensaje("Complete los campos obligatorios: " + camposFaltantes + ".", false);
            return;
        }

        String mensaje = service.restablecerConCodigo(correo, codigo, nueva, confirmar);

        boolean exito = mensaje.startsWith("Contraseña actualizada");
        mostrarMensaje(mensaje, exito);

        if (exito) {
            cerrar();
        }
    }

    @FXML
    private void cancelar() {
        cerrar();
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

    private void mostrarMensaje(String mensaje, boolean exito) {
        lblMensaje.setStyle(exito
                ? "-fx-text-fill: #0f9d58;"
                : "-fx-text-fill: #d32f2f;");
        lblMensaje.setText(mensaje);

        Alert.AlertType tipo = exito ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR;
        Alert alert = new Alert(tipo);
        alert.setTitle(exito ? "Éxito" : "Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void cerrar() {
        Stage stage = (Stage) txtCorreo.getScene().getWindow();
        stage.close();
    }
}
