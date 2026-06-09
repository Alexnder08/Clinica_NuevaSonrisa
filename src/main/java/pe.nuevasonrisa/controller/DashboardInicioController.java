package pe.nuevasonrisa.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import pe.nuevasonrisa.dao.impl.DashboardInicioDAOImpl;
import pe.nuevasonrisa.model.Auditoria;
import pe.nuevasonrisa.model.CitaHoy;
import pe.nuevasonrisa.model.DashboardResumen;
import pe.nuevasonrisa.service.DashboardInicioService;

public class DashboardInicioController {

    @FXML private Label lblPacientes;
    @FXML private Label lblPendientes;
    @FXML private Label lblHoy;
    @FXML private Label lblOdontologos;

    @FXML private TableView<Auditoria> tablaAuditoria;
    @FXML private TableColumn<Auditoria, String> colUsuario;
    @FXML private TableColumn<Auditoria, String> colAccion;
    @FXML private TableColumn<Auditoria, String> colModulo;
    @FXML private TableColumn<Auditoria, String> colDetalle;

    @FXML private TableView<CitaHoy> tablaCitasHoy;
    @FXML private TableColumn<CitaHoy, String> colPacienteHoy;
    @FXML private TableColumn<CitaHoy, String> colHoraHoy;
    @FXML private TableColumn<CitaHoy, String> colDoctorHoy;

    private final DashboardInicioService service =
            new DashboardInicioService(new DashboardInicioDAOImpl());

    @FXML
    public void initialize() {

        DashboardResumen resumen = service.obtenerResumen();

        lblPacientes.setText(String.valueOf(resumen.getPacientes()));
        lblPendientes.setText(String.valueOf(resumen.getCitasPendientes()));
        lblHoy.setText(String.valueOf(resumen.getCitasHoy()));
        lblOdontologos.setText(String.valueOf(resumen.getOdontologos()));

        colUsuario.setCellValueFactory(new PropertyValueFactory<>("usuario"));
        colAccion.setCellValueFactory(new PropertyValueFactory<>("accion"));
        colModulo.setCellValueFactory(new PropertyValueFactory<>("modulo"));
        colDetalle.setCellValueFactory(new PropertyValueFactory<>("detalle"));

        tablaAuditoria.setItems(
                FXCollections.observableArrayList(
                        service.obtenerUltimasAcciones()
                )
        );

        colPacienteHoy.setCellValueFactory(new PropertyValueFactory<>("paciente"));
        colHoraHoy.setCellValueFactory(new PropertyValueFactory<>("hora"));
        colDoctorHoy.setCellValueFactory(new PropertyValueFactory<>("doctor"));

        tablaCitasHoy.setItems(
                FXCollections.observableArrayList(
                        service.obtenerCitaHoy()
                )
        );
    }
}