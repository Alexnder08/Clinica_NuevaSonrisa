package pe.nuevasonrisa.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import pe.nuevasonrisa.dao.impl.CitaDoctorDAOImpl;
import pe.nuevasonrisa.model.CitaDoctorTabla;
import pe.nuevasonrisa.service.CitaDoctorService;
import pe.nuevasonrisa.util.SessionManager;

public class MisCitasDoctorController {

    @FXML private TableView<CitaDoctorTabla> tablaCitas;

    @FXML private TableColumn<CitaDoctorTabla,String> colPaciente;
    @FXML private TableColumn<CitaDoctorTabla,Object> colFecha;
    @FXML private TableColumn<CitaDoctorTabla,String> colHora;
    @FXML private TableColumn<CitaDoctorTabla,String> colEstado;
    @FXML private TableColumn<CitaDoctorTabla,String> colServicio;
    @FXML private TableColumn<CitaDoctorTabla,String> colMotivo;
    @FXML private TableColumn<CitaDoctorTabla,String> colNotas;

    private final CitaDoctorService service =
            new CitaDoctorService(
                    new CitaDoctorDAOImpl()
            );

    @FXML
    public void initialize() {

        colPaciente.setCellValueFactory(new PropertyValueFactory<>("paciente"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colHora.setCellValueFactory(new PropertyValueFactory<>("hora"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colServicio.setCellValueFactory(new PropertyValueFactory<>("servicio"));
        colMotivo.setCellValueFactory(new PropertyValueFactory<>("motivoConsulta"));
        colNotas.setCellValueFactory(new PropertyValueFactory<>("notas"));

        cargarDatos();
    }

    private void cargarDatos() {

        tablaCitas.setItems(
                FXCollections.observableArrayList(
                        service.obtenerMisCitas(
                                SessionManager
                                        .getUsuarioActual()
                                        .getId()
                        )
                )
        );
    }

    @FXML
    private void agregarNota() {

        CitaDoctorTabla cita =
                tablaCitas.getSelectionModel()
                        .getSelectedItem();

        if (cita == null) {

            Alert alert =
                    new Alert(Alert.AlertType.WARNING);

            alert.setHeaderText(null);
            alert.setContentText(
                    "Seleccione una cita."
            );

            alert.showAndWait();
            return;
        }

        TextInputDialog dialog =
                new TextInputDialog(
                        cita.getNotas()
                );

        dialog.setTitle("Notas");
        dialog.setHeaderText(
                "Agregar / Editar nota clÃ­nica"
        );

        dialog.showAndWait()
                .ifPresent(nota -> {

                    boolean ok =
                            service.actualizarNotas(
                                    cita.getId(),
                                    SessionManager
                                            .getUsuarioActual()
                                            .getId(),
                                    nota
                            );

                    if (ok) {
                        cargarDatos();
                    }
                });
    }
}