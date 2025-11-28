package com.appcitas.serviceImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.appcitas.model.Usuario;
import com.appcitas.repository.UsuarioRepo;
import com.appcitas.service.UsuarioService;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    UsuarioRepo usuarioRepositorio;

    @Override
    public List<Usuario> findAllUsuarios() {
        return usuarioRepositorio.findAll();
    }

    @Override
    public Optional<Usuario> findUsuarioById(Long id) {
        return usuarioRepositorio.findById(id);
    }

    @Override
    public Usuario saveUsuario(Usuario nuevoUsuario) {

        if (usuarioRepositorio.existsByEmail(nuevoUsuario.getEmail())) {
            throw new RuntimeException("EMAIL_EXISTS");
        }

        return usuarioRepositorio.save(nuevoUsuario);
    }

    @Override
    public String deleteUsuario(Long id) {
        Optional<Usuario> u = usuarioRepositorio.findById(id);
        if (u.isPresent()) {
            usuarioRepositorio.deleteById(id);
            return "Usuario eliminado satisfactoriamente";
        }
        return "El usuario no existe";
    }

    @Override
    public Usuario updateUsuario(Usuario usuarioActualizar) {
        if (usuarioActualizar != null &&
                usuarioRepositorio.findById(usuarioActualizar.getId()).isPresent()) {

            usuarioRepositorio.save(usuarioActualizar);
            return usuarioActualizar;
        }
        return null;
    }

    @Override
    public Usuario login(String email, String passwordHash) {

        Usuario usuario = usuarioRepositorio.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("BAD_CREDENTIALS"));

        if (!usuario.getPassword().equals(passwordHash)) {
            throw new RuntimeException("BAD_CREDENTIALS");
        }

        return usuario;
    }

    @Override
    public Usuario findByEmail(String email) {
        return usuarioRepositorio.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con email: " + email));
    }
}
