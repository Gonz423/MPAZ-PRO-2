package com.example.appmenu.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

// cliente centralizado para los microservicios Spring Boot - mpaz pro 2.°

object BackendClient {

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(4, TimeUnit.SECONDS)
        .readTimeout(4, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    // ms-progreso (puerto 8083)
    val progresoApi: ProgresoApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BackendConfig.URL_PROGRESO)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ProgresoApiService::class.java)
    }

    // ms-contenidos (puerto 8082)
    val contenidosApi: ContenidosApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BackendConfig.URL_CONTENIDOS)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ContenidosApiService::class.java)
    }
}
