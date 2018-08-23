package com.jerp.ugpracticas.entity;

public class FotosPractica {
    private String fotoentrada;
    private String fotosalida;

    public FotosPractica(String fotoentrada, String fotosalida) {
        this.fotoentrada = fotoentrada;
        this.fotosalida = fotosalida;
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
