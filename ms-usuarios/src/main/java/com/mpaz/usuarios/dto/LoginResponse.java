package com.mpaz.usuarios.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {

    private String token;
    private Long id;
    private String username;
    private String nombreCompleto;
    private String email;
    private String rol;
    private String curso;
    @Builder.Default
    private String tokenType = "Bearer";
}
