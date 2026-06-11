package edu.eci.arsw.ejercicio3;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

public class LabRmiClient {

    public static void main(String[] args) throws Exception {
        Registry registry = LocateRegistry.getRegistry("127.0.0.1", 23000);
        LabService service = (LabService) registry.lookup("labService");

        System.out.println("=== Todos los equipos ===");
        List<String> equipos = service.consultarEquipos();
        for (String e : equipos) {
            System.out.println(e);
        }

        System.out.println("\n=== Consultar PC01 ===");
        System.out.println(service.consultarEquipo("PC01"));

        System.out.println("\n=== Reservar PC01 ===");
        boolean reservado = service.reservarEquipo("PC01");
        System.out.println(reservado ? "RESERVA_EXITOSA" : "ERROR_OPERACION_INVALIDA");

        System.out.println("\n=== Intentar reservar PC01 de nuevo ===");
        reservado = service.reservarEquipo("PC01");
        System.out.println(reservado ? "RESERVA_EXITOSA" : "ERROR_OPERACION_INVALIDA");

        System.out.println("\n=== Liberar PC01 ===");
        boolean liberado = service.liberarEquipo("PC01");
        System.out.println(liberado ? "LIBERACION_EXITOSA" : "ERROR_OPERACION_INVALIDA");
    }
}