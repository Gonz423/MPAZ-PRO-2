package com.mpaz.contenidos.dto;

/** Ejercicio expuesto por la API. */
public record EjercicioResponse(
        Long id,
        String materiaId,
        Long unidadId,
        String titulo,
        String objetivoAprendizaje,
        String codigoGuia,
        String pregunta,
        String opciones,
        Integer respuestaCorrectaIndex,
        Boolean disponible,
        Boolean esTarea,
        Long fechaEntrega) {}
