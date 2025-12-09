package com.appcitas.dto;

public class CitaFiltroRequest {

    private Integer temporada;
    private Integer dinero;
    private Integer intensidad;
    private Integer cercania;
    private Integer facilidad;
    private Long creadorId;
    private Long id;
    private Long coupleId;

    public Integer getTemporada() {
        return temporada;
    }

    public void setTemporada(Integer temporada) {
        this.temporada = temporada;
    }

    public Integer getDinero() {
        return dinero;
    }

    public void setDinero(Integer dinero) {
        this.dinero = dinero;
    }

    public Integer getIntensidad() {
        return intensidad;
    }

    public void setIntensidad(Integer intensidad) {
        this.intensidad = intensidad;
    }

    public Integer getCercania() {
        return cercania;
    }

    public void setCercania(Integer cercania) {
        this.cercania = cercania;
    }

    public Integer getFacilidad() {
        return facilidad;
    }

    public void setFacilidad(Integer facilidad) {
        this.facilidad = facilidad;
    }

    public Long getCreadorId() {
        return creadorId;
    }

    public void setCreadorId(Long creadorId) {
        this.creadorId = creadorId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCoupleId() {
        return coupleId;
    }

    public void setCoupleId(Long coupleId) {
        this.coupleId = coupleId;
    }
}
