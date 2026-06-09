package pe.nuevasonrisa.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import pe.nuevasonrisa.dao.impl.UsuarioGestionDAOImpl;
import pe.nuevasonrisa.model.Usuario;

public class NuevoUsuarioController {

    @FXML
    private TextField txtUsuario;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtApellido;

    @FXML
    private TextField txtDni;

    @FXML
    private TextField txtCelular;

    @FXML
    private ComboBox<String> cbRol;

    @FXML
    public void initialize() {

        cbRol.setItems(
                FXCollections.observableArrayList(
                        "Administrador",
                        "Doctor",
                        "Recepción"
                )
        );
    }

    @FXML
    private void guardar() {

        try {

            Usuario usuario = new Usuario();

            usuario.setUsuario(txtUsuario.getText());
            usuario.setPassword(txtPassword.getText());
            usuario.setNombre(txtNombre.getText());
            usuario.setApellido(txtApellido.getText());
            usuario.setDni(txtDni.getText());
            usuario.setCelular(txtCelular.getText());
            usuario.setRol(cbRol.getValue());

            boolean creado =
                    new UsuarioGestionDAOImpl()
                            .crearUsuario(usuario);

            if (creado) {

                Alert alert =
                        new Alert(Alert.AlertType.INFORMATION);

                alert.setContentText("Usuario creado correctamente");
                alert.showAndWait();

                cerrar();

            } else {

                Alert alert =
                        new Alert(Alert.AlertType.ERROR);

                alert.setContentText("No se pudo crear el usuario");
                alert.showAndWait();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void cancelar() {
        cerrar();
    }

    private void cerrar() {
        Stage stage =
                (Stage) txtUsuario.getScene().getWindow();

        stage.close();
    }
}