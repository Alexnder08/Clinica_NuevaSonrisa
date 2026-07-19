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
import pe.nuevasonrisa.dao.impl.CitaDAOImpl;
import pe.nuevasonrisa.model.CitaTabla;
import pe.nuevasonrisa.service.CitaService;
import pe.nuevasonrisa.service.AuditoriaService;

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
    @FXML private TableColumn<PacienteTabla, String> colEstado;

    private final PacienteService service =
            new PacienteService(new PacienteDAOImpl());

    private final CitaService citaService = new CitaService(new CitaDAOImpl());
    private final AuditoriaService auditoriaService = new AuditoriaService();

    private List<PacienteTabla> pacientesCache = new ArrayList<>();

    @FXML
    public void initialize() {
        tablaPacientes.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDni.setCellValueFactory(new PropertyValueFactory<>("dni"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

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
            pe.nuevasonrisa.util.AppLogger.error(getClass(), "Unhandled error while completing the operation.", e);
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
            pe.nuevasonrisa.util.AppLogger.error(getClass(), "Unhandled error while completing the operation.", e);
        }
    }

    @FXML
    private void verHistorialCitas() {
        PacienteTabla paciente = tablaPacientes.getSelectionModel().getSelectedItem();
        if (paciente == null) {
            mostrarInfo("Aviso", "Seleccione un paciente para consultar su historial.");
            return;
        }

        List<CitaTabla> historial = citaService.obtenerCitas().stream()
                .filter(cita -> cita.getPacienteId() == paciente.getId())
                .toList();

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Historial de citas");
        dialog.setHeaderText(paciente.getNombre() + " " + paciente.getApellido() + " - DNI " + paciente.getDni());
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        TableView<CitaTabla> tabla = new TableView<>(FXCollections.observableArrayList(historial));
        tabla.setPrefSize(850, 420);
        tabla.getColumns().add(columna("Fecha", "fecha", 110));
        tabla.getColumns().add(columna("Hora", "hora", 90));
        tabla.getColumns().add(columna("Odontologo", "doctor", 180));
        tabla.getColumns().add(columna("Tratamiento", "servicio", 180));
        tabla.getColumns().add(columna("Estado", "estado", 110));
        tabla.getColumns().add(columna("Motivo", "motivoConsulta", 200));
        dialog.getDialogPane().setContent(tabla);
        dialog.showAndWait();
    }

    @FXML
    private void cambiarEstadoPaciente() {
        PacienteTabla paciente = tablaPacientes.getSelectionModel().getSelectedItem();
        if (paciente == null) {
            mostrarInfo("Aviso", "Seleccione un paciente.");
            return;
        }
        if (service.cambiarEstado(paciente.getId(), paciente.getEstado())) {
            auditoriaService.registrar("CAMBIAR_ESTADO", "PACIENTES", "Paciente ID " + paciente.getId() + " cambio de estado.");
            cargarPacientes();
            mostrarInfo("Estado actualizado", "El estado del paciente fue actualizado correctamente.");
        } else {
            mostrarInfo("Error", "No se pudo actualizar el estado del paciente.");
        }
    }

    private <T> TableColumn<CitaTabla, T> columna(String titulo, String propiedad, double ancho) {
        TableColumn<CitaTabla, T> columna = new TableColumn<>(titulo);
        columna.setCellValueFactory(new PropertyValueFactory<>(propiedad));
        columna.setPrefWidth(ancho);
        return columna;
    }

    private void mostrarInfo(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
