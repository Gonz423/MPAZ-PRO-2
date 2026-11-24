package com.example.appmenu.data.remote.external

import com.example.appmenu.data.remote.external.dto.WikipediaResumenDto
import retrofit2.http.GET
import retrofit2.http.Path

/** API publica de Wikipedia en español (sin clave). https://es.wikipedia.org/api/rest_v1/ */
interface WikipediaApi {
    @GET("api/rest_v1/page/summary/{titulo}")
    suspend fun obtenerResumen(@Path("titulo") titulo: String): WikipediaResumenDto
}
