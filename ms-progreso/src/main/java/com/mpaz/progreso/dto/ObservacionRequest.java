package com.mpaz.progreso.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Datos de entrada para crear o actualizar una observacion del docente. */
public record ObservacionRequest(
        Long docenteId,
        Long estudianteId,
        @NotBlank(message = "El contenido es obligatorio")
        @Size(max = 2000, message = "El contenido no puede superar 2000 caracteres") String contenido) {}
