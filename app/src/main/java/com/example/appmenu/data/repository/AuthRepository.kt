package com.example.appmenu.data.repository

import com.example.appmenu.data.local.SessionManager
import com.example.appmenu.data.remote.backend.UsuariosRemoteDataSource
import com.example.appmenu.data.remote.backend.dto.LoginResponseDto
import com.example.appmenu.data.remote.backend.dto.RegisterRequestDto
import com.example.appmenu.domain.model.UsuarioSesion
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val sesion: Flow<UsuarioSesion>

    /** Login contra ms-usuarios; si es correcto guarda la sesion con el JWT. */
    suspend fun login(identificador: String, password: String): Result<UsuarioSesion>

    /**
     * Modo sin conexion (solo cuando el servidor no responde): acepta las cuentas de prueba
     * con clave 1234 para poder usar la app en una demostracion sin backend.
     * Devuelve null si las credenciales no corresponden a una cuenta de prueba.
     */
    suspend fun loginSinConexion(identificador: String, password: String): UsuarioSesion?

    /** Registra en ms-usuarios e inicia sesion automaticamente. */
    suspend fun registrar(nombre: String, email: String, password: String, rol: String, curso: String): Result<UsuarioSesion>

    /** Registro guardado solo en el dispositivo cuando el servidor no responde. */
    suspend fun registrarSinConexion(nombre: String, email: String, rol: String, curso: String): UsuarioSesion

    suspend fun cerrarSesion()
}

class AuthRepositoryImpl(
    private val remoto: UsuariosRemoteDataSource,
    private val sessionManager: SessionManager,
    private val generarId: () -> Long = { System.currentTimeMillis() % 10000 }
) : AuthRepository {

    override val sesion: Flow<UsuarioSesion> = sessionManager.sesionFlow

    override suspend fun login(identificador: String, password: String): Result<UsuarioSesion> =
        remoto.login(identificador, password).map { dto ->
            dto.toSesion().also { sessionManager.guardarSesion(it) }
        }

    override suspend fun loginSinConexion(identificador: String, password: String): UsuarioSesion? {
        if (password != CLAVE_DEMO) return null
        val ident = identificador.lowercase()
        val rol = when {
            ident.contains("docente") || ident.contains("profesor") || ident == "utp" -> "DOCENTE"
            ident.contains("estudiante") || ident.contains("alumno") || ident.contains("@") -> "ESTUDIANTE"
            else -> return null
        }
        val sesion = UsuarioSesion(
            token = UsuarioSesion.TOKEN_SIN_CONEXION,
            id = UsuarioSesion.ID_ESTUDIANTE_DEMO,
            username = identificador,
            nombreCompleto = if (rol == "DOCENTE") "Profesora Marcela" else "Mateo Silva",
            email = if (identificador.contains("@")) identificador else "$identificador@marcelapaz.cl",
            rol = rol,
            curso = "2° Básico A",
            estaAutenticado = true
        )
        sessionManager.guardarSesion(sesion)
        return sesion
    }

    override suspend fun registrar(
        nombre: String, email: String, password: String, rol: String, curso: String
    ): Result<UsuarioSesion> {
        val username = usernameDesdeEmail(email)
        val registro = remoto.registrar(
            RegisterRequestDto(username, password, nombre, email, rol, curso)
        )
        if (registro.isFailure) return Result.failure(registro.exceptionOrNull()!!)
        return login(username, password)
    }

    override suspend fun registrarSinConexion(nombre: String, email: String, rol: String, curso: String): UsuarioSesion {
        val sesion = UsuarioSesion(
            token = UsuarioSesion.TOKEN_SIN_CONEXION,
            id = generarId(),
            username = usernameDesdeEmail(email),
            nombreCompleto = nombre,
            email = email,
            rol = rol,
            curso = curso,
            estaAutenticado = true
        )
        sessionManager.guardarSesion(sesion)
        return sesion
    }

    override suspend fun cerrarSesion() = sessionManager.cerrarSesion()

    companion object {
        const val CLAVE_DEMO = "1234"

        /** "maria.perez@correo.cl" -> "maria_perez" */
        fun usernameDesdeEmail(email: String): String = email.substringBefore("@").replace(".", "_")
    }
}

private fun LoginResponseDto.toSesion() = UsuarioSesion(
    token = token,
    id = id,
    username = username,
    nombreCompleto = nombreCompleto,
    email = email ?: "",
    rol = rol,
    curso = curso ?: "2° Básico A",
    estaAutenticado = true
)
