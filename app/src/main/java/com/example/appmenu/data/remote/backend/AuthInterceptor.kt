package com.example.appmenu.data.remote.backend

import com.example.appmenu.domain.model.UsuarioSesion
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Agrega "Authorization: Bearer <jwt>" a las peticiones hacia NUESTROS microservicios.
 * Solo se instala en el cliente del backend: el token nunca viaja a la API externa.
 *
 * Si el servidor responde 401 a una peticion que llevaba token, la sesion vencio o es invalida:
 * se avisa con [onNoAutorizado] para cerrar la sesion y volver al login.
 */
class AuthInterceptor(
    private val tokenActual: () -> String?,
    private val onNoAutorizado: () -> Unit
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val token = tokenActual()?.takeIf { it.isNotBlank() && it != UsuarioSesion.TOKEN_SIN_CONEXION }
        val esLogin = original.url.encodedPath.startsWith("/api/auth/")

        val peticion = if (token != null && !esLogin) {
            original.newBuilder().header("Authorization", "Bearer $token").build()
        } else {
            original
        }

        val respuesta = chain.proceed(peticion)
        if (respuesta.code == 401 && token != null && !esLogin) {
            onNoAutorizado()
        }
        return respuesta
    }
}
