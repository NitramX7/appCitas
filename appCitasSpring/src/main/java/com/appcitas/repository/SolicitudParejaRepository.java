package com.appcitas.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.appcitas.model.SolicitudPareja;
import com.appcitas.model.Usuario;

public interface SolicitudParejaRepository extends JpaRepository<SolicitudPareja, Long> {

    List<SolicitudPareja> findBySolicitadoAndEstado(Usuario solicitado, String estado);

    // Para buscar si ya existe una solicitud entre dos usuarios (en cualquier
    // dirección)
    // Esto es útil para validar antes de enviar una nueva
    // Pero por simplicidad y siguiendo las instrucciones, nos enfocaremos en lo
    // pedido.
    // Aunque sería bueno tener:
    // boolean existsBySolicitanteAndSolicitadoAndEstado(Usuario solicitante,
    // Usuario solicitado, String estado);
}
