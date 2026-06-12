package edu.eci.arsw.ejercicio5;

import io.grpc.Server;
import io.grpc.ServerBuilder;

public class RecreationGrpcServer {
    public static void main(String[] args) throws Exception {
        Server server = ServerBuilder.forPort(50054)
            .addService(new RecreationServiceImpl())
            .build();

        server.start();
        System.out.println("RecreationService gRPC iniciado en puerto 50054");
        server.awaitTermination();
    }
}