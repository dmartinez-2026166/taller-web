package com.diegomartinez.taller.service;

public class TrabajoNoEncontradoException extends RuntimeException {
    public TrabajoNoEncontradoException() {
        super("El trabajo ya no existe en la base de datos.");
    }
}
