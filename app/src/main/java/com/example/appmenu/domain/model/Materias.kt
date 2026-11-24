package com.example.appmenu.domain.model

/** Reglas para relacionar los nombres de asignatura que ve el usuario con su id interno. */
object Materias {

    /** "Lenguaje y Comunicación" -> "lenguaje", "Matemática" -> "matematica", etc. */
    fun idDesdeCategoria(categoria: String): String = when {
        categoria.contains("Lenguaje", ignoreCase = true) -> "lenguaje"
        categoria.contains("Matemática", ignoreCase = true) ||
            categoria.contains("Matematica", ignoreCase = true) -> "matematica"
        categoria.contains("Ciencias", ignoreCase = true) -> "ciencias"
        categoria.contains("Historia", ignoreCase = true) -> "historia"
        else -> "lenguaje"
    }

    /** Normaliza el nombre recibido desde otra pantalla al texto exacto del selector del formulario. */
    fun normalizarCategoria(categoria: String): String = when {
        categoria.contains("Lenguaje", ignoreCase = true) -> "Lenguaje y Comunicación"
        categoria.contains("Matemática", ignoreCase = true) ||
            categoria.contains("Matematica", ignoreCase = true) -> "Matemática"
        categoria.contains("Ciencias", ignoreCase = true) -> "Ciencias Naturales"
        categoria.contains("Historia", ignoreCase = true) -> "Historia y Geografía"
        else -> categoria
    }
}
