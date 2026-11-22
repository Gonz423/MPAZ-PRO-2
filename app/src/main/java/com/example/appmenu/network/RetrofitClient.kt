package com.example.appmenu.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// singleton retrofit apuntando a open trivia db - mpaz pro 2.°

object RetrofitClient {

    private const val BASE_URL = "https://opentdb.com/"

    // interceptor de log para depuracion en debug
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // instancia de la api de trivia
    val triviaApi: TriviaApi by lazy {
        retrofit.create(TriviaApi::class.java)
    }
}
