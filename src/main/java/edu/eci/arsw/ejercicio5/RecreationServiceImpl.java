package edu.eci.arsw.ejercicio5;

import io.grpc.stub.StreamObserver;
import java.util.*;

public class RecreationServiceImpl extends RecreationServiceGrpc.RecreationServiceImplBase {

    private final Map<String, RecreationResource> resources = new HashMap<>();

    public RecreationServiceImpl() {
        resources.put("REC-1", RecreationResource.newBuilder()
            .setId("REC-1").setName("Cancha de fútbol").setType("Deporte").setAvailable(true).build());

        resources.put("REC-2", RecreationResource.newBuilder()
            .setId("REC-2").setName("Mesa de ping pong").setType("Juego").setAvailable(true).build());

        resources.put("REC-3", RecreationResource.newBuilder()
            .setId("REC-3").setName("Sala de juegos de mesa").setType("Juego").setAvailable(true).build());
    }

    @Override
    public void getResources(Empty request, StreamObserver<ResourceList> obs) {
        obs.onNext(ResourceList.newBuilder().addAllResources(resources.values()).build());
        obs.onCompleted();
    }

    @Override
    public synchronized void reserveResource(RecreationRequest request,
                                              StreamObserver<RecreationResponse> obs) {
        RecreationResource resource = resources.get(request.getResourceId());
        RecreationResponse response;

        if (resource == null) {
            response = RecreationResponse.newBuilder()
                .setSuccess(false).setMessage("Recurso no encontrado: " + request.getResourceId())
                .build();
        } else if (!resource.getAvailable()) {
            response = RecreationResponse.newBuilder()
                .setSuccess(false).setMessage("Recurso no disponible: " + resource.getId())
                .build();
        } else {
            RecreationResource updated = resource.toBuilder().setAvailable(false).build();
            resources.put(updated.getId(), updated);

            response = RecreationResponse.newBuilder()
                .setSuccess(true)
                .setMessage("Reserva exitosa de " + updated.getId()
                    + " para estudiante " + request.getStudentId())
                .build();
        }

        obs.onNext(response);
        obs.onCompleted();
    }
}