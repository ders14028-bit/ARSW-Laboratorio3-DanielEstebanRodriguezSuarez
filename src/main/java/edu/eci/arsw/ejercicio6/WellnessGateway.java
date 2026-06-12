package edu.eci.arsw.ejercicio6;

import edu.eci.arsw.ejercicio4.*;
import edu.eci.arsw.ejercicio5.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.Scanner;

public class WellnessGateway {

    private final ManagedChannel appointmentChannel;
    private final ManagedChannel medicalChannel;
    private final ManagedChannel gymChannel;
    private final ManagedChannel recreationChannel;

    private final AppointmentServiceGrpc.AppointmentServiceBlockingStub appointmentStub;
    private final MedicalServiceGrpc.MedicalServiceBlockingStub medicalStub;
    private final GymServiceGrpc.GymServiceBlockingStub gymStub;
    private final RecreationServiceGrpc.RecreationServiceBlockingStub recreationStub;

    public WellnessGateway() {
        appointmentChannel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext().build();
        medicalChannel = ManagedChannelBuilder.forAddress("localhost", 50052)
                .usePlaintext().build();
        gymChannel = ManagedChannelBuilder.forAddress("localhost", 50053)
                .usePlaintext().build();
        recreationChannel = ManagedChannelBuilder.forAddress("localhost", 50054)
                .usePlaintext().build();

        appointmentStub = AppointmentServiceGrpc.newBlockingStub(appointmentChannel);
        medicalStub = MedicalServiceGrpc.newBlockingStub(medicalChannel);
        gymStub = GymServiceGrpc.newBlockingStub(gymChannel);
        recreationStub = RecreationServiceGrpc.newBlockingStub(recreationChannel);
    }

    public void requestAppointment(String studentId, String serviceType, String date) {
        ServiceType type;
        try {
            type = ServiceType.valueOf(serviceType.toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println("Tipo de servicio inválido. Use MEDICINE, PSYCHOLOGY o DENTISTRY.");
            return;
        }

        AppointmentRequest request = AppointmentRequest.newBuilder()
                .setStudentId(studentId)
                .setServiceType(type)
                .setDate(date)
                .build();

        AppointmentResponse response = appointmentStub.requestAppointment(request);

        System.out.println("Resultado: " + (response.getSuccess() ? "OK" : "ERROR"));
        System.out.println("Mensaje: " + response.getMessage());
        if (response.getSuccess()) {
            Appointment a = response.getAppointment();
            System.out.println("Cita creada -> id: " + a.getId()
                    + ", servicio: " + a.getServiceType()
                    + ", fecha: " + a.getDate()
                    + ", estado: " + a.getStatus());
        }
    }

    public void getStudentWellnessSummary(String studentId) {
        System.out.println("\n========== RESUMEN DE BIENESTAR: " + studentId + " ==========");

        System.out.println("\n-- Citas --");
        StudentRequest studentRequest = StudentRequest.newBuilder()
                .setStudentId(studentId)
                .build();
        AppointmentList appointments = appointmentStub.getAppointments(studentRequest);
        if (appointments.getAppointmentsCount() == 0) {
            System.out.println("  (sin citas registradas)");
        } else {
            for (Appointment a : appointments.getAppointmentsList()) {
                System.out.println("  - " + a.getId() + " | " + a.getServiceType()
                        + " | " + a.getDate() + " | " + a.getStatus());
            }
        }

        System.out.println("\n-- Especialidades médicas disponibles --");
        SpecialtyList specialties = medicalStub.getSpecialties(Empty.newBuilder().build());
        for (Specialty s : specialties.getSpecialtiesList()) {
            System.out.println("  - " + s.getCode() + " | " + s.getName()
                    + " | disponible: " + s.getAvailable());
        }

        System.out.println("\n-- Sesiones de gimnasio --");
        SessionList sessions = gymStub.getSessions(Empty.newBuilder().build());
        for (GymSession g : sessions.getSessionsList()) {
            System.out.println("  - " + g.getId() + " | " + g.getActivity()
                    + " | " + g.getTimeSlot()
                    + " | cupos: " + g.getReserved() + "/" + g.getCapacity());
        }

        System.out.println("\n-- Recursos de recreación --");
        ResourceList resources = recreationStub.getResources(Empty.newBuilder().build());
        for (RecreationResource r : resources.getResourcesList()) {
            System.out.println("  - " + r.getId() + " | " + r.getName()
                    + " | " + r.getType() + " | disponible: " + r.getAvailable());
        }

        System.out.println("==================================================\n");
    }

    public void reserveGymSession(String studentId, String timeSlot) {
        SessionList sessions = gymStub.getSessions(Empty.newBuilder().build());

        GymSession target = null;
        for (GymSession g : sessions.getSessionsList()) {
            if (g.getTimeSlot().equalsIgnoreCase(timeSlot)) {
                target = g;
                break;
            }
        }

        if (target == null) {
            System.out.println("ERROR: no existe una sesión de gimnasio en la franja '" + timeSlot + "'");
            return;
        }

        GymReservationRequest request = GymReservationRequest.newBuilder()
                .setStudentId(studentId)
                .setSessionId(target.getId())
                .build();

        GymReservationResponse response = gymStub.reserveSession(request);
        System.out.println("Resultado: " + (response.getSuccess() ? "OK" : "ERROR"));
        System.out.println("Mensaje: " + response.getMessage());
    }

    public void reserveRecreationResource(String studentId, String resourceId) {
        RecreationRequest request = RecreationRequest.newBuilder()
                .setStudentId(studentId)
                .setResourceId(resourceId)
                .build();

        RecreationResponse response = recreationStub.reserveResource(request);
        System.out.println("Resultado: " + (response.getSuccess() ? "OK" : "ERROR"));
        System.out.println("Mensaje: " + response.getMessage());
    }

    private void shutdown() {
        appointmentChannel.shutdown();
        medicalChannel.shutdown();
        gymChannel.shutdown();
        recreationChannel.shutdown();
    }

    public static void main(String[] args) {
        WellnessGateway gateway = new WellnessGateway();
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== WellnessGateway ===");
        System.out.println("Conectado a:");
        System.out.println("  AppointmentService -> localhost:50051");
        System.out.println("  MedicalService     -> localhost:50052");
        System.out.println("  GymService         -> localhost:50053");
        System.out.println("  RecreationService  -> localhost:50054");

        boolean continuar = true;
        while (continuar) {
            System.out.println("\n--- Menú ---");
            System.out.println("1. Solicitar cita");
            System.out.println("2. Ver resumen de bienestar de un estudiante");
            System.out.println("3. Reservar sesión de gimnasio");
            System.out.println("4. Reservar recurso de recreación");
            System.out.println("5. Salir");
            System.out.print("Opción: ");

            String opcion = scanner.nextLine().trim();

            switch (opcion) {
                case "1":
                    System.out.print("ID del estudiante: ");
                    String studentId1 = scanner.nextLine().trim();
                    System.out.print("Tipo de servicio (MEDICINE, PSYCHOLOGY, DENTISTRY): ");
                    String serviceType = scanner.nextLine().trim();
                    System.out.print("Fecha (ej: 2026-06-20): ");
                    String date = scanner.nextLine().trim();
                    gateway.requestAppointment(studentId1, serviceType, date);
                    break;

                case "2":
                    System.out.print("ID del estudiante: ");
                    String studentId2 = scanner.nextLine().trim();
                    gateway.getStudentWellnessSummary(studentId2);
                    break;

                case "3":
                    System.out.print("ID del estudiante: ");
                    String studentId3 = scanner.nextLine().trim();
                    System.out.print("Franja horaria (timeSlot): ");
                    String timeSlot = scanner.nextLine().trim();
                    gateway.reserveGymSession(studentId3, timeSlot);
                    break;

                case "4":
                    System.out.print("ID del estudiante: ");
                    String studentId4 = scanner.nextLine().trim();
                    System.out.print("ID del recurso: ");
                    String resourceId = scanner.nextLine().trim();
                    gateway.reserveRecreationResource(studentId4, resourceId);
                    break;

                case "5":
                    continuar = false;
                    break;

                default:
                    System.out.println("Opción inválida.");
            }
        }

        gateway.shutdown();
        scanner.close();
        System.out.println("Gateway cerrado.");
    }
}