package com.example.appmenu.domain.model

/** Errores de red ya clasificados; los Repositories los devuelven dentro de Result.failure. */
sealed class ErrorRed(mensaje: String) : Exception(mensaje) {

    /** No se pudo conectar (servidor apagado, sin wifi, timeout). */
    class SinConexion : ErrorRed("No se pudo conectar con el servidor")

    /** 401: credenciales invalidas o token vencido. */
    class NoAutorizado(val detalle: String? = null) : ErrorRed(detalle ?: "No autorizado")

    /** Cualquier otra respuesta de error del servidor (400, 403, 404, 409, 500...). */
    class Http(val codigo: Int, val detalle: String?) : ErrorRed(detalle ?: "Error del servidor ($codigo)")

    /** La respuesta no se pudo interpretar. */
    class Desconocido(causa: Throwable) : ErrorRed(causa.message ?: "Error inesperado")
}

/** Mensaje en español apto para mostrar al usuario. */
fun Throwable.mensajeUsuario(): String = when (this) {
    is ErrorRed.SinConexion -> "Sin conexión con el servidor. Revisa tu red e inténtalo de nuevo."
    is ErrorRed.NoAutorizado -> detalle ?: "Tu sesión no es válida. Inicia sesión nuevamente."
    is ErrorRed.Http -> detalle ?: "El servidor respondió con un error ($codigo)."
    else -> "Ocurrió un error inesperado."
}
