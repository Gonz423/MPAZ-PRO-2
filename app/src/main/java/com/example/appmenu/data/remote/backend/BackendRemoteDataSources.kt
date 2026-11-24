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
import com.example.appmenu.data.remote.safeApiCall
import com.example.appmenu.domain.model.ErrorRed
import retrofit2.Response

// Cada DataSource habla con UN microservicio y devuelve Result: nunca lanza excepciones de red.

open class UsuariosRemoteDataSource(private val api: UsuariosApi) {
    open suspend fun login(username: String, password: String): Result<LoginResponseDto> =
        safeApiCall { api.login(LoginRequestDto(username, password)) }

    open suspend fun registrar(request: RegisterRequestDto): Result<UsuarioResponseDto> =
        safeApiCall { api.registrar(request) }
}

open class ContenidosRemoteDataSource(private val api: ContenidosApi) {
    open suspend fun listarUnidades(): Result<List<UnidadDto>> = safeApiCall { api.listarUnidades() }

    open suspend fun crearUnidad(unidad: UnidadDto): Result<UnidadDto> = safeApiCall { api.crearUnidad(unidad) }

    open suspend fun listarEjercicios(): Result<List<EjercicioDto>> = safeApiCall { api.listarEjercicios() }

    open suspend fun crearEjercicio(ejercicio: EjercicioDto): Result<EjercicioDto> =
        safeApiCall { api.crearEjercicio(ejercicio) }

    open suspend fun actualizarEjercicio(id: Long, ejercicio: EjercicioDto): Result<EjercicioDto> =
        safeApiCall { api.actualizarEjercicio(id, ejercicio) }

    open suspend fun eliminarEjercicio(id: Long): Result<Unit> =
        safeApiCall { api.eliminarEjercicio(id).sinCuerpo() }
}

open class ProgresoRemoteDataSource(private val api: ProgresoApi) {
    open suspend fun registrarResultado(resultado: ResultadoRequestDto): Result<ResultadoDto> =
        safeApiCall { api.registrarResultado(resultado) }

    open suspend fun listarResultados(estudianteId: Long): Result<List<ResultadoDto>> =
        safeApiCall { api.listarResultados(estudianteId) }

    open suspend fun crearObservacion(observacion: ObservacionDto): Result<ObservacionDto> =
        safeApiCall { api.crearObservacion(observacion) }
}

/** Para endpoints sin cuerpo (DELETE 204): convierte un codigo de error en ErrorRed. */
private fun Response<Unit>.sinCuerpo() {
    if (!isSuccessful) {
        throw if (code() == 401) ErrorRed.NoAutorizado() else ErrorRed.Http(code(), null)
    }
}
