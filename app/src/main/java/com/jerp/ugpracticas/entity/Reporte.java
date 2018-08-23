package com.jerp.ugpracticas.entity;

import com.google.firebase.database.IgnoreExtraProperties;

import java.sql.Timestamp;

@IgnoreExtraProperties
public class Reporte {
    private String cualbeneficio;
    private String cuantosbeneficios;
    private String fecha;
    private String horaentrada;
    private String horasalida;
    private Double latitud;
    private String localizacion;
    private Double longitud;
    private String usuario;
    private String queactividad;
    private String fotoentrada;
    private String fotosalida;

    public Reporte() {
    }

    public Reporte(String queactividad, String cualbeneficio, String cuantosbeneficios) {
        this.queactividad = queactividad;
        this.cualbeneficio = cualbeneficio;
        this.cuantosbeneficios = cuantosbeneficios;
    }

    public Reporte(String fecha, String usuario) {
        this.fecha = fecha;
        this.usuario = usuario;
    }

    public Reporte(Double latitud, Double longitud, String localizacion) {
        this.latitud = latitud;
        this.longitud = longitud;
        this.localizacion = localizacion;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getQueactividad() {
        return queactividad;
    }

    public void setQueactividad(String queactividad) {
        this.queactividad = queactividad;
    }

    public String getCualbeneficio() {
        return cualbeneficio;
    }

    public void setCualbeneficio(String cualbeneficio) {
        this.cualbeneficio = cualbeneficio;
    }

    public String getCuantosbeneficios() {
        return cuantosbeneficios;
    }

    public void setCuantosbeneficios(String cuantosbeneficios) {
        this.cuantosbeneficios = cuantosbeneficios;
    }

    public String getHoraentrada() {
        return horaentrada;
    }

    public void setHoraentrada(String horaentrada) {
        this.horaentrada = horaentrada;
    }

    public String getHorasalida() {
        return horasalida;
    }

    public void setHorasalida(String horasalida) {
        this.horasalida = horasalida;
    }

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

    public String getLocalizacion() {
        return localizacion;
    }

    public void setLocalizacion(String localizacion) {
        this.localizacion = localizacion;
    }

    public String getFotoentrada() {
        return fotoentrada;
    }

    public void setFotoentrada(String fotoentrada) {
        this.fotoentrada = fotoentrada;
    }

    public String getFotosalida() {
        return fotosalida;
    }

    public void setFotosalida(String fotosalida) {
        this.fotosalida = fotosalida;
    }
}
