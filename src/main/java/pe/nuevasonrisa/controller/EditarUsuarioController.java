package pe.nuevasonrisa.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import pe.nuevasonrisa.dao.impl.UsuarioGestionDAOImpl;
import pe.nuevasonrisa.model.Usuario;
import pe.nuevasonrisa.model.UsuarioTabla;
import pe.nuevasonrisa.service.AuditoriaService;

import java.util.ArrayList;
import java.util.List;

public class EditarUsuarioController {

    private static final String ROL_RECEPCION_LEGACY = "Recepci" + (char) 195 + (char) 179 + "n";

    @FXML private TextField txtUsuario;
    @FXML private TextField txtNombre;
    @FXML private TextField txtApellido;
    @FXML private TextField txtDni;
    @FXML private TextField txtCelular;
    @FXML private TextField txtEmail;
    @FXML private ComboBox<String> cbRol;

    private UsuarioTabla usuarioSeleccionado;
    private final AuditoriaService auditoriaService = new AuditoriaService();

    @FXML
    public void initialize() {
        cbRol.setItems(FXCollections.observableArrayList(
                "Administrador",
                "Doctor",
                "Recepción"
        ));
    }

    public void cargarUsuario(UsuarioTabla usuario) {
        this.usuarioSeleccionado = usuario;

        txtUsuario.setText(usuario.getUsuario());
        txtNombre.setText(usuario.getNombre());
        txtApellido.setText(usuario.getApellido());
        txtDni.setText(usuario.getDni());
        txtCelular.setText(usuario.getCelular());
        txtEmail.setText(usuario.getEmail());
        cbRol.setValue(normalizarRol(usuario.getRol()));
    }

    @FXML
    private void guardar() {
        if (usuarioSeleccionado == null) {
            return;
        }

        String usuarioTexto = txtUsuario.getText().trim();
        String nombre = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        String dni = txtDni.getText().trim();
        String celular = txtCelular.getText().trim();
        String email = txtEmail.getText().trim();
        String rol = cbRol.getValue();

        String camposFaltantes = camposFaltantes(
                campoFaltante(usuarioTexto, "usuario"),
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

        if (!email.matches("^[A-Za-z][A-Za-z0-9._%+-]{2,}@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            mostrarError("Ingrese un correo valido. Ejemplo: usuario@correo.com");
            return;
        }

        Usuario usuario = new Usuario();
        usuario.setId(usuarioSeleccionado.getId());
        usuario.setUsuario(usuarioTexto);
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setDni(dni);
        usuario.setCelular(celular);
        usuario.setEmail(email);
        usuario.setRol(rol);

        boolean actualizado = new UsuarioGestionDAOImpl().actualizarUsuario(usuario);

        if (actualizado) {
            auditoriaService.registrar("EDITAR", "USUARIOS", "Cuenta de usuario ID " + usuario.getId() + " actualizada.");
            cerrar();
        } else {
            mostrarError("No se pudo actualizar el usuario. Verifique que el usuario o el DNI no estén repetidos.");
        }
    }

    @FXML
    private void cancelar() {
        cerrar();
    }

    private void cerrar() {
        Stage stage = (Stage) txtUsuario.getScene().getWindow();
        stage.close();
    }

    private boolean textoValido(String texto) {
        return texto != null && texto.matches("^[A-Za-zÁÉÍÓÚáéíóúÑñ ]{2,50}$");
    }

    private String normalizarRol(String rol) {
        return ROL_RECEPCION_LEGACY.equalsIgnoreCase(rol) ? "Recepción" : rol;
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
}
