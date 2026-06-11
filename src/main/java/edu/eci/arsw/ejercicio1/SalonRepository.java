package edu.eci.arsw.ejercicio1;

import java.util.HashMap;
import java.util.Map;

public class SalonRepository {

    private final Map<String, Salon> salones = new HashMap<>();

    public SalonRepository() {
        salones.put("E301", new Salon("E301"));
        salones.put("E302", new Salon("E302"));
        salones.put("E303", new Salon("E303"));
        salones.put("E304", new Salon("E304"));
    }

    public Salon findById(String id) {
        return salones.get(id);
    }
}
