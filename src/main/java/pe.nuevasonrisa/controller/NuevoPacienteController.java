package pe.nuevasonrisa.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import pe.nuevasonrisa.dao.impl.PacienteDAOImpl;
import pe.nuevasonrisa.model.Paciente;
import pe.nuevasonrisa.service.PacienteService;
import pe.nuevasonrisa.service.AuditoriaService;

public class NuevoPacienteController {

    @FXML private TextField txtDni;
    @FXML private TextField txtNombre;
    @FXML private TextField txtApellido;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtCorreo;

    private final PacienteService service =
            new PacienteService(new PacienteDAOImpl());

    private final AuditoriaService auditoriaService =
            new AuditoriaService();

    @FXML
    private void guardar() {

        String dni = txtDni.getText().trim();
        String nombre = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String correo = txtCorreo.getText().trim();

        if (!validar(dni, nombre, apellido, telefono, correo)) {
            return;
        }

        Paciente paciente = new Paciente();

        paciente.setDni(dni);
        paciente.setNombre(nombre);
        paciente.setApellido(apellido);
        paciente.setTelefono(telefono);
        paciente.setCorreo(correo);

        boolean creado = service.crearPaciente(paciente);

        if (creado) {

            auditoriaService.registrar(
                    "CREAR",
                    "PACIENTES",
                    "Paciente " + nombre + " " + apellido + " creado"
            );

            mostrarInfo("Éxito", "Paciente registrado correctamente.");
            cerrar();
        } else {
            mostrarError("No se pudo registrar. Verifique si el DNI ya existe.");
        }
    }

    private boolean validar(String dni,
                            String nombre,
                            String apellido,
                            String telefono,
                            String correo) {

        if (dni.isBlank() || nombre.isBlank() || apellido.isBlank()) {
            mostrarError("DNI, nombre y apellido son obligatorios.");
            return false;
        }

        if (!dni.matches("\\d{8}")) {
            mostrarError("El DNI debe tener 8 dígitos.");
            return false;
        }

        if (!telefono.isBlank() && !telefono.matches("\\d{9}")) {
            mostrarError("El teléfono debe tener 9 dígitos.");
            return false;
        }

        if (!correo.isBlank() && !correo.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            mostrarError("El correo no tiene un formato válido.");
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

    private void mostrarInfo(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}