package com.example.appmenu.data.remote.backend.dto

import com.google.gson.annotations.SerializedName

// DTOs de los microservicios propios. Los nombres coinciden con los records Java del backend.
// Solo los usa la capa data/remote/backend; nunca se mezclan con los DTOs de la API externa.

// ---------------- ms-usuarios (8081) ----------------

data class LoginRequestDto(
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String
)

data class LoginResponseDto(
    @SerializedName("token") val token: String = "",
    @SerializedName("id") val id: Long = 0,
    @SerializedName("username") val username: String = "",
    @SerializedName("nombreCompleto") val nombreCompleto: String = "",
    @SerializedName("email") val email: String? = null,
    @SerializedName("rol") val rol: String = "ESTUDIANTE",
    @SerializedName("curso") val curso: String? = null,
    @SerializedName("tokenType") val tokenType: String? = "Bearer"
)

data class RegisterRequestDto(
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String,
    @SerializedName("nombreCompleto") val nombreCompleto: String,
    @SerializedName("email") val email: String? = null,
    @SerializedName("rol") val rol: String = "ESTUDIANTE",
    @SerializedName("curso") val curso: String? = null
)

data class UsuarioResponseDto(
    @SerializedName("id") val id: Long = 0,
    @SerializedName("username") val username: String = "",
    @SerializedName("nombreCompleto") val nombreCompleto: String = "",
    @SerializedName("email") val email: String? = null,
    @SerializedName("rol") val rol: String = "ESTUDIANTE",
    @SerializedName("curso") val curso: String? = null,
    @SerializedName("activo") val activo: Boolean? = true
)

// ---------------- ms-contenidos (8082) ----------------

data class UnidadDto(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("materiaId") val materiaId: String,
    @SerializedName("numero") val numero: Int? = null,
    @SerializedName("titulo") val titulo: String,
    @SerializedName("objetivoAprendizaje") val objetivoAprendizaje: String? = null
)

data class EjercicioDto(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("materiaId") val materiaId: String,
    @SerializedName("unidadId") val unidadId: Long,
    @SerializedName("titulo") val titulo: String,
    @SerializedName("objetivoAprendizaje") val objetivoAprendizaje: String? = null,
    @SerializedName("codigoGuia") val codigoGuia: String? = null,
    @SerializedName("pregunta") val pregunta: String? = null,
    /** Texto JSON con la lista de alternativas: ["A","B","C"] */
    @SerializedName("opciones") val opciones: String? = null,
    @SerializedName("respuestaCorrectaIndex") val respuestaCorrectaIndex: Int? = 0,
    @SerializedName("disponible") val disponible: Boolean? = true,
    @SerializedName("esTarea") val esTarea: Boolean? = false,
    @SerializedName("fechaEntrega") val fechaEntrega: Long? = null
)

// ---------------- ms-progreso (8083) ----------------

data class ResultadoRequestDto(
    @SerializedName("estudianteId") val estudianteId: Long,
    @SerializedName("ejercicioId") val ejercicioId: Long,
    @SerializedName("nota") val nota: Int,
    @SerializedName("aciertos") val aciertos: Int,
    @SerializedName("totalPreguntas") val totalPreguntas: Int,
    @SerializedName("materiaId") val materiaId: String
)

data class ResultadoDto(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("estudianteId") val estudianteId: Long = 0,
    @SerializedName("ejercicioId") val ejercicioId: Long = 0,
    @SerializedName("nota") val nota: Int = 0,
    @SerializedName("aciertos") val aciertos: Int? = 0,
    @SerializedName("totalPreguntas") val totalPreguntas: Int? = 1,
    @SerializedName("intentos") val intentos: Int? = 1,
    @SerializedName("materiaId") val materiaId: String? = null,
    @SerializedName("fechaRealizado") val fechaRealizado: Long? = null
)

data class ObservacionDto(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("docenteId") val docenteId: Long? = null,
    @SerializedName("estudianteId") val estudianteId: Long? = null,
    @SerializedName("contenido") val contenido: String,
    @SerializedName("fecha") val fecha: Long? = null
)
