package com.mpaz.progreso.dto;

/** Observacion expuesta por la API. fecha: timestamp Unix en milisegundos. */
public record ObservacionResponse(
        Long id,
        Long docenteId,
        Long estudianteId,
        String contenido,
        Long fecha) {}
