package com.appcitas.model;

import java.io.Serializable;
import java.time.LocalDateTime; // si tu columna fecha_registro es DATETIME

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usuarios")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    // --------------------------
    // CAMPOS PRINCIPALES
    // --------------------------
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    String username;

    @Column(nullable = false, unique = true)
    String email;

    @Column(name = "password", nullable = false)
    String password;

    @Column
    String nombre;

    @Column(name = "fcm_token", length = 400)
    String fcmToken;

    @Column(name = "foto_url")
    private String fotoUrl;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    @Column(name = "estado_p", nullable = false, columnDefinition = "int default 0")
    private Integer estado_p = 0;

    @javax.persistence.OneToOne(fetch = javax.persistence.FetchType.LAZY)
    @javax.persistence.JoinColumn(name = "id_pareja")
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties({ "pareja", "password", "fcmToken", "citas",
            "solicitudesEnviadas", "solicitudesRecibidas", "hibernateLazyInitializer", "handler" })
    private Usuario pareja;

    // --------------------------
    // GETTERS / SETTERS
    // (Lombok los genera igual, pero en las prácticas se incluyen)
    // --------------------------

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public Integer getEstado_p() {
        return estado_p;
    }

    public void setEstado_p(Integer estado_p) {
        this.estado_p = estado_p;
    }

    public Usuario getPareja() {
        return pareja;
    }

    public void setPareja(Usuario pareja) {
        this.pareja = pareja;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }
}
