package com.appcitas.dto;

public class CitaFiltroRequest {

    private String temporada;   // VERANO / INVIERNO
    private String dinero;      // BARATO / MEDIO / CARO
    private String intensidad;  // BAJA / MEDIA / ALTA
    private String cercania;    // CERCA / LEJOS
    private String facilidad;   // FACIL / NORMAL / COMPLEJA

    public String getTemporada() {
        return temporada;
    }

    public void setTemporada(String temporada) {
        this.temporada = temporada;
    }

    public String getDinero() {
        return dinero;
    }

    public void setDinero(String dinero) {
        this.dinero = dinero;
    }

    public String getIntensidad() {
        return intensidad;
    }

    public void setIntensidad(String intensidad) {
        this.intensidad = intensidad;
    }

    public String getCercania() {
        return cercania;
    }

    public void setCercania(String cercania) {
        this.cercania = cercania;
    }

    public String getFacilidad() {
        return facilidad;
    }

    public void setFacilidad(String facilidad) {
        this.facilidad = facilidad;
    }
}
