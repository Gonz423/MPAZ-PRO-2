package com.example.appmenu.data.repository

import com.example.appmenu.data.remote.external.WikipediaRemoteDataSource
import com.example.appmenu.data.remote.external.dto.WikipediaResumenDto
import com.example.appmenu.domain.model.Curiosidades
import com.example.appmenu.domain.model.ErrorRed
import com.example.appmenu.domain.model.PreguntaTrivia
import com.example.appmenu.domain.model.TemaCuriosidad
import kotlin.random.Random

/**
 * Repository de la API externa. Esta totalmente aislado: no conoce Room, la sesion
 * ni los microservicios propios, por lo que un fallo de la API externa no afecta al resto de la app.
 */
interface TriviaRepository {
    suspend fun obtenerPregunta(): Result<PreguntaTrivia>
}

/**
 * Arma cada pregunta con el resumen en español de Wikipedia de un tema elegido al azar.
 * Guarda en memoria los resumenes ya descargados para no repetir consultas (Wikipedia limita la frecuencia).
 */
class TriviaRepositoryImpl(
    private val remoto: WikipediaRemoteDataSource,
    private val temas: List<TemaCuriosidad> = Curiosidades.temas,
    private val aleatorio: Random = Random.Default
) : TriviaRepository {

    private val cache = mutableMapOf<String, WikipediaResumenDto>()
    private var ultimoTema: TemaCuriosidad? = null

    override suspend fun obtenerPregunta(): Result<PreguntaTrivia> {
        // no repite el mismo tema dos veces seguidas
        val tema = temas.filter { it != ultimoTema }.ifEmpty { temas }.random(aleatorio)

        val resumen = cache[tema.articulo] ?: remoto.obtenerResumen(tema.articulo)
            .getOrElse { return Result.failure(it) }
            .also { cache[tema.articulo] = it }

        val dato = Curiosidades.primeraFrase(resumen.resumen.orEmpty())
        if (dato.isBlank()) {
            return Result.failure(ErrorRed.Http(200, "Wikipedia no entregó un resumen para \"${tema.nombre}\"."))
        }
        ultimoTema = tema
        return Result.success(
            PreguntaTrivia(
                categoria = tema.categoria,
                pista = Curiosidades.ocultar(dato, tema.palabrasOcultas),
                pregunta = Curiosidades.PREGUNTA,
                respuestaCorrecta = tema.nombre,
                respuestasIncorrectas = Curiosidades.alternativasIncorrectas(tema, aleatorio),
                imagenUrl = resumen.miniatura?.url,
                datoCompleto = dato,
                imagenProporcion = resumen.miniatura?.let { img ->
                    val ancho = img.ancho ?: 0
                    val alto = img.alto ?: 0
                    if (ancho > 0 && alto > 0) ancho.toFloat() / alto else null
                }
            )
        )
    }
}
