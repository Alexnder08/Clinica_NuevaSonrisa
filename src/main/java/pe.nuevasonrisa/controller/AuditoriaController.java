package pe.nuevasonrisa.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import pe.nuevasonrisa.model.Auditoria;
import pe.nuevasonrisa.service.AuditoriaService;

public class AuditoriaController {

    @FXML private TableView<Auditoria> tablaAuditoria;

    @FXML private TableColumn<Auditoria,String> colUsuario;
    @FXML private TableColumn<Auditoria,String> colAccion;
    @FXML private TableColumn<Auditoria,String> colModulo;
    @FXML private TableColumn<Auditoria,String> colDetalle;
    @FXML private TableColumn<Auditoria,String> colFecha;

    private final AuditoriaService service =
            new AuditoriaService();

    @FXML
    public void initialize() {

        colUsuario.setCellValueFactory(new PropertyValueFactory<>("usuario"));
        colAccion.setCellValueFactory(new PropertyValueFactory<>("accion"));
        colModulo.setCellValueFactory(new PropertyValueFactory<>("modulo"));
        colDetalle.setCellValueFactory(new PropertyValueFactory<>("detalle"));
colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaFormateada"));
        tablaAuditoria.setItems(
                FXCollections.observableArrayList(
                        service.listar()
                )
        );
    }
}