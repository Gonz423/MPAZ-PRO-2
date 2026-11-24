package com.example.appmenu.domain.model

/** Datos que ingresa el docente para crear un ejercicio o una tarea. */
data class NuevoEjercicio(
    val materiaId: String,
    val unidadId: String,
    val titulo: String,
    val objetivoAprendizaje: String,
    val codigoGuia: String,
    val pregunta: String = "¿Cuál es la opción correcta?",
    val opciones: List<String> = listOf("Opción A", "Opción B", "Opción C"),
    val respuestaCorrectaIndex: Int = 0,
    val disponible: Boolean = true,
    val esTarea: Boolean = false,
    val fechaEntrega: Long? = null
)
