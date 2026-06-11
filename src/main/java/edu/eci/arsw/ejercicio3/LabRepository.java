package edu.eci.arsw.ejercicio3;

import java.util.HashMap;
import java.util.Map;

public class LabRepository {

    private final Map<String, Equipo> equipos = new HashMap<>();

    public LabRepository() {
        equipos.put("PC01", new Equipo("PC01", "PC Escritorio", "Lab Redes"));
        equipos.put("PC02", new Equipo("PC02", "PC Escritorio", "Lab Redes"));
        equipos.put("RB01", new Equipo("RB01", "Router Cisco", "Lab Redes"));
        equipos.put("AR01", new Equipo("AR01", "Brazo Robótico", "Lab Robótica"));
        equipos.put("AR02", new Equipo("AR02", "Arduino Kit", "Lab Robótica"));
    }

    public Equipo findById(String codigo) {
        return equipos.get(codigo);
    }

    public Map<String, Equipo> findAll() {
        return equipos;
    }
}