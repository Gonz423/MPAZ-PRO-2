package com.mpaz.progreso.dto;

/** Resumen de progreso por asignatura - usado en panel docente y pantalla de progreso. */
public record ResumenProgresoDto(
        Long estudianteId,
        String materiaId,
        Double promedioNota,
        Integer ejerciciosRealizados,
        Integer ejerciciosTotales,
        Integer porcentajeCompletado) {}
