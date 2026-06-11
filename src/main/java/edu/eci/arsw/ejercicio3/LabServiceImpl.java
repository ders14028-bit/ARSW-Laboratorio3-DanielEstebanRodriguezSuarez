package edu.eci.arsw.ejercicio3;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class LabServiceImpl extends UnicastRemoteObject implements LabService {

    private final LabRepository repository;

    public LabServiceImpl() throws RemoteException {
        this.repository = new LabRepository();
    }

    @Override
    public List<String> consultarEquipos() throws RemoteException {
        List<String> resultado = new ArrayList<>();
        for (Equipo e : repository.findAll().values()) {
            resultado.add(e.toString());
        }
        return resultado;
    }

    @Override
    public String consultarEquipo(String codigo) throws RemoteException {
        Equipo equipo = repository.findById(codigo);
        if (equipo == null) return "ERROR: equipo no encontrado";
        return equipo.toString();
    }

    @Override
    public synchronized boolean reservarEquipo(String codigo) throws RemoteException {
        Equipo equipo = repository.findById(codigo);
        if (equipo == null || equipo.isReservado()) return false;
        equipo.reservar();
        return true;
    }

    @Override
    public synchronized boolean liberarEquipo(String codigo) throws RemoteException {
        Equipo equipo = repository.findById(codigo);
        if (equipo == null || !equipo.isReservado()) return false;
        equipo.liberar();
        return true;
    }
}