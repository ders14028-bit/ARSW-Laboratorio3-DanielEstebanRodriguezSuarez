package edu.eci.arsw.ejercicio3;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Scanner;

public class LabRmiClient {

    public static void main(String[] args) throws Exception {
        Registry registry = LocateRegistry.getRegistry("127.0.0.1", 23000);
        LabService service = (LabService) registry.lookup("labService");

        Scanner scanner = new Scanner(System.in);
        int opcion;

        do {
            System.out.println("\n=== Inventario de Laboratorios ===");
            System.out.println("1. Consultar todos los equipos");
            System.out.println("2. Consultar equipo por código");
            System.out.println("3. Reservar equipo");
            System.out.println("4. Liberar equipo");
            System.out.println("0. Salir");
            System.out.print("Opción: ");
            opcion = Integer.parseInt(scanner.nextLine());

            switch (opcion) {
                case 1 -> {
                    List<String> equipos = service.consultarEquipos();
                    equipos.forEach(System.out::println);
                }
                case 2 -> {
                    System.out.print("Código del equipo: ");
                    String codigo = scanner.nextLine();
                    System.out.println(service.consultarEquipo(codigo));
                }
                case 3 -> {
                    System.out.print("Código del equipo a reservar: ");
                    String codigo = scanner.nextLine();
                    boolean ok = service.reservarEquipo(codigo);
                    System.out.println(ok ? "RESERVA_EXITOSA" : "ERROR_OPERACION_INVALIDA");
                }
                case 4 -> {
                    System.out.print("Código del equipo a liberar: ");
                    String codigo = scanner.nextLine();
                    boolean ok = service.liberarEquipo(codigo);
                    System.out.println(ok ? "LIBERACION_EXITOSA" : "ERROR_OPERACION_INVALIDA");
                }
                case 0 -> System.out.println("Saliendo...");
                default -> System.out.println("Opción inválida");
            }
        } while (opcion != 0);

        scanner.close();
    }
}