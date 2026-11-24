package com.example.appmenu.domain.model

/** Datos del usuario que inicio sesion (guardados en DataStore por SessionManager). */
data class UsuarioSesion(
    val token: String = "",
    val id: Long = 0,
    val username: String = "",
    val nombreCompleto: String = "",
    val email: String = "",
    val rol: String = "ESTUDIANTE",
    val curso: String = "2° Básico A",
    val estaAutenticado: Boolean = false
) {
    val esDocente: Boolean
        get() = rol.equals("DOCENTE", ignoreCase = true) || rol.equals("UTP", ignoreCase = true)

    /** true si la sesion tiene un JWT real emitido por ms-usuarios (no el modo sin conexion). */
    val tieneTokenReal: Boolean
        get() = token.isNotBlank() && token != TOKEN_SIN_CONEXION

    /**
     * Id del estudiante para consultar y registrar progreso. En el acceso rapido (sin login)
     * se usa el estudiante de demostracion, igual que la version anterior de la app.
     */
    val idEstudiante: Long
        get() = if (estaAutenticado && id > 0) id else ID_ESTUDIANTE_DEMO

    companion object {
        const val TOKEN_SIN_CONEXION = "local-offline-token"
        const val ID_ESTUDIANTE_DEMO = 1L
    }
}
