package com.diegomartinez.taller.config;

import com.diegomartinez.taller.model.UsuarioRegistro;
import com.diegomartinez.taller.repository.UsuarioRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Al iniciar crea el administrador y el empleado definidos en application.properties.
 * - Si el usuario no existe, lo crea con la contraseña cifrada con BCrypt.
 * - Si existe pero su contraseña es del sistema de escritorio (MD5), la migra a BCrypt.
 * - Si la contraseña configurada empieza con "CAMBIAR" no hace nada (aún no la ha definido).
 */
@Component
public class UsuariosBaseInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(UsuariosBaseInitializer.class);

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuariosBaseProperties properties;

    public UsuariosBaseInitializer(UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder, UsuariosBaseProperties properties) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.properties = properties;
    }

    @Override
    public void run(ApplicationArguments args) {
        asegurar(properties.getAdministrador(), "ADMINISTRADOR");
        asegurar(properties.getEmpleado(), "EMPLEADO");
    }

    private void asegurar(UsuariosBaseProperties.Base base, String rol) {
        if (isBlank(base.getUsuario()) || isBlank(base.getPassword())
                || base.getPassword().startsWith("CAMBIAR")) {
            log.warn("Usuario base {} no configurado: defina usuario y contraseña en application.properties.", rol);
            return;
        }
        Optional<UsuarioRegistro> existente = usuarioRepository.findByUsuario(base.getUsuario());
        if (existente.isEmpty()) {
            String nombre = isBlank(base.getNombre()) ? base.getUsuario() : base.getNombre();
            usuarioRepository.insert(nombre, base.getUsuario(), passwordEncoder.encode(base.getPassword()), rol);
            log.info("Usuario base {} creado: {}", rol, base.getUsuario());
        } else if (!existente.get().password().startsWith("$2")) {
            usuarioRepository.updatePassword(existente.get().idUsuario(), passwordEncoder.encode(base.getPassword()));
            log.info("Usuario {} migrado a BCrypt", base.getUsuario());
        }
    }

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}
