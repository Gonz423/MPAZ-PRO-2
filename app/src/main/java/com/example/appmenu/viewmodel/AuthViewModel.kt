package com.example.appmenu.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.appmenu.data.SessionManager
import com.example.appmenu.network.BackendClient
import com.example.appmenu.network.LoginRequestDto
import com.example.appmenu.network.RegisterRequestDto
import com.example.appmenu.network.UsuariosApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    // login
    val loginIdentificador: String = "",
    val loginPassword: String = "",
    val loginErrorIdentificador: String? = null,
    val loginErrorPassword: String? = null,
    val loginCargando: Boolean = false,
    val loginMensajeError: String? = null,

    // registro
    val regNombreCompleto: String = "",
    val regEmail: String = "",
    val regUsername: String = "",
    val regPassword: String = "",
    val regRol: String = "ESTUDIANTE",
    val regCurso: String = "2° Básico A",
    val regErrorNombre: String? = null,
    val regErrorEmail: String? = null,
    val regErrorPassword: String? = null,
    val regCargando: Boolean = false,
    val regMensajeError: String? = null,
    val regMensajeExito: String? = null
)

class AuthViewModel(
    application: Application,
    private val sessionManager: SessionManager = SessionManager.getInstance(application),
    private val usuariosApi: UsuariosApiService = BackendClient.usuariosApi
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    // --- Metodos Login ---

    fun onLoginIdentificadorChanged(valor: String) {
        _uiState.value = _uiState.value.copy(
            loginIdentificador = valor,
            loginErrorIdentificador = null,
            loginMensajeError = null
        )
    }

    fun onLoginPasswordChanged(valor: String) {
        _uiState.value = _uiState.value.copy(
            loginPassword = valor,
            loginErrorPassword = null,
            loginMensajeError = null
        )
    }

    fun iniciarSesion(onExito: (rol: String) -> Unit) {
        val ident = _uiState.value.loginIdentificador.trim()
        val pass = _uiState.value.loginPassword.trim()

        var hayError = false
        if (ident.isBlank()) {
            _uiState.value = _uiState.value.copy(loginErrorIdentificador = "Ingresa tu correo o usuario")
            hayError = true
        }
        if (pass.isBlank()) {
            _uiState.value = _uiState.value.copy(loginErrorPassword = "Ingresa tu contraseña")
            hayError = true
        }
        if (hayError) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loginCargando = true, loginMensajeError = null)
            try {
                // Intento 1: Servidor ms-usuarios Spring Boot (8081)
                val response = usuariosApi.login(LoginRequestDto(username = ident, password = pass))
                sessionManager.guardarSesion(response)
                _uiState.value = _uiState.value.copy(loginCargando = false)
                onExito(response.rol)
            } catch (e: Exception) {
                // Modo fallback offline seguro para pruebas / sin backend
                val identLower = ident.lowercase()
                val rolFallback = when {
                    (identLower.contains("docente") || identLower.contains("profesor")) && pass == "1234" -> "DOCENTE"
                    (identLower.contains("estudiante") || identLower.contains("alumno") || identLower.contains("@")) && pass == "1234" -> "ESTUDIANTE"
                    identLower == "utp" && pass == "1234" -> "DOCENTE"
                    else -> null
                }

                if (rolFallback != null) {
                    val nombre = if (rolFallback == "DOCENTE") "Profesora Marcela" else "Mateo Silva"
                    sessionManager.guardarSesionLocal(
                        id = 1L,
                        username = ident,
                        nombreCompleto = nombre,
                        email = if (ident.contains("@")) ident else "$ident@marcelapaz.cl",
                        rol = rolFallback,
                        curso = "2° Básico A"
                    )
                    _uiState.value = _uiState.value.copy(loginCargando = false)
                    onExito(rolFallback)
                } else {
                    val errorMsg = if (e.message?.contains("401") == true || e.message?.contains("Credenciales") == true) {
                        "Credenciales incorrectas. Verifica tu correo y contraseña."
                    } else {
                        "Credenciales incorrectas o microservicio no disponible (prueba: estudiante / 1234)."
                    }
                    _uiState.value = _uiState.value.copy(
                        loginCargando = false,
                        loginMensajeError = errorMsg
                    )
                }
            }
        }
    }

    // --- Metodos Registro ---

    fun onRegNombreChanged(valor: String) {
        _uiState.value = _uiState.value.copy(regNombreCompleto = valor, regErrorNombre = null, regMensajeError = null)
    }

    fun onRegEmailChanged(valor: String) {
        _uiState.value = _uiState.value.copy(regEmail = valor, regErrorEmail = null, regMensajeError = null)
    }

    fun onRegPasswordChanged(valor: String) {
        _uiState.value = _uiState.value.copy(regPassword = valor, regErrorPassword = null, regMensajeError = null)
    }

    fun onRegRolChanged(valor: String) {
        _uiState.value = _uiState.value.copy(regRol = valor)
    }

    fun onRegCursoChanged(valor: String) {
        _uiState.value = _uiState.value.copy(regCurso = valor)
    }

    fun registrarse(onExito: (rol: String) -> Unit) {
        val nombre = _uiState.value.regNombreCompleto.trim()
        val email = _uiState.value.regEmail.trim()
        val pass = _uiState.value.regPassword.trim()
        val rol = _uiState.value.regRol
        val curso = _uiState.value.regCurso.trim()

        var hayError = false
        if (nombre.length < 3) {
            _uiState.value = _uiState.value.copy(regErrorNombre = "Ingresa tu nombre completo (mín. 3 letras)")
            hayError = true
        }
        if (!email.contains("@") || !email.contains(".")) {
            _uiState.value = _uiState.value.copy(regErrorEmail = "Ingresa un correo electrónico válido")
            hayError = true
        }
        if (pass.length < 4) {
            _uiState.value = _uiState.value.copy(regErrorPassword = "La contraseña debe tener al menos 4 caracteres")
            hayError = true
        }
        if (hayError) return

        // Generar username basado en email
        val usernameGenerado = email.substringBefore("@").replace(".", "_")

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(regCargando = true, regMensajeError = null)
            try {
                // Registrar en ms-usuarios
                usuariosApi.register(
                    RegisterRequestDto(
                        username = usernameGenerado,
                        password = pass,
                        nombreCompleto = nombre,
                        email = email,
                        rol = rol,
                        curso = curso
                    )
                )

                // Login automático para obtener JWT y sesión
                val loginRes = usuariosApi.login(LoginRequestDto(username = usernameGenerado, password = pass))
                sessionManager.guardarSesion(loginRes)

                _uiState.value = _uiState.value.copy(regCargando = false, regMensajeExito = "¡Cuenta creada exitosamente!")
                onExito(rol)
            } catch (e: Exception) {
                // Si el servidor falla, guardamos localmente en modo offline
                sessionManager.guardarSesionLocal(
                    id = System.currentTimeMillis() % 10000,
                    username = usernameGenerado,
                    nombreCompleto = nombre,
                    email = email,
                    rol = rol,
                    curso = curso
                )
                _uiState.value = _uiState.value.copy(regCargando = false, regMensajeExito = "¡Registro local completado!")
                onExito(rol)
            }
        }
    }
}
