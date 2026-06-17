package pe.nuevasonrisa.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import pe.nuevasonrisa.model.ReporteCitasDoctor;
import pe.nuevasonrisa.model.ReporteEstado;
import pe.nuevasonrisa.model.ReporteServicio;
import pe.nuevasonrisa.model.CitaTabla;

import javafx.stage.FileChooser;
import java.io.FileOutputStream;
import java.util.List;
import java.io.File;

public class ExcelExporter {

    public static void exportarReportes(
            List<ReporteCitasDoctor> doctores,
            List<ReporteServicio> servicios,
            List<ReporteEstado> estados,
            String rutaArchivo
    ) {

        try (Workbook workbook = new XSSFWorkbook()) {

            crearHojaDoctores(workbook, doctores);
            crearHojaServicios(workbook, servicios);
            crearHojaEstados(workbook, estados);

            try (FileOutputStream fos =
                         new FileOutputStream(rutaArchivo)) {

                workbook.write(fos);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void crearHojaDoctores(
            Workbook workbook,
            List<ReporteCitasDoctor> lista
    ) {

        Sheet sheet =
                workbook.createSheet("Citas por Doctor");

        Row header = sheet.createRow(0);

        header.createCell(0).setCellValue("Doctor");
        header.createCell(1).setCellValue("Total");
        header.createCell(2).setCellValue("Realizadas");
        header.createCell(3).setCellValue("Canceladas");
        header.createCell(4).setCellValue("No Asistió");

        int fila = 1;

        for (ReporteCitasDoctor r : lista) {

            Row row = sheet.createRow(fila++);

            row.createCell(0).setCellValue(r.getDoctor());
            row.createCell(1).setCellValue(r.getTotalCitas());
            row.createCell(2).setCellValue(r.getRealizadas());
            row.createCell(3).setCellValue(r.getCanceladas());
            row.createCell(4).setCellValue(r.getNoAsistio());
        }

        for (int i = 0; i < 5; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private static void crearHojaServicios(
            Workbook workbook,
            List<ReporteServicio> lista
    ) {

        Sheet sheet =
                workbook.createSheet("Servicios");

        Row header = sheet.createRow(0);

        header.createCell(0).setCellValue("Servicio");
        header.createCell(1).setCellValue("Total");
        header.createCell(2).setCellValue("Realizadas");
        header.createCell(3).setCellValue("Canceladas");

        int fila = 1;

        for (ReporteServicio r : lista) {

            Row row = sheet.createRow(fila++);

            row.createCell(0).setCellValue(r.getServicio());
            row.createCell(1).setCellValue(r.getTotalCitas());
            row.createCell(2).setCellValue(r.getRealizadas());
            row.createCell(3).setCellValue(r.getCanceladas());
        }

        for (int i = 0; i < 4; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private static void crearHojaEstados(
            Workbook workbook,
            List<ReporteEstado> lista
    ) {

        Sheet sheet =
                workbook.createSheet("Estados");

        Row header = sheet.createRow(0);

        header.createCell(0).setCellValue("Estado");
        header.createCell(1).setCellValue("Cantidad");

        int fila = 1;

        for (ReporteEstado r : lista) {

            Row row = sheet.createRow(fila++);

            row.createCell(0).setCellValue(r.getEstado());
            row.createCell(1).setCellValue(r.getTotal());
        }

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }

    public static void exportarCitas(
            List<CitaTabla> citas
    ) {
        try (Workbook workbook = new XSSFWorkbook()) {

            Sheet sheet =
                    workbook.createSheet("Citas");

            Row header =
                    sheet.createRow(0);

            header.createCell(0).setCellValue("Paciente");
            header.createCell(1).setCellValue("Doctor");
            header.createCell(2).setCellValue("Servicio");
            header.createCell(3).setCellValue("Fecha");
            header.createCell(4).setCellValue("Hora");
            header.createCell(5).setCellValue("Estado");

            int fila = 1;

            for (CitaTabla c : citas) {

                Row row =
                        sheet.createRow(fila++);

                row.createCell(0).setCellValue(c.getPaciente());
                row.createCell(1).setCellValue(c.getDoctor());
                row.createCell(2).setCellValue(c.getServicio());
                row.createCell(3).setCellValue(c.getFecha().toString());
                row.createCell(4).setCellValue(c.getHora().toString());
                row.createCell(5).setCellValue(c.getEstado());
            }

            FileChooser chooser =
                    new FileChooser();

            chooser.setTitle(
                    "Guardar Excel"
            );

            chooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter(
                            "Excel",
                            "*.xlsx"
                    )
            );

            File archivo =
                    chooser.showSaveDialog(null);

            if (archivo != null) {

                try (FileOutputStream fos =
                             new FileOutputStream(archivo)) {

                    workbook.write(fos);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}