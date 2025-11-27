package com.appcitas.dto;

public class CitaFiltroRequest {

    private String temporada;
    private Integer dinero;
    private Integer intensidad;
    private Integer cercania;
    private Integer facilidad;

    public String getTemporada() {
        return temporada;
    }

    public void setTemporada(String temporada) {
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
}
