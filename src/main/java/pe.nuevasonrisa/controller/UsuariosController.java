package pe.nuevasonrisa.controller;

import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
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
    private TextField txtBuscar;

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

        // Crear FilteredList a partir del cache de usuarios
        FilteredList<UsuarioTabla> filteredList = 
                new FilteredList<>(FXCollections.observableArrayList(usuariosCache));

        // Crear SortedList a partir de FilteredList para mantener el ordenamiento
        SortedList<UsuarioTabla> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(tablaUsuarios.comparatorProperty());

        // Establecer la SortedList en la tabla
        tablaUsuarios.setItems(sortedList);

        // Limpiar el campo de búsqueda
        txtBuscar.clear();
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
                            + " cambiÃ³ de estado"
            );

            cargarUsuarios();

            mostrarInfo(
                    "Ã‰xito",
                    "Estado del usuario actualizado."
            );
        } else {
            mostrarInfo(
                    "Error",
                    "No se pudo cambiar el estado del usuario."
            );
        }
    }

    @FXML
    private void buscarUsuarios() {
        String filtro = txtBuscar.getText().toLowerCase().trim();

        // Obtener el SortedList actual de la tabla
        if (tablaUsuarios.getItems() instanceof SortedList<?>) {
            SortedList<UsuarioTabla> sortedList = (SortedList<UsuarioTabla>) tablaUsuarios.getItems();
            FilteredList<UsuarioTabla> filteredList = (FilteredList<UsuarioTabla>) sortedList.getSource();

            // Establecer el predicate del FilteredList según el término de búsqueda
            if (filtro.isBlank()) {
                filteredList.setPredicate(null);
            } else {
                filteredList.setPredicate(usuario ->
                        usuario.getUsuario().toLowerCase().contains(filtro)
                                || usuario.getNombre().toLowerCase().contains(filtro)
                                || usuario.getApellido().toLowerCase().contains(filtro)
                                || usuario.getRol().toLowerCase().contains(filtro)
                );
            }
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