package pe.nuevasonrisa.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import pe.nuevasonrisa.dao.impl.CitaDoctorDAOImpl;
import pe.nuevasonrisa.model.CitaDoctorTabla;
import pe.nuevasonrisa.model.Usuario;
import pe.nuevasonrisa.service.CitaDoctorService;
import pe.nuevasonrisa.util.SessionManager;

import java.util.List;

public class DashboardDoctorInicioController {

    @FXML
    private Label lblCitasHoy;

    @FXML
    private Label lblPendientes;

    @FXML
    private Label lblRealizadas;

    @FXML
    private TableView<CitaDoctorTabla> tablaCitasHoy;

    @FXML
    private TableColumn<CitaDoctorTabla,String> colPaciente;

    @FXML
    private TableColumn<CitaDoctorTabla,String> colHora;

    @FXML
    private TableColumn<CitaDoctorTabla,String> colEstado;

    private final CitaDoctorService service =
            new CitaDoctorService(
                    new CitaDoctorDAOImpl()
            );

    @FXML
    public void initialize() {

        Usuario doctor =
                SessionManager.getUsuarioActual();

        List<CitaDoctorTabla> citas =
                service.obtenerCitasHoy(
                        doctor.getId()
                );

        long pendientes =
                citas.stream()
                        .filter(c ->
                                "Pendiente".equalsIgnoreCase(
                                        c.getEstado()
                                )
                        )
                        .count();

        long realizadas =
                citas.stream()
                        .filter(c ->
                                "Realizado".equalsIgnoreCase(
                                        c.getEstado()
                                )
                        )
                        .count();

        lblCitasHoy.setText(
                String.valueOf(
                        citas.size()
                )
        );

        lblPendientes.setText(
                String.valueOf(
                        pendientes
                )
        );

        lblRealizadas.setText(
                String.valueOf(
                        realizadas
                )
        );

        colPaciente.setCellValueFactory(
                new PropertyValueFactory<>("paciente")
        );

        colHora.setCellValueFactory(
                new PropertyValueFactory<>("hora")
        );

        colEstado.setCellValueFactory(
                new PropertyValueFactory<>("estado")
        );

        tablaCitasHoy.setItems(
                FXCollections.observableArrayList(
                        citas
                )
        );
    }
}