package com.example.appmenu.data.remote.backend

import com.example.appmenu.data.remote.backend.api.ContenidosApi
import com.example.appmenu.data.remote.backend.api.ProgresoApi
import com.example.appmenu.data.remote.backend.api.UsuariosApi
import com.example.appmenu.data.remote.backend.dto.EjercicioDto
import com.example.appmenu.data.remote.backend.dto.LoginRequestDto
import com.example.appmenu.data.remote.backend.dto.LoginResponseDto
import com.example.appmenu.data.remote.backend.dto.ObservacionDto
import com.example.appmenu.data.remote.backend.dto.RegisterRequestDto
import com.example.appmenu.data.remote.backend.dto.ResultadoDto
import com.example.appmenu.data.remote.backend.dto.ResultadoRequestDto
import com.example.appmenu.data.remote.backend.dto.UnidadDto
import com.example.appmenu.data.remote.backend.dto.UsuarioResponseDto
import com.example.appmenu.domain.model.ErrorRed
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response
import java.io.IOException

class BackendRemoteDataSourcesTest {

    private val usuariosApi: UsuariosApi = mockk()
    private val contenidosApi: ContenidosApi = mockk()
    private val progresoApi: ProgresoApi = mockk()

    private val unidad = UnidadDto(id = 1, materiaId = "lenguaje", titulo = "Lectura")
    private val ejercicio = EjercicioDto(id = 2, materiaId = "lenguaje", unidadId = 1, titulo = "Fábula")

    @Test
    fun `usuarios login y registro devuelven la respuesta del servidor`() = runTest {
        val ds = UsuariosRemoteDataSource(usuariosApi)
        coEvery { usuariosApi.login(LoginRequestDto("ana", "1234")) } returns LoginResponseDto(token = "jwt")
        coEvery { usuariosApi.registrar(any()) } returns UsuarioResponseDto(id = 3)

        assertEquals("jwt", ds.login("ana", "1234").getOrThrow().token)
        assertEquals(3L, ds.registrar(RegisterRequestDto("ana", "1234", "Ana")).getOrThrow().id)
    }

    @Test
    fun `usuarios login sin red devuelve SinConexion`() = runTest {
        coEvery { usuariosApi.login(any()) } throws IOException()

        assertTrue(UsuariosRemoteDataSource(usuariosApi).login("a", "b").exceptionOrNull() is ErrorRed.SinConexion)
    }

    @Test
    fun `contenidos lista, crea y actualiza unidades y ejercicios`() = runTest {
        val ds = ContenidosRemoteDataSource(contenidosApi)
        coEvery { contenidosApi.listarUnidades(any()) } returns listOf(unidad)
        coEvery { contenidosApi.crearUnidad(unidad) } returns unidad
        coEvery { contenidosApi.listarEjercicios(any(), any()) } returns listOf(ejercicio)
        coEvery { contenidosApi.crearEjercicio(ejercicio) } returns ejercicio
        coEvery { contenidosApi.actualizarEjercicio(2, ejercicio) } returns ejercicio

        assertEquals(listOf(unidad), ds.listarUnidades().getOrThrow())
        assertEquals(unidad, ds.crearUnidad(unidad).getOrThrow())
        assertEquals(listOf(ejercicio), ds.listarEjercicios().getOrThrow())
        assertEquals(ejercicio, ds.crearEjercicio(ejercicio).getOrThrow())
        assertEquals(ejercicio, ds.actualizarEjercicio(2, ejercicio).getOrThrow())
    }

    @Test
    fun `eliminarEjercicio interpreta el codigo de la respuesta sin cuerpo`() = runTest {
        val ds = ContenidosRemoteDataSource(contenidosApi)
        coEvery { contenidosApi.eliminarEjercicio(1) } returns Response.success(204, Unit)
        coEvery { contenidosApi.eliminarEjercicio(2) } returns Response.error(401, "".toResponseBody())
        coEvery { contenidosApi.eliminarEjercicio(3) } returns Response.error(404, "".toResponseBody())

        assertTrue(ds.eliminarEjercicio(1).isSuccess)
        assertTrue(ds.eliminarEjercicio(2).exceptionOrNull() is ErrorRed.NoAutorizado)
        assertEquals(404, (ds.eliminarEjercicio(3).exceptionOrNull() as ErrorRed.Http).codigo)
    }

    @Test
    fun `progreso registra, lista resultados y crea observaciones`() = runTest {
        val ds = ProgresoRemoteDataSource(progresoApi)
        val request = ResultadoRequestDto(1, 1001, 10, 1, 1, "lenguaje")
        val observacion = ObservacionDto(contenido = "Muy bien")
        coEvery { progresoApi.registrarResultado(request) } returns ResultadoDto(id = 9)
        coEvery { progresoApi.listarResultados(1) } returns listOf(ResultadoDto(id = 9))
        coEvery { progresoApi.crearObservacion(observacion) } returns observacion.copy(id = 5)

        assertEquals(9L, ds.registrarResultado(request).getOrThrow().id)
        assertEquals(1, ds.listarResultados(1).getOrThrow().size)
        assertEquals(5L, ds.crearObservacion(observacion).getOrThrow().id)
    }
}
