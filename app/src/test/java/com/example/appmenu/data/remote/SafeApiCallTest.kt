package com.example.appmenu.data.remote

import com.example.appmenu.domain.model.ErrorRed
import com.example.appmenu.domain.model.mensajeUsuario
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

class SafeApiCallTest {

    private fun httpError(codigo: Int, cuerpo: String) =
        HttpException(Response.error<Any>(codigo, cuerpo.toResponseBody("application/json".toMediaType())))

    @Test
    fun `devuelve success con el valor de la llamada`() = runTest {
        assertEquals("ok", safeApiCall { "ok" }.getOrThrow())
    }

    @Test
    fun `IOException se convierte en SinConexion`() = runTest {
        val r = safeApiCall<String> { throw IOException("timeout") }
        assertTrue(r.exceptionOrNull() is ErrorRed.SinConexion)
    }

    @Test
    fun `401 se convierte en NoAutorizado con el detalle del ProblemDetail`() = runTest {
        val r = safeApiCall<String> { throw httpError(401, """{"detail":"Credenciales invalidas"}""") }

        val error = r.exceptionOrNull() as ErrorRed.NoAutorizado
        assertEquals("Credenciales invalidas", error.detalle)
    }

    @Test
    fun `otros codigos HTTP se convierten en Http, con o sin detalle`() = runTest {
        val conDetalle = safeApiCall<String> { throw httpError(409, """{"detail":"Ya existe"}""") }.exceptionOrNull() as ErrorRed.Http
        assertEquals(409, conDetalle.codigo)
        assertEquals("Ya existe", conDetalle.detalle)

        val detalleNulo = safeApiCall<String> { throw httpError(500, """{"detail":null}""") }.exceptionOrNull() as ErrorRed.Http
        assertNull(detalleNulo.detalle)

        val vacio = safeApiCall<String> { throw httpError(404, "") }.exceptionOrNull() as ErrorRed.Http
        assertNull(vacio.detalle)

        val noJson = safeApiCall<String> { throw httpError(502, "<html>") }.exceptionOrNull() as ErrorRed.Http
        assertNull(noJson.detalle)
    }

    @Test
    fun `un ErrorRed lanzado se devuelve tal cual`() = runTest {
        val original = ErrorRed.Http(403, "Prohibido")
        assertEquals(original, safeApiCall<String> { throw original }.exceptionOrNull())
    }

    @Test
    fun `cualquier otra excepcion se convierte en Desconocido`() = runTest {
        val r = safeApiCall<String> { throw IllegalStateException("json roto") }
        assertTrue(r.exceptionOrNull() is ErrorRed.Desconocido)
        assertEquals("json roto", r.exceptionOrNull()?.message)
    }

    @Test(expected = CancellationException::class)
    fun `CancellationException se relanza para no romper la cancelacion`() = runTest {
        safeApiCall<String> { throw CancellationException("cancelada") }
    }

    @Test
    fun `mensajeUsuario da un texto en español para cada error`() {
        assertTrue(ErrorRed.SinConexion().mensajeUsuario().startsWith("Sin conexión"))
        assertEquals("Clave mala", ErrorRed.NoAutorizado("Clave mala").mensajeUsuario())
        assertTrue(ErrorRed.NoAutorizado().mensajeUsuario().startsWith("Tu sesión"))
        assertEquals("Ya existe", ErrorRed.Http(409, "Ya existe").mensajeUsuario())
        assertTrue(ErrorRed.Http(500, null).mensajeUsuario().contains("500"))
        assertEquals("Ocurrió un error inesperado.", RuntimeException().mensajeUsuario())
        assertEquals("Error inesperado", ErrorRed.Desconocido(RuntimeException()).message)
    }
}
