package pe.nuevasonrisa.util;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import pe.nuevasonrisa.model.CitaTabla;
import pe.nuevasonrisa.model.ReporteCitasDoctor;
import pe.nuevasonrisa.model.ReporteEstado;
import pe.nuevasonrisa.model.ReporteServicio;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public final class PdfExporter {

    private static final Color TEAL = new Color(15, 118, 110);
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private PdfExporter() {
    }

    public static File exportarCitas(List<CitaTabla> citas, Window owner) throws Exception {
        File archivo = seleccionarArchivo(owner, "Citas_NuevaSonrisa.pdf");
        if (archivo == null) {
            return null;
        }

        generarCitas(citas, archivo);
        return archivo;
    }

    static void generarCitas(List<CitaTabla> citas, File archivo) throws Exception {
        Document documento = abrirDocumento(archivo, "LISTADO DE CITAS");
        PdfPTable tabla = crearTabla(new String[]{"Paciente", "Odontologo", "Tratamiento", "Fecha", "Hora", "Estado"});
        for (CitaTabla cita : citas) {
            agregarFila(tabla,
                    cita.getPaciente(), cita.getDoctor(), cita.getServicio(),
                    cita.getFecha().toString(), cita.getHora().toString(), cita.getEstado());
        }
        documento.add(tabla);
        agregarTotal(documento, citas.size());
        documento.close();
    }

    public static File exportarReportes(
            List<ReporteCitasDoctor> doctores,
            List<ReporteServicio> servicios,
            List<ReporteEstado> estados,
            Window owner
    ) throws Exception {
        File archivo = seleccionarArchivo(owner, "Reportes_NuevaSonrisa.pdf");
        if (archivo == null) {
            return null;
        }

        Document documento = abrirDocumento(archivo, "REPORTE GENERAL DE CITAS");
        agregarSeccion(documento, "Resumen por odontologo");
        PdfPTable tablaDoctores = crearTabla(new String[]{"Odontologo", "Total", "Realizadas", "Canceladas", "No asistio"});
        for (ReporteCitasDoctor item : doctores) {
            agregarFila(tablaDoctores, item.getDoctor(), item.getTotalCitas(), item.getRealizadas(), item.getCanceladas(), item.getNoAsistio());
        }
        documento.add(tablaDoctores);

        agregarSeccion(documento, "Resumen por tratamiento");
        PdfPTable tablaServicios = crearTabla(new String[]{"Tratamiento", "Total", "Realizadas", "Canceladas"});
        for (ReporteServicio item : servicios) {
            agregarFila(tablaServicios, item.getServicio(), item.getTotalCitas(), item.getRealizadas(), item.getCanceladas());
        }
        documento.add(tablaServicios);

        agregarSeccion(documento, "Resumen por estado");
        PdfPTable tablaEstados = crearTabla(new String[]{"Estado", "Cantidad"});
        for (ReporteEstado item : estados) {
            agregarFila(tablaEstados, item.getEstado(), item.getTotal());
        }
        documento.add(tablaEstados);
        documento.close();
        return archivo;
    }

    private static Document abrirDocumento(File archivo, String titulo) throws Exception {
        Document documento = new Document(PageSize.A4.rotate(), 32, 32, 38, 38);
        PdfWriter.getInstance(documento, new FileOutputStream(archivo));
        documento.open();

        Font fuenteTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.WHITE);
        PdfPTable cabecera = new PdfPTable(1);
        cabecera.setWidthPercentage(100);
        cabecera.setSpacingAfter(12);
        PdfPCell celda = new PdfPCell(new Paragraph(titulo + " - NUEVA SONRISA", fuenteTitulo));
        celda.setBackgroundColor(TEAL);
        celda.setHorizontalAlignment(Element.ALIGN_CENTER);
        celda.setPadding(12);
        cabecera.addCell(celda);
        documento.add(cabecera);

        Paragraph generado = new Paragraph("Generado: " + FECHA.format(LocalDateTime.now()),
                FontFactory.getFont(FontFactory.HELVETICA, 9, Color.DARK_GRAY));
        generado.setAlignment(Element.ALIGN_RIGHT);
        generado.setSpacingAfter(14);
        documento.add(generado);
        return documento;
    }

    private static PdfPTable crearTabla(String[] encabezados) {
        PdfPTable tabla = new PdfPTable(encabezados.length);
        tabla.setWidthPercentage(100);
        tabla.setHeaderRows(1);
        tabla.setSpacingAfter(15);
        Font fuente = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE);
        for (String encabezado : encabezados) {
            PdfPCell celda = new PdfPCell(new Paragraph(encabezado, fuente));
            celda.setBackgroundColor(TEAL);
            celda.setHorizontalAlignment(Element.ALIGN_CENTER);
            celda.setPadding(7);
            tabla.addCell(celda);
        }
        return tabla;
    }

    private static void agregarFila(PdfPTable tabla, Object... valores) {
        Font fuente = FontFactory.getFont(FontFactory.HELVETICA, 8);
        for (Object valor : valores) {
            PdfPCell celda = new PdfPCell(new Paragraph(valor == null ? "" : String.valueOf(valor), fuente));
            celda.setPadding(6);
            tabla.addCell(celda);
        }
    }

    private static void agregarSeccion(Document documento, String titulo) throws Exception {
        Paragraph parrafo = new Paragraph(titulo, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, TEAL));
        parrafo.setSpacingBefore(8);
        parrafo.setSpacingAfter(7);
        documento.add(parrafo);
    }

    private static void agregarTotal(Document documento, int total) throws Exception {
        Paragraph parrafo = new Paragraph("Total de registros: " + total,
                FontFactory.getFont(FontFactory.HELVETICA, 9, Color.DARK_GRAY));
        parrafo.setAlignment(Element.ALIGN_RIGHT);
        documento.add(parrafo);
    }

    private static File seleccionarArchivo(Window owner, String nombre) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Guardar PDF");
        chooser.setInitialFileName(nombre);
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF (*.pdf)", "*.pdf"));
        return chooser.showSaveDialog(owner);
    }
}
