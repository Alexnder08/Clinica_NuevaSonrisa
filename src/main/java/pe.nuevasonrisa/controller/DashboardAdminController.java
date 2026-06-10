package pe.nuevasonrisa.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import pe.nuevasonrisa.model.Usuario;
import pe.nuevasonrisa.util.SessionManager;

public class DashboardAdminController {

    @FXML
    private Label lblTitulo;

    @FXML
    private Label lblUsuario;

    @FXML
    private StackPane contenedorContenido;

    @FXML
    public void initialize() {
        Usuario usuario = SessionManager.getUsuarioActual();

        if (usuario != null) {
            lblUsuario.setText(usuario.getNombreCompleto() + " | " + usuario.getRol());
        }

        mostrarInicio();

    }



    @FXML
    private void mostrarInicio() {
        lblTitulo.setText("Dashboard Administrador");
        cargarVista("/fxml/dashboard_inicio.fxml");
    }

    @FXML
    private void mostrarUsuarios() {
        lblTitulo.setText("GestiÃ³n de Usuarios");
        cargarVista("/fxml/usuarios.fxml");
    }

    @FXML
    private void mostrarServicios() {
        lblTitulo.setText("GestiÃ³n de Servicios");
        cargarVista("/fxml/servicios.fxml");
    }

    @FXML
    private void mostrarPacientes() {
        lblTitulo.setText("GestiÃ³n de Pacientes");
        cargarVista("/fxml/pacientes.fxml");
    }

    @FXML
    private void mostrarOdontologos() {
        lblTitulo.setText("GestiÃ³n de OdontÃ³logos");
        cargarVista("/fxml/odontologos.fxml");
    }

    @FXML
    private void mostrarCitas() {
        lblTitulo.setText("GestiÃ³n de Citas");
        cargarVista("/fxml/citas.fxml");
    }

    @FXML
    private void mostrarReportes() {
        lblTitulo.setText("Reportes");
        cargarVista("/fxml/reportes.fxml");
    }

    @FXML
    private void mostrarAccesos() {
        lblTitulo.setText("Accesos");
        cargarVista("/fxml/accesos.fxml");
    }

    @FXML
    private void mostrarAuditoria() {
        lblTitulo.setText("AuditorÃ­a");
        cargarVista("/fxml/auditoria.fxml");
    }



    private void mostrarTexto(String texto) {
        Label label = new Label(texto);
        label.setWrapText(true);
        label.setStyle("-fx-font-size: 16px; -fx-text-fill: #455a64;");
        contenedorContenido.getChildren().setAll(label);
    }

    private void cargarVista(String ruta) {
        try {
            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(ruta)
                    );

            Node vista = loader.load();

            contenedorContenido
                    .getChildren()
                    .setAll(vista);

        } catch (Exception e) {
            e.printStackTrace();

            mostrarTexto(
                    "Error al cargar la vista: "
                            + ruta
            );
        }
    }

    @FXML
    private void cerrarSesion() {
        SessionManager.cerrarSesion();
        System.exit(0);
    }
}