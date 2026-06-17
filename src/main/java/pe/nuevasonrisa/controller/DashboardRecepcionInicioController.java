package pe.nuevasonrisa.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import pe.nuevasonrisa.dao.impl.DashboardInicioDAOImpl;
import pe.nuevasonrisa.model.CitaHoy;
import pe.nuevasonrisa.service.DashboardInicioService;

public class DashboardRecepcionInicioController {

    @FXML private Label lblCitasHoy;
    @FXML private Label lblPendientes;
    @FXML private Label lblRealizadasHoy;
    @FXML private Label lblCanceladasHoy;

    @FXML private TableView<CitaHoy> tablaCitasDia;
    @FXML private TableColumn<CitaHoy, String> colPaciente;
    @FXML private TableColumn<CitaHoy, String> colHora;
    @FXML private TableColumn<CitaHoy, String> colDoctor;
    @FXML private TableColumn<CitaHoy, String> colEstado;

    private final DashboardInicioService service =
            new DashboardInicioService(new DashboardInicioDAOImpl());

    @FXML
    public void initialize() {

        lblCitasHoy.setText(String.valueOf(service.contarCitasHoy()));
        lblPendientes.setText(String.valueOf(service.contarPendientesHoy()));
        lblRealizadasHoy.setText(String.valueOf(service.contarRealizadasHoy()));
        lblCanceladasHoy.setText(String.valueOf(service.contarCanceladasHoy()));

        colPaciente.setCellValueFactory(new PropertyValueFactory<>("paciente"));
        colHora.setCellValueFactory(new PropertyValueFactory<>("hora"));
        colDoctor.setCellValueFactory(new PropertyValueFactory<>("doctor"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        tablaCitasDia.setItems(
                FXCollections.observableArrayList(
                        service.obtenerCitaHoy()
                )
        );
    }
}