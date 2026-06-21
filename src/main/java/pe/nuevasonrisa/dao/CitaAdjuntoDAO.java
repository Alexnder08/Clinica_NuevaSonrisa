package pe.nuevasonrisa.dao;

import pe.nuevasonrisa.model.CitaAdjunto;

import java.util.List;

public interface CitaAdjuntoDAO {
    boolean guardar(int citaId, int usuarioId, String nombre, String tipo, byte[] contenido);
    List<CitaAdjunto> listarPorCita(int citaId, int doctorId);
    byte[] obtenerContenido(long adjuntoId, int doctorId);
}
