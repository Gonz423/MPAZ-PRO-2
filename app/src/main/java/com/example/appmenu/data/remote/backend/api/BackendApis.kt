package com.example.appmenu.data.remote.backend.api

import com.example.appmenu.data.remote.backend.dto.EjercicioDto
import com.example.appmenu.data.remote.backend.dto.LoginRequestDto
import com.example.appmenu.data.remote.backend.dto.LoginResponseDto
import com.example.appmenu.data.remote.backend.dto.ObservacionDto
import com.example.appmenu.data.remote.backend.dto.RegisterRequestDto
import com.example.appmenu.data.remote.backend.dto.ResultadoDto
import com.example.appmenu.data.remote.backend.dto.ResultadoRequestDto
import com.example.appmenu.data.remote.backend.dto.UnidadDto
import com.example.appmenu.data.remote.backend.dto.UsuarioResponseDto
import retrofit2.Response
import retrofit2.http.*

/** ms-usuarios (puerto 8081): autenticacion y perfiles. */
interface UsuariosApi {
    @POST("api/auth/login")
    suspend fun login(@Body body: LoginRequestDto): LoginResponseDto

    @POST("api/auth/register")
    suspend fun registrar(@Body body: RegisterRequestDto): UsuarioResponseDto

    @GET("api/usuarios")
    suspend fun listar(): List<UsuarioResponseDto>

    @GET("api/usuarios/{id}")
    suspend fun obtener(@Path("id") id: Long): UsuarioResponseDto

    @GET("api/usuarios/rol/{rol}")
    suspend fun listarPorRol(@Path("rol") rol: String): List<UsuarioResponseDto>
}

/** ms-contenidos (puerto 8082): CRUD de unidades y ejercicios. */
interface ContenidosApi {
    @GET("api/unidades")
    suspend fun listarUnidades(@Query("materia") materia: String? = null): List<UnidadDto>

    @POST("api/unidades")
    suspend fun crearUnidad(@Body unidad: UnidadDto): UnidadDto

    @PUT("api/unidades/{id}")
    suspend fun actualizarUnidad(@Path("id") id: Long, @Body unidad: UnidadDto): UnidadDto

    @DELETE("api/unidades/{id}")
    suspend fun eliminarUnidad(@Path("id") id: Long): Response<Unit>

    @GET("api/ejercicios")
    suspend fun listarEjercicios(
        @Query("unidadId") unidadId: Long? = null,
        @Query("materia") materia: String? = null
    ): List<EjercicioDto>

    @GET("api/ejercicios/{id}")
    suspend fun obtenerEjercicio(@Path("id") id: Long): EjercicioDto

    @POST("api/ejercicios")
    suspend fun crearEjercicio(@Body ejercicio: EjercicioDto): EjercicioDto

    @PUT("api/ejercicios/{id}")
    suspend fun actualizarEjercicio(@Path("id") id: Long, @Body ejercicio: EjercicioDto): EjercicioDto

    @DELETE("api/ejercicios/{id}")
    suspend fun eliminarEjercicio(@Path("id") id: Long): Response<Unit>
}

/** ms-progreso (puerto 8083): resultados y observaciones. */
interface ProgresoApi {
    @POST("api/resultados")
    suspend fun registrarResultado(@Body resultado: ResultadoRequestDto): ResultadoDto

    @GET("api/resultados")
    suspend fun listarResultados(@Query("estudianteId") estudianteId: Long): List<ResultadoDto>

    @GET("api/observaciones")
    suspend fun listarObservaciones(
        @Query("estudianteId") estudianteId: Long? = null,
        @Query("docenteId") docenteId: Long? = null
    ): List<ObservacionDto>

    @POST("api/observaciones")
    suspend fun crearObservacion(@Body observacion: ObservacionDto): ObservacionDto

    @DELETE("api/observaciones/{id}")
    suspend fun eliminarObservacion(@Path("id") id: Long): Response<Unit>
}
