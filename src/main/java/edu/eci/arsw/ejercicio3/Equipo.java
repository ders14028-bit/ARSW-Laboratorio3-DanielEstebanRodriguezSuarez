package edu.eci.arsw.ejercicio3;

import java.io.Serializable;

public class Equipo implements Serializable {

    private String codigo;
    private String nombre;
    private String laboratorio;
    private boolean reservado;

    public Equipo(String codigo, String nombre, String laboratorio) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.laboratorio = laboratorio;
        this.reservado = false;
    }

    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public String getLaboratorio() { return laboratorio; }
    public boolean isReservado() { return reservado; }

    public void reservar() { this.reservado = true; }
    public void liberar() { this.reservado = false; }

    public String getEstado() {
        return reservado ? "RESERVADO" : "DISPONIBLE";
    }

    @Override
    public String toString() {
        return codigo + " | " + nombre + " | " + laboratorio + " | " + getEstado();
    }
}
