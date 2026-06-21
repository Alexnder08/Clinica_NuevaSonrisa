package pe.nuevasonrisa.dao.impl;

import pe.nuevasonrisa.config.DatabaseConnection;
import pe.nuevasonrisa.dao.CitaAdjuntoDAO;
import pe.nuevasonrisa.model.CitaAdjunto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CitaAdjuntoDAOImpl implements CitaAdjuntoDAO {

    @Override
    public boolean guardar(int citaId, int usuarioId, String nombre, String tipo, byte[] contenido) {
        String sql = """
            INSERT INTO cita_adjuntos (cita_id, usuario_id, nombre_archivo, tipo_contenido, tamano_bytes, contenido)
            SELECT c.id, ?, ?, ?, ?, ?
            FROM citas c
            WHERE c.id = ? AND c.doctor_id = ?
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            ps.setString(2, nombre);
            ps.setString(3, tipo);
            ps.setLong(4, contenido.length);
            ps.setBytes(5, contenido);
            ps.setInt(6, citaId);
            ps.setInt(7, usuarioId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("No se pudo guardar el adjunto: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<CitaAdjunto> listarPorCita(int citaId, int doctorId) {
        List<CitaAdjunto> adjuntos = new ArrayList<>();
        String sql = """
            SELECT a.id, a.cita_id, a.nombre_archivo, a.tipo_contenido, a.tamano_bytes, a.creado_en
            FROM cita_adjuntos a
            INNER JOIN citas c ON c.id = a.cita_id
            WHERE a.cita_id = ? AND c.doctor_id = ?
            ORDER BY a.creado_en DESC
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, citaId);
            ps.setInt(2, doctorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    adjuntos.add(new CitaAdjunto(
                            rs.getLong("id"), rs.getInt("cita_id"), rs.getString("nombre_archivo"),
                            rs.getString("tipo_contenido"), rs.getLong("tamano_bytes"),
                            rs.getTimestamp("creado_en").toLocalDateTime()
                    ));
                }
            }
        } catch (Exception e) {
            System.err.println("No se pudieron listar los adjuntos: " + e.getMessage());
        }
        return adjuntos;
    }

    @Override
    public byte[] obtenerContenido(long adjuntoId, int doctorId) {
        String sql = """
            SELECT a.contenido
            FROM cita_adjuntos a
            INNER JOIN citas c ON c.id = a.cita_id
            WHERE a.id = ? AND c.doctor_id = ?
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, adjuntoId);
            ps.setInt(2, doctorId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getBytes("contenido") : null;
            }
        } catch (Exception e) {
            System.err.println("No se pudo descargar el adjunto: " + e.getMessage());
            return null;
        }
    }
}
