package edu.eci.arsw.ejercicio1;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;


public class SalonClient {

    private static final String HOST = "127.0.0.1";
    private static final int    PORT = 35000;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== Cliente de Gestión de Salones ===");
        System.out.println("Conectando a " + HOST + ":" + PORT);

        boolean continuar = true;

        while (continuar) {
            System.out.println("\n--- Menú ---");
            System.out.println("1. Consultar salón");
            System.out.println("2. Reservar salón");
            System.out.println("3. Liberar salón");
            System.out.println("4. Salir");
            System.out.print("Opción: ");

            String opcion = scanner.nextLine().trim();

            String operacion;
            switch (opcion) {
                case "1": operacion = "CONSULTAR_SALON"; break;
                case "2": operacion = "RESERVAR_SALON";  break;
                case "3": operacion = "LIBERAR_SALON";   break;
                case "4": continuar = false; continue;
                default:
                    System.out.println("Opción inválida.");
                    continue;
            }

            System.out.print("ID del salón (ej: E301, E302, E303, E304): ");
            String salonId = scanner.nextLine().trim();

            String mensaje = operacion + "," + salonId;
            String respuesta = enviar(mensaje);

            System.out.println("► Respuesta del servidor: " + respuesta);
        }

        System.out.println("Conexión cerrada.");
        scanner.close();
    }

    private static String enviar(String mensaje) {
        try (
            Socket socket = new Socket(HOST, PORT);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()))
        ) {
            out.println(mensaje);
            return in.readLine();

        } catch (Exception e) {
            return "ERROR: no se pudo conectar al servidor (" + e.getMessage() + ")";
        }
    }
}