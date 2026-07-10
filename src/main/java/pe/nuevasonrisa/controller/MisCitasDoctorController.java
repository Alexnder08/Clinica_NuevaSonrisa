package pe.nuevasonrisa.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import pe.nuevasonrisa.dao.impl.CitaDoctorDAOImpl;
import pe.nuevasonrisa.model.CitaDoctorTabla;
import pe.nuevasonrisa.service.CitaDoctorService;
import pe.nuevasonrisa.util.SessionManager;
import pe.nuevasonrisa.dao.impl.CitaAdjuntoDAOImpl;
import pe.nuevasonrisa.model.CitaAdjunto;
import pe.nuevasonrisa.service.CitaAdjuntoService;
import javafx.stage.FileChooser;
import java.io.File;
import java.util.List;

public class MisCitasDoctorController {

    @FXML private TableView<CitaDoctorTabla> tablaCitas;

    @FXML private TableColumn<CitaDoctorTabla,String> colPaciente;
    @FXML private TableColumn<CitaDoctorTabla,Object> colFecha;
    @FXML private TableColumn<CitaDoctorTabla,String> colHora;
    @FXML private TableColumn<CitaDoctorTabla,String> colEstado;
    @FXML private TableColumn<CitaDoctorTabla,String> colServicio;
    @FXML private TableColumn<CitaDoctorTabla,String> colMotivo;
    @FXML private TableColumn<CitaDoctorTabla,String> colNotas;

    private final CitaDoctorService service =
            new CitaDoctorService(
                    new CitaDoctorDAOImpl()
            );

    private final CitaAdjuntoService adjuntoService = new CitaAdjuntoService(new CitaAdjuntoDAOImpl());

    @FXML
    public void initialize() {

        tablaCitas.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        colPaciente.setCellValueFactory(new PropertyValueFactory<>("paciente"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colHora.setCellValueFactory(new PropertyValueFactory<>("hora"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colServicio.setCellValueFactory(new PropertyValueFactory<>("servicio"));
        colMotivo.setCellValueFactory(new PropertyValueFactory<>("motivoConsulta"));
        colNotas.setCellValueFactory(new PropertyValueFactory<>("notas"));

        cargarDatos();
    }

    private void cargarDatos() {

        tablaCitas.setItems(
                FXCollections.observableArrayList(
                        service.obtenerMisCitas(
                                SessionManager
                                        .getUsuarioActual()
                                        .getId()
                        )
                )
        );
    }

    @FXML
    private void agregarNota() {

        CitaDoctorTabla cita =
                tablaCitas.getSelectionModel()
                        .getSelectedItem();

        if (cita == null) {

            Alert alert =
                    new Alert(Alert.AlertType.WARNING);

            alert.setHeaderText(null);
            alert.setContentText(
                    "Seleccione una cita."
            );

            alert.showAndWait();
            return;
        }

        TextInputDialog dialog =
                new TextInputDialog(
                        cita.getNotas()
                );

        dialog.setTitle("Notas");
        dialog.setHeaderText(
                "Agregar / Editar nota clínica"
        );

        dialog.showAndWait()
                .ifPresent(nota -> {

                    boolean ok =
                            service.actualizarNotas(
                                    cita.getId(),
                                    SessionManager
                                            .getUsuarioActual()
                                            .getId(),
                                    nota
                            );

                    if (ok) {
                        cargarDatos();
                    } else {
                        mostrarAviso("No se pudo guardar la nota clinica. Verifique que la cita le pertenezca.");
                    }
                });
    }

    @FXML
    private void finalizarCita() {
        CitaDoctorTabla cita = tablaCitas.getSelectionModel().getSelectedItem();
        if (cita == null) {
            mostrarAviso("Seleccione una cita.");
            return;
        }
        if (!"En espera".equalsIgnoreCase(cita.getEstado())) {
            mostrarAviso("La cita debe tener la asistencia confirmada y estar En espera.");
            return;
        }
        if (cita.getNotas() == null || cita.getNotas().isBlank()) {
            mostrarAviso("Registre una nota clinica antes de finalizar la cita.");
            return;
        }

        int doctorId = SessionManager.getUsuarioActual().getId();
        if (service.finalizarCita(cita.getId(), doctorId)) {
            cargarDatos();
            mostrarAviso("La cita fue finalizada correctamente.");
        } else {
            mostrarAviso("No se pudo finalizar la cita. Verifique su estado y las notas clinicas.");
        }
    }

    private void mostrarAviso(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    private void adjuntarArchivo() {
        CitaDoctorTabla cita = tablaCitas.getSelectionModel().getSelectedItem();
        if (cita == null) {
            mostrarAviso("Seleccione una cita.");
            return;
        }
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Adjuntar archivo a la cita");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(
                "Documentos e imagenes", "*.pdf", "*.png", "*.jpg", "*.jpeg", "*.doc", "*.docx"));
        File archivo = chooser.showOpenDialog(tablaCitas.getScene().getWindow());
        if (archivo == null) return;
        String error = adjuntoService.adjuntar(cita.getId(), SessionManager.getUsuarioActual().getId(), archivo);
        mostrarAviso(error == null ? "Archivo adjuntado correctamente." : error);
    }

    @FXML
    private void descargarAdjunto() {
        CitaDoctorTabla cita = tablaCitas.getSelectionModel().getSelectedItem();
        if (cita == null) {
            mostrarAviso("Seleccione una cita.");
            return;
        }
        int doctorId = SessionManager.getUsuarioActual().getId();
        List<CitaAdjunto> adjuntos = adjuntoService.listar(cita.getId(), doctorId);
        if (adjuntos.isEmpty()) {
            mostrarAviso("Esta cita no tiene archivos adjuntos.");
            return;
        }
        ChoiceDialog<CitaAdjunto> dialog = new ChoiceDialog<>(adjuntos.getFirst(), adjuntos);
        dialog.setTitle("Archivos adjuntos");
        dialog.setHeaderText("Seleccione el archivo que desea descargar");
        dialog.showAndWait().ifPresent(adjunto -> {
            FileChooser chooser = new FileChooser();
            chooser.setInitialFileName(adjunto.getNombreArchivo());
            File destino = chooser.showSaveDialog(tablaCitas.getScene().getWindow());
            if (destino != null) {
                mostrarAviso(adjuntoService.descargar(adjunto, doctorId, destino)
                        ? "Archivo guardado correctamente." : "No se pudo guardar el archivo.");
            }
        });
    }
}
