package com.diegomartinez.taller.repository;

import com.diegomartinez.taller.model.UsuarioRegistro;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UsuarioRepository {

    private final JdbcTemplate jdbc;

    public UsuarioRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Optional<UsuarioRegistro> findByUsuario(String usuario) {
        List<UsuarioRegistro> list = jdbc.query(
                "SELECT id_usuario, nombre, usuario, password, rol, user_status FROM Usuario WHERE usuario = ?",
                (rs, i) -> new UsuarioRegistro(
                        rs.getString("id_usuario"),
                        rs.getString("nombre"),
                        rs.getString("usuario"),
                        rs.getString("password"),
                        rs.getString("rol"),
                        rs.getBoolean("user_status")),
                usuario);
        return list.stream().findFirst();
    }

    public void insert(String nombre, String usuario, String passwordHash, String rol) {
        jdbc.update(
                "INSERT INTO Usuario(nombre, usuario, password, rol, user_status, id_usuario) VALUES (?,?,?,?,true,?)",
                nombre, usuario, passwordHash, rol, UUID.randomUUID().toString());
    }

    public void updatePassword(String idUsuario, String passwordHash) {
        jdbc.update("UPDATE Usuario SET password = ? WHERE id_usuario = ?", passwordHash, idUsuario);
    }
}
