package pe.nuevasonrisa.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import pe.nuevasonrisa.dao.impl.UsuarioDAOImpl;
import pe.nuevasonrisa.service.AuthService;

public class CambiarPasswordController {

    @FXML
    private TextField txtUsuario;

    @FXML
    private PasswordField txtActual;

    @FXML
    private PasswordField txtNueva;

    @FXML
    private PasswordField txtConfirmar;

    @FXML
    private Label lblMensaje;

    private final AuthService authService =
            new AuthService(
                    new UsuarioDAOImpl()
            );

    @FXML
    private void guardar() {

        if (!txtNueva.getText()
                .equals(txtConfirmar.getText())) {

            lblMensaje.setText(
                    "Las contraseÃ±as no coinciden."
            );

            return;
        }

        boolean ok =
                authService.cambiarPassword(
                        txtUsuario.getText(),
                        txtActual.getText(),
                        txtNueva.getText()
                );

        if (ok) {

            lblMensaje.setStyle(
                    "-fx-text-fill: green;"
            );

            lblMensaje.setText(
                    "ContraseÃ±a actualizada."
            );

            Alert alert =
                    new Alert(Alert.AlertType.INFORMATION);

            alert.setTitle("Ã‰xito");
            alert.setHeaderText(null);
            alert.setContentText(
                    "ContraseÃ±a actualizada correctamente."
            );

            alert.showAndWait();

            Stage stage =
                    (Stage) txtUsuario
                            .getScene()
                            .getWindow();

            stage.close();

        } else {

            lblMensaje.setStyle(
                    "-fx-text-fill: red;"
            );

            lblMensaje.setText(
                    "Usuario o contraseÃ±a actual incorrecta."
            );
        }
    }
}