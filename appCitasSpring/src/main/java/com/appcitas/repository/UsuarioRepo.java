package com.appcitas.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.appcitas.model.Usuario;

public interface UsuarioRepo extends JpaRepository<Usuario, Long> {

    boolean existsByEmail(String email);
    
    Optional<Usuario> findByEmail(String email);
}
