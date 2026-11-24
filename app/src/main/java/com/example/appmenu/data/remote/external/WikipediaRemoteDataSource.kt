package com.example.appmenu.data.remote.external

import com.example.appmenu.data.remote.external.dto.WikipediaResumenDto
import com.example.appmenu.data.remote.safeApiCall

/** Unica puerta de entrada a Wikipedia. Devuelve Result y nunca lanza excepciones de red. */
open class WikipediaRemoteDataSource(private val api: WikipediaApi) {
    open suspend fun obtenerResumen(articulo: String): Result<WikipediaResumenDto> =
        safeApiCall { api.obtenerResumen(articulo) }
}
