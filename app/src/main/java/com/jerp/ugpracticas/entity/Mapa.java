package com.jerp.ugpracticas.entity;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Mapa {

    private Double latitud;
    private Double longitud;

    public Mapa() { }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }
}
