package com.appcitas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.appcitas.model.Couple;

public interface CoupleRepo extends JpaRepository<Couple, Long> {

    List<Couple> findByUser1IdOrUser2Id(Long user1Id, Long user2Id);
}
