package edu.eci.arsw.ejercicio5;

import io.grpc.Server;
import io.grpc.ServerBuilder;

public class GymGrpcServer {
    public static void main(String[] args) throws Exception {
        Server server = ServerBuilder.forPort(50053)
            .addService(new GymServiceImpl())
            .build();

        server.start();
        System.out.println("GymService gRPC iniciado en puerto 50053");
        server.awaitTermination();
    }
}