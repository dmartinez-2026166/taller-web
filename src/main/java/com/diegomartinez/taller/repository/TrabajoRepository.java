package com.diegomartinez.taller.repository;

import com.diegomartinez.taller.model.Trabajo;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class TrabajoRepository {

    private final JdbcTemplate jdbc;

    private final RowMapper<Trabajo> mapper = (ResultSet rs, int rowNum) -> mapRow(rs);

    public TrabajoRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Trabajo> findAll() {
        return jdbc.query("SELECT * FROM view_read_trabajo ORDER BY ID DESC", mapper);
    }

    public Optional<Trabajo> findById(Integer id) {
        return jdbc.query("SELECT * FROM view_read_trabajo WHERE ID = ?", mapper, id)
                .stream().findFirst();
    }

    public void create(Trabajo t) {
        jdbc.update("CALL sp_create_trabajo_completo(?,?,?,?,?,?,?,?,?,?,?,?)",
                t.getNombreCliente(), t.getTelefono(), t.getDireccion(),
                t.getPlaca(), t.getMarca(), t.getModelo(), t.getAnio(), t.getColor(),
                t.getDescripcion(), Date.valueOf(t.getFechaIngreso()), t.getEstado(), t.getCostoEstimado());
    }

    public void update(Trabajo t) {
        jdbc.update("CALL sp_update_trabajo_completo(?,?,?,?,?,?,?,?,?,?,?,?,?)",
                t.getIdTrabajo(),
                t.getNombreCliente(), t.getTelefono(), t.getDireccion(),
                t.getPlaca(), t.getMarca(), t.getModelo(), t.getAnio(), t.getColor(),
                t.getDescripcion(), Date.valueOf(t.getFechaIngreso()), t.getEstado(), t.getCostoEstimado());
    }

    public void delete(Integer id) {
        jdbc.update("CALL sp_delete_trabajo(?)", id);
    }

    private Trabajo mapRow(ResultSet rs) throws SQLException {
        Trabajo t = new Trabajo();
        t.setIdTrabajo(rs.getInt("ID"));
        t.setDescripcion(rs.getString("Trabajo"));
        Date ingreso = rs.getDate("Ingreso");
        t.setFechaIngreso(ingreso == null ? null : ingreso.toLocalDate());
        t.setEstado(rs.getString("Estado"));
        t.setCostoEstimado(rs.getBigDecimal("Costo"));
        t.setIdCarro(rs.getInt("id_carro"));
        t.setPlaca(rs.getString("Placa"));
        t.setMarca(rs.getString("Marca"));
        t.setModelo(rs.getString("Modelo"));
        t.setAnio(rs.getInt("Anio"));
        t.setColor(rs.getString("Color"));
        t.setIdCliente(rs.getInt("id_cliente"));
        t.setNombreCliente(rs.getString("Cliente"));
        t.setTelefono(rs.getString("Telefono"));
        t.setDireccion(rs.getString("Direccion"));
        return t;
    }
}
