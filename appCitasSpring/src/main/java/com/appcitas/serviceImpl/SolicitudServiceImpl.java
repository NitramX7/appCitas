package com.appcitas.serviceImpl;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.appcitas.dto.SolicitudRequest;
import com.appcitas.model.SolicitudPareja;
import com.appcitas.model.Usuario;
import com.appcitas.repository.SolicitudParejaRepository;
import com.appcitas.repository.UsuarioRepo;
import com.appcitas.service.SolicitudService;

@Service
public class SolicitudServiceImpl implements SolicitudService {

    @Autowired
    private SolicitudParejaRepository solicitudRepository;

    @Autowired
    private UsuarioRepo usuarioRepository;

    @Override
    public SolicitudPareja enviarSolicitud(Long solicitanteId, SolicitudRequest request) {
        Usuario solicitante = usuarioRepository.findById(solicitanteId)
                .orElseThrow(() -> new RuntimeException("Solicitante no encontrado"));

        Usuario solicitado = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario solicitado no encontrado"));

        if (solicitante.getId().equals(solicitado.getId())) {
            throw new RuntimeException("No puedes enviarte una solicitud a ti mismo");
        }

        if (solicitante.getEstado_p() == 1 || solicitado.getEstado_p() == 1) {
            throw new RuntimeException("Uno de los usuarios ya tiene pareja");
        }

        SolicitudPareja solicitud = new SolicitudPareja();
        solicitud.setSolicitante(solicitante);
        solicitud.setSolicitado(solicitado);
        solicitud.setEstado("PENDIENTE");

        return solicitudRepository.save(solicitud);
    }

    @Override
    public List<SolicitudPareja> obtenerSolicitudesRecibidas(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return solicitudRepository.findBySolicitadoAndEstado(usuario, "PENDIENTE");
    }

    @Override
    @Transactional
    public void aceptarSolicitud(Long solicitudId) {
        SolicitudPareja solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        if (!"PENDIENTE".equals(solicitud.getEstado())) {
            throw new RuntimeException("La solicitud no está pendiente");
        }

        Usuario solicitante = solicitud.getSolicitante();
        Usuario solicitado = solicitud.getSolicitado();

        // Actualizar usuarios
        solicitante.setEstado_p(1);
        solicitante.setPareja(solicitado);

        solicitado.setEstado_p(1);
        solicitado.setPareja(solicitante);

        usuarioRepository.save(solicitante);
        usuarioRepository.save(solicitado);

        // Actualizar solicitud
        solicitud.setEstado("ACEPTADA");
        solicitudRepository.save(solicitud);

        // Rechazar otras solicitudes pendientes (Opcional, pero recomendado)
        // Aquí se podría implementar la lógica para rechazar otras solicitudes
    }

    @Override
    public void rechazarSolicitud(Long solicitudId) {
        SolicitudPareja solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        solicitud.setEstado("RECHAZADA");
        solicitudRepository.save(solicitud);
    }
}
