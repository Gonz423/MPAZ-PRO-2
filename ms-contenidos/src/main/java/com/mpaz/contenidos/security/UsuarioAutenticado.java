package com.mpaz.contenidos.security;

/** Datos del usuario extraidos del JWT emitido por ms-usuarios. */
public record UsuarioAutenticado(Long id, String username, String rol) {

    public boolean esDocente() {
        return "DOCENTE".equalsIgnoreCase(rol) || "UTP".equalsIgnoreCase(rol);
    }
}
