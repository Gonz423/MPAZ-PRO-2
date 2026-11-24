package com.mpaz.progreso.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

/**
 * Resultado de un estudiante al completar un ejercicio.
 * nota: 0-10 (calificacion numerica - mejora sobre booleano)
 */
@Entity @Table(name = "resultados")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Resultado {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false) private Long estudianteId;
    @Column(nullable = false) private Long ejercicioId;

    /** Calificacion numerica 0-10 */
    @Min(0) @Max(10) @Column(nullable = false) @Builder.Default private Integer nota = 0;

    @Builder.Default private Integer aciertos = 0;
    @Builder.Default private Integer totalPreguntas = 1;
    @Builder.Default private Integer intentos = 1;

    /** Timestamp Unix milisegundos */
    private Long fechaRealizado;

    /** Para consultas de progreso por asignatura */
    private String materiaId;
}
