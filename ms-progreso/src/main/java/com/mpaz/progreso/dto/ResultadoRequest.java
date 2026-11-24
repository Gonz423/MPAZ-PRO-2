package com.mpaz.progreso.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/** Datos de entrada para registrar o actualizar el resultado de un ejercicio. */
public record ResultadoRequest(
        @NotNull(message = "El estudiante es obligatorio") Long estudianteId,
        @NotNull(message = "El ejercicio es obligatorio") Long ejercicioId,
        @NotNull(message = "La nota es obligatoria")
        @Min(value = 0, message = "La nota minima es 0")
        @Max(value = 10, message = "La nota maxima es 10") Integer nota,
        @Min(value = 0, message = "Los aciertos no pueden ser negativos") Integer aciertos,
        @Min(value = 1, message = "Debe haber al menos una pregunta") Integer totalPreguntas,
        String materiaId) {}
