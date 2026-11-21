package com.mpaz.progreso.model;

import jakarta.persistence.*;
import lombok.*;

/** Observacion del docente - equivale al cuaderno de notas del panel docente */
@Entity @Table(name = "observaciones")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Observacion {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private Long docenteId;
    private Long estudianteId;
    @Column(nullable = false, length = 2000) private String contenido;
    private Long fecha;
}
