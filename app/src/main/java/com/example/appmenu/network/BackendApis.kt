package com.example.appmenu.network

import retrofit2.http.*

// endpoints de comunicacion con los microservicios Spring Boot - mpaz pro 2.°

// microservicio ms-progreso (puerto 8083)
interface ProgresoApiService {
    @POST("api/resultados")
    suspend fun registrarResultado(@Body resultado: ResultadoDto): ResultadoDto

    @GET("api/resultados")
    suspend fun listarResultados(@Query("estudianteId") estudianteId: Long): List<ResultadoDto>

    @GET("api/observaciones")
    suspend fun listarObservaciones(@Query("docenteId") docenteId: Long? = null): List<ObservacionDto>

    @POST("api/observaciones")
    suspend fun crearObservacion(@Body observacion: ObservacionDto): ObservacionDto
}

// microservicio ms-contenidos (puerto 8082)
interface ContenidosApiService {
    @GET("api/unidades")
    suspend fun listarUnidades(@Query("materia") materia: String? = null): List<UnidadDto>

    @GET("api/ejercicios")
    suspend fun listarEjercicios(
        @Query("unidadId") unidadId: Long? = null,
        @Query("materia")  materia: String? = null
    ): List<EjercicioDto>

    @POST("api/ejercicios")
    suspend fun crearEjercicio(@Body ejercicio: EjercicioDto): EjercicioDto

    @DELETE("api/ejercicios/{id}")
    suspend fun eliminarEjercicio(@Path("id") id: Long)
}

// microservicio ms-usuarios (puerto 8081)
interface UsuariosApiService {
    @POST("api/auth/login")
    suspend fun login(@Body body: LoginRequestDto): LoginResponseDto

    @POST("api/auth/register")
    suspend fun register(@Body body: RegisterRequestDto): UsuarioResponseDto

    @GET("api/usuarios")
    suspend fun listarUsuarios(): List<UsuarioResponseDto>

    @GET("api/usuarios/{id}")
    suspend fun obtenerUsuario(@Path("id") id: Long): UsuarioResponseDto

    @GET("api/usuarios/rol/{rol}")
    suspend fun listarPorRol(@Path("rol") rol: String): List<UsuarioResponseDto>
}
