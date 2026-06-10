package pe.nuevasonrisa.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import pe.nuevasonrisa.dao.UsuarioDAO;
import pe.nuevasonrisa.dao.impl.UsuarioDAOImpl;
import pe.nuevasonrisa.service.HistorialAccesoService;
import pe.nuevasonrisa.model.Usuario;
import pe.nuevasonrisa.service.AuthService;
import pe.nuevasonrisa.util.SessionManager;
import javafx.scene.control.Alert;

import java.util.Optional;

public class LoginController {

    private int intentosFallidos = 0;

    @FXML
    private TextField txtUsuario;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Label lblMensaje;

    private final UsuarioDAO usuarioDAO = new UsuarioDAOImpl();
    private final AuthService authService = new AuthService(usuarioDAO);
    private final HistorialAccesoService historialAccesoService = new HistorialAccesoService();

    @FXML
    private void iniciarSesion() {
        String usuario = txtUsuario.getText();
        String password = txtPassword.getText();

        Optional<Usuario> usuarioAutenticado = authService.login(usuario, password);

        if (usuarioAutenticado.isPresent()) {
            Usuario user = usuarioAutenticado.get();
            if (!"Activo".equalsIgnoreCase(user.getEstado())) {
                lblMensaje.setStyle("-fx-text-fill: #d32f2f;");
                lblMensaje.setText("Usuario inactivo. Contacte al administrador.");
                return;
            }

            SessionManager.setUsuarioActual(user);
            historialAccesoService.registrarLogin();

            if (user.getRol().equalsIgnoreCase("Administrador")) {
                abrirPantalla("/fxml/dashboard_admin.fxml");
            } else if (user.getRol().equalsIgnoreCase("RecepciÃ³n")) {
                abrirPantalla("/fxml/dashboard_recepcion.fxml");
            } else if (user.getRol().equalsIgnoreCase("Doctor")) {
                abrirPantalla("/fxml/dashboard_doctor.fxml");
            } else {
                lblMensaje.setStyle("-fx-text-fill: #d32f2f;");
                lblMensaje.setText("Rol no reconocido.");
            }

        } else {

            intentosFallidos++;

            if (intentosFallidos >= 3) {
                lblMensaje.setStyle("-fx-text-fill: #d32f2f;");
                lblMensaje.setText("Demasiados intentos fallidos. El sistema se cerrarÃ¡.");

                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Acceso bloqueado");
                alert.setHeaderText(null);
                alert.setContentText("Ha superado los 3 intentos permitidos.");
                alert.showAndWait();

                System.exit(0);
                return;
            }

            lblMensaje.setStyle("-fx-text-fill: #d32f2f;");
            lblMensaje.setText(
                    "Usuario o contraseÃ±a incorrectos. Intento "
                            + intentosFallidos
                            + " de 3."
            );
        }
    }

    private void abrirPantalla(String ruta) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(ruta));
            Scene scene = new Scene(loader.load());

            Stage stage = (Stage) txtUsuario.getScene().getWindow();
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            lblMensaje.setStyle("-fx-text-fill: #d32f2f;");
            lblMensaje.setText(e.getMessage());
        }
    }

    @FXML
    private void cambiarPassword() {
        try {
            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(
                                    "/fxml/cambiar_password.fxml"
                            )
                    );

            Scene scene =
                    new Scene(loader.load());

            Stage stage = new Stage();
            stage.setTitle("Cambiar contraseÃ±a");
            stage.setScene(scene);
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            lblMensaje.setText("No se pudo abrir cambio de contraseÃ±a.");
        }
    }
}