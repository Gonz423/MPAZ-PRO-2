package com.example.appmenu.network

import retrofit2.http.GET
import retrofit2.http.Query

// interfaz retrofit para la API Open Trivia Database - mpaz pro 2.°

interface TriviaApi {
    @GET("api.php")
    suspend fun obtenerPregunta(
        @Query("amount")   cantidad: Int    = 1,
        @Query("category") categoria: Int   = 17,   // 17 = Ciencias y Naturaleza
        @Query("type")     tipo: String     = "multiple"
    ): TriviaResponse
}
