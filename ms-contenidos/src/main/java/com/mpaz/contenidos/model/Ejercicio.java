package com.mpaz.contenidos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/** Ejercicio educativo - refleja EjercicioEducativo del cliente Android */
@Entity @Table(name = "ejercicios")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Ejercicio {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @NotBlank @Column(nullable = false) private String materiaId;
    @Column(nullable = false) private Long unidadId;
    @NotBlank @Column(nullable = false) private String titulo;
    private String objetivoAprendizaje;
    private String codigoGuia;
    @Column(length = 1000) private String pregunta;
    /** JSON: ["Opcion A","Opcion B","Opcion C"] */
    @Column(length = 1000) private String opciones;
    @Builder.Default private Integer respuestaCorrectaIndex = 0;
    @Builder.Default private Boolean disponible = true;
    @Builder.Default private Boolean esTarea = false;
    private Long fechaEntrega;
}
