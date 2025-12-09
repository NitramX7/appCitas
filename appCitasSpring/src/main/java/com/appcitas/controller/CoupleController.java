package com.appcitas.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.appcitas.model.Couple;
import com.appcitas.service.CoupleService;

@RestController
@RequestMapping("/couples")
public class CoupleController {

    @Autowired
    private CoupleService coupleService;

    @GetMapping
    public List<Couple> getCouples() {
        return coupleService.findAllCouples();
    }

    @GetMapping("/{id}")
    public Optional<Couple> getCoupleById(@PathVariable Long id) {
        return coupleService.findCoupleById(id);
    }

    @GetMapping("/by-user/{userId}")
    public List<Couple> getCouplesByUser(@PathVariable Long userId) {
        return coupleService.findCouplesByUser(userId);
    }

    @PostMapping
    public ResponseEntity<Couple> saveCouple(@RequestBody Couple couple) {
        Couple savedCouple = coupleService.saveCouple(couple);
        return ResponseEntity.ok(savedCouple);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCouple(@PathVariable Long id) {
        boolean deleted = coupleService.deleteCouple(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
