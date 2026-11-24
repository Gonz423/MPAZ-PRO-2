package com.mpaz.progreso.security;

import com.mpaz.progreso.exception.AccesoDenegadoException;

/** Reglas de propiedad de los datos: un estudiante solo accede a su propio progreso. */
public final class Permisos {

    private Permisos() {}

    /** Docente y UTP pueden ver a cualquier estudiante; un estudiante solo a si mismo. */
    public static void verificarAccesoEstudiante(UsuarioAutenticado usuario, Long estudianteId) {
        if (usuario == null) {
            throw new AccesoDenegadoException("Debes iniciar sesión");
        }
        if (usuario.esDocente()) {
            return;
        }
        if (usuario.id() == null || !usuario.id().equals(estudianteId)) {
            throw new AccesoDenegadoException("No puedes acceder a los datos de otro estudiante");
        }
    }

    /**
     * Filtro efectivo por estudiante para los listados: un docente puede pedir cualquiera (o todos con null);
     * a un estudiante se le aplica siempre su propio id.
     */
    public static Long estudianteVisible(UsuarioAutenticado usuario, Long estudianteSolicitado) {
        if (usuario != null && usuario.esDocente()) {
            return estudianteSolicitado;
        }
        Long propio = usuario == null ? null : usuario.id();
        if (estudianteSolicitado != null) {
            verificarAccesoEstudiante(usuario, estudianteSolicitado);
        }
        if (propio == null) {
            throw new AccesoDenegadoException("Debes iniciar sesión");
        }
        return propio;
    }
}
