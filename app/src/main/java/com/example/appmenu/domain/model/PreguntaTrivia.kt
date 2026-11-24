package com.example.appmenu.domain.model

/**
 * Pregunta del "Desafío Científico · Curiosidades", armada con un resumen en español de Wikipedia.
 * El estudiante lee la pista (con el nombre oculto), mira la imagen y elige de qué se trata.
 */
data class PreguntaTrivia(
    val categoria: String,
    /** Primera frase del resumen con el nombre reemplazado por "___". */
    val pista: String,
    val pregunta: String,
    val respuestaCorrecta: String,
    val respuestasIncorrectas: List<String>,
    /** Imagen del tema (puede no existir). */
    val imagenUrl: String?,
    /** El mismo dato sin ocultar, para mostrar "¿Sabías que...?" despues de responder. */
    val datoCompleto: String,
    /** Ancho / alto de la imagen, para mostrarla completa sin recortes (null si no se conoce). */
    val imagenProporcion: Float? = null
)
