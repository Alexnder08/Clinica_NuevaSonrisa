package pe.nuevasonrisa.util;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import pe.nuevasonrisa.model.CitaTabla;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public final class CalendarioCitasDialog {

    private static final DateTimeFormatter MES = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.forLanguageTag("es-PE"));
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");
    private final List<CitaTabla> citas;
    private final Dialog<Void> dialog = new Dialog<>();
    private final GridPane calendario = new GridPane();
    private final Label titulo = new Label();
    private YearMonth mesActual = YearMonth.now();

    public CalendarioCitasDialog(List<CitaTabla> citas) {
        this.citas = citas;
        configurarDialogo();
        renderizarMes();
    }

    public void mostrar() {
        dialog.showAndWait();
    }

    private void configurarDialogo() {
        dialog.setTitle("Calendario de citas");
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.getDialogPane().setPrefSize(980, 680);

        Button anterior = new Button("<");
        Button siguiente = new Button(">");
        anterior.setOnAction(event -> cambiarMes(-1));
        siguiente.setOnAction(event -> cambiarMes(1));
        titulo.setStyle("-fx-font-size:20px;-fx-font-weight:bold;-fx-text-fill:#0F172A;");

        HBox navegacion = new HBox(12, anterior, titulo, siguiente);
        navegacion.setAlignment(Pos.CENTER);

        HBox leyenda = new HBox(16,
                crearLeyenda("Pendiente", "#FEF3C7"),
                crearLeyenda("En espera", "#DBEAFE"),
                crearLeyenda("Realizado", "#DCFCE7"),
                crearLeyenda("Cancelado / No asistio", "#FEE2E2"));
        leyenda.setAlignment(Pos.CENTER);

        calendario.setHgap(7);
        calendario.setVgap(7);
        VBox contenido = new VBox(14, navegacion, leyenda, calendario);
        contenido.setPadding(new Insets(8));
        dialog.getDialogPane().setContent(contenido);
    }

    private void renderizarMes() {
        calendario.getChildren().clear();
        titulo.setText(capitalizar(MES.format(mesActual)));
        String[] dias = {"Lun", "Mar", "Mie", "Jue", "Vie", "Sab", "Dom"};
        for (int col = 0; col < dias.length; col++) {
            Label dia = new Label(dias[col]);
            dia.setMaxWidth(Double.MAX_VALUE);
            dia.setAlignment(Pos.CENTER);
            dia.setStyle("-fx-font-weight:bold;-fx-text-fill:#475569;-fx-padding:6;");
            calendario.add(dia, col, 0);
            GridPane.setHgrow(dia, Priority.ALWAYS);
        }

        LocalDate primero = mesActual.atDay(1);
        int desplazamiento = primero.getDayOfWeek().getValue() - DayOfWeek.MONDAY.getValue();
        for (int dia = 1; dia <= mesActual.lengthOfMonth(); dia++) {
            int posicion = desplazamiento + dia - 1;
            calendario.add(crearCelda(mesActual.atDay(dia)), posicion % 7, posicion / 7 + 1);
        }
    }

    private VBox crearCelda(LocalDate fecha) {
        List<CitaTabla> citasDia = citas.stream()
                .filter(cita -> fecha.equals(cita.getFecha()))
                .sorted(Comparator.comparing(CitaTabla::getHora))
                .toList();

        Label numero = new Label(String.valueOf(fecha.getDayOfMonth()));
        numero.setStyle("-fx-font-weight:bold;-fx-text-fill:#0F172A;");
        VBox celda = new VBox(4, numero);
        celda.setPadding(new Insets(7));
        celda.setPrefSize(128, 92);
        celda.setStyle("-fx-background-color:" + colorDia(citasDia)
                + ";-fx-background-radius:8;-fx-border-color:#CBD5E1;-fx-border-radius:8;");

        citasDia.stream().limit(3).forEach(cita -> {
            Label detalle = new Label(HORA.format(cita.getHora()) + " " + abreviar(cita.getPaciente()));
            detalle.setStyle("-fx-font-size:10px;-fx-text-fill:#334155;");
            celda.getChildren().add(detalle);
        });
        if (citasDia.size() > 3) {
            celda.getChildren().add(new Label("+" + (citasDia.size() - 3) + " cita(s)"));
        }
        return celda;
    }

    private String colorDia(List<CitaTabla> citasDia) {
        if (citasDia.isEmpty()) return "#FFFFFF";
        if (citasDia.stream().anyMatch(c -> "En espera".equalsIgnoreCase(c.getEstado()))) return "#DBEAFE";
        if (citasDia.stream().anyMatch(c -> "Pendiente".equalsIgnoreCase(c.getEstado()))) return "#FEF3C7";
        if (citasDia.stream().anyMatch(c -> "Realizado".equalsIgnoreCase(c.getEstado()))) return "#DCFCE7";
        return "#FEE2E2";
    }

    private HBox crearLeyenda(String texto, String color) {
        Label muestra = new Label("  ");
        muestra.setStyle("-fx-background-color:" + color + ";-fx-border-color:#CBD5E1;-fx-background-radius:4;");
        return new HBox(5, muestra, new Label(texto));
    }

    private void cambiarMes(int meses) {
        mesActual = mesActual.plusMonths(meses);
        renderizarMes();
    }

    private String abreviar(String nombre) {
        return nombre.length() <= 15 ? nombre : nombre.substring(0, 14) + "...";
    }

    private String capitalizar(String texto) {
        return texto.substring(0, 1).toUpperCase(Locale.ROOT) + texto.substring(1);
    }
}
