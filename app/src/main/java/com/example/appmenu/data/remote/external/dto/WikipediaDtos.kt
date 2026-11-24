package com.example.appmenu.data.remote.external.dto

import com.google.gson.annotations.SerializedName

// DTOs de la API REST de Wikipedia en español (GET /api/rest_v1/page/summary/{titulo}).
// Solo los usa la capa data/remote/external.

data class WikipediaResumenDto(
    @SerializedName("title") val titulo: String? = null,
    @SerializedName("extract") val resumen: String? = null,
    @SerializedName("thumbnail") val miniatura: WikipediaImagenDto? = null
)

data class WikipediaImagenDto(
    @SerializedName("source") val url: String? = null,
    @SerializedName("width") val ancho: Int? = null,
    @SerializedName("height") val alto: Int? = null
)
