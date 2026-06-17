package pe.nuevasonrisa.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import pe.nuevasonrisa.dao.impl.UsuarioDAOImpl;
import pe.nuevasonrisa.service.AuthService;

import java.util.ArrayList;
import java.util.List;

public class CambiarPasswordController {

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtActual;
    @FXML private PasswordField txtNueva;
    @FXML private PasswordField txtConfirmar;
    @FXML private Label lblMensaje;

    private final AuthService authService =
            new AuthService(new UsuarioDAOImpl());

    @FXML
    private void guardar() {
        String usuario = txtUsuario.getText().trim();
        String actual = txtActual.getText().trim();
        String nueva = txtNueva.getText().trim();
        String confirmar = txtConfirmar.getText().trim();

        String camposFaltantes = camposFaltantes(
                campoFaltante(usuario, "usuario"),
                campoFaltante(actual, "contraseña actual"),
                campoFaltante(nueva, "nueva contraseña"),
                campoFaltante(confirmar, "confirmación de contraseña")
        );

        if (camposFaltantes != null) {
            mostrarMensajeError("Complete los campos obligatorios: " + camposFaltantes + ".");
            return;
        }

        if (nueva.length() < 6) {
            mostrarMensajeError("La nueva contraseña debe tener mínimo 6 caracteres.");
            return;
        }

        if (!nueva.equals(confirmar)) {
            mostrarMensajeError("La nueva contraseña y su confirmación no coinciden.");
            return;
        }

        boolean ok = authService.cambiarPassword(usuario, actual, nueva);

        if (ok) {
            lblMensaje.setStyle("-fx-text-fill: green;");
            lblMensaje.setText("Contraseña actualizada.");

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Éxito");
            alert.setHeaderText(null);
            alert.setContentText("Contraseña actualizada correctamente.");
            alert.showAndWait();

            Stage stage = (Stage) txtUsuario.getScene().getWindow();
            stage.close();
        } else {
            mostrarMensajeError("No se pudo cambiar la contraseña. Verifique el usuario y la contraseña actual.");
        }
    }

    private void mostrarMensajeError(String mensaje) {
        lblMensaje.setStyle("-fx-text-fill: red;");
        lblMensaje.setText(mensaje);
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
}
