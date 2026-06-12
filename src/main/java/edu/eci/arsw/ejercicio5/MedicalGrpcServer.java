package edu.eci.arsw.ejercicio5;

import io.grpc.Server;
import io.grpc.ServerBuilder;

public class MedicalGrpcServer {
    public static void main(String[] args) throws Exception {
        Server server = ServerBuilder.forPort(50052)
            .addService(new MedicalServiceImpl())
            .build();

        server.start();
        System.out.println("MedicalService gRPC iniciado en puerto 50052");
        server.awaitTermination();
    }
}