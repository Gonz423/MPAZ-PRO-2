package com.example.appmenu.data.repository

import com.example.appmenu.data.local.SessionManager
import com.example.appmenu.data.remote.backend.UsuariosRemoteDataSource
import com.example.appmenu.data.remote.backend.dto.LoginResponseDto
import com.example.appmenu.data.remote.backend.dto.RegisterRequestDto
import com.example.appmenu.data.remote.backend.dto.UsuarioResponseDto
import com.example.appmenu.domain.model.ErrorRed
import com.example.appmenu.domain.model.UsuarioSesion
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthRepositoryImplTest {

    private val remoto: UsuariosRemoteDataSource = mockk()
    private val sessionManager: SessionManager = mockk(relaxed = true)
    private val repo = AuthRepositoryImpl(remoto, sessionManager, generarId = { 321 })

    private val respuestaLogin = LoginResponseDto(
        token = "jwt", id = 4, username = "sofia_m", nombreCompleto = "Sofía Morales",
        email = null, rol = "ESTUDIANTE", curso = null
    )

    @Test
    fun `sesion viene del SessionManager`() = runTest {
        val guardada = UsuarioSesion(token = "t", estaAutenticado = true)
        every { sessionManager.sesionFlow } returns flowOf(guardada)

        assertEquals(guardada, AuthRepositoryImpl(remoto, sessionManager).sesion.first())
    }

    @Test
    fun `login exitoso convierte la respuesta y guarda la sesion`() = runTest {
        coEvery { remoto.login("sofia_m", "clave") } returns Result.success(respuestaLogin)

        val sesion = repo.login("sofia_m", "clave").getOrThrow()

        assertEquals("jwt", sesion.token)
        assertEquals("Sofía Morales", sesion.nombreCompleto)
        assertEquals("", sesion.email)
        assertEquals("2° Básico A", sesion.curso)
        assertTrue(sesion.estaAutenticado)
        coVerify { sessionManager.guardarSesion(sesion) }
    }

    @Test
    fun `login fallido no guarda la sesion`() = runTest {
        coEvery { remoto.login(any(), any()) } returns Result.failure(ErrorRed.NoAutorizado())

        assertTrue(repo.login("x", "y").exceptionOrNull() is ErrorRed.NoAutorizado)
        coVerify(exactly = 0) { sessionManager.guardarSesion(any()) }
    }

    @Test
    fun `loginSinConexion reconoce docentes y estudiantes de prueba`() = runTest {
        val docente = repo.loginSinConexion("Profesor", "1234")!!
        assertEquals("DOCENTE", docente.rol)
        assertEquals("Profesora Marcela", docente.nombreCompleto)
        assertEquals("Profesor@marcelapaz.cl", docente.email)
        assertEquals(UsuarioSesion.TOKEN_SIN_CONEXION, docente.token)

        assertEquals("DOCENTE", repo.loginSinConexion("utp", "1234")!!.rol)

        val alumno = repo.loginSinConexion("ana@colegio.cl", "1234")!!
        assertEquals("ESTUDIANTE", alumno.rol)
        assertEquals("Mateo Silva", alumno.nombreCompleto)
        assertEquals("ana@colegio.cl", alumno.email)
        coVerify(exactly = 3) { sessionManager.guardarSesion(any()) }
    }

    @Test
    fun `loginSinConexion rechaza clave incorrecta o usuario desconocido`() = runTest {
        assertNull(repo.loginSinConexion("estudiante", "0000"))
        assertNull(repo.loginSinConexion("visitante", "1234"))
        coVerify(exactly = 0) { sessionManager.guardarSesion(any()) }
    }

    @Test
    fun `registrar crea la cuenta con username desde el correo y luego inicia sesion`() = runTest {
        val enviado = slot<RegisterRequestDto>()
        coEvery { remoto.registrar(capture(enviado)) } returns Result.success(UsuarioResponseDto(id = 4))
        coEvery { remoto.login("sofia_morales", "clave") } returns Result.success(respuestaLogin)

        val sesion = repo.registrar("Sofía Morales", "sofia.morales@marcelapaz.cl", "clave", "ESTUDIANTE", "2° Básico B")

        assertTrue(sesion.isSuccess)
        assertEquals("sofia_morales", enviado.captured.username)
        assertEquals("2° Básico B", enviado.captured.curso)
    }

    @Test
    fun `registrar devuelve el error del servidor sin intentar login`() = runTest {
        coEvery { remoto.registrar(any()) } returns Result.failure(ErrorRed.Http(409, "Ya existe"))

        val resultado = repo.registrar("A", "a@b.cl", "clave", "ESTUDIANTE", "2° Básico A")

        assertEquals("Ya existe", resultado.exceptionOrNull()?.message)
        coVerify(exactly = 0) { remoto.login(any(), any()) }
    }

    @Test
    fun `registrarSinConexion guarda una sesion local con id generado`() = runTest {
        val sesion = repo.registrarSinConexion("Ana Pérez", "ana.perez@correo.cl", "DOCENTE", "2° Básico A")

        assertEquals(321L, sesion.id)
        assertEquals("ana_perez", sesion.username)
        assertEquals("DOCENTE", sesion.rol)
        assertEquals(UsuarioSesion.TOKEN_SIN_CONEXION, sesion.token)
        coVerify { sessionManager.guardarSesion(sesion) }
    }

    @Test
    fun `cerrarSesion borra la sesion guardada`() = runTest {
        repo.cerrarSesion()
        coVerify { sessionManager.cerrarSesion() }
    }

    @Test
    fun `usernameDesdeEmail toma la parte antes de la arroba y cambia puntos por guion bajo`() {
        assertEquals("maria_perez", AuthRepositoryImpl.usernameDesdeEmail("maria.perez@correo.cl"))
    }
}
