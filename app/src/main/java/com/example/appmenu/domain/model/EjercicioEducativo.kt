package com.example.appmenu.domain.model

// modelo de unidades didacticas por materia - mpaz pro 2.º
data class UnidadEducativa(
    val id: String,
    val materiaId: String,          // "lenguaje", "matematica", "ciencias", "historia"
    val numero: Int,
    val titulo: String,
    val objetivoAprendizaje: String, // ej: "oa 3, oa 6 · el valor de la amistad"
    val remoteId: Long? = null       // id en ms-contenidos (null si aun no se sincroniza)
)

// modelo de ejercicios y actividades pedagogicas asociadas a una unidad
data class EjercicioEducativo(
    val id: String,
    val materiaId: String,          // "lenguaje", "matematica", "ciencias", "historia"
    val unidadId: String,           // asociado directamente a la unidadeducativa
    val titulo: String,
    val objetivoAprendizaje: String,// ej: oa 1, oa 3
    val codigoGuia: String,
    val completado: Boolean = false,
    val disponibleParaEstudiantes: Boolean = true,
    val esTarea: Boolean = false,
    val fechaEntrega: Long? = null,
    // contenido pedagogico interactivo para probar
    val pregunta: String = "¿Cuál es la respuesta correcta?",
    val opciones: List<String> = listOf("Opción 1", "Opción 2", "Opción 3"),
    val respuestaCorrectaIndex: Int = 0,
    // calificacion numerica: 0 = no realizado, 1-10 = nota obtenida
    val nota: Int = 0,
    // cantidad de intentos realizados por el estudiante
    val intentos: Int = 0,
    // id en ms-contenidos (null si aun no se sincroniza)
    val remoteId: Long? = null
)
