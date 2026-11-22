package com.mpaz.progreso.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/** Resumen de progreso por asignatura - usado en panel docente y pantalla de progreso */
@Data @AllArgsConstructor
public class ResumenProgresoDto {
    private Long estudianteId;
    private String materiaId;
    private Double promedioNota;
    private Integer ejerciciosRealizados;
    private Integer ejerciciosTotales;
    private Integer porcentajeCompletado;
}
