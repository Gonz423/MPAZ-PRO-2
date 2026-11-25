package com.example.appmenu.data.remote

import com.example.appmenu.data.remote.backend.AuthInterceptor
import com.example.appmenu.data.remote.external.UserAgentInterceptor
import com.example.appmenu.domain.model.UsuarioSesion
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class InterceptoresTest {

    /** Chain falsa: guarda la peticion que sigue y responde con [codigo]. */
    private fun cadena(url: String, codigo: Int, enviada: io.mockk.CapturingSlot<Request>): Interceptor.Chain {
        val chain: Interceptor.Chain = mockk()
        every { chain.request() } returns Request.Builder().url(url).build()
        every { chain.proceed(capture(enviada)) } answers {
            Response.Builder().request(enviada.captured).protocol(Protocol.HTTP_1_1).code(codigo).message("x").build()
        }
        return chain
    }

    private fun authInterceptor(token: String?, avisos: MutableList<Unit>) =
        AuthInterceptor(tokenActual = { token }, onNoAutorizado = { avisos += Unit })

    @Test
    fun `agrega el JWT como Bearer a las peticiones del backend`() {
        val enviada = slot<Request>()
        val avisos = mutableListOf<Unit>()

        authInterceptor("jwt123", avisos).intercept(cadena("http://10.0.2.2:8082/api/ejercicios", 200, enviada))

        assertEquals("Bearer jwt123", enviada.captured.header("Authorization"))
        assertEquals(0, avisos.size)
    }

    @Test
    fun `no envia token en login, sin sesion ni en modo sin conexion`() {
        val avisos = mutableListOf<Unit>()
        for ((token, url) in listOf(
            "jwt" to "http://10.0.2.2:8081/api/auth/login",
            null to "http://10.0.2.2:8082/api/ejercicios",
            "" to "http://10.0.2.2:8082/api/ejercicios",
            UsuarioSesion.TOKEN_SIN_CONEXION to "http://10.0.2.2:8082/api/ejercicios"
        )) {
            val enviada = slot<Request>()
            authInterceptor(token, avisos).intercept(cadena(url, 401, enviada))
            assertNull(enviada.captured.header("Authorization"))
        }
        // un 401 sin token (o en el login) no significa sesion vencida
        assertEquals(0, avisos.size)
    }

    @Test
    fun `un 401 con token avisa que la sesion vencio`() {
        val avisos = mutableListOf<Unit>()

        val respuesta = authInterceptor("jwt", avisos).intercept(cadena("http://10.0.2.2:8083/api/resultados", 401, slot()))

        assertEquals(401, respuesta.code)
        assertEquals(1, avisos.size)
    }

    @Test
    fun `UserAgentInterceptor identifica la app ante Wikipedia`() {
        val enviada = slot<Request>()

        UserAgentInterceptor().intercept(cadena("https://es.wikipedia.org/api/rest_v1/page/summary/x", 200, enviada))

        assertEquals(UserAgentInterceptor.USER_AGENT, enviada.captured.header("User-Agent"))
    }
}
