package edu.eci.arsw.ejercicio5;

import edu.eci.arsw.ejercicio4.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.util.Scanner;

public class WellnessClient {
    public static void main(String[] args) {
        ManagedChannel appointmentChannel = ManagedChannelBuilder
            .forAddress("localhost", 50051).usePlaintext().build();
        ManagedChannel medicalChannel = ManagedChannelBuilder
            .forAddress("localhost", 50052).usePlaintext().build();
        ManagedChannel gymChannel = ManagedChannelBuilder
            .forAddress("localhost", 50053).usePlaintext().build();
        ManagedChannel recreationChannel = ManagedChannelBuilder
            .forAddress("localhost", 50054).usePlaintext().build();

        AppointmentServiceGrpc.AppointmentServiceBlockingStub appointmentStub =
            AppointmentServiceGrpc.newBlockingStub(appointmentChannel);
        MedicalServiceGrpc.MedicalServiceBlockingStub medicalStub =
            MedicalServiceGrpc.newBlockingStub(medicalChannel);
        GymServiceGrpc.GymServiceBlockingStub gymStub =
            GymServiceGrpc.newBlockingStub(gymChannel);
        RecreationServiceGrpc.RecreationServiceBlockingStub recreationStub =
            RecreationServiceGrpc.newBlockingStub(recreationChannel);

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n=== Sistema de Bienestar Universitario ===");
            System.out.println("1. Ver especialidades médicas disponibles");
            System.out.println("2. Solicitar cita");
            System.out.println("3. Cancelar cita");
            System.out.println("4. Ver mis citas activas");
            System.out.println("5. Ver sesiones de gimnasio");
            System.out.println("6. Reservar sesión de gimnasio");
            System.out.println("7. Ver recursos recreativos");
            System.out.println("8. Reservar recurso recreativo");
            System.out.println("9. Salir");
            System.out.print("Opción: ");
            String opcion = scanner.nextLine();

            switch (opcion) {
                case "1" -> {
                    SpecialtyList list = medicalStub.getSpecialties(Empty.newBuilder().build());
                    list.getSpecialtiesList().forEach(s ->
                        System.out.println(s.getCode() + " | " + s.getName() + " | " + s.getDescription())
                    );
                }
                case "2" -> {
                    System.out.print("ID estudiante: ");
                    String studentId = scanner.nextLine();
                    System.out.println("Servicios: 0=MEDICINE, 1=PSYCHOLOGY, 2=DENTISTRY");
                    System.out.print("Tipo (0/1/2): ");
                    int tipoNum = Integer.parseInt(scanner.nextLine());
                    ServiceType tipo = ServiceType.forNumber(tipoNum);
                    System.out.print("Fecha (ej: 2026-06-20): ");
                    String fecha = scanner.nextLine();

                    AppointmentResponse response = appointmentStub.requestAppointment(
                        AppointmentRequest.newBuilder()
                            .setStudentId(studentId).setServiceType(tipo).setDate(fecha).build());
                    System.out.println("→ " + response.getMessage());
                }
                case "3" -> {
                    System.out.print("ID de la cita a cancelar: ");
                    String aptId = scanner.nextLine();
                    CancelResponse cancel = appointmentStub.cancelAppointment(
                        CancelRequest.newBuilder().setAppointmentId(aptId).build());
                    System.out.println("→ " + cancel.getMessage());
                }
                case "4" -> {
                    System.out.print("ID estudiante: ");
                    String studentId = scanner.nextLine();
                    AppointmentList list = appointmentStub.getAppointments(
                        StudentRequest.newBuilder().setStudentId(studentId).build());

                    if (list.getAppointmentsCount() == 0) {
                        System.out.println("→ No hay citas activas.");
                    } else {
                        list.getAppointmentsList().forEach(a ->
                            System.out.println("   " + a.getId() + " | " + a.getServiceType()
                                + " | " + a.getDate() + " | " + a.getStatus())
                        );
                    }
                }
                case "5" -> {
                    SessionList list = gymStub.getSessions(Empty.newBuilder().build());
                    list.getSessionsList().forEach(s ->
                        System.out.println(s.getId() + " | " + s.getActivity()
                            + " | " + s.getTimeSlot()
                            + " | " + s.getReserved() + "/" + s.getCapacity())
                    );
                }
                case "6" -> {
                    System.out.print("ID estudiante: ");
                    String studentId = scanner.nextLine();
                    System.out.print("ID de sesión (ej: GYM-1): ");
                    String sessionId = scanner.nextLine();

                    GymReservationResponse response = gymStub.reserveSession(
                        GymReservationRequest.newBuilder()
                            .setStudentId(studentId).setSessionId(sessionId).build());
                    System.out.println("→ " + response.getMessage());
                }
                case "7" -> {
                    ResourceList list = recreationStub.getResources(Empty.newBuilder().build());
                    list.getResourcesList().forEach(r ->
                        System.out.println(r.getId() + " | " + r.getName()
                            + " | " + r.getType()
                            + " | " + (r.getAvailable() ? "DISPONIBLE" : "NO_DISPONIBLE"))
                    );
                }
                case "8" -> {
                    System.out.print("ID estudiante: ");
                    String studentId = scanner.nextLine();
                    System.out.print("ID de recurso (ej: REC-1): ");
                    String resourceId = scanner.nextLine();

                    RecreationResponse response = recreationStub.reserveResource(
                        RecreationRequest.newBuilder()
                            .setStudentId(studentId).setResourceId(resourceId).build());
                    System.out.println("→ " + response.getMessage());
                }
                case "9" -> running = false;
                default -> System.out.println("Opción inválida.");
            }
        }

        scanner.close();
        appointmentChannel.shutdown();
        medicalChannel.shutdown();
        gymChannel.shutdown();
        recreationChannel.shutdown();
    }
}