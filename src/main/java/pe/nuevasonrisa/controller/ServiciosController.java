package pe.nuevasonrisa.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;
import java.util.ArrayList;

import pe.nuevasonrisa.dao.impl.ServicioDAOImpl;
import pe.nuevasonrisa.model.ServicioTabla;
import pe.nuevasonrisa.service.ServicioService;

public class ServiciosController {

    private List<ServicioTabla> serviciosCache = new ArrayList<>();

    @FXML private TextField txtBuscar;

    @FXML private TableView<ServicioTabla> tablaServicios;

    @FXML private TableColumn<ServicioTabla,Integer> colId;
    @FXML private TableColumn<ServicioTabla,String> colNombre;
    @FXML private TableColumn<ServicioTabla,Integer> colDuracion;
    @FXML private TableColumn<ServicioTabla,Object> colCosto;

    private final ServicioService service =
            new ServicioService(new ServicioDAOImpl());

    @FXML
    public void initialize() {

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDuracion.setCellValueFactory(new PropertyValueFactory<>("duracion"));
        colCosto.setCellValueFactory(new PropertyValueFactory<>("costo"));

        cargarServicios();
    }

    @FXML
    private void cargarServicios() {

        serviciosCache =
                service.obtenerServicios();

        tablaServicios.setItems(
                FXCollections.observableArrayList(
                        serviciosCache
                )
        );
    }

    @FXML
    private void buscarServicios() {

        String filtro =
                txtBuscar.getText()
                        .toLowerCase()
                        .trim();

        if (filtro.isBlank()) {

            tablaServicios.setItems(
                    FXCollections.observableArrayList(
                            serviciosCache
                    )
            );

            return;
        }

        tablaServicios.setItems(
                FXCollections.observableArrayList(
                        serviciosCache.stream()
                                .filter(s ->
                                        s.getNombre()
                                                .toLowerCase()
                                                .contains(filtro)
                                )
                                .toList()
                )
        );
    }

    @FXML
    private void nuevoServicio() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/nuevo_servicio.fxml")
            );

            Scene scene = new Scene(loader.load());

            Stage stage = new Stage();
            stage.setTitle("Nuevo Servicio");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            cargarServicios();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void editarServicio() {
        ServicioTabla seleccionado = tablaServicios.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarInfo("Aviso", "Seleccione un servicio para editar.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/editar_servicio.fxml")
            );

            Scene scene = new Scene(loader.load());

            EditarServicioController controller = loader.getController();
            controller.cargarServicio(seleccionado);

            Stage stage = new Stage();
            stage.setTitle("Editar Servicio");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            cargarServicios();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mostrarInfo(String titulo,
                             String mensaje) {

        Alert alert =
                new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);

        alert.showAndWait();
    }
}