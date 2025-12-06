package com.appcitas.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.appcitas.dto.FcmTokenRequest;
import com.appcitas.dto.LoginRequest;
import com.appcitas.model.Usuario;
import com.appcitas.service.UsuarioService;

@RestController
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping(value = "/usuarios")
    public List<Usuario> getUsuarios() {
        return usuarioService.findAllUsuarios();
    }

    @GetMapping(value = "/usuarios/{id}")
    public Optional<Usuario> getUsuarioById(@PathVariable Long id) {
        return usuarioService.findUsuarioById(id);
    }

    @PostMapping("/usuarios")
    public ResponseEntity<?> addUsuario(@RequestBody Usuario usuario) {
        try {
            Usuario guardado = usuarioService.saveUsuario(usuario);
            return ResponseEntity.ok(guardado); // 200 OK
        } catch (RuntimeException ex) {
            if ("EMAIL_EXISTS".equals(ex.getMessage())) {
                return ResponseEntity
                        .status(HttpStatus.CONFLICT) // 409
                        .body("Ese correo ya está registrado");
            }

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR) // 500
                    .body("Error al crear usuario");
        }
    }

    @DeleteMapping(value = "/usuarios/delete/{id}")
    public String deleteUsuario(@PathVariable Long id) {
        return usuarioService.deleteUsuario(id);
    }

    @PutMapping(value = "/usuarios")
    public Usuario updateUsuario(@RequestBody Usuario usuario) {
        return usuarioService.updateUsuario(usuario);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            Usuario usuario = usuarioService.login(
                    loginRequest.getEmail(),
                    loginRequest.getPassword());
            return ResponseEntity.ok(usuario); // 200
        } catch (RuntimeException ex) {
            if ("BAD_CREDENTIALS".equals(ex.getMessage())) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED) // 401
                        .body("Email o contraseña incorrectos");
            }
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR) // 500
                    .body("Error en el servidor");
        }
    }

    @GetMapping("/usuarios/by-email")
    public ResponseEntity<Usuario> getUsuarioByEmail(@RequestParam String email) {
        Usuario usuario = usuarioService.findByEmail(email);
        return ResponseEntity.ok(usuario);
    }

    @PostMapping("/usuarios/{id}/fcm-token")
    public ResponseEntity<?> actualizarFcmToken(
            @PathVariable Long id,
            @RequestBody FcmTokenRequest request) {
        try {
            Usuario actualizado = usuarioService.updateFcmToken(id, request.getToken());
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException ex) {
            if ("Usuario no encontrado".equals(ex.getMessage())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar token");
        }
    }
}
