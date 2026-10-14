package com.diegomartinez.taller.service;

import com.diegomartinez.taller.model.Trabajo;
import com.diegomartinez.taller.repository.TrabajoRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

@Service
public class TrabajoService {

    public static final List<String> ESTADOS = List.of("Pendiente", "En proceso", "Finalizado", "Entregado");

    private final TrabajoRepository repo;

    public TrabajoService(TrabajoRepository repo) {
        this.repo = repo;
    }

    public List<Trabajo> listar() {
        return repo.findAll();
    }

    public Optional<Trabajo> buscar(Integer id) {
        return repo.findById(id);
    }

    /**
     * Registra cliente + carro + trabajo. Si la placa ya existe, se reutiliza
     * ese carro (y su cliente) y solo se agrega el trabajo nuevo.
     */
    public void crear(Trabajo t) {
        normalizar(t);
        repo.create(t);
    }

    public void actualizar(Trabajo t) {
        if (t.getIdTrabajo() == null || repo.findById(t.getIdTrabajo()).isEmpty()) {
            throw new TrabajoNoEncontradoException();
        }
        normalizar(t);
        try {
            repo.update(t);
        } catch (DuplicateKeyException e) {
            throw new PlacaDuplicadaException();
        }
    }

    public void eliminar(Integer id) {
        if (repo.findById(id).isEmpty()) {
            throw new TrabajoNoEncontradoException();
        }
        repo.delete(id);
    }

    /** Limpia los textos: trim, placa en mayúsculas y opcionales vacíos como null. */
    private void normalizar(Trabajo t) {
        t.setNombreCliente(t.getNombreCliente().trim());
        t.setTelefono(t.getTelefono().trim());
        t.setDireccion(blank(t.getDireccion()) ? null : t.getDireccion().trim());
        t.setPlaca(t.getPlaca().trim().toUpperCase());
        t.setMarca(t.getMarca().trim());
        t.setModelo(t.getModelo().trim());
        t.setColor(blank(t.getColor()) ? null : t.getColor().trim());
        t.setDescripcion(t.getDescripcion().trim());
        t.setEstado(t.getEstado().trim());
    }

    private boolean blank(String s) {
        return s == null || s.isBlank();
    }
}
