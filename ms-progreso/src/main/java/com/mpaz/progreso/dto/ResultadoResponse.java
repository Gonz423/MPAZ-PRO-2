package com.mpaz.progreso.dto;

/** Resultado expuesto por la API. fechaRealizado: timestamp Unix en milisegundos. */
public record ResultadoResponse(
        Long id,
        Long estudianteId,
        Long ejercicioId,
        Integer nota,
        Integer aciertos,
        Integer totalPreguntas,
        Integer intentos,
        String materiaId,
        Long fechaRealizado) {}
