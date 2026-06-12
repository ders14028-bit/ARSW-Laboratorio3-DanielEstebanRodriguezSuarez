package edu.eci.arsw.ejercicio5;

import io.grpc.stub.StreamObserver;
import java.util.*;

public class GymServiceImpl extends GymServiceGrpc.GymServiceImplBase {

    private final Map<String, GymSession> sessions = new HashMap<>();

    public GymServiceImpl() {
        sessions.put("GYM-1", GymSession.newBuilder()
            .setId("GYM-1").setActivity("Spinning").setTimeSlot("Lunes 6:00am")
            .setCapacity(10).setReserved(0).build());

        sessions.put("GYM-2", GymSession.newBuilder()
            .setId("GYM-2").setActivity("Yoga").setTimeSlot("Martes 5:00pm")
            .setCapacity(15).setReserved(0).build());

        sessions.put("GYM-3", GymSession.newBuilder()
            .setId("GYM-3").setActivity("Pesas").setTimeSlot("Miércoles 7:00am")
            .setCapacity(8).setReserved(0).build());
    }

    @Override
    public void getSessions(Empty request, StreamObserver<SessionList> obs) {
        obs.onNext(SessionList.newBuilder().addAllSessions(sessions.values()).build());
        obs.onCompleted();
    }

    @Override
    public synchronized void reserveSession(GymReservationRequest request,
                                             StreamObserver<GymReservationResponse> obs) {
        GymSession session = sessions.get(request.getSessionId());
        GymReservationResponse response;

        if (session == null) {
            response = GymReservationResponse.newBuilder()
                .setSuccess(false).setMessage("Sesión no encontrada: " + request.getSessionId())
                .build();
        } else if (session.getReserved() >= session.getCapacity()) {
            response = GymReservationResponse.newBuilder()
                .setSuccess(false).setMessage("Sesión llena: " + session.getId())
                .build();
        } else {
            GymSession updated = session.toBuilder()
                .setReserved(session.getReserved() + 1)
                .build();
            sessions.put(updated.getId(), updated);

            response = GymReservationResponse.newBuilder()
                .setSuccess(true)
                .setMessage("Reserva exitosa en " + updated.getId()
                    + " para estudiante " + request.getStudentId())
                .build();
        }

        obs.onNext(response);
        obs.onCompleted();
    }
}