package edu.eci.arsw.ejercicio4;

import io.grpc.stub.StreamObserver;
import java.util.*;

public class AppointmentServiceImpl
    extends AppointmentServiceGrpc.AppointmentServiceImplBase {

   
    private final Map<String, Appointment> store = new HashMap<>();
    private int counter = 1;

    @Override
    public void requestAppointment(AppointmentRequest req,
                                   StreamObserver<AppointmentResponse> obs) {
        String id = "APT-" + counter++;
        Appointment apt = Appointment.newBuilder()
            .setId(id)
            .setStudentId(req.getStudentId())
            .setServiceType(req.getServiceType())
            .setDate(req.getDate())
            .setStatus(Status.REQUESTED)
            .build();

        store.put(id, apt);

        AppointmentResponse response = AppointmentResponse.newBuilder()
            .setSuccess(true)
            .setMessage("Cita registrada con ID: " + id)
            .setAppointment(apt)
            .build();

        obs.onNext(response);
        obs.onCompleted();
    }

    @Override
    public void cancelAppointment(CancelRequest req,
                                  StreamObserver<CancelResponse> obs) {
        Appointment existing = store.get(req.getAppointmentId());

        if (existing == null) {
            obs.onNext(CancelResponse.newBuilder()
                .setSuccess(false)
                .setMessage("Cita no encontrada: " + req.getAppointmentId())
                .build());
        } else {
           
            Appointment cancelled = existing.toBuilder()
                .setStatus(Status.CANCELLED)
                .build();
            store.put(cancelled.getId(), cancelled);

            obs.onNext(CancelResponse.newBuilder()
                .setSuccess(true)
                .setMessage("Cita " + cancelled.getId() + " cancelada.")
                .build());
        }
        obs.onCompleted();
    }

    @Override
    public void getAppointments(StudentRequest req,
                                StreamObserver<AppointmentList> obs) {
        List<Appointment> result = new ArrayList<>();

        for (Appointment apt : store.values()) {
            // Solo incluir citas activas (no canceladas)
            if (apt.getStudentId().equals(req.getStudentId())
                && apt.getStatus() != Status.CANCELLED) {
                result.add(apt);
            }
        }

        obs.onNext(AppointmentList.newBuilder()
            .addAllAppointments(result)
            .build());
        obs.onCompleted();
    }
}