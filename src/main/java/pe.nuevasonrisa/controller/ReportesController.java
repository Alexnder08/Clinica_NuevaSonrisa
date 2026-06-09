package pe.nuevasonrisa.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import pe.nuevasonrisa.util.ExcelExporter;

import java.io.File;

import pe.nuevasonrisa.dao.impl.ReporteDAOImpl;
import pe.nuevasonrisa.model.ReporteCitasDoctor;
import pe.nuevasonrisa.model.ReporteEstado;
import pe.nuevasonrisa.model.ReporteServicio;
import pe.nuevasonrisa.service.ReporteService;

public class ReportesController {

    @FXML private TableView<ReporteCitasDoctor> tablaDoctores;
    @FXML private TableView<ReporteServicio> tablaServicios;
    @FXML private TableView<ReporteEstado> tablaEstados;

    @FXML private TableColumn<ReporteCitasDoctor,String> colDoctor;
    @FXML private TableColumn<ReporteCitasDoctor,Integer> colTotal;
    @FXML private TableColumn<ReporteCitasDoctor,Integer> colRealizadas;
    @FXML private TableColumn<ReporteCitasDoctor,Integer> colCanceladas;
    @FXML private TableColumn<ReporteCitasDoctor,Integer> colNoAsistio;

    @FXML private TableColumn<ReporteServicio,String> colServicio;
    @FXML private TableColumn<ReporteServicio,Integer> colTotalServicio;
    @FXML private TableColumn<ReporteServicio,Integer> colRealizadasServicio;
    @FXML private TableColumn<ReporteServicio,Integer> colCanceladasServicio;

    @FXML private TableColumn<ReporteEstado,String> colEstado;
    @FXML private TableColumn<ReporteEstado,Integer> colCantidad;

    private final ReporteService service =
            new ReporteService(new ReporteDAOImpl());

    @FXML
    public void initialize() {

        colDoctor.setCellValueFactory(new PropertyValueFactory<>("doctor"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("totalCitas"));
        colRealizadas.setCellValueFactory(new PropertyValueFactory<>("realizadas"));
        colCanceladas.setCellValueFactory(new PropertyValueFactory<>("canceladas"));
        colNoAsistio.setCellValueFactory(new PropertyValueFactory<>("noAsistio"));

        colServicio.setCellValueFactory(new PropertyValueFactory<>("servicio"));
        colTotalServicio.setCellValueFactory(new PropertyValueFactory<>("totalCitas"));
        colRealizadasServicio.setCellValueFactory(new PropertyValueFactory<>("realizadas"));
        colCanceladasServicio.setCellValueFactory(new PropertyValueFactory<>("canceladas"));

        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("total"));

        cargarReportes();
    }

    @FXML
    private void cargarReportes() {

        tablaDoctores.setItems(
                FXCollections.observableArrayList(
                        service.obtenerReporteCitasDoctor()
                )
        );

        tablaServicios.setItems(
                FXCollections.observableArrayList(
                        service.obtenerReporteServicios()
                )
        );

        tablaEstados.setItems(
                FXCollections.observableArrayList(
                        service.obtenerReporteEstados()
                )
        );
    }

    @FXML
    private void exportarExcel() {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar reporte Excel");
        fileChooser.setInitialFileName("NuevaSonrisa_Reportes.xlsx");

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Excel (*.xlsx)", "*.xlsx")
        );

        Stage stage = (Stage) tablaDoctores.getScene().getWindow();
        File archivo = fileChooser.showSaveDialog(stage);

        if (archivo == null) {
            return;
        }

        ExcelExporter.exportarReportes(
                service.obtenerReporteCitasDoctor(),
                service.obtenerReporteServicios(),
                service.obtenerReporteEstados(),
                archivo.getAbsolutePath()
        );
    }
}