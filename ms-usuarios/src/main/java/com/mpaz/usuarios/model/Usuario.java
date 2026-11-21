package com.mpaz.usuarios.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "usuarios")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Usuario {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Column(nullable = false)
    private String nombre;

    @NotBlank @Email(message = "Formato de correo invalido")
    @Column(nullable = false, unique = true)
    private String correo;

    @NotBlank
    @Column(nullable = false)
    private String password;

    /** ESTUDIANTE o DOCENTE */
    @NotBlank
    @Column(nullable = false)
    private String rol;

    /** Curso asignado: ej. "2A", "2B" */
    private String curso;

    private Boolean activo = true;
}
