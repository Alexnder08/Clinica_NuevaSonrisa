package pe.nuevasonrisa.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import pe.nuevasonrisa.dao.impl.HorarioDoctorDAOImpl;
import pe.nuevasonrisa.model.HorarioDoctorTabla;
import pe.nuevasonrisa.service.AuditoriaService;
import pe.nuevasonrisa.service.HorarioDoctorService;

public class HorariosDoctorController {

    private int doctorId;

    @FXML private TableView<HorarioDoctorTabla> tablaHorarios;
    @FXML private TableColumn<HorarioDoctorTabla, Integer> colId;
    @FXML private TableColumn<HorarioDoctorTabla, String> colDoctor;
    @FXML private TableColumn<HorarioDoctorTabla, String> colDia;
    @FXML private TableColumn<HorarioDoctorTabla, Object> colInicio;
    @FXML private TableColumn<HorarioDoctorTabla, Object> colFin;

    private final HorarioDoctorService service =
            new HorarioDoctorService(new HorarioDoctorDAOImpl());

    private final AuditoriaService auditoriaService =
            new AuditoriaService();

    public void cargarDoctor(int doctorId) {
        this.doctorId = doctorId;
        cargarHorarios();
    }

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDoctor.setCellValueFactory(new PropertyValueFactory<>("doctor"));
        colDia.setCellValueFactory(new PropertyValueFactory<>("dia"));
        colInicio.setCellValueFactory(new PropertyValueFactory<>("horaInicio"));
        colFin.setCellValueFactory(new PropertyValueFactory<>("horaFin"));
    }

    @FXML
    private void cargarHorarios() {
        tablaHorarios.setItems(FXCollections.observableArrayList(
                service.listarPorDoctor(doctorId)
        ));
    }

    @FXML
    private void editarHorario() {
        HorarioDoctorTabla seleccionado =
                tablaHorarios.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarInfo("Aviso", "Seleccione un horario para editar.");
            return;
        }

        ChoiceDialog<String> diaDialog = new ChoiceDialog<>(
                seleccionado.getDia(),
                "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"
        );

        diaDialog.setTitle("Editar día");
        diaDialog.setHeaderText(null);
        diaDialog.setContentText("Día:");

        diaDialog.showAndWait().ifPresent(dia -> {
            TextInputDialog inicioDialog =
                    new TextInputDialog(seleccionado.getHoraInicio().toString().substring(0, 5));

            inicioDialog.setTitle("Hora de inicio");
            inicioDialog.setHeaderText(null);
            inicioDialog.setContentText("Hora de inicio (HH:mm):");

            inicioDialog.showAndWait().ifPresent(inicio -> {
                TextInputDialog finDialog =
                        new TextInputDialog(seleccionado.getHoraFin().toString().substring(0, 5));

                finDialog.setTitle("Hora de fin");
                finDialog.setHeaderText(null);
                finDialog.setContentText("Hora de fin (HH:mm):");

                finDialog.showAndWait().ifPresent(fin -> actualizarHorarioSeleccionado(
                        seleccionado,
                        dia,
                        inicio.trim(),
                        fin.trim()
                ));
            });
        });
    }

    private void actualizarHorarioSeleccionado(HorarioDoctorTabla seleccionado,
                                               String dia,
                                               String inicio,
                                               String fin) {
        String validacion = service.validarFormatoHorario(
                seleccionado.getDoctorId(),
                convertirDiaANumero(dia),
                inicio,
                fin
        );

        if (validacion != null) {
            mostrarInfo("Validación", validacion);
            return;
        }

        boolean ok = service.actualizarHorario(
                seleccionado.getId(),
                convertirDiaANumero(dia),
                inicio,
                fin
        );

        if (ok) {
            auditoriaService.registrar(
                    "EDITAR",
                    "HORARIOS",
                    "Horario ID " + seleccionado.getId() +
                            " actualizado a " +
                            dia + " " + inicio + " - " + fin
            );

            cargarHorarios();
        } else {
            mostrarInfo("Error", "No se pudo actualizar el horario.");
        }
    }

    @FXML
    private void nuevoHorario() {
        if (doctorId <= 0) {
            mostrarInfo("Error", "No se encontró el doctor seleccionado.");
            return;
        }

        ChoiceDialog<String> diaDialog = new ChoiceDialog<>(
                "Lunes",
                "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"
        );

        diaDialog.setTitle("Nuevo horario");
        diaDialog.setHeaderText(null);
        diaDialog.setContentText("Seleccione el día:");

        diaDialog.showAndWait().ifPresent(dia -> {
            ChoiceDialog<String> inicioDialog = new ChoiceDialog<>(
                    "08:00",
                    "08:00", "09:00", "10:00", "11:00", "12:00",
                    "13:00", "14:00", "15:00", "16:00", "17:00"
            );

            inicioDialog.setTitle("Hora de inicio");
            inicioDialog.setHeaderText(null);
            inicioDialog.setContentText("Seleccione hora de inicio:");

            inicioDialog.showAndWait().ifPresent(inicio -> {
                ChoiceDialog<String> finDialog = new ChoiceDialog<>(
                        "18:00",
                        "09:00", "10:00", "11:00", "12:00", "13:00",
                        "14:00", "15:00", "16:00", "17:00", "18:00"
                );

                finDialog.setTitle("Hora de fin");
                finDialog.setHeaderText(null);
                finDialog.setContentText("Seleccione hora de fin:");

                finDialog.showAndWait().ifPresent(fin -> crearHorarioSeleccionado(
                        dia,
                        inicio,
                        fin
                ));
            });
        });
    }

    private void crearHorarioSeleccionado(String dia, String inicio, String fin) {
        int diaSemana = convertirDiaANumero(dia);

        String validacion = service.validarNuevoHorario(
                doctorId,
                diaSemana,
                inicio,
                fin
        );

        if (validacion != null) {
            mostrarInfo("Validación", validacion);
            return;
        }

        boolean creado = service.crearHorario(
                doctorId,
                diaSemana,
                inicio,
                fin
        );

        if (creado) {
            auditoriaService.registrar(
                    "CREAR",
                    "HORARIOS",
                    "Horario creado: " +
                            dia + " " + inicio + " - " + fin
            );

            cargarHorarios();
        } else {
            mostrarInfo("Error", "No se pudo crear el horario.");
        }
    }

    private int convertirDiaANumero(String dia) {
        return switch (dia) {
            case "Lunes" -> 1;
            case "Martes" -> 2;
            case "Miércoles" -> 3;
            case "Jueves" -> 4;
            case "Viernes" -> 5;
            case "Sábado" -> 6;
            case "Domingo" -> 7;
            default -> 1;
        };
    }

    @FXML
    private void eliminarHorario() {
        HorarioDoctorTabla seleccionado =
                tablaHorarios.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarInfo("Aviso", "Seleccione un horario para eliminar.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Eliminar horario");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Desea eliminar este horario?");

        confirmacion.showAndWait().ifPresent(respuesta -> {
            if (respuesta == ButtonType.OK) {
                boolean eliminado = service.eliminarHorario(seleccionado.getId());

                if (eliminado) {
                    auditoriaService.registrar(
                            "ELIMINAR",
                            "HORARIOS",
                            "Horario ID " + seleccionado.getId() + " eliminado"
                    );

                    cargarHorarios();
                } else {
                    mostrarInfo("Error", "No se pudo eliminar el horario.");
                }
            }
        });
    }

    private void mostrarInfo(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
