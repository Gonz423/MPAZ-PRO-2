package com.mpaz.contenidos.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Datos de entrada para crear o actualizar una unidad didactica. */
public record UnidadRequest(
        @NotBlank(message = "La materia es obligatoria") String materiaId,
        @Min(value = 1, message = "El numero de unidad debe ser mayor a 0") Integer numero,
        @NotBlank(message = "El titulo es obligatorio") @Size(max = 255) String titulo,
        @Size(max = 500) String objetivoAprendizaje) {}
