package com.diegomartinez.taller.service;

public class PlacaDuplicadaException extends RuntimeException {
    public PlacaDuplicadaException() {
        super("Ya existe otro vehículo registrado con esa placa.");
    }
}
