package com.appcitas.service;

import java.util.List;
import java.util.Optional;

import com.appcitas.dto.CitaFiltroRequest;
import com.appcitas.model.Cita;

public interface CitaService {

    public List<Cita> findAllCitas();

    public Optional<Cita> findCitaById(Long id);

    public Cita saveCita(Cita nuevaCita);

    public String deleteCita(Long id);

    public Cita updateCita(Cita citaActualizar);

    List<Cita> filtrarCitas(CitaFiltroRequest filtros);

}
