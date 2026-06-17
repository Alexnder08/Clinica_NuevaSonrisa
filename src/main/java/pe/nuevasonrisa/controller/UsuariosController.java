package pe.nuevasonrisa.controller;

import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Alert;

import java.text.Normalizer;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.awt.Color;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPCell;

import pe.nuevasonrisa.dao.impl.UsuarioGestionDAOImpl;
import pe.nuevasonrisa.model.UsuarioTabla;
import pe.nuevasonrisa.service.UsuarioService;
import pe.nuevasonrisa.service.AuditoriaService;

public class UsuariosController {

    private List<UsuarioTabla> usuariosCache = new ArrayList<>();
    private FilteredList<UsuarioTabla> usuariosFiltrados;

    @FXML
    private TextField txtBuscar;

    @FXML
    private TableView<UsuarioTabla> tablaUsuarios;

    @FXML
    private TableColumn<UsuarioTabla, Integer> colId;

    @FXML
    private TableColumn<UsuarioTabla, String> colUsuario;

    @FXML
    private TableColumn<UsuarioTabla, String> colNombre;

    @FXML
    private TableColumn<UsuarioTabla, String> colApellido;

    @FXML
    private TableColumn<UsuarioTabla, String> colRol;

    @FXML
    private TableColumn<UsuarioTabla, String> colEstado;

    private final UsuarioService service =
            new UsuarioService(new UsuarioGestionDAOImpl());

    private final AuditoriaService auditoriaService =
            new AuditoriaService();

