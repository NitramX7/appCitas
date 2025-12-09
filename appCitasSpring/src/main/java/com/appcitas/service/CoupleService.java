package com.appcitas.service;

import java.util.List;
import java.util.Optional;

import com.appcitas.model.Couple;

public interface CoupleService {

    List<Couple> findAllCouples();

    Optional<Couple> findCoupleById(Long id);

    Couple saveCouple(Couple couple);

    boolean deleteCouple(Long id);

    List<Couple> findCouplesByUser(Long userId);
}
