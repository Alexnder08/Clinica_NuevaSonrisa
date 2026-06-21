package pe.nuevasonrisa.dao;

import pe.nuevasonrisa.model.Paciente;
import pe.nuevasonrisa.model.PacienteTabla;

import java.util.List;

public interface PacienteDAO {

    List<PacienteTabla> listarPacientes();

    boolean crearPaciente(Paciente paciente);

    boolean actualizarPaciente(Paciente paciente);
    boolean cambiarEstado(int pacienteId, String estado);
}
