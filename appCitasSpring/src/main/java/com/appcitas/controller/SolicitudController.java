package com.appcitas.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.appcitas.dto.SolicitudRequest;
import com.appcitas.model.SolicitudPareja;
import com.appcitas.service.SolicitudService;

@RestController
@RequestMapping("/api/solicitudes")
public class SolicitudController {

    @Autowired
    private SolicitudService solicitudService;

    @PostMapping("/enviar")
    public ResponseEntity<?> enviarSolicitud(@RequestParam Long solicitanteId, @RequestBody SolicitudRequest request) {
        try {
            SolicitudPareja solicitud = solicitudService.enviarSolicitud(solicitanteId, request);
            return ResponseEntity.ok(solicitud);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/recibidas")
    public ResponseEntity<List<SolicitudPareja>> obtenerSolicitudesRecibidas(@RequestParam Long userId) {
        List<SolicitudPareja> solicitudes = solicitudService.obtenerSolicitudesRecibidas(userId);
        return ResponseEntity.ok(solicitudes);
    }

    @PostMapping("/{id}/aceptar")
    public ResponseEntity<?> aceptarSolicitud(@PathVariable Long id) {
        try {
            solicitudService.aceptarSolicitud(id);
            return ResponseEntity.ok("Solicitud aceptada");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/rechazar")
    public ResponseEntity<?> rechazarSolicitud(@PathVariable Long id) {
        try {
            solicitudService.rechazarSolicitud(id);
            return ResponseEntity.ok("Solicitud rechazada");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
