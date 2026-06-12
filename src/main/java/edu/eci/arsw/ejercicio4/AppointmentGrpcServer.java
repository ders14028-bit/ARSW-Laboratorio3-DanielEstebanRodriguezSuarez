package edu.eci.arsw.ejercicio4;

import io.grpc.Server;
import io.grpc.ServerBuilder;

public class AppointmentGrpcServer {
    public static void main(String[] args) throws Exception {
        Server server = ServerBuilder.forPort(50051)
            .addService(new AppointmentServiceImpl())
            .build();

        server.start();
        System.out.println("AppointmentService gRPC iniciado en puerto 50051");
        server.awaitTermination();
    }
}