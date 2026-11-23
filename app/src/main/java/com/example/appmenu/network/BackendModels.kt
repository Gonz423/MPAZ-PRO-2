package com.example.appmenu.network

import com.google.gson.annotations.SerializedName

// DTOs para comunicacion con microservicios Spring Boot - mpaz pro 2.°

data class ResultadoDto(
    @SerializedName("id")             val id: Long? = null,
    @SerializedName("estudianteId")   val estudianteId: Long = 1,
    @SerializedName("ejercicioId")    val ejercicioId: Long = 1,
    @SerializedName("nota")           val nota: Int = 10,
    @SerializedName("aciertos")       val aciertos: Int = 1,
    @SerializedName("totalPreguntas") val totalPreguntas: Int = 1,
    @SerializedName("intentos")       val intentos: Int = 1,
    @SerializedName("materiaId")      val materiaId: String = "lenguaje",
    @SerializedName("fechaRealizado") val fechaRealizado: Long? = null
)

data class ObservacionDto(
    @SerializedName("id")           val id: Long? = null,
    @SerializedName("docenteId")    val docenteId: Long? = 1,
    @SerializedName("estudianteId") val estudianteId: Long? = 1,
    @SerializedName("contenido")    val contenido: String = "",
    @SerializedName("fecha")        val fecha: Long? = null
)

data class EjercicioDto(
    @SerializedName("id")                     val id: Long? = null,
    @SerializedName("materiaId")              val materiaId: String = "lenguaje",
    @SerializedName("unidadId")               val unidadId: Long = 1,
    @SerializedName("titulo")                 val titulo: String = "",
    @SerializedName("objetivoAprendizaje")    val objetivoAprendizaje: String? = null,
    @SerializedName("codigoGuia")             val codigoGuia: String? = null,
    @SerializedName("pregunta")               val pregunta: String? = null,
    @SerializedName("opciones")               val opciones: String? = null,
    @SerializedName("respuestaCorrectaIndex") val respuestaCorrectaIndex: Int = 0,
    @SerializedName("disponible")             val disponible: Boolean = true,
    @SerializedName("esTarea")                val esTarea: Boolean = false,
    @SerializedName("fechaEntrega")           val fechaEntrega: Long? = null
)

data class UnidadDto(
    @SerializedName("id")                  val id: Long? = null,
    @SerializedName("materiaId")           val materiaId: String = "lenguaje",
    @SerializedName("numero")              val numero: Int = 1,
    @SerializedName("titulo")              val titulo: String = "",
    @SerializedName("objetivoAprendizaje") val objetivoAprendizaje: String? = null
)

// DTOs para autenticacion y gestion de usuarios (ms-usuarios : 8081)

data class LoginRequestDto(
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String
)

data class LoginResponseDto(
    @SerializedName("token")          val token: String = "",
    @SerializedName("id")             val id: Long = 1,
    @SerializedName("username")       val username: String = "",
    @SerializedName("nombreCompleto") val nombreCompleto: String = "",
    @SerializedName("email")          val email: String? = null,
    @SerializedName("rol")            val rol: String = "ESTUDIANTE",
    @SerializedName("curso")          val curso: String? = null,
    @SerializedName("tokenType")      val tokenType: String? = "Bearer"
)

data class RegisterRequestDto(
    @SerializedName("username")       val username: String,
    @SerializedName("password")       val password: String,
    @SerializedName("nombreCompleto") val nombreCompleto: String,
    @SerializedName("email")          val email: String? = null,
    @SerializedName("rol")            val rol: String = "ESTUDIANTE",
    @SerializedName("curso")          val curso: String? = "2° Básico A"
)

data class UsuarioResponseDto(
    @SerializedName("id")             val id: Long = 1,
    @SerializedName("username")       val username: String = "",
    @SerializedName("nombreCompleto") val nombreCompleto: String = "",
    @SerializedName("email")          val email: String? = null,
    @SerializedName("rol")            val rol: String = "ESTUDIANTE",
    @SerializedName("curso")          val curso: String? = null,
    @SerializedName("activo")         val activo: Boolean? = true
)
