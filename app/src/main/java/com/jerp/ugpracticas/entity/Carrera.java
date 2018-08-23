package com.jerp.ugpracticas.entity;

import java.util.ArrayList;
import java.util.List;

public class Carrera {
    private String nombre;
    private String facultad;

    public Carrera() {}

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFacultad() {
        return facultad;
    }

    public void setFacultad(String facultad) {
        this.facultad = facultad;
    }
}
