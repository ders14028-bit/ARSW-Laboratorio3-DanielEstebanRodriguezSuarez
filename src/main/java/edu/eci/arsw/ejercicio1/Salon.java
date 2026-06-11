package edu.eci.arsw.ejercicio1;

public class Salon {

    private String id;
    private boolean reservado;

    public Salon(String id) {
        this.id = id;
        this.reservado = false;
    }

    public String getId() {
        return id;
    }

    public boolean isReservado() {
        return reservado;
    }

    public void reservar() {
        this.reservado = true;
    }

    public void liberar() {
        this.reservado = false;
    }

    public String getEstado() {
        return reservado ? "RESERVADO" : "DISPONIBLE";
    }
}
