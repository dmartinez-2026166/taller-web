package com.diegomartinez.taller.model;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * Un trabajo del taller junto con el carro y el cliente asociados.
 * Sirve para leer (view_read_trabajo) y como objeto del formulario.
 */
public class Trabajo {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ---- Trabajo ----
    private Integer idTrabajo;

    @NotBlank(message = "Describa el trabajo a realizar")
    @Size(max = 255, message = "Máximo 255 caracteres")
    private String descripcion;

    @NotNull(message = "Indique la fecha de ingreso")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaIngreso;

    @NotBlank(message = "Seleccione un estado")
    @Pattern(regexp = "Pendiente|En proceso|Finalizado|Entregado", message = "Estado no válido")
    private String estado;

    @NotNull(message = "Indique el costo estimado")
    @DecimalMin(value = "0.00", message = "El costo no puede ser negativo")
    @DecimalMax(value = "99999999.99", message = "Costo demasiado grande")
    @Digits(integer = 8, fraction = 2, message = "Máximo 2 decimales")
    private BigDecimal costoEstimado;

    // ---- Carro ----
    private Integer idCarro;

    @NotBlank(message = "Ingrese la placa")
    @Size(max = 15, message = "Máximo 15 caracteres")
    private String placa;

    @NotBlank(message = "Ingrese la marca")
    @Size(max = 50, message = "Máximo 50 caracteres")
    private String marca;

    @NotBlank(message = "Ingrese el modelo")
    @Size(max = 50, message = "Máximo 50 caracteres")
    private String modelo;

    @NotNull(message = "Ingrese el año")
    @Min(value = 1900, message = "El año debe ser 1900 o mayor")
    @Max(value = 2100, message = "El año debe ser 2100 o menor")
    private Integer anio;

    @Size(max = 30, message = "Máximo 30 caracteres")
    private String color;

    // ---- Cliente ----
    private Integer idCliente;

    @NotBlank(message = "Ingrese el nombre del cliente")
    @Size(max = 100, message = "Máximo 100 caracteres")
    private String nombreCliente;

    @NotBlank(message = "Ingrese el teléfono")
    @Size(max = 20, message = "Máximo 20 caracteres")
    private String telefono;

    @Size(max = 150, message = "Máximo 150 caracteres")
    private String direccion;

    public Integer getIdTrabajo() { return idTrabajo; }
    public void setIdTrabajo(Integer idTrabajo) { this.idTrabajo = idTrabajo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public LocalDate getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(LocalDate fechaIngreso) { this.fechaIngreso = fechaIngreso; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public BigDecimal getCostoEstimado() { return costoEstimado; }
    public void setCostoEstimado(BigDecimal costoEstimado) { this.costoEstimado = costoEstimado; }

    public Integer getIdCarro() { return idCarro; }
    public void setIdCarro(Integer idCarro) { this.idCarro = idCarro; }

    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public Integer getAnio() { return anio; }
    public void setAnio(Integer anio) { this.anio = anio; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public Integer getIdCliente() { return idCliente; }
    public void setIdCliente(Integer idCliente) { this.idCliente = idCliente; }

    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    /** Texto para la columna "Vehículo" de la tabla. */
    public String getVehiculo() {
        return marca + " " + modelo + " (" + anio + ")";
    }

    /** Texto para la columna "Costo" de la tabla. */
    public String getCosto() {
        return costoEstimado == null ? "" : String.format(Locale.US, "Q %,.2f", costoEstimado);
    }

    /** Texto para la columna "Ingreso" de la tabla. */
    public String getIngreso() {
        return fechaIngreso == null ? "" : fechaIngreso.format(DATE_FORMAT);
    }
}
