package com.mpaz.usuarios.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegistroRequest {
    @NotBlank private String nombre;
    @NotBlank @Email private String correo;
    @NotBlank private String password;
    @NotBlank private String rol;
    private String curso;
}
