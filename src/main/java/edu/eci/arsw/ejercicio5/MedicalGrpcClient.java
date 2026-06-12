package edu.eci.arsw.ejercicio5;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.util.Scanner;

public class MedicalGrpcClient {
    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder
            .forAddress("localhost", 50052)
            .usePlaintext()
            .build();

        MedicalServiceGrpc.MedicalServiceBlockingStub stub =
            MedicalServiceGrpc.newBlockingStub(channel);

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n=== Especialidades Médicas ===");
            System.out.println("1. Ver todas las especialidades");
            System.out.println("2. Consultar especialidad por código");
            System.out.println("3. Salir");
            System.out.print("Opción: ");
            String opcion = scanner.nextLine();

            switch (opcion) {
                case "1" -> {
                    SpecialtyList list = stub.getSpecialties(Empty.newBuilder().build());
                    list.getSpecialtiesList().forEach(s ->
                        System.out.println(s.getCode() + " | " + s.getName()
                            + " | " + s.getDescription()
                            + " | " + (s.getAvailable() ? "DISPONIBLE" : "NO_DISPONIBLE"))
                    );
                }
                case "2" -> {
                    System.out.print("Código (MED/PSI/ODO): ");
                    String code = scanner.nextLine();
                    SpecialtyResponse response = stub.getSpecialty(
                        SpecialtyRequest.newBuilder().setCode(code).build());
                    if (response.getFound()) {
                        Specialty s = response.getSpecialty();
                        System.out.println(s.getCode() + " | " + s.getName()
                            + " | " + s.getDescription());
                    } else {
                        System.out.println("→ Especialidad no encontrada.");
                    }
                }
                case "3" -> running = false;
                default -> System.out.println("Opción inválida.");
            }
        }

        scanner.close();
        channel.shutdown();
    }
}