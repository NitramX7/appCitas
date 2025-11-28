package com.appcitas.service;

import java.util.List;
import java.util.Optional;

import com.appcitas.model.Usuario;

public interface UsuarioService {

    public List<Usuario> findAllUsuarios();

    public Optional<Usuario> findUsuarioById(Long id);

    public Usuario saveUsuario(Usuario nuevoUsuario);

    public String deleteUsuario(Long id);

    public Usuario updateUsuario(Usuario usuarioActualizar);

    public Usuario login(String email, String passwordHash);

    public Usuario findByEmail(String email);

}
