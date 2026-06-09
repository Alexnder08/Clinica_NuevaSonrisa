package pe.nuevasonrisa.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Alert;

import java.util.List;
import java.util.ArrayList;

import pe.nuevasonrisa.dao.impl.UsuarioGestionDAOImpl;
import pe.nuevasonrisa.model.UsuarioTabla;
import pe.nuevasonrisa.service.UsuarioService;
import pe.nuevasonrisa.service.AuditoriaService;

public class UsuariosController {

    private List<UsuarioTabla> usuariosCache = new ArrayList<>();

    @FXML
    private TableView<UsuarioTabla> tablaUsuarios;

    @FXML
    private TableColumn<UsuarioTabla, Integer> colId;

    @FXML
    private TableColumn<UsuarioTabla, String> colUsuario;

    @FXML
    private TableColumn<UsuarioTabla, String> colNombre;

    @FXML
    private TableColumn<UsuarioTabla, String> colApellido;

    @FXML
    private TableColumn<UsuarioTabla, String> colRol;

    @FXML
    private TableColumn<UsuarioTabla, String> colEstado;

    private final UsuarioService service =
            new UsuarioService(new UsuarioGestionDAOImpl());

    private final AuditoriaService auditoriaService =
            new AuditoriaService();

    @FXML
    public void initialize() {

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colUsuario.setCellValueFactory(new PropertyValueFactory<>("usuario"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        colRol.setCellValueFactory(new PropertyValueFactory<>("rol"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        cargarUsuarios();
    }

    @FXML
    private void cargarUsuarios() {

        usuariosCache = service.obtenerUsuarios();

        tablaUsuarios.setItems(
                FXCollections.observableArrayList(
                        usuariosCache
                )
        );
    }

    @FXML
    private void nuevoUsuario() {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(
                                    "/fxml/nuevo_usuario.fxml"
                            )
                    );

            Scene scene =
                    new Scene(loader.load());

            Stage stage = new Stage();

            stage.setTitle("Nuevo Usuario");
            stage.setScene(scene);

            stage.initModality(Modality.APPLICATION_MODAL);

            stage.showAndWait();

            cargarUsuarios();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void editarUsuario() {
        UsuarioTabla seleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarInfo("Aviso", "Seleccione un usuario para editar.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/editar_usuario.fxml")
            );

            Scene scene = new Scene(loader.load());

            EditarUsuarioController controller = loader.getController();
            controller.cargarUsuario(seleccionado);

            Stage stage = new Stage();
            stage.setTitle("Editar Usuario");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            cargarUsuarios();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void cambiarEstado() {

        UsuarioTabla usuario =
                tablaUsuarios
                        .getSelectionModel()
                        .getSelectedItem();

        if (usuario == null) {
            mostrarInfo(
                    "Aviso",
                    "Seleccione un usuario."
            );
            return;
        }

        boolean ok =
                service.cambiarEstadoUsuario(
                        usuario.getId(),
                        usuario.getEstado()
                );

        if (ok) {

            auditoriaService.registrar(
                    "CAMBIAR ESTADO",
                    "USUARIOS",
                    "Usuario "
                            + usuario.getUsuario()
                            + " cambió de estado"
            );

            cargarUsuarios();

            mostrarInfo(
                    "Éxito",
                    "Estado del usuario actualizado."
            );
        } else {
            mostrarInfo(
                    "Error",
                    "No se pudo cambiar el estado del usuario."
            );
        }
    }

    private void mostrarInfo(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

}