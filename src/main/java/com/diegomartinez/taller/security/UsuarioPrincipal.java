package com.diegomartinez.taller.security;

import com.diegomartinez.taller.model.UsuarioRegistro;
import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

/** Usuario autenticado; además del login guarda el nombre y el rol para mostrarlos en pantalla. */
public class UsuarioPrincipal extends User {

    private final String nombre;
    private final String rol;

    public UsuarioPrincipal(UsuarioRegistro u) {
        super(u.usuario(), u.password(), u.activo(), true, true, true,
                List.of(new SimpleGrantedAuthority("ROLE_" + u.rol())));
        this.nombre = u.nombre();
        this.rol = u.rol();
    }

    public String getNombre() {
        return nombre;
    }

    public String getRol() {
        return rol;
    }
}
