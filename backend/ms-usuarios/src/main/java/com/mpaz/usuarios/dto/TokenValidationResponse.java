package com.mpaz.usuarios.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenValidationResponse {

    private boolean valid;
    private String username;
    private String rol;
    private Long id;
    private String message;
}
