package pe.nuevasonrisa.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import pe.nuevasonrisa.dao.impl.PacienteDAOImpl;
import pe.nuevasonrisa.model.Paciente;
import pe.nuevasonrisa.service.AuditoriaService;
import pe.nuevasonrisa.service.PacienteService;

import java.util.ArrayList;
import java.util.List;

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
            mostrarError("No se pudo registrar el paciente. Verifique que el DNI no esté repetido.");
        }
    }

    private boolean validar(String dni,
                            String nombre,
                            String apellido,
                            String telefono,
                            String correo) {
        String camposFaltantes = camposFaltantes(
                campoFaltante(dni, "DNI"),
                campoFaltante(nombre, "nombre"),
                campoFaltante(apellido, "apellido"),
                campoFaltante(telefono, "teléfono"),
                campoFaltante(correo, "correo")
        );

        if (camposFaltantes != null) {
            mostrarError("Complete los campos obligatorios: " + camposFaltantes + ".");
            return false;
        }

        if (!dni.matches("\\d{8}")) {
            mostrarError("El DNI debe tener exactamente 8 dígitos numéricos.");
            return false;
        }

        if (!nombre.matches("^[A-Za-zÁÉÍÓÚáéíóúÑñ ]{2,50}$")) {
            mostrarError("El nombre solo puede contener letras y debe tener entre 2 y 50 caracteres.");
            return false;
        }

        if (!apellido.matches("^[A-Za-zÁÉÍÓÚáéíóúÑñ ]{2,50}$")) {
            mostrarError("El apellido solo puede contener letras y debe tener entre 2 y 50 caracteres.");
            return false;
        }

        if (!telefono.matches("^9\\d{8}$")) {
            mostrarError("El teléfono debe tener exactamente 9 dígitos y empezar con 9.");
            return false;
        }

        if (!correo.matches("^[A-Za-z][A-Za-z0-9._%+-]{2,}@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
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
