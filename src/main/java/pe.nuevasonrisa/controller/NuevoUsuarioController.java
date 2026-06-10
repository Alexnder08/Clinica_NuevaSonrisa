package pe.nuevasonrisa.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import pe.nuevasonrisa.dao.impl.UsuarioGestionDAOImpl;
import pe.nuevasonrisa.model.Usuario;

public class NuevoUsuarioController {

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPassword;
    @FXML private TextField txtNombre;
    @FXML private TextField txtApellido;
    @FXML private TextField txtDni;
    @FXML private TextField txtCelular;
    @FXML private ComboBox<String> cbRol;

    @FXML
    public void initialize() {
        cbRol.setItems(FXCollections.observableArrayList(
                "Administrador",
                "Doctor",
                "RecepciÃ³n"
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
        String rol = cbRol.getValue();

        if (usuarioTexto.isBlank() || password.isBlank()
                || nombre.isBlank() || apellido.isBlank()
                || dni.isBlank() || celular.isBlank()
                || rol == null) {
            mostrarError("Complete todos los campos.");
            return;
        }

        if (!textoValido(nombre)) {
            mostrarError("El nombre solo puede contener letras.");
            return;
        }

        if (!textoValido(apellido)) {
            mostrarError("El apellido solo puede contener letras.");
            return;
        }

        if (!dni.matches("\\d{8}")) {
            mostrarError("El DNI debe tener 8 dígitos.");
            return;
        }

        if (!celular.matches("^9\\d{8}$")) {
            mostrarError("El celular debe tener 9 dígitos y empezar con 9.");
            return;
        }

        if (password.length() < 6) {
            mostrarError("La contraseña debe tener mínimo 6 caracteres.");
            return;
        }

        Usuario usuario = new Usuario();
        usuario.setUsuario(usuarioTexto);
        usuario.setPassword(password);
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setDni(dni);
        usuario.setCelular(celular);
        usuario.setRol(rol);

        boolean creado = new UsuarioGestionDAOImpl().crearUsuario(usuario);

        if (creado) {
            mostrarInfo("Usuario creado correctamente.");
            cerrar();
        } else {
            mostrarError("No se pudo crear el usuario. Verifique si el usuario o DNI ya existe.");
        }
    }

    @FXML
    private void cancelar() {
        cerrar();
    }

    private boolean textoValido(String texto) {
        return texto != null &&
                texto.matches("^[A-Za-zÃÃ‰ÃÃ“ÃšÃ¡Ã©Ã­Ã³ÃºÃ‘Ã± ]{2,50}$");
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