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
import pe.nuevasonrisa.dao.impl.PacienteDAOImpl;
import pe.nuevasonrisa.model.CitaTabla;
import pe.nuevasonrisa.model.PacienteTabla;
import pe.nuevasonrisa.service.CitaService;
import pe.nuevasonrisa.service.AuditoriaService;
import pe.nuevasonrisa.service.CorreoService;
import pe.nuevasonrisa.service.NotificacionCitaService;
import pe.nuevasonrisa.service.PacienteService;
import pe.nuevasonrisa.util.ExcelExporter;
import pe.nuevasonrisa.util.PdfExporter;
import pe.nuevasonrisa.util.CalendarioCitasDialog;
import javafx.print.PrinterJob;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

public class CitasController {

    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> cbFiltroServicio;
    @FXML private ComboBox<String> cbFiltroDoctor;
    @FXML private ComboBox<String> cbFiltroEstado;
    @FXML private DatePicker dpFechaDesde;
    @FXML private DatePicker dpFechaHasta;
    @FXML private Label lblResultados;

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
    private final PacienteService pacienteService = new PacienteService(new PacienteDAOImpl());
    private final NotificacionCitaService notificacionService =
            new NotificacionCitaService(new CorreoService());

    @FXML
    public void initialize() {

        tablaCitas.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
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
        cargarOpcionesFiltros();
        aplicarFiltros();
    }

    @FXML
    private void buscarCitas() {
        aplicarFiltros();
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
                mostrarInfo("Aviso", "El motivo de cancelación es obligatorio.");
                return;
            }

            boolean ok = service.cancelarCita(cita.getId(), motivo.trim());

            if (ok) {
                auditoriaService.registrar(
                        "CANCELAR",
                        "CITAS",
                        "Cita #" + cita.getId() + " cancelada. Motivo: " + motivo
                );
                pacienteService.obtenerPacientes().stream()
                        .filter(paciente -> paciente.getId() == cita.getPacienteId())
                        .findFirst()
                        .ifPresent(paciente -> notificacionService.enviarCancelacion(
                                cita, paciente, motivo.trim()
                        ));
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

    @FXML
    private void aplicarFiltros() {
        String texto = valor(txtBuscar.getText()).toLowerCase();
        String servicio = cbFiltroServicio == null ? null : cbFiltroServicio.getValue();
        String doctor = cbFiltroDoctor == null ? null : cbFiltroDoctor.getValue();
        String estado = cbFiltroEstado == null ? null : cbFiltroEstado.getValue();
        LocalDate desde = dpFechaDesde == null ? null : dpFechaDesde.getValue();
        LocalDate hasta = dpFechaHasta == null ? null : dpFechaHasta.getValue();

        if (desde != null && hasta != null && desde.isAfter(hasta)) {
            mostrarInfo("Rango invalido", "La fecha inicial no puede ser posterior a la fecha final.");
            return;
        }

        List<CitaTabla> filtradas = citasCache.stream()
                .filter(c -> texto.isBlank()
                        || c.getPaciente().toLowerCase().contains(texto)
                        || c.getDoctor().toLowerCase().contains(texto)
                        || c.getServicio().toLowerCase().contains(texto)
                        || c.getEstado().toLowerCase().contains(texto))
                .filter(c -> esTodos(servicio) || c.getServicio().equalsIgnoreCase(servicio))
                .filter(c -> esTodos(doctor) || c.getDoctor().equalsIgnoreCase(doctor))
                .filter(c -> esTodos(estado) || c.getEstado().equalsIgnoreCase(estado))
                .filter(c -> desde == null || !c.getFecha().isBefore(desde))
                .filter(c -> hasta == null || !c.getFecha().isAfter(hasta))
                .toList();

        tablaCitas.setItems(FXCollections.observableArrayList(filtradas));
        if (lblResultados != null) {
            lblResultados.setText(filtradas.size() + " cita(s) encontrada(s)");
        }
    }

    @FXML
    private void limpiarFiltros() {
        txtBuscar.clear();
        cbFiltroServicio.setValue("Todos los tratamientos");
        cbFiltroDoctor.setValue("Todos los odontologos");
        cbFiltroEstado.setValue("Todos los estados");
        dpFechaDesde.setValue(null);
        dpFechaHasta.setValue(null);
        aplicarFiltros();
    }

    @FXML
    private void verPendientesHoy() {
        limpiarFiltros();
        LocalDate hoy = LocalDate.now();
        dpFechaDesde.setValue(hoy);
        dpFechaHasta.setValue(hoy);
        cbFiltroEstado.setValue("Pendiente");
        aplicarFiltros();
    }

    @FXML
    private void verPacientesEnEspera() {
        limpiarFiltros();
        LocalDate hoy = LocalDate.now();
        dpFechaDesde.setValue(hoy);
        dpFechaHasta.setValue(hoy);
        cbFiltroEstado.setValue("En espera");
        aplicarFiltros();
    }

    @FXML
    private void confirmarAsistencia() {
        CitaTabla cita = tablaCitas.getSelectionModel().getSelectedItem();
        if (cita == null) {
            mostrarInfo("Aviso", "Seleccione la cita del paciente que acaba de llegar.");
            return;
        }
        if (!LocalDate.now().equals(cita.getFecha())) {
            mostrarInfo("Aviso", "Solo se puede confirmar la asistencia de citas del dia de hoy.");
            return;
        }
        if (!"Pendiente".equalsIgnoreCase(cita.getEstado())) {
            mostrarInfo("Aviso", "Solo una cita pendiente puede pasar a la lista de espera.");
            return;
        }

        if (service.cambiarEstado(cita.getId(), "En espera")) {
            auditoriaService.registrar(
                    "CONFIRMAR_ASISTENCIA",
                    "CITAS",
                    "Asistencia confirmada para la cita #" + cita.getId() + " de " + cita.getPaciente()
            );
            cargarCitas();
            mostrarInfo("Asistencia confirmada", "El paciente fue agregado a la lista de espera.");
        } else {
            mostrarInfo("Error", "No se pudo confirmar la asistencia del paciente.");
        }
    }

    private void cargarOpcionesFiltros() {
        String servicioActual = cbFiltroServicio.getValue();
        String doctorActual = cbFiltroDoctor.getValue();
        String estadoActual = cbFiltroEstado.getValue();

        List<String> servicios = new ArrayList<>();
        servicios.add("Todos los tratamientos");
        servicios.addAll(citasCache.stream().map(CitaTabla::getServicio).distinct().sorted().toList());
        cbFiltroServicio.setItems(FXCollections.observableArrayList(servicios));

        List<String> doctores = new ArrayList<>();
        doctores.add("Todos los odontologos");
        doctores.addAll(citasCache.stream().map(CitaTabla::getDoctor).distinct().sorted().toList());
        cbFiltroDoctor.setItems(FXCollections.observableArrayList(doctores));

        List<String> estados = new ArrayList<>();
        estados.add("Todos los estados");
        estados.addAll(citasCache.stream().map(CitaTabla::getEstado).distinct().sorted().toList());
        if (!estados.contains("En espera")) {
            estados.add("En espera");
        }
        cbFiltroEstado.setItems(FXCollections.observableArrayList(estados));

        cbFiltroServicio.setValue(servicios.contains(servicioActual) ? servicioActual : servicios.getFirst());
        cbFiltroDoctor.setValue(doctores.contains(doctorActual) ? doctorActual : doctores.getFirst());
        cbFiltroEstado.setValue(estados.contains(estadoActual) ? estadoActual : estados.getFirst());
    }

    private boolean esTodos(String valor) {
        return valor == null || valor.startsWith("Todos");
    }

    private String valor(String texto) {
        return texto == null ? "" : texto.trim();
    }

    @FXML
    private void exportarCitas() {
        ExcelExporter.exportarCitas(
                tablaCitas.getItems()
        );
    }

    @FXML
    private void exportarCitasPdf() {
        try {
            List<CitaTabla> citas = new ArrayList<>(tablaCitas.getItems());
            if (citas.isEmpty()) {
                mostrarInfo("Aviso", "No hay citas para exportar.");
                return;
            }
            PdfExporter.exportarCitas(citas, tablaCitas.getScene().getWindow());
        } catch (Exception e) {
            mostrarInfo("Error", "No se pudo generar el PDF: " + e.getMessage());
        }
    }

    @FXML
    private void imprimirCitas() {
        PrinterJob trabajo = PrinterJob.createPrinterJob();
        if (trabajo == null) {
            mostrarInfo("Impresion", "No se encontro una impresora disponible.");
            return;
        }
        if (trabajo.showPrintDialog(tablaCitas.getScene().getWindow())
                && trabajo.printPage(tablaCitas)) {
            trabajo.endJob();
        }
    }

    @FXML
    private void verCalendario() {
        new CalendarioCitasDialog(citasCache).mostrar();
    }

}
