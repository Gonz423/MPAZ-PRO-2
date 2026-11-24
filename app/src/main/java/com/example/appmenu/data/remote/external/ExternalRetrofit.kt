package com.example.appmenu.data.remote.external

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Capa de red de la API EXTERNA (Wikipedia en español), aislada de la de nuestros microservicios:
 * OkHttpClient propio, sin AuthInterceptor (nuestro JWT nunca se envia a terceros) y con sus timeouts.
 */
class ExternalRetrofit(baseUrl: String, registrarTrafico: Boolean) {

    private val cliente: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(8, TimeUnit.SECONDS)
        .readTimeout(8, TimeUnit.SECONDS)
        // la politica de uso de Wikimedia pide identificar la aplicacion con un User-Agent propio
        .addInterceptor(UserAgentInterceptor())
        .apply {
            if (registrarTrafico) {
                addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC })
            }
        }
        .build()

    val wikipediaApi: WikipediaApi by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(cliente)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WikipediaApi::class.java)
    }
}
