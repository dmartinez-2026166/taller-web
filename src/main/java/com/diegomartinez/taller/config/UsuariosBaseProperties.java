package com.diegomartinez.taller.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/** Usuarios base definidos en application.properties (prefijo app.usuarios). */
@ConfigurationProperties(prefix = "app.usuarios")
public class UsuariosBaseProperties {

    private Base administrador = new Base();
    private Base empleado = new Base();

    public Base getAdministrador() {
        return administrador;
    }

    public void setAdministrador(Base administrador) {
        this.administrador = administrador;
    }

    public Base getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Base empleado) {
        this.empleado = empleado;
    }

    public static class Base {
        private String nombre;
        private String usuario;
        private String password;

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getUsuario() {
            return usuario;
        }

        public void setUsuario(String usuario) {
            this.usuario = usuario;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
