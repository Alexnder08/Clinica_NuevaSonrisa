package pe.nuevasonrisa.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import pe.nuevasonrisa.dao.impl.PacienteDAOImpl;
import pe.nuevasonrisa.model.Paciente;
import pe.nuevasonrisa.model.PacienteTabla;
import pe.nuevasonrisa.service.AuditoriaService;
import pe.nuevasonrisa.service.PacienteService;

import java.util.ArrayList;
import java.util.List;

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

        if (!validar(paciente)) {
            return;
        }

        boolean actualizado = service.actualizarPaciente(paciente);

        if (actualizado) {
            auditoriaService.registrar(
                    "EDITAR",
                    "PACIENTES",
                    "Paciente ID " + paciente.getId() + " actualizado."
            );

            cerrar();
        } else {
            mostrarError("No se pudo actualizar el paciente. Verifique que el DNI no esté repetido.");
        }
    }

    private boolean validar(Paciente paciente) {
        String camposFaltantes = camposFaltantes(
                campoFaltante(paciente.getDni(), "DNI"),
                campoFaltante(paciente.getNombre(), "nombre"),
                campoFaltante(paciente.getApellido(), "apellido"),
                campoFaltante(paciente.getTelefono(), "teléfono"),
                campoFaltante(paciente.getCorreo(), "correo")
        );

        if (camposFaltantes != null) {
            mostrarError("Complete los campos obligatorios: " + camposFaltantes + ".");
            return false;
        }

        if (!paciente.getDni().matches("\\d{8}")) {
            mostrarError("El DNI debe tener exactamente 8 dígitos numéricos.");
            return false;
        }

        if (!paciente.getNombre().matches("^[A-Za-zÁÉÍÓÚáéíóúÑñ ]{2,50}$")) {
            mostrarError("El nombre solo puede contener letras y debe tener entre 2 y 50 caracteres.");
            return false;
        }

        if (!paciente.getApellido().matches("^[A-Za-zÁÉÍÓÚáéíóúÑñ ]{2,50}$")) {
            mostrarError("El apellido solo puede contener letras y debe tener entre 2 y 50 caracteres.");
            return false;
        }

        if (!paciente.getTelefono().matches("^9\\d{8}$")) {
            mostrarError("El teléfono debe tener exactamente 9 dígitos y empezar con 9.");
            return false;
        }

        if (!paciente.getCorreo().matches("^[A-Za-z][A-Za-z0-9._%+-]{2,}@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            mostrarError("Ingrese un correo válido. Ejemplo: paciente@correo.com");
            return false;
        }

        return true;
    }

    @FXML
    private void cancelar() {
        cerrar();
    }

    private void cerrar() {
        Stage stage = (Stage) txtDni.getScene().getWindow();
        stage.close();
    }

    private String campoFaltante(String valor, String nombreCampo) {
        return valor == null || valor.isBlank() ? nombreCampo : null;
    }

    private String camposFaltantes(String... campos) {
        List<String> faltantes = new ArrayList<>();
        for (String campo : campos) {
            if (campo != null) {
                faltantes.add(campo);
            }
        }
        return faltantes.isEmpty() ? null : String.join(", ", faltantes);
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
