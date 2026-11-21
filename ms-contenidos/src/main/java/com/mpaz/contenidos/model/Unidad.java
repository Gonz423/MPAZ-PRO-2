package com.mpaz.contenidos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/** Unidad didactica - refleja UnidadEducativa del cliente Android */
@Entity @Table(name = "unidades")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Unidad {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @NotBlank @Column(nullable = false) private String materiaId;
    private Integer numero;
    @NotBlank @Column(nullable = false) private String titulo;
    @Column(length = 500) private String objetivoAprendizaje;
}
