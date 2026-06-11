package edu.eci.arsw.ejercicio2;

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;

public class SalonHttpServer {

    public static void main(String[] args) throws Exception {
        SalonRepository repository = new SalonRepository();
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/rooms", new SalonHandler(repository));

        server.setExecutor(null);
        server.start();
        System.out.println("SalonHttpServer escuchando en http://localhost:8080");
        System.out.println("Rutas disponibles:");
        System.out.println("  GET  /rooms           -> lista todos los salones");
        System.out.println("  GET  /rooms?id=E303   -> consulta un salón");
        System.out.println("  POST /rooms/reserve?id=E303 -> reservar");
        System.out.println("  POST /rooms/release?id=E303 -> liberar");
    }
}