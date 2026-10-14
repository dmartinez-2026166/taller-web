package com.diegomartinez.taller.model;

/** Fila de la tabla Usuario. */
public record UsuarioRegistro(
        String idUsuario,
        String nombre,
        String usuario,
        String password,
        String rol,
        boolean activo) {
}
