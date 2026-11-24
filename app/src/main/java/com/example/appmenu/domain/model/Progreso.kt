package com.example.appmenu.domain.model

/** Resultado de un ejercicio registrado en ms-progreso. ejercicioId = codigo de la guia. */
data class ResultadoEstudiante(
    val id: Long?,
    val estudianteId: Long,
    val ejercicioId: Long,
    val nota: Int,
    val intentos: Int,
    val materiaId: String
)

/** Pregunta de una guia creada en el formulario del docente (varias por guia). */
data class PreguntaFormulario(
    val id: Int,
    var enunciado: String = "",
    var opcion1: String = "",
    var opcion2: String = "",
    var opcion3: String = ""
)
