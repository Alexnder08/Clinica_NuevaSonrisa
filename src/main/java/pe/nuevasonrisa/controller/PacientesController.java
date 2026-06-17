package pe.nuevasonrisa.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import pe.nuevasonrisa.dao.impl.PacienteDAOImpl;
import pe.nuevasonrisa.model.PacienteTabla;
import pe.nuevasonrisa.service.PacienteService;

import java.util.ArrayList;
import java.util.List;

public class PacientesController {

    @FXML private TextField txtBuscar;

    @FXML private TableView<PacienteTabla> tablaPacientes;

    @FXML private TableColumn<PacienteTabla, Integer> colId;
    @FXML private TableColumn<PacienteTabla, String> colDni;
    @FXML private TableColumn<PacienteTabla, String> colNombre;
    @FXML private TableColumn<PacienteTabla, String> colApellido;
    @FXML private TableColumn<PacienteTabla, String> colTelefono;
    @FXML private TableColumn<PacienteTabla, String> colCorreo;

    private final PacienteService service =
            new PacienteService(new PacienteDAOImpl());

    private List<PacienteTabla> pacientesCache = new ArrayList<>();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDni.setCellValueFactory(new PropertyValueFactory<>("dni"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));

        cargarPacientes();
    }

    @FXML
    private void cargarPacientes() {
        pacientesCache = service.obtenerPacientes();

        tablaPacientes.setItems(
                FXCollections.observableArrayList(pacientesCache)
        );
    }

    @FXML
    private void buscarPacientes() {
        String filtro = txtBuscar.getText().toLowerCase().trim();

        if (filtro.isBlank()) {
            tablaPacientes.setItems(
                    FXCollections.observableArrayList(pacientesCache)
            );
            return;
        }

        tablaPacientes.setItems(
                FXCollections.observableArrayList(
                        pacientesCache.stream()
                                .filter(p ->
                                        p.getDni().toLowerCase().contains(filtro)
                                                || p.getNombre().toLowerCase().contains(filtro)
                                                || p.getApellido().toLowerCase().contains(filtro)
                                                || p.getTelefono().toLowerCase().contains(filtro)
                                                || p.getCorreo().toLowerCase().contains(filtro)
                                )
                                .toList()
                )
        );
    }

    @FXML
    private void nuevoPaciente() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/nuevo_paciente.fxml")
            );

            Scene scene = new Scene(loader.load());

            Stage stage = new Stage();
            stage.setTitle("Nuevo Paciente");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            cargarPacientes();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void editarPaciente() {
        PacienteTabla seleccionado =
                tablaPacientes.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarInfo("Aviso", "Seleccione un paciente para editar.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/editar_paciente.fxml")
            );

            Scene scene = new Scene(loader.load());

            EditarPacienteController controller = loader.getController();
            controller.cargarPaciente(seleccionado);

            Stage stage = new Stage();
            stage.setTitle("Editar Paciente");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            cargarPacientes();

        } catch (Exception e) {
            e.printStackTrace();
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