package com.appcitas.service;

import java.util.List;
import com.appcitas.dto.SolicitudRequest;
import com.appcitas.model.SolicitudPareja;

public interface SolicitudService {
    SolicitudPareja enviarSolicitud(Long solicitanteId, SolicitudRequest request);

    List<SolicitudPareja> obtenerSolicitudesRecibidas(Long usuarioId);

    void aceptarSolicitud(Long solicitudId);

    void rechazarSolicitud(Long solicitudId);
}
