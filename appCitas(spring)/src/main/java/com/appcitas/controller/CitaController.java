package com.appcitas.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.appcitas.dto.CitaFiltroRequest;
import com.appcitas.model.Cita;
import com.appcitas.service.CitaService;

@RestController
public class CitaController {

    @Autowired
    private CitaService citaService;

    @GetMapping(value = "/citas")
    public List<Cita> getCitas() {
        return citaService.findAllCitas();
    }

    @GetMapping(value = "/citas/{id}")
    public Optional<Cita> getCitaById(@PathVariable Long id) {
        return citaService.findCitaById(id);
    }

    @PostMapping(value = "/citas")
    public ResponseEntity<Cita> addCita(@RequestBody Cita cita) {
        Cita savedCita = citaService.saveCita(cita);
        return new ResponseEntity<>(savedCita, org.springframework.http.HttpStatus.CREATED);
    }

    @PostMapping(value = "/citas/filtrar")
    public List<Cita> filtrarCitas(@RequestBody CitaFiltroRequest filtros) {
        return citaService.filtrarCitas(filtros);
    }

    @DeleteMapping(value = { "/citas/{id}", "/citas/delete/{id}" })
    public ResponseEntity<Void> deleteCita(@PathVariable Long id) {
        boolean deleted = citaService.deleteCita(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping(value = "/citas/{id}")
    public ResponseEntity<Cita> updateCita(@PathVariable Long id, @RequestBody Cita cita) {
        cita.setId(id);
        Cita updatedCita = citaService.updateCita(cita);
        return ResponseEntity.ok(updatedCita);
    }

}
