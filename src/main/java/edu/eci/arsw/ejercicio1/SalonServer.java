package edu.eci.arsw.ejercicio1;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class SalonServer {

    private static final int PORT = 35000;

    public static void main(String[] args) throws Exception {
        SalonRepository repository = new SalonRepository();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("SalonServer TCP escuchando en puerto " + PORT + "...");
            System.out.println("Salones disponibles: E301, E302, E303, E304");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Cliente conectado: " + clientSocket.getInetAddress());

                
                new Thread(() -> handleClient(clientSocket, repository)).start();
            }
        }
    }

    private static void handleClient(Socket clientSocket, SalonRepository repository) {
        try (
            BufferedReader in = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            String request = in.readLine();
            System.out.println("Solicitud recibida: " + request);

            String response = processRequest(request, repository);
            System.out.println("Respuesta enviada:  " + response);

            out.println(response);

        } catch (Exception e) {
            System.err.println("Error atendiendo cliente: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (Exception ignored) {}
        }
    }

    private static synchronized String processRequest(String request,
                                                      SalonRepository repository) {
        if (request == null || request.isBlank()) {
            return "ERROR_OPERACION_INVALIDA";
        }

        String[] parts = request.split(",");
        if (parts.length != 2) {
            return "ERROR_OPERACION_INVALIDA";
        }

        String operacion = parts[0].trim();
        String salonId   = parts[1].trim();

        Salon salon = repository.findById(salonId);
        if (salon == null) {
            return "ERROR_SALON_NO_EXISTE";
        }

        switch (operacion) {
            case "CONSULTAR_SALON":
                return salon.isReservado() ? "SALON_RESERVADO" : "SALON_DISPONIBLE";

            case "RESERVAR_SALON":
                if (salon.isReservado()) {
                    return "ERROR_OPERACION_INVALIDA"; // ya está reservado
                }
                salon.reservar();
                return "RESERVA_EXITOSA";

            case "LIBERAR_SALON":
                if (!salon.isReservado()) {
                    return "ERROR_OPERACION_INVALIDA"; // ya está libre
                }
                salon.liberar();
                return "LIBERACION_EXITOSA";

            default:
                return "ERROR_OPERACION_INVALIDA";
        }
    }
}