package com.mpaz.contenidos.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Datos de entrada para crear o actualizar un ejercicio.
 * opciones: texto JSON, por ejemplo ["Opcion A","Opcion B","Opcion C"].
 */
public record EjercicioRequest(
        @NotBlank(message = "La materia es obligatoria") String materiaId,
        @NotNull(message = "La unidad es obligatoria") Long unidadId,
        @NotBlank(message = "El titulo es obligatorio") @Size(max = 255) String titulo,
        @Size(max = 255) String objetivoAprendizaje,
        @Size(max = 255) String codigoGuia,
        @Size(max = 1000) String pregunta,
        @Size(max = 1000) String opciones,
        @Min(value = 0, message = "El indice de respuesta no puede ser negativo") Integer respuestaCorrectaIndex,
        Boolean disponible,
        Boolean esTarea,
        Long fechaEntrega) {}
