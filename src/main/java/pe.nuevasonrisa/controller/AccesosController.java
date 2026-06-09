package pe.nuevasonrisa.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import pe.nuevasonrisa.model.HistorialAcceso;
import pe.nuevasonrisa.service.HistorialAccesoService;

public class AccesosController {

    @FXML
    private TableView<HistorialAcceso> tablaAccesos;

    @FXML
    private TableColumn<HistorialAcceso,String> colUsuario;

    @FXML
    private TableColumn<HistorialAcceso,String> colRol;

    @FXML
    private TableColumn<HistorialAcceso,String> colEstado;

    @FXML
    private TableColumn<HistorialAcceso,String> colFecha;

    private final HistorialAccesoService service =
            new HistorialAccesoService();

    @FXML
    public void initialize() {

        colUsuario.setCellValueFactory(
                new PropertyValueFactory<>("usuario")
        );

        colRol.setCellValueFactory(
                new PropertyValueFactory<>("rol")
        );

        colEstado.setCellValueFactory(
                new PropertyValueFactory<>("estado")
        );

        colFecha.setCellValueFactory(
                new PropertyValueFactory<>("fechaFormateada")
        );

        tablaAccesos.setItems(
                FXCollections.observableArrayList(
                        service.listar()
                )
        );
    }
}