package pe.nuevasonrisa.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import pe.nuevasonrisa.dao.UsuarioDAO;
import pe.nuevasonrisa.dao.impl.UsuarioDAOImpl;
import pe.nuevasonrisa.model.Usuario;
import pe.nuevasonrisa.service.AuthService;
import pe.nuevasonrisa.service.HistorialAccesoService;
import pe.nuevasonrisa.util.SessionManager;
import org.slf4j.Logger;
import pe.nuevasonrisa.util.AppLogger;
import pe.nuevasonrisa.service.AuditoriaService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LoginController {

    private static final Logger LOGGER = AppLogger.getLogger(LoginController.class);

    private static final String ROL_RECEPCION_LEGACY = "Recepci" + (char) 195 + (char) 179 + "n";

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
    private final AuditoriaService auditoriaService = new AuditoriaService();

    @FXML
    private void iniciarSesion() {
        String usuario = txtUsuario.getText().trim();
        String password = txtPassword.getText().trim();

        String camposFaltantes = camposFaltantes(
                campoFaltante(usuario, "usuario"),
                campoFaltante(password, "contraseña")
        );

        if (camposFaltantes != null) {
            mostrarMensajeError("Complete los campos obligatorios: " + camposFaltantes + ".");
            return;
        }

        Optional<Usuario> usuarioAutenticado;
        try {
            usuarioAutenticado = authService.login(usuario, password);
        } catch (IllegalStateException e) {
            LOGGER.error("No se pudo conectar a la base de datos durante el inicio de sesion.");
            mostrarMensajeError("No se pudo conectar con la base de datos. Intente nuevamente en unos segundos.");
            return;
        }

        if (usuarioAutenticado.isPresent()) {
            Usuario user = usuarioAutenticado.get();
            if (!"Activo".equalsIgnoreCase(user.getEstado())) {
                mostrarMensajeError("El usuario está inactivo. Contacte al administrador.");
                return;
            }

            SessionManager.setUsuarioActual(user);
            historialAccesoService.registrarLogin();
            auditoriaService.registrar("LOGIN", "SEGURIDAD", "Inicio de sesion exitoso.");
            LOGGER.info("Inicio de sesion exitoso.");

            if (user.getRol().equalsIgnoreCase("Administrador")) {
                abrirPantalla("/fxml/dashboard_admin.fxml");
            } else if (esRecepcion(user.getRol())) {
                abrirPantalla("/fxml/dashboard_recepcion.fxml");
            } else if (user.getRol().equalsIgnoreCase("Doctor")) {
                abrirPantalla("/fxml/dashboard_doctor.fxml");
            } else {
                mostrarMensajeError("El rol del usuario no está reconocido: " + user.getRol() + ".");
            }

        } else {
            intentosFallidos++;
            auditoriaService.registrarParaUsuario(usuario, "LOGIN_FALLIDO", "SEGURIDAD", "Credenciales invalidas.");
            LOGGER.warn("Intento fallido de inicio de sesion {}.", intentosFallidos);

            if (intentosFallidos >= 3) {
                mostrarMensajeError("Demasiados intentos fallidos. El sistema se cerrará.");

                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Acceso bloqueado");
                alert.setHeaderText(null);
                alert.setContentText("Ha superado los 3 intentos permitidos.");
                alert.showAndWait();

                System.exit(0);
                return;
            }

            mostrarMensajeError(
                    "Usuario o contraseña incorrectos. Intento "
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
            stage.setResizable(true);
            stage.setMinWidth(1100);
            stage.setMinHeight(700);
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.centerOnScreen();
            stage.show();

        } catch (Exception e) {
            pe.nuevasonrisa.util.AppLogger.error(getClass(), "Unhandled error while completing the operation.", e);
            mostrarMensajeError("No se pudo abrir la pantalla: " + e.getMessage());
        }
    }

    @FXML
    private void cambiarPassword() {
        abrirVentana("/fxml/cambiar_password.fxml", "Cambiar contraseña");
    }

    @FXML
    private void recuperarPassword() {
        abrirVentana("/fxml/recuperar_password.fxml", "Recuperar contraseña");
    }

    private void abrirVentana(String ruta, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(ruta));
            Scene scene = new Scene(loader.load());

            Stage stage = new Stage();
            stage.setTitle(titulo);
            stage.setScene(scene);
            stage.showAndWait();

        } catch (Exception e) {
            pe.nuevasonrisa.util.AppLogger.error(getClass(), "Unhandled error while completing the operation.", e);
            mostrarMensajeError("No se pudo abrir la ventana solicitada: " + titulo + ".");
        }
    }

    private void mostrarMensajeError(String mensaje) {
        lblMensaje.setStyle("-fx-text-fill: #d32f2f;");
        lblMensaje.setText(mensaje);
    }

    private boolean esRecepcion(String rol) {
        return "Recepción".equalsIgnoreCase(rol)
                || ROL_RECEPCION_LEGACY.equalsIgnoreCase(rol);
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
