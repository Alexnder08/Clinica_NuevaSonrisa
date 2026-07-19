package pe.nuevasonrisa.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import pe.nuevasonrisa.dao.impl.UsuarioGestionDAOImpl;
import pe.nuevasonrisa.model.Usuario;
import pe.nuevasonrisa.service.AuditoriaService;

import java.util.ArrayList;
import java.util.List;

public class NuevoUsuarioController {

    private final AuditoriaService auditoriaService = new AuditoriaService();

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPassword;
    @FXML private TextField txtNombre;
    @FXML private TextField txtApellido;
    @FXML private TextField txtDni;
    @FXML private TextField txtCelular;
    @FXML private TextField txtEmail;
    @FXML private ComboBox<String> cbRol;

    @FXML
    public void initialize() {
        cbRol.setItems(FXCollections.observableArrayList(
                "Administrador",
                "Doctor",
                "Recepción"
        ));
    }

    @FXML
    private void guardar() {
        String usuarioTexto = txtUsuario.getText().trim();
        String password = txtPassword.getText().trim();
        String nombre = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        String dni = txtDni.getText().trim();
        String celular = txtCelular.getText().trim();
        String email = txtEmail.getText().trim();
        String rol = cbRol.getValue();

        String camposFaltantes = camposFaltantes(
                campoFaltante(usuarioTexto, "usuario"),
                campoFaltante(password, "contraseña"),
                campoFaltante(nombre, "nombre"),
                campoFaltante(apellido, "apellido"),
                campoFaltante(dni, "DNI"),
                campoFaltante(celular, "celular"),
                campoFaltante(email, "correo"),
                rol == null ? "rol" : null
        );

        if (camposFaltantes != null) {
            mostrarError("Complete los campos obligatorios: " + camposFaltantes + ".");
            return;
        }

        if (!textoValido(nombre)) {
            mostrarError("El nombre solo puede contener letras y debe tener entre 2 y 50 caracteres.");
            return;
        }

        if (!textoValido(apellido)) {
            mostrarError("El apellido solo puede contener letras y debe tener entre 2 y 50 caracteres.");
            return;
        }

        if (!dni.matches("\\d{8}")) {
            mostrarError("El DNI debe tener exactamente 8 dígitos numéricos.");
            return;
        }

        if (!celular.matches("^9\\d{8}$")) {
            mostrarError("El celular debe tener exactamente 9 dígitos y empezar con 9.");
            return;
        }

        if (password.length() < 6) {
            mostrarError("La contraseña debe tener mínimo 6 caracteres.");
            return;
        }

        if (!email.matches("^[A-Za-z][A-Za-z0-9._%+-]{2,}@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            mostrarError("Ingrese un correo valido. Ejemplo: usuario@correo.com");
            return;
        }

        Usuario usuario = new Usuario();
        usuario.setUsuario(usuarioTexto);
        usuario.setPassword(password);
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setDni(dni);
        usuario.setCelular(celular);
        usuario.setEmail(email);
        usuario.setRol(rol);

        boolean creado = new UsuarioGestionDAOImpl().crearUsuario(usuario);

        if (creado) {
            auditoriaService.registrar("CREAR", "USUARIOS", "Cuenta de usuario creada.");
            mostrarInfo("Usuario creado correctamente.");
            cerrar();
        } else {
            mostrarError("No se pudo crear el usuario. Verifique que el usuario o el DNI no estén repetidos.");
        }
    }

    @FXML
    private void cancelar() {
        cerrar();
    }

    private boolean textoValido(String texto) {
        return texto != null && texto.matches("^[A-Za-zÁÉÍÓÚáéíóúÑñ ]{2,50}$");
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
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarInfo(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void cerrar() {
        Stage stage = (Stage) txtUsuario.getScene().getWindow();
        stage.close();
    }
}
