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
@Data @AllArgsConstructor @NoArgsConstructor
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
    int dinero;

    @Column
    int intensidad;

    @Column
    int cercania;

    @Column
    int facilidad;

    @Column(name = "es_default")
    boolean esDefault;

    @Column(name = "fecha_creacion")
    LocalDateTime fechaCreacion;

    // --------------------------
    // RELACIÓN CON USUARIO (creador_id)
    // --------------------------
    @ManyToOne
    @JoinColumn(name = "creador_id")
    private Usuario creador;

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

    public int getDinero() {
        return dinero;
    }

    public void setDinero(int dinero) {
        this.dinero = dinero;
    }

    public int getIntensidad() {
        return intensidad;
    }

    public void setIntensidad(int intensidad) {
        this.intensidad = intensidad;
    }

    public int getCercania() {
        return cercania;
    }

    public void setCercania(int cercania) {
        this.cercania = cercania;
    }

    public int getFacilidad() {
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

    public Usuario getCreador() {
        return creador;
    }

    public void setCreador(Usuario creador) {
        this.creador = creador;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }
}
