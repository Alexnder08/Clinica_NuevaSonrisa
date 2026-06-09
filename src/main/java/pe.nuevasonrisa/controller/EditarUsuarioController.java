package pe.nuevasonrisa.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import pe.nuevasonrisa.dao.impl.UsuarioGestionDAOImpl;
import pe.nuevasonrisa.model.Usuario;
import pe.nuevasonrisa.model.UsuarioTabla;

public class EditarUsuarioController {

    @FXML private TextField txtUsuario;
    @FXML private TextField txtNombre;
    @FXML private TextField txtApellido;
    @FXML private TextField txtDni;
    @FXML private TextField txtCelular;
    @FXML private ComboBox<String> cbRol;

    private UsuarioTabla usuarioSeleccionado;

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
        cbRol.setValue(usuario.getRol());
    }

    @FXML
    private void guardar() {
        if (usuarioSeleccionado == null) {
            return;
        }

        Usuario usuario = new Usuario();

        usuario.setId(usuarioSeleccionado.getId());
        usuario.setUsuario(txtUsuario.getText());
        usuario.setNombre(txtNombre.getText());
        usuario.setApellido(txtApellido.getText());
        usuario.setDni(txtDni.getText());
        usuario.setCelular(txtCelular.getText());
        usuario.setRol(cbRol.getValue());

        boolean actualizado =
                new UsuarioGestionDAOImpl().actualizarUsuario(usuario);

        if (actualizado) {
            cerrar();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("No se pudo actualizar. Verifique que el DNI o usuario no estén repetidos.");
            alert.showAndWait();
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
}