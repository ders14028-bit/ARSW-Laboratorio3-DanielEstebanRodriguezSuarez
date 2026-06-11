package edu.eci.arsw.ejercicio3;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class LabRmiServer {

    public static void main(String[] args) throws Exception {
    LabService service = new LabServiceImpl();
    Registry registry = LocateRegistry.createRegistry(23000);
    registry.rebind("labService", service);
    System.out.println("LabService RMI publicado en puerto 23000...");
    Thread.currentThread().join(); // mantiene el servidor vivo
}
}