package pe.nuevasonrisa.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class DashboardRecepcionController {

    @FXML
    private Label lblTitulo;

    @FXML
    private StackPane contenedorContenido;

    @FXML
    public void initialize() {
        mostrarInicio();
        cargarVista("/fxml/dashboard_recepcion_inicio.fxml");
    }

    @FXML
    private void mostrarInicio() {

        lblTitulo.setText("Dashboard");

        cargarVista(
                "/fxml/dashboard_recepcion_inicio.fxml"
        );
    }

    @FXML
    private void mostrarServicios() {

        lblTitulo.setText("Servicios");

        cargarVista(
                "/fxml/servicios.fxml"
        );
    }

    @FXML
    private void mostrarPacientes() {

        lblTitulo.setText("Pacientes");

        cargarVista(
                "/fxml/pacientes.fxml"
        );
    }

    @FXML
    private void mostrarOdontologos() {

        lblTitulo.setText("Odontólogos");

        cargarVista(
                "/fxml/odontologos.fxml"
        );
    }

    @FXML
    private void mostrarCitas() {

        lblTitulo.setText("Citas");

        cargarVista(
                "/fxml/citas.fxml"
        );
    }

    @FXML
    private void mostrarReportes() {

        lblTitulo.setText("Reportes");

        cargarVista(
                "/fxml/reportes.fxml"
        );
    }

    @FXML
    private void cerrarSesion() {
        pe.nuevasonrisa.util.SessionManager.cerrarSesion();
        System.exit(0);
    }

    private void cargarVista(String ruta) {

        try {
            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(ruta)
                    );

            Node vista =
                    loader.load();

            contenedorContenido
                    .getChildren()
                    .setAll(vista);

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}