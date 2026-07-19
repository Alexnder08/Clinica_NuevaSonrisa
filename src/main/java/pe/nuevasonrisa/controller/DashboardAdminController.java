package pe.nuevasonrisa.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import pe.nuevasonrisa.model.Usuario;
import pe.nuevasonrisa.util.SessionManager;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javafx.stage.FileChooser;
import pe.nuevasonrisa.service.BackupService;
import pe.nuevasonrisa.service.AuditoriaService;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DashboardAdminController {

    @FXML
    private Label lblTitulo;

    @FXML
    private Label lblUsuario;

    @FXML
    private StackPane contenedorContenido;

    private final BackupService backupService = new BackupService();
    private final AuditoriaService auditoriaService = new AuditoriaService();

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
        lblTitulo.setText("Gestión de Usuarios");
        cargarVista("/fxml/usuarios.fxml");
    }

    @FXML
    private void mostrarServicios() {
        lblTitulo.setText("Gestión de Servicios");
        cargarVista("/fxml/servicios.fxml");
    }

    @FXML
    private void mostrarPacientes() {
        lblTitulo.setText("Gestión de Pacientes");
        cargarVista("/fxml/pacientes.fxml");
    }

    @FXML
    private void mostrarOdontologos() {
        lblTitulo.setText("Gestión de Odontólogos");
        cargarVista("/fxml/odontologos.fxml");
    }

    @FXML
    private void mostrarCitas() {
        lblTitulo.setText("Gestión de Citas");
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
        lblTitulo.setText("Auditoría");
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
            pe.nuevasonrisa.util.AppLogger.error(getClass(), "Unhandled error while completing the operation.", e);

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

    @FXML
    private void crearCopiaSeguridad() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Guardar copia de seguridad");
        chooser.setInitialFileName("NuevaSonrisa_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm")) + ".backup");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Copia PostgreSQL (*.backup)", "*.backup"));
        File destino = chooser.showSaveDialog(contenedorContenido.getScene().getWindow());
        if (destino != null) {
            mostrarResultado("Copia de seguridad", backupService.crearBackup(destino));
        }
    }

    @FXML
    private void restaurarCopiaSeguridad() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Seleccionar copia de seguridad");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Copia PostgreSQL (*.backup)", "*.backup"));
        File origen = chooser.showOpenDialog(contenedorContenido.getScene().getWindow());
        if (origen == null) return;

        TextInputDialog confirmacion = new TextInputDialog();
        confirmacion.setTitle("Restaurar copia de seguridad");
        confirmacion.setHeaderText("Esta operacion reemplazara los datos actuales.");
        confirmacion.setContentText("Escriba RESTAURAR para continuar:");
        confirmacion.showAndWait().filter("RESTAURAR"::equals).ifPresent(texto ->
                mostrarResultado("Restauracion", backupService.restaurarBackup(origen)));
    }

    private void mostrarResultado(String titulo, BackupService.Resultado resultado) {
        if (resultado.exitoso()) {
            String accion = "Copia de seguridad".equals(titulo) ? "CREAR_BACKUP" : "RESTAURAR_BACKUP";
            auditoriaService.registrar(accion, "RESPALDOS", "Operacion de respaldo completada.");
        }
        Alert alert = new Alert(resultado.exitoso() ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(resultado.exitoso() ? "Operacion completada" : "No se pudo completar");
        alert.setContentText(resultado.mensaje());
        alert.showAndWait();
    }
}
