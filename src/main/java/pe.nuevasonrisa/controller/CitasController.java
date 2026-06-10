package pe.nuevasonrisa.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import pe.nuevasonrisa.dao.impl.CitaDAOImpl;
import pe.nuevasonrisa.model.CitaTabla;
import pe.nuevasonrisa.service.CitaService;
import pe.nuevasonrisa.service.AuditoriaService;
import pe.nuevasonrisa.util.ExcelExporter;

import java.util.ArrayList;
import java.util.List;

public class CitasController {

    @FXML private TextField txtBuscar;

    @FXML private TableView<CitaTabla> tablaCitas;

    @FXML private TableColumn<CitaTabla,Integer> colId;
    @FXML private TableColumn<CitaTabla,String> colPaciente;
    @FXML private TableColumn<CitaTabla,String> colDoctor;
    @FXML private TableColumn<CitaTabla,String> colServicio;
    @FXML private TableColumn<CitaTabla,Object> colFecha;
    @FXML private TableColumn<CitaTabla,Object> colHora;
    @FXML private TableColumn<CitaTabla,String> colEstado;

    private final CitaService service =
            new CitaService(new CitaDAOImpl());

    private List<CitaTabla> citasCache =
            new ArrayList<>();

    private final AuditoriaService auditoriaService =
            new AuditoriaService();

    @FXML
    public void initialize() {

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colPaciente.setCellValueFactory(new PropertyValueFactory<>("paciente"));
        colDoctor.setCellValueFactory(new PropertyValueFactory<>("doctor"));
        colServicio.setCellValueFactory(new PropertyValueFactory<>("servicio"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colHora.setCellValueFactory(new PropertyValueFactory<>("hora"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        cargarCitas();
    }

    @FXML
    private void cargarCitas() {

        citasCache = service.obtenerCitas();

        tablaCitas.setItems(
                FXCollections.observableArrayList(
                        citasCache
                )
        );
    }

    @FXML
    private void buscarCitas() {

        String filtro = txtBuscar.getText() == null
                ? ""
                : txtBuscar.getText().toLowerCase().trim();

        if (filtro.isBlank()) {
            tablaCitas.setItems(
                    FXCollections.observableArrayList(citasCache)
            );
            return;
        }

        tablaCitas.setItems(
                FXCollections.observableArrayList(
                        citasCache.stream()
                                .filter(c ->
                                        c.getPaciente().toLowerCase().contains(filtro)
                                                || c.getDoctor().toLowerCase().contains(filtro)
                                                || c.getServicio().toLowerCase().contains(filtro)
                                                || c.getEstado().toLowerCase().contains(filtro)
                                                || c.getFecha().toString().contains(filtro)
                                )
                                .toList()
                )
        );
    }

    @FXML
    private void nuevaCita() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/nueva_cita.fxml")
            );

            Scene scene = new Scene(loader.load());

            Stage stage = new Stage();
            stage.setTitle("Nueva Cita");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            cargarCitas();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void editarCita() {

        CitaTabla cita =
                tablaCitas.getSelectionModel()
                        .getSelectedItem();

        if (cita == null) {

            mostrarInfo(
                    "Aviso",
                    "Seleccione una cita."
            );

            return;
        }

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(
                                    "/fxml/editar_cita.fxml"
                            )
                    );

            Scene scene =
                    new Scene(loader.load());

            EditarCitaController controller =
                    loader.getController();

            controller.cargarCita(cita);

            Stage stage = new Stage();

            stage.setTitle("Editar Cita");
            stage.setScene(scene);

            stage.initModality(
                    Modality.APPLICATION_MODAL
            );

            stage.showAndWait();

            cargarCitas();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void cancelarCita() {
        CitaTabla cita = tablaCitas.getSelectionModel().getSelectedItem();

        if (cita == null) {
            mostrarInfo("Aviso", "Seleccione una cita.");
            return;
        }

        if ("Realizado".equalsIgnoreCase(cita.getEstado())
                || "Cancelado".equalsIgnoreCase(cita.getEstado())) {
            mostrarInfo("Aviso", "No se puede cancelar una cita realizada o ya cancelada.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Cancelar cita");
        dialog.setHeaderText("Ingrese el motivo de cancelación");
        dialog.setContentText("Motivo:");

        dialog.showAndWait().ifPresent(motivo -> {
            if (motivo.trim().isBlank()) {
                mostrarInfo("Aviso", "El motivo de cancelaciÃ³n es obligatorio.");
                return;
            }

            boolean ok = service.cancelarCita(cita.getId(), motivo.trim());

            if (ok) {
                auditoriaService.registrar(
                        "CANCELAR",
                        "CITAS",
                        "Cita #" + cita.getId() + " cancelada. Motivo: " + motivo
                );
                cargarCitas();
            } else {
                mostrarInfo("Error", "No se pudo cancelar la cita.");
            }
        });
    }
    private void mostrarInfo(
            String titulo,
            String mensaje
    ) {

        Alert alert =
                new Alert(
                        Alert.AlertType.INFORMATION
                );

        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML private DatePicker dpFiltroFecha;

    @FXML
    private void filtrarPorFecha() {
        if (dpFiltroFecha.getValue() == null) {
            mostrarInfo("Aviso", "Seleccione una fecha.");
            return;
        }

        tablaCitas.setItems(
                FXCollections.observableArrayList(
                        citasCache.stream()
                                .filter(c -> c.getFecha().equals(dpFiltroFecha.getValue()))
                                .toList()
                )
        );
    }

    @FXML
    private void exportarCitas() {
        ExcelExporter.exportarCitas(
                tablaCitas.getItems()
        );
    }

}