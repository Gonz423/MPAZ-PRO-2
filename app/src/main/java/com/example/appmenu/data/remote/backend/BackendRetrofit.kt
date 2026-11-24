package com.example.appmenu.data.remote.backend

import com.example.appmenu.data.remote.backend.api.ContenidosApi
import com.example.appmenu.data.remote.backend.api.ProgresoApi
import com.example.appmenu.data.remote.backend.api.UsuariosApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Capa de red de los microservicios PROPIOS (ms-usuarios, ms-contenidos, ms-progreso).
 * Tiene su propio OkHttpClient con AuthInterceptor; no comparte nada con la API externa.
 */
class BackendRetrofit(
    urlUsuarios: String,
    urlContenidos: String,
    urlProgreso: String,
    authInterceptor: AuthInterceptor,
    registrarTrafico: Boolean
) {
    private val cliente: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(4, TimeUnit.SECONDS)
        .readTimeout(6, TimeUnit.SECONDS)
        .addInterceptor(authInterceptor)
        .apply {
            if (registrarTrafico) {
                addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                    redactHeader("Authorization") // el JWT nunca se escribe en el log
                })
            }
        }
        .build()

    private fun retrofit(baseUrl: String): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(cliente)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val usuariosApi: UsuariosApi by lazy { retrofit(urlUsuarios).create(UsuariosApi::class.java) }
    val contenidosApi: ContenidosApi by lazy { retrofit(urlContenidos).create(ContenidosApi::class.java) }
    val progresoApi: ProgresoApi by lazy { retrofit(urlProgreso).create(ProgresoApi::class.java) }
}
