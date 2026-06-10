package pe.nuevasonrisa.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import pe.nuevasonrisa.dao.impl.PacienteDAOImpl;
import pe.nuevasonrisa.model.Paciente;
import pe.nuevasonrisa.model.PacienteTabla;
import pe.nuevasonrisa.service.PacienteService;
import pe.nuevasonrisa.service.AuditoriaService;

public class EditarPacienteController {

    @FXML private TextField txtDni;
    @FXML private TextField txtNombre;
    @FXML private TextField txtApellido;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtCorreo;

    private int idPaciente;

    private final PacienteService service =
            new PacienteService(new PacienteDAOImpl());

    private final AuditoriaService auditoriaService =
            new AuditoriaService();

    public void cargarPaciente(PacienteTabla paciente) {

        idPaciente = paciente.getId();

        txtDni.setText(paciente.getDni());
        txtNombre.setText(paciente.getNombre());
        txtApellido.setText(paciente.getApellido());
        txtTelefono.setText(paciente.getTelefono());
        txtCorreo.setText(paciente.getCorreo());
    }

    @FXML
    private void guardar() {

        Paciente paciente = new Paciente();

        paciente.setId(idPaciente);
        paciente.setDni(txtDni.getText().trim());
        paciente.setNombre(txtNombre.getText().trim());
        paciente.setApellido(txtApellido.getText().trim());
        paciente.setTelefono(txtTelefono.getText().trim());
        paciente.setCorreo(txtCorreo.getText().trim());

        if (!paciente.getNombre().matches(
                "^[A-Za-zÃÃ‰ÃÃ“ÃšÃ¡Ã©Ã­Ã³ÃºÃ‘Ã± ]{2,50}$")) {

            mostrarError("El nombre solo puede contener letras.");
            return;
        }

        if (!paciente.getApellido().matches(
                "^[A-Za-zÃÃ‰ÃÃ“ÃšÃ¡Ã©Ã­Ã³ÃºÃ‘Ã± ]{2,50}$")) {

            mostrarError("El apellido solo puede contener letras.");
            return;
        }

        if (!paciente.getDni().matches("\\d{8}")) {

            mostrarError("El DNI debe tener 8 dÃ­gitos.");
            return;
        }

        if (!paciente.getTelefono().matches("^9\\d{8}$")) {

            mostrarError(
                    "El celular debe tener 9 dÃ­gitos y empezar con 9."
            );
            return;
        }

        if (!paciente.getCorreo().matches(
                "^[A-Za-z][A-Za-z0-9._%+-]{2,}@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
        )) {

            mostrarError("Ingrese un correo vÃ¡lido.");
            return;
        }


        boolean actualizado =
                service.actualizarPaciente(paciente);

        if (actualizado) {

            auditoriaService.registrar(
                    "EDITAR",
                    "PACIENTES",
                    "Paciente " +
                            paciente.getNombre() + " " +
                            paciente.getApellido() +
                            " actualizado"
            );

            cerrar();
        } else {
            Alert alert =
                    new Alert(Alert.AlertType.ERROR);

            alert.setContentText(
                    "No se pudo actualizar el paciente."
            );

            alert.showAndWait();
        }
    }

    @FXML
    private void cancelar() {
        cerrar();
    }

    private void cerrar() {
        Stage stage =
                (Stage) txtDni.getScene().getWindow();

        stage.close();
    }

    private void mostrarError(String mensaje) {

        Alert alert =
                new Alert(Alert.AlertType.ERROR);

        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}