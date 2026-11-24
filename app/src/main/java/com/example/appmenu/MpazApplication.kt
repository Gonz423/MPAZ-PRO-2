package com.example.appmenu

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.example.appmenu.data.remote.external.UserAgentInterceptor
import com.example.appmenu.di.AppContainer
import okhttp3.OkHttpClient

/**
 * Application de la app: crea el contenedor de dependencias una sola vez y
 * configura Coil (carga de imagenes) para que se identifique ante Wikimedia.
 */
class MpazApplication : Application(), ImageLoaderFactory {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }

    override fun newImageLoader(): ImageLoader =
        ImageLoader.Builder(this)
            .okHttpClient { OkHttpClient.Builder().addInterceptor(UserAgentInterceptor()).build() }
            .crossfade(true)
            .build()
}
