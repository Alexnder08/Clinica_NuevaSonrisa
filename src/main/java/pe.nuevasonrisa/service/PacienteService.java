package pe.nuevasonrisa.service;

import pe.nuevasonrisa.dao.PacienteDAO;
import pe.nuevasonrisa.model.PacienteTabla;
import pe.nuevasonrisa.model.Paciente;

import java.util.List;

public class PacienteService {

    private final PacienteDAO dao;

    public PacienteService(PacienteDAO dao) {
        this.dao = dao;
    }

    public List<PacienteTabla> obtenerPacientes() {
        return dao.listarPacientes();
    }

    public boolean crearPaciente(Paciente paciente){
        return dao.crearPaciente(paciente);
    }

    public boolean actualizarPaciente(Paciente paciente){
        return dao.actualizarPaciente(paciente);
    }

}
