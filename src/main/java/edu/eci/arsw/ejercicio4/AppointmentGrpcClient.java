package edu.eci.arsw.ejercicio4;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.util.Scanner;

public class AppointmentGrpcClient {
    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder
            .forAddress("localhost", 50051)
            .usePlaintext()
            .build();

        AppointmentServiceGrpc.AppointmentServiceBlockingStub stub =
            AppointmentServiceGrpc.newBlockingStub(channel);

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n=== Sistema de Bienestar Universitario ===");
            System.out.println("1. Solicitar cita");
            System.out.println("2. Cancelar cita");
            System.out.println("3. Ver mis citas activas");
            System.out.println("4. Salir");
            System.out.print("Opción: ");
            String opcion = scanner.nextLine();

            switch (opcion) {
                case "1" -> {
                    System.out.print("ID estudiante: ");
                    String studentId = scanner.nextLine();

                    System.out.println("Servicios: 0=MEDICINE, 1=PSYCHOLOGY, 2=DENTISTRY");
                    System.out.print("Tipo de servicio (0/1/2): ");
                    int tipoNum = Integer.parseInt(scanner.nextLine());
                    ServiceType tipo = ServiceType.forNumber(tipoNum);

                    System.out.print("Fecha (ej: 2026-06-20): ");
                    String fecha = scanner.nextLine();

                    AppointmentRequest request = AppointmentRequest.newBuilder()
                        .setStudentId(studentId)
                        .setServiceType(tipo)
                        .setDate(fecha)
                        .build();

                    AppointmentResponse response = stub.requestAppointment(request);
                    System.out.println("→ " + response.getMessage());
                }
                case "2" -> {
                    System.out.print("ID de la cita a cancelar (ej: APT-1): ");
                    String aptId = scanner.nextLine();

                    CancelResponse cancel = stub.cancelAppointment(
                        CancelRequest.newBuilder().setAppointmentId(aptId).build()
                    );
                    System.out.println("→ " + cancel.getMessage());
                }
                case "3" -> {
                    System.out.print("ID estudiante: ");
                    String studentId = scanner.nextLine();

                    AppointmentList list = stub.getAppointments(
                        StudentRequest.newBuilder().setStudentId(studentId).build()
                    );

                    if (list.getAppointmentsCount() == 0) {
                        System.out.println("→ No hay citas activas.");
                    } else {
                        System.out.println("→ Citas activas:");
                        list.getAppointmentsList().forEach(a ->
                            System.out.println("   " + a.getId()
                                + " | " + a.getServiceType()
                                + " | " + a.getDate()
                                + " | " + a.getStatus())
                        );
                    }
                }
                case "4" -> running = false;
                default -> System.out.println("Opción inválida.");
            }
        }

        scanner.close();
        channel.shutdown();
        System.out.println("Cliente desconectado.");
    }
}