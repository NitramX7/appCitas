package com.appcitas.repository;

import com.appcitas.model.Cita;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CitaRepo extends JpaRepository<Cita, Long> {

        @Query("SELECT c FROM Cita c " +
                        "WHERE (:temporada IS NULL OR c.temporada = :temporada) " +
                        "AND (:dinero IS NULL OR c.dinero = :dinero) " +
                        "AND (:intensidad IS NULL OR c.intensidad = :intensidad) " +
                        "AND (:cercania IS NULL OR c.cercania = :cercania) " +
                        "AND (:facilidad IS NULL OR c.facilidad = :facilidad)")
        List<Cita> filtrarCitas(
                        @Param("temporada") Integer temporada,
                        @Param("dinero") Integer dinero,
                        @Param("intensidad") Integer intensidad,
                        @Param("cercania") Integer cercania,
                        @Param("facilidad") Integer facilidad);

        List<Cita> findByCreadorId(Long creadorId);

}
