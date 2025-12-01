package com.appcitas.serviceImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.appcitas.dto.CitaFiltroRequest;
import com.appcitas.model.Cita;
import com.appcitas.repository.CitaRepo;
import com.appcitas.service.CitaService;

@Service
public class CitaServiceImpl implements CitaService {

    @Autowired
    CitaRepo citaRepositorio;

    @Override
    public List<Cita> findAllCitas() {
        return citaRepositorio.findAll();
    }

    @Override
    public Optional<Cita> findCitaById(Long id) {
        return citaRepositorio.findById(id);
    }

    @Override
    public Cita saveCita(Cita nuevaCita) {
        if (nuevaCita != null) {
            return citaRepositorio.save(nuevaCita);
        }
        return null;
    }

    @Override
    public boolean deleteCita(Long id) {
        Optional<Cita> c = citaRepositorio.findById(id);
        if (c.isPresent()) {
            citaRepositorio.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public Cita updateCita(Cita citaActualizar) {
        if (citaActualizar != null &&
                citaRepositorio.findById(citaActualizar.getId()).isPresent()) {

            citaRepositorio.save(citaActualizar);
            return citaActualizar;
        }
        return null;
    }

    @Override
    public List<Cita> filtrarCitas(CitaFiltroRequest filtros) {
        return citaRepositorio.filtrarCitas(
                filtros.getTemporada(),
                filtros.getDinero(),
                filtros.getIntensidad(),
                filtros.getCercania(),
                filtros.getFacilidad(),
                filtros.getCreadorId(),
                filtros.getId());
    }

}
