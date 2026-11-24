package com.mpaz.usuarios.dto;

import com.mpaz.usuarios.model.Rol;
import com.mpaz.usuarios.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioResponse {

    private Long id;
    private String username;
    private String nombreCompleto;
    private String email;
    private Rol rol;
    private String curso;
    private Boolean activo;
    private LocalDateTime fechaCreacion;

    public static UsuarioResponse fromEntity(Usuario u) {
        if (u == null) return null;
        return UsuarioResponse.builder()
                .id(u.getId())
                .username(u.getUsername())
                .nombreCompleto(u.getNombreCompleto())
                .email(u.getEmail())
                .rol(u.getRol())
                .curso(u.getCurso())
                .activo(u.getActivo())
                .fechaCreacion(u.getFechaCreacion())
                .build();
    }
}