    @FXML
    public void initialize() {

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colUsuario.setCellValueFactory(new PropertyValueFactory<>("usuario"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        colRol.setCellValueFactory(new PropertyValueFactory<>("rol"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        cargarUsuarios();

        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
            buscarUsuarios();
        });
    }

    @FXML
    private void cargarUsuarios() {

        usuariosCache = service.obtenerUsuarios();

        usuariosFiltrados =
                new FilteredList<>(FXCollections.observableArrayList(usuariosCache));

        SortedList<UsuarioTabla> sortedList = new SortedList<>(usuariosFiltrados);
        sortedList.comparatorProperty().bind(tablaUsuarios.comparatorProperty());

        tablaUsuarios.setItems(sortedList);

        txtBuscar.clear();
    }

    @FXML
    private void nuevoUsuario() {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(
                                    "/fxml/nuevo_usuario.fxml"
                            )
                    );

            Scene scene =
                    new Scene(loader.load());

            Stage stage = new Stage();

            stage.setTitle("Nuevo Usuario");
            stage.setScene(scene);

            stage.initModality(Modality.APPLICATION_MODAL);

            stage.showAndWait();

            cargarUsuarios();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void editarUsuario() {
        UsuarioTabla seleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarInfo("Aviso", "Seleccione un usuario para editar.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/editar_usuario.fxml")
            );

            Scene scene = new Scene(loader.load());

            EditarUsuarioController controller = loader.getController();
            controller.cargarUsuario(seleccionado);

            Stage stage = new Stage();
            stage.setTitle("Editar Usuario");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            cargarUsuarios();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void cambiarEstado() {

        UsuarioTabla usuario =
                tablaUsuarios
                        .getSelectionModel()
                        .getSelectedItem();

        if (usuario == null) {
            mostrarInfo(
                    "Aviso",
                    "Seleccione un usuario."
            );
            return;
        }

        boolean ok =
                service.cambiarEstadoUsuario(
                        usuario.getId(),
                        usuario.getEstado()
                );

        if (ok) {

            auditoriaService.registrar(
                    "CAMBIAR ESTADO",
                    "USUARIOS",
                    "Usuario "
                            + usuario.getUsuario()
                            + " cambió de estado"
            );

            cargarUsuarios();

            mostrarInfo(
                    "Éxito",
                    "Estado del usuario actualizado."
            );
        } else {
            mostrarInfo(
                    "Error",
                    "No se pudo cambiar el estado del usuario."
            );
        }
    }

    @FXML
    private void buscarUsuarios() {
        String filtro = normalizarTexto(txtBuscar.getText().trim());

        if (usuariosFiltrados == null) {
            return;
        }

        if (filtro.isBlank()) {
            usuariosFiltrados.setPredicate(null);
        } else {
            usuariosFiltrados.setPredicate(usuario ->
                    normalizarTexto(usuario.getUsuario()).startsWith(filtro)
                            || normalizarTexto(usuario.getNombre()).startsWith(filtro)
                            || normalizarTexto(usuario.getApellido()).startsWith(filtro)
                            || normalizarTexto(usuario.getRol()).startsWith(filtro)
            );
        }
    }

    private String normalizarTexto(String texto) {
        if (texto == null || texto.isEmpty()) {
            return "";
        }

        String textoNormalizado = Normalizer.normalize(texto, Normalizer.Form.NFD);
        textoNormalizado = textoNormalizado.replaceAll("\\p{M}", "");
        return textoNormalizado.toLowerCase();
    }

    @FXML
    private void exportarPDF() {
        try {
            List<UsuarioTabla> usuariosExportar = tablaUsuarios.getItems() != null
                    ? new ArrayList<>(tablaUsuarios.getItems())
                    : new ArrayList<>();

            if (usuariosExportar.isEmpty()) {
                mostrarInfo("Aviso", "No hay usuarios para exportar.");
                return;
            }

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar archivo PDF");
            fileChooser.setInitialFileName("Usuarios_" + System.currentTimeMillis() + ".pdf");

            FileChooser.ExtensionFilter pdfFilter =
                    new FileChooser.ExtensionFilter("Archivos PDF (*.pdf)", "*.pdf");
            fileChooser.getExtensionFilters().add(pdfFilter);

            File archivo = fileChooser.showSaveDialog(tablaUsuarios.getScene().getWindow());

            if (archivo != null) {
                generarPDF(archivo, usuariosExportar);
                mostrarInfo("Éxito", "PDF exportado correctamente en:\n" + archivo.getAbsolutePath());
            }

        } catch (Exception e) {
            mostrarInfo("Error", "No se pudo exportar el PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void generarPDF(File archivo, List<UsuarioTabla> usuarios) throws Exception {
        Document document = new Document(PageSize.A4, 40, 40, 60, 60);

        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(archivo));

        writer.setPageEvent(new PaginationHelper());

        document.open();

        PdfPTable tablaTitulo = new PdfPTable(1);
        tablaTitulo.setWidthPercentage(100);
        tablaTitulo.setSpacingAfter(20);

        Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.WHITE);
        PdfPCell celdaTitulo = new PdfPCell(new Paragraph("USUARIOS DEL SISTEMA - CLÍNICA NUEVA SONRISA", fontTitulo));
        celdaTitulo.setBackgroundColor(new Color(20, 184, 166));
        celdaTitulo.setHorizontalAlignment(Element.ALIGN_CENTER);
        celdaTitulo.setVerticalAlignment(Element.ALIGN_MIDDLE);
        celdaTitulo.setPadding(12);
        tablaTitulo.addCell(celdaTitulo);

        document.add(tablaTitulo);

        LocalDateTime ahora = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String fechaFormato = ahora.format(formatter);

        Font fontFecha = FontFactory.getFont(FontFactory.HELVETICA, 10);
        Paragraph parrafoFecha = new Paragraph("Fecha: " + fechaFormato, fontFecha);
        parrafoFecha.setAlignment(Element.ALIGN_RIGHT);
        parrafoFecha.setSpacingAfter(20);
        document.add(parrafoFecha);

        PdfPTable tabla = new PdfPTable(6);
        tabla.setWidthPercentage(100);
        tabla.setSpacingBefore(10);
        tabla.setSpacingAfter(10);

        float[] anchos = {8, 18, 22, 22, 15, 15};
        tabla.setWidths(anchos);

        Font fontEncabezado = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.WHITE);

        String[] encabezados = {"ID", "Usuario", "Nombre", "Apellido", "Rol", "Estado"};

        Color colorEncabezado = new Color(20, 184, 166);

        for (String encabezado : encabezados) {
            PdfPCell celda = new PdfPCell(new Paragraph(encabezado, fontEncabezado));
            celda.setBackgroundColor(colorEncabezado);
            celda.setHorizontalAlignment(Element.ALIGN_CENTER);
            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celda.setPadding(8);
            tabla.addCell(celda);
        }

        Font fontDatos = FontFactory.getFont(FontFactory.HELVETICA, 10);
        Color colorAlternado = new Color(241, 245, 249);

        int numeroFila = 0;
        for (UsuarioTabla usuario : usuarios) {

            Color colorFondo = numeroFila % 2 == 0 ? Color.WHITE : colorAlternado;

            PdfPCell celdaId = new PdfPCell(new Paragraph(String.valueOf(usuario.getId()), fontDatos));
            celdaId.setBackgroundColor(colorFondo);
            celdaId.setHorizontalAlignment(Element.ALIGN_CENTER);
            celdaId.setPadding(6);
            tabla.addCell(celdaId);

            PdfPCell celdaUsuario = new PdfPCell(new Paragraph(usuario.getUsuario(), fontDatos));
            celdaUsuario.setBackgroundColor(colorFondo);
            celdaUsuario.setPadding(6);
            tabla.addCell(celdaUsuario);

            PdfPCell celdaNombre = new PdfPCell(new Paragraph(usuario.getNombre(), fontDatos));
            celdaNombre.setBackgroundColor(colorFondo);
            celdaNombre.setPadding(6);
            tabla.addCell(celdaNombre);

            PdfPCell celdaApellido = new PdfPCell(new Paragraph(usuario.getApellido(), fontDatos));
            celdaApellido.setBackgroundColor(colorFondo);
            celdaApellido.setPadding(6);
            tabla.addCell(celdaApellido);

            PdfPCell celdaRol = new PdfPCell(new Paragraph(usuario.getRol(), fontDatos));
            celdaRol.setBackgroundColor(colorFondo);
            celdaRol.setHorizontalAlignment(Element.ALIGN_CENTER);
            celdaRol.setPadding(6);
            tabla.addCell(celdaRol);

            PdfPCell celdaEstado = new PdfPCell(new Paragraph(usuario.getEstado(), fontDatos));
            celdaEstado.setBackgroundColor(colorFondo);
            celdaEstado.setHorizontalAlignment(Element.ALIGN_CENTER);
            celdaEstado.setPadding(6);
            tabla.addCell(celdaEstado);

            numeroFila++;
        }

        document.add(tabla);

        Font fontInfo = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.GRAY);
        Paragraph parrafoInfo = new Paragraph("Total de registros: " + usuarios.size(), fontInfo);
        parrafoInfo.setAlignment(Element.ALIGN_RIGHT);
        parrafoInfo.setSpacingBefore(15);
        document.add(parrafoInfo);

        document.close();
    }

    private static class PaginationHelper extends PdfPageEventHelper {
        private int pageNumber = 0;

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            pageNumber++;

            Font fontPageNumber = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.GRAY);
            Paragraph pageNumParagraph = new Paragraph("Página " + pageNumber, fontPageNumber);
            pageNumParagraph.setAlignment(Element.ALIGN_CENTER);

            try {
                document.add(pageNumParagraph);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void mostrarInfo(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
