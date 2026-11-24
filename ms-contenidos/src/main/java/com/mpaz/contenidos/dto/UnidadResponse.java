package com.mpaz.contenidos.dto;

/** Unidad didactica expuesta por la API. */
public record UnidadResponse(
        Long id,
        String materiaId,
        Integer numero,
        String titulo,
        String objetivoAprendizaje) {}
