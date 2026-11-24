package com.example.appmenu.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appmenu.data.repository.AuthRepository
import com.example.appmenu.domain.model.ErrorRed
import com.example.appmenu.domain.model.mensajeUsuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    // ---------------- login ----------------

    fun onLoginIdentificadorChanged(valor: String) = _uiState.update {
        it.copy(loginIdentificador = valor, loginErrorIdentificador = null, loginMensajeError = null)
    }

    fun onLoginPasswordChanged(valor: String) = _uiState.update {
        it.copy(loginPassword = valor, loginErrorPassword = null, loginMensajeError = null)
    }

    fun iniciarSesion(onExito: (rol: String) -> Unit) {
        val ident = _uiState.value.loginIdentificador.trim()
        val pass = _uiState.value.loginPassword.trim()

        val errorIdent = if (ident.isBlank()) "Ingresa tu correo o usuario" else null
        val errorPass = if (pass.isBlank()) "Ingresa tu contraseña" else null
        if (errorIdent != null || errorPass != null) {
            _uiState.update { it.copy(loginErrorIdentificador = errorIdent, loginErrorPassword = errorPass) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(loginCargando = true, loginMensajeError = null) }
            authRepository.login(ident, pass)
                .onSuccess { sesion ->
                    _uiState.update { it.copy(loginCargando = false) }
                    onExito(sesion.rol)
                }
                .onFailure { error ->
                    // el modo sin conexion solo se usa si el servidor NO responde;
                    // si responde 401 las credenciales son realmente incorrectas
                    val sesionLocal = if (error is ErrorRed.SinConexion) {
                        authRepository.loginSinConexion(ident, pass)
                    } else null

                    if (sesionLocal != null) {
                        _uiState.update { it.copy(loginCargando = false) }
                        onExito(sesionLocal.rol)
                    } else {
                        _uiState.update { it.copy(loginCargando = false, loginMensajeError = mensajeLogin(error)) }
                    }
                }
        }
    }

    private fun mensajeLogin(error: Throwable): String = when (error) {
        is ErrorRed.NoAutorizado -> "Credenciales incorrectas. Verifica tu correo y contraseña."
        is ErrorRed.SinConexion -> "Microservicio no disponible. Sin conexión solo funcionan las cuentas de prueba (estudiante / 1234)."
        else -> error.mensajeUsuario()
    }

    // ---------------- registro ----------------

    fun onRegNombreChanged(valor: String) = _uiState.update {
        it.copy(regNombreCompleto = valor, regErrorNombre = null, regMensajeError = null)
    }

    fun onRegEmailChanged(valor: String) = _uiState.update {
        it.copy(regEmail = valor, regErrorEmail = null, regMensajeError = null)
    }

    fun onRegPasswordChanged(valor: String) = _uiState.update {
        it.copy(regPassword = valor, regErrorPassword = null, regMensajeError = null)
    }

    fun onRegRolChanged(valor: String) = _uiState.update { it.copy(regRol = valor) }

    fun onRegCursoChanged(valor: String) = _uiState.update { it.copy(regCurso = valor) }

    fun registrarse(onExito: (rol: String) -> Unit) {
        val estado = _uiState.value
        val nombre = estado.regNombreCompleto.trim()
        val email = estado.regEmail.trim()
        val pass = estado.regPassword.trim()
        val rol = estado.regRol
        val curso = estado.regCurso.trim()

        val errorNombre = if (nombre.length < 3) "Ingresa tu nombre completo (mín. 3 letras)" else null
        val errorEmail = if (!email.contains("@") || !email.contains(".")) "Ingresa un correo electrónico válido" else null
        val errorPass = if (pass.length < 4) "La contraseña debe tener al menos 4 caracteres" else null
        if (errorNombre != null || errorEmail != null || errorPass != null) {
            _uiState.update {
                it.copy(regErrorNombre = errorNombre, regErrorEmail = errorEmail, regErrorPassword = errorPass)
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(regCargando = true, regMensajeError = null) }
            authRepository.registrar(nombre, email, pass, rol, curso)
                .onSuccess {
                    _uiState.update { it.copy(regCargando = false, regMensajeExito = "¡Cuenta creada exitosamente!") }
                    onExito(rol)
                }
                .onFailure { error ->
                    if (error is ErrorRed.SinConexion) {
                        // sin servidor: la cuenta queda solo en este dispositivo
                        authRepository.registrarSinConexion(nombre, email, rol, curso)
                        _uiState.update { it.copy(regCargando = false, regMensajeExito = "¡Registro local completado!") }
                        onExito(rol)
                    } else {
                        // por ejemplo 400 "El nombre de usuario ya está registrado"
                        _uiState.update { it.copy(regCargando = false, regMensajeError = error.mensajeUsuario()) }
                    }
                }
        }
    }
}
