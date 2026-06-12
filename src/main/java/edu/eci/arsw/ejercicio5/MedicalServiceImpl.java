package edu.eci.arsw.ejercicio5;

import io.grpc.stub.StreamObserver;
import java.util.*;

public class MedicalServiceImpl extends MedicalServiceGrpc.MedicalServiceImplBase {

    private final Map<String, Specialty> specialties = new HashMap<>();

    public MedicalServiceImpl() {
        specialties.put("MED", Specialty.newBuilder()
            .setCode("MED").setName("Medicina General")
            .setDescription("Consulta médica general").setAvailable(true).build());

        specialties.put("PSI", Specialty.newBuilder()
            .setCode("PSI").setName("Psicología")
            .setDescription("Atención psicológica para estudiantes").setAvailable(true).build());

        specialties.put("ODO", Specialty.newBuilder()
            .setCode("ODO").setName("Odontología")
            .setDescription("Atención odontológica básica").setAvailable(true).build());
    }

    @Override
    public void getSpecialties(Empty request, StreamObserver<SpecialtyList> obs) {
        SpecialtyList list = SpecialtyList.newBuilder()
            .addAllSpecialties(specialties.values())
            .build();
        obs.onNext(list);
        obs.onCompleted();
    }

    @Override
    public void getSpecialty(SpecialtyRequest request, StreamObserver<SpecialtyResponse> obs) {
        Specialty s = specialties.get(request.getCode());
        SpecialtyResponse response;
        if (s == null) {
            response = SpecialtyResponse.newBuilder().setFound(false).build();
        } else {
            response = SpecialtyResponse.newBuilder().setFound(true).setSpecialty(s).build();
        }
        obs.onNext(response);
        obs.onCompleted();
    }
}