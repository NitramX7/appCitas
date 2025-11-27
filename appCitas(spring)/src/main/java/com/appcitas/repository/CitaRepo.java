package com.appcitas.repository;

import com.appcitas.model.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CitaRepo extends JpaRepository<Cita, Long> {

    @Query("SELECT c FROM Cita c " +
           "WHERE (:temporada IS NULL OR c.temporada = :temporada) " +
           "AND (:dinero IS NULL OR c.dinero = :dinero) " +
           "AND (:intensidad IS NULL OR c.intensidad = :intensidad) " +
           "AND (:cercania IS NULL OR c.cercania = :cercania) " +
           "AND (:facilidad IS NULL OR c.facilidad = :facilidad)")
    List<Cita> filtrarCitas(
            @Param("temporada") String temporada,
            @Param("dinero") String dinero,
            @Param("intensidad") String intensidad,
            @Param("cercania") String cercania,
            @Param("facilidad") String facilidad
    );
}
