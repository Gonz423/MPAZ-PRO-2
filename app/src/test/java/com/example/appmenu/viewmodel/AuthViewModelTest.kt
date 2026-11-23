package com.example.appmenu.viewmodel

import android.app.Application
import com.example.appmenu.data.SessionManager
import com.example.appmenu.network.LoginRequestDto
import com.example.appmenu.network.LoginResponseDto
import com.example.appmenu.network.RegisterRequestDto
import com.example.appmenu.network.UsuarioResponseDto
import com.example.appmenu.network.UsuariosApiService
import com.example.appmenu.util.MainDispatcherRule
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val application: Application = mockk(relaxed = true)
    private val sessionManager: SessionManager = mockk(relaxed = true)
    private val usuariosApi: UsuariosApiService = mockk()

    @Test
    fun `iniciarSesion con campos vacios marca errores y no llama api`() = runTest {
        val viewModel = AuthViewModel(application, sessionManager, usuariosApi)

        var exitoLlamado = false
        viewModel.iniciarSesion { exitoLlamado = true }

        assertFalse(exitoLlamado)
        assertNotNull(viewModel.uiState.value.loginErrorIdentificador)
        assertNotNull(viewModel.uiState.value.loginErrorPassword)
        coVerify(exactly = 0) { usuariosApi.login(any()) }
    }

    @Test
    fun `iniciarSesion exitoso con ms-usuarios guarda sesion y llama callback`() = runTest {
        val mockResponse = LoginResponseDto(
            token = "jwt-token-123",
            id = 1L,
            username = "estudiante",
            nombreCompleto = "Mateo Silva",
            email = "estudiante@marcelapaz.cl",
            rol = "ESTUDIANTE",
            curso = "2° Básico A"
        )
        coEvery { usuariosApi.login(any()) } returns mockResponse

        val viewModel = AuthViewModel(application, sessionManager, usuariosApi)
        viewModel.onLoginIdentificadorChanged("estudiante@marcelapaz.cl")
        viewModel.onLoginPasswordChanged("1234")

        var rolRetornado: String? = null
        viewModel.iniciarSesion { rol -> rolRetornado = rol }

        assertEquals("ESTUDIANTE", rolRetornado)
        assertFalse(viewModel.uiState.value.loginCargando)
        assertNull(viewModel.uiState.value.loginMensajeError)
        coVerify { sessionManager.guardarSesion(mockResponse) }
    }

    @Test
    fun `iniciarSesion modo offline con credenciales por defecto inicia sesion local`() = runTest {
        coEvery { usuariosApi.login(any()) } throws IOException("Servidor no disponible")

        val viewModel = AuthViewModel(application, sessionManager, usuariosApi)
        viewModel.onLoginIdentificadorChanged("docente@marcelapaz.cl")
        viewModel.onLoginPasswordChanged("1234")

        var rolRetornado: String? = null
        viewModel.iniciarSesion { rol -> rolRetornado = rol }

        assertEquals("DOCENTE", rolRetornado)
        coVerify { sessionManager.guardarSesionLocal(any(), any(), any(), any(), "DOCENTE", any()) }
    }

    @Test
    fun `registrarse valida campos y registra exitosamente en ms-usuarios`() = runTest {
        val mockUser = UsuarioResponseDto(
            id = 5L,
            username = "sofia_morales",
            nombreCompleto = "Sofía Morales",
            email = "sofia@marcelapaz.cl",
            rol = "ESTUDIANTE",
            curso = "2° Básico A"
        )
        val mockLogin = LoginResponseDto(
            token = "jwt-reg-token",
            id = 5L,
            username = "sofia_morales",
            nombreCompleto = "Sofía Morales",
            email = "sofia@marcelapaz.cl",
            rol = "ESTUDIANTE",
            curso = "2° Básico A"
        )
        coEvery { usuariosApi.register(any()) } returns mockUser
        coEvery { usuariosApi.login(any()) } returns mockLogin

        val viewModel = AuthViewModel(application, sessionManager, usuariosApi)
        viewModel.onRegNombreChanged("Sofía Morales")
        viewModel.onRegEmailChanged("sofia@marcelapaz.cl")
        viewModel.onRegPasswordChanged("clave123")
        viewModel.onRegRolChanged("ESTUDIANTE")
        viewModel.onRegCursoChanged("2° Básico A")

        var rolRetornado: String? = null
        viewModel.registrarse { rol -> rolRetornado = rol }

        assertEquals("ESTUDIANTE", rolRetornado)
        assertFalse(viewModel.uiState.value.regCargando)
        coVerify { usuariosApi.register(any()) }
        coVerify { sessionManager.guardarSesion(mockLogin) }
    }
}
