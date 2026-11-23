package com.mpaz.usuarios.dto;

import com.mpaz.usuarios.model.Rol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioUpdateRequest {

    private String nombreCompleto;
    private String email;
    private Rol rol;
    private String curso;
    private Boolean activo;
    private String password;
}
