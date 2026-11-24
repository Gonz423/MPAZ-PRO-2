package com.example.appmenu.viewmodel

import com.example.appmenu.data.repository.AuthRepository
import com.example.appmenu.domain.model.ErrorRed
import com.example.appmenu.domain.model.UsuarioSesion
import com.example.appmenu.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repo: AuthRepository = mockk(relaxed = true)

    private val sesionEstudiante = UsuarioSesion(token = "jwt", id = 1, rol = "ESTUDIANTE", estaAutenticado = true)

    @Test
    fun `iniciarSesion con campos vacios marca errores y no llama al repositorio`() = runTest {
        val viewModel = AuthViewModel(repo)

        var exitoLlamado = false
        viewModel.iniciarSesion { exitoLlamado = true }

        assertFalse(exitoLlamado)
        assertNotNull(viewModel.uiState.value.loginErrorIdentificador)
        assertNotNull(viewModel.uiState.value.loginErrorPassword)
        coVerify(exactly = 0) { repo.login(any(), any()) }
    }

    @Test
    fun `iniciarSesion exitoso con ms-usuarios llama callback con el rol`() = runTest {
        coEvery { repo.login("estudiante", "1234") } returns Result.success(sesionEstudiante)
        val viewModel = AuthViewModel(repo)
        viewModel.onLoginIdentificadorChanged(" estudiante ")
        viewModel.onLoginPasswordChanged("1234")

        var rolRetornado: String? = null
        viewModel.iniciarSesion { rolRetornado = it }

        assertEquals("ESTUDIANTE", rolRetornado)
        assertFalse(viewModel.uiState.value.loginCargando)
        assertNull(viewModel.uiState.value.loginMensajeError)
    }

    @Test
    fun `iniciarSesion sin conexion usa el modo local con las cuentas de prueba`() = runTest {
        coEvery { repo.login(any(), any()) } returns Result.failure(ErrorRed.SinConexion())
        coEvery { repo.loginSinConexion("docente@marcelapaz.cl", "1234") } returns sesionEstudiante.copy(rol = "DOCENTE")
        val viewModel = AuthViewModel(repo)
        viewModel.onLoginIdentificadorChanged("docente@marcelapaz.cl")
        viewModel.onLoginPasswordChanged("1234")

        var rolRetornado: String? = null
        viewModel.iniciarSesion { rolRetornado = it }

        assertEquals("DOCENTE", rolRetornado)
    }

    @Test
    fun `iniciarSesion con 401 muestra credenciales incorrectas y no usa el modo local`() = runTest {
        coEvery { repo.login(any(), any()) } returns Result.failure(ErrorRed.NoAutorizado())
        val viewModel = AuthViewModel(repo)
        viewModel.onLoginIdentificadorChanged("estudiante")
        viewModel.onLoginPasswordChanged("1234")

        var exito = false
        viewModel.iniciarSesion { exito = true }

        assertFalse(exito)
        assertEquals("Credenciales incorrectas. Verifica tu correo y contraseña.", viewModel.uiState.value.loginMensajeError)
        coVerify(exactly = 0) { repo.loginSinConexion(any(), any()) }
    }

    @Test
    fun `registrarse valida campos y registra exitosamente en ms-usuarios`() = runTest {
        coEvery { repo.registrar("Sofía Morales", "sofia@marcelapaz.cl", "clave123", "ESTUDIANTE", "2° Básico A") } returns
            Result.success(sesionEstudiante)
        val viewModel = AuthViewModel(repo)
        viewModel.onRegNombreChanged("Sofía Morales")
        viewModel.onRegEmailChanged("sofia@marcelapaz.cl")
        viewModel.onRegPasswordChanged("clave123")
        viewModel.onRegRolChanged("ESTUDIANTE")
        viewModel.onRegCursoChanged("2° Básico A")

        var rolRetornado: String? = null
        viewModel.registrarse { rolRetornado = it }

        assertEquals("ESTUDIANTE", rolRetornado)
        assertFalse(viewModel.uiState.value.regCargando)
        assertEquals("¡Cuenta creada exitosamente!", viewModel.uiState.value.regMensajeExito)
    }

    @Test
    fun `registrarse con usuario duplicado muestra el error del servidor`() = runTest {
        coEvery { repo.registrar(any(), any(), any(), any(), any()) } returns
            Result.failure(ErrorRed.Http(400, "El nombre de usuario ya está registrado"))
        val viewModel = AuthViewModel(repo)
        viewModel.onRegNombreChanged("Sofía Morales")
        viewModel.onRegEmailChanged("sofia@marcelapaz.cl")
        viewModel.onRegPasswordChanged("clave123")

        var exito = false
        viewModel.registrarse { exito = true }

        assertFalse(exito)
        assertEquals("El nombre de usuario ya está registrado", viewModel.uiState.value.regMensajeError)
        coVerify(exactly = 0) { repo.registrarSinConexion(any(), any(), any(), any()) }
    }
}
