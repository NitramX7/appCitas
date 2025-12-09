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

    @Autowired
    com.appcitas.repository.UsuarioRepo usuarioRepo;

    @Override
    public List<Cita> filtrarCitas(CitaFiltroRequest filtros) {
        Long partnerId = null;
        if (filtros.getCreadorId() != null) {
            Optional<com.appcitas.model.Usuario> userOpt = usuarioRepo.findById(filtros.getCreadorId());
            if (userOpt.isPresent()) {
                com.appcitas.model.Usuario user = userOpt.get();
                if (user.getEstado_p() != null && user.getEstado_p() == 1 && user.getPareja() != null) {
                    partnerId = user.getPareja().getId();
                }
            }
        }

        return citaRepositorio.filtrarCitas(
                filtros.getTemporada(),
                filtros.getDinero(),
                filtros.getIntensidad(),
                filtros.getCercania(),
                filtros.getFacilidad(),
                filtros.getCreadorId(),
                partnerId, // Use the calculated partnerId
                filtros.getId());
    }

}
