package com.appcitas.serviceImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.appcitas.model.Couple;
import com.appcitas.repository.CoupleRepo;
import com.appcitas.service.CoupleService;

@Service
public class CoupleServiceImpl implements CoupleService {

    @Autowired
    private CoupleRepo coupleRepo;

    @Override
    public List<Couple> findAllCouples() {
        return coupleRepo.findAll();
    }

    @Override
    public Optional<Couple> findCoupleById(Long id) {
        return coupleRepo.findById(id);
    }

    @Override
    public Couple saveCouple(Couple couple) {
        if (couple != null) {
            return coupleRepo.save(couple);
        }
        return null;
    }

    @Override
    public boolean deleteCouple(Long id) {
        Optional<Couple> couple = coupleRepo.findById(id);
        if (couple.isPresent()) {
            coupleRepo.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public List<Couple> findCouplesByUser(Long userId) {
        return coupleRepo.findByUser1IdOrUser2Id(userId, userId);
    }
}
