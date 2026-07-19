package pe.nuevasonrisa.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import pe.nuevasonrisa.dao.impl.OdontologoDAOImpl;
import pe.nuevasonrisa.model.OdontologoTabla;
import pe.nuevasonrisa.service.OdontologoService;
import pe.nuevasonrisa.controller.AsignarServiciosDoctorController;

import java.util.ArrayList;
import java.util.List;

public class OdontologosController {

    @FXML private TextField txtBuscar;

    @FXML private TableView<OdontologoTabla> tablaOdontologos;

    @FXML private TableColumn<OdontologoTabla,Integer> colId;
    @FXML private TableColumn<OdontologoTabla,String> colUsuario;
    @FXML private TableColumn<OdontologoTabla,String> colNombre;
    @FXML private TableColumn<OdontologoTabla,String> colApellido;
    @FXML private TableColumn<OdontologoTabla,String> colDni;
    @FXML private TableColumn<OdontologoTabla,String> colCelular;
    @FXML private TableColumn<OdontologoTabla,String> colServicio;
    @FXML private TableColumn<OdontologoTabla,String> colEstado;

    private final OdontologoService service =
            new OdontologoService(new OdontologoDAOImpl());

    private List<OdontologoTabla> odontologosCache =
            new ArrayList<>();

    @FXML
    public void initialize() {
        tablaOdontologos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colUsuario.setCellValueFactory(new PropertyValueFactory<>("usuario"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        colDni.setCellValueFactory(new PropertyValueFactory<>("dni"));
        colCelular.setCellValueFactory(new PropertyValueFactory<>("celular"));
        colServicio.setCellValueFactory(new PropertyValueFactory<>("servicio"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        cargarOdontologos();
    }

    @FXML
    private void cargarOdontologos() {
        odontologosCache = service.obtenerOdontologos();

        tablaOdontologos.setItems(
                FXCollections.observableArrayList(odontologosCache)
        );
    }

    @FXML
    private void buscarOdontologos() {
        String filtro = txtBuscar.getText().toLowerCase().trim();

        tablaOdontologos.setItems(
                FXCollections.observableArrayList(
                        odontologosCache.stream()
                                .filter(o ->
                                        o.getNombre().toLowerCase().contains(filtro)
                                                || o.getApellido().toLowerCase().contains(filtro)
                                                || o.getDni().toLowerCase().contains(filtro)
                                                || o.getServicio().toLowerCase().contains(filtro)
                                )
                                .toList()
                )
        );
    }

    @FXML
    private void verHorarios() {
        OdontologoTabla doctor =
                tablaOdontologos.getSelectionModel().getSelectedItem();

        if (doctor == null) {
            mostrarInfo("Aviso", "Seleccione un odontólogo.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/horarios_doctor.fxml")
            );

            Scene scene = new Scene(loader.load());

            HorariosDoctorController controller =
                    loader.getController();

            controller.cargarDoctor(doctor.getId());

            Stage stage = new Stage();
            stage.setTitle(
                    "Horarios - " + doctor.getNombre() + " " + doctor.getApellido()
            );
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (Exception e) {
            pe.nuevasonrisa.util.AppLogger.error(getClass(), "Unhandled error while completing the operation.", e);
            mostrarInfo("Error", "No se pudo abrir los horarios del doctor.");
        }
    }

    private void mostrarInfo(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    private void asignarServicios(){

        OdontologoTabla doctor =
                tablaOdontologos.getSelectionModel().getSelectedItem();

        if (doctor == null){
            mostrarInfo(
                    "Aviso",
                    "Seleccione un odontólogo"
            );
            return;
        }

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/fxml/asignar_servicios_doctor.fxml"
                    )
            );

            Scene scene = new Scene(loader.load());

            AsignarServiciosDoctorController controller =
                    loader.getController();

            controller.cargarDoctor(
                    doctor.getId(),
                    doctor.getNombre() + " " + doctor.getApellido()
            );

            Stage stage = new Stage();
            stage.setTitle("Servicos del Doctor");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (Exception e) {
            pe.nuevasonrisa.util.AppLogger.error(getClass(), "Unhandled error while completing the operation.", e);

            mostrarInfo(
                    "Error",
                    "No se pudo abrir la ventana de servicios"
            );
        }
    }
}
