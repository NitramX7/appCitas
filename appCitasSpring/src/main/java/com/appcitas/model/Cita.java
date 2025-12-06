package com.appcitas.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "citas")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cita implements Serializable {

    private static final long serialVersionUID = 1L;

    // --------------------------
    // CAMPOS PRINCIPALES
    // --------------------------
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    String titulo;

    @Column
    String descripcion;

    @Column
    Integer temporada;

    @Column
    Integer dinero;

    @Column
    Integer intensidad;

    @Column
    Integer cercania;

    @Column
    Integer facilidad;

    @Column(name = "es_default")
    boolean esDefault;

    @Column(name = "fecha_creacion")
    LocalDateTime fechaCreacion;

    @Column(name = "tokenfirebase", length = 400)
    String tokenFirebase;

    // --------------------------
    // RELACIÓN CON USUARIO (creador_id)
    // --------------------------
    @Column(name = "creador_id")
    private Long creadorId;

    // --------------------------
    // GETTERS / SETTERS
    // --------------------------

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getTemporada() {
        return temporada;
    }

    public void setTemporada(Integer temporada) {
        this.temporada = temporada;
    }

    public Integer getDinero() {
        return dinero;
    }

    public void setDinero(int dinero) {
        this.dinero = dinero;
    }

    public Integer getIntensidad() {
        return intensidad;
    }

    public void setIntensidad(int intensidad) {
        this.intensidad = intensidad;
    }

    public Integer getCercania() {
        return cercania;
    }

    public void setCercania(int cercania) {
        this.cercania = cercania;
    }

    public Integer getFacilidad() {
        return facilidad;
    }

    public void setFacilidad(int facilidad) {
        this.facilidad = facilidad;
    }

    public boolean isEsDefault() {
        return esDefault;
    }

    public void setEsDefault(boolean esDefault) {
        this.esDefault = esDefault;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Long getCreadorId() {
        return creadorId;
    }

    public void setCreadorId(Long creadorId) {
        this.creadorId = creadorId;
    }

    public String getTokenFirebase() {
        return tokenFirebase;
    }

    public void setTokenFirebase(String tokenFirebase) {
        this.tokenFirebase = tokenFirebase;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }
}
