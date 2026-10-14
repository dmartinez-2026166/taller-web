package com.diegomartinez.taller.controller;

import com.diegomartinez.taller.model.Trabajo;
import com.diegomartinez.taller.service.PlacaDuplicadaException;
import com.diegomartinez.taller.service.TrabajoNoEncontradoException;
import com.diegomartinez.taller.service.TrabajoService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/trabajos")
public class TrabajoController {

    private static final Logger log = LoggerFactory.getLogger(TrabajoController.class);

    private final TrabajoService service;

    public TrabajoController(TrabajoService service) {
        this.service = service;
    }

    /** Lista los trabajos. Con ?editar=ID carga ese trabajo en el formulario. */
    @GetMapping
    public String listar(@RequestParam(required = false) Integer editar, Model model) {
        Trabajo form = null;
        if (editar != null) {
            form = service.buscar(editar).orElse(null);
            if (form == null) {
                model.addAttribute("error", "El trabajo ya no existe en la base de datos.");
            }
        }
        if (form == null) {
            form = nuevoTrabajo();
        }
        model.addAttribute("form", form);
        cargarListado(model);
        return "trabajos";
    }

    /** Crea (sin id) o actualiza (con id) un trabajo junto con su cliente y vehículo. */
    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("form") Trabajo form, BindingResult result,
            Model model, RedirectAttributes redirect) {
        if (!result.hasErrors()) {
            try {
                if (form.getIdTrabajo() == null) {
                    service.crear(form);
                    redirect.addFlashAttribute("ok", "Trabajo registrado: cliente, vehículo y trabajo guardados.");
                } else {
                    service.actualizar(form);
                    redirect.addFlashAttribute("ok", "Cambios guardados.");
                }
                return "redirect:/trabajos";
            } catch (PlacaDuplicadaException e) {
                result.rejectValue("placa", "placa.duplicada", e.getMessage());
            } catch (TrabajoNoEncontradoException e) {
                redirect.addFlashAttribute("error", e.getMessage());
                return "redirect:/trabajos";
            } catch (DataAccessException e) {
                log.error("Error al guardar el trabajo", e);
                result.reject("error.bd", "Ocurrió un error al guardar en la base de datos.");
            }
        }
        cargarListado(model);
        return "trabajos";
    }

    /** Solo el administrador puede eliminar (también se valida en SecurityConfig). */
    @PostMapping("/{id}/eliminar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public String eliminar(@PathVariable Integer id, RedirectAttributes redirect) {
        try {
            service.eliminar(id);
            redirect.addFlashAttribute("ok", "Trabajo eliminado.");
        } catch (TrabajoNoEncontradoException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        } catch (DataAccessException e) {
            log.error("Error al eliminar el trabajo", e);
            redirect.addFlashAttribute("error", "Ocurrió un error al eliminar el trabajo.");
        }
        return "redirect:/trabajos";
    }

    private Trabajo nuevoTrabajo() {
        Trabajo t = new Trabajo();
        t.setFechaIngreso(LocalDate.now());
        t.setEstado(TrabajoService.ESTADOS.get(0));
        return t;
    }

    private void cargarListado(Model model) {
        model.addAttribute("trabajos", service.listar());
        model.addAttribute("estados", TrabajoService.ESTADOS);
    }
}
