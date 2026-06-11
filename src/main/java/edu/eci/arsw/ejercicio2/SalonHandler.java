package edu.eci.arsw.ejercicio2;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

public class SalonHandler implements HttpHandler {

    private static final List<String> SALON_IDS = Arrays.asList("E301", "E302", "E303", "E304");
    private final SalonRepository repository;

    public SalonHandler(SalonRepository repository) {
        this.repository = repository;
    }

    @Override
    public void handle(HttpExchange exchange) throws java.io.IOException {
        String method = exchange.getRequestMethod();
        String path   = exchange.getRequestURI().getPath();   
        String query  = exchange.getRequestURI().getQuery();  

        String response;

        if (method.equals("GET") && path.equals("/rooms")) {
            if (query == null) {
                response = listarTodos();
            } else {
                String id = extraerId(query);
                response = consultarSalon(id);
            }

        } else if (method.equals("POST") && path.equals("/rooms/reserve")) {
            String id = extraerId(query);
            response = reservarSalon(id);

        } else if (method.equals("POST") && path.equals("/rooms/release")) {
            String id = extraerId(query);
            response = liberarSalon(id);

        } else {
            response = "ERROR: ruta no encontrada. Rutas válidas:\n"
                     + "  GET  /rooms\n"
                     + "  GET  /rooms?id=E303\n"
                     + "  POST /rooms/reserve?id=E303\n"
                     + "  POST /rooms/release?id=E303";
        }

        exchange.sendResponseHeaders(200, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private String listarTodos() {
        StringBuilder sb = new StringBuilder("=== Salones ===\n");
        for (String id : SALON_IDS) {
            Salon s = repository.findById(id);
            sb.append(s.getId()).append(" -> ").append(s.getEstado()).append("\n");
        }
        return sb.toString();
    }

    private String consultarSalon(String id) {
        if (id == null) return "ERROR: falta el parámetro id";
        Salon salon = repository.findById(id);
        if (salon == null) return "ERROR_SALON_NO_EXISTE";
        return salon.getId() + " -> " + salon.getEstado();
    }

    private synchronized String reservarSalon(String id) {
        if (id == null) return "ERROR: falta el parámetro id";
        Salon salon = repository.findById(id);
        if (salon == null)       return "ERROR_SALON_NO_EXISTE";
        if (salon.isReservado()) return "ERROR_OPERACION_INVALIDA: el salón ya está reservado";
        salon.reservar();
        return "RESERVA_EXITOSA: " + id;
    }

    private synchronized String liberarSalon(String id) {
        if (id == null) return "ERROR: falta el parámetro id";
        Salon salon = repository.findById(id);
        if (salon == null)        return "ERROR_SALON_NO_EXISTE";
        if (!salon.isReservado()) return "ERROR_OPERACION_INVALIDA: el salón ya está libre";
        salon.liberar();
        return "LIBERACION_EXITOSA: " + id;
    }

    private String extraerId(String query) {
        if (query == null || !query.startsWith("id=")) return null;
        return query.substring(3);
    }
}