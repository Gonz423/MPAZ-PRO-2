package com.example.appmenu.data.remote.external

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Identifica la app ante Wikimedia. Su politica de uso rechaza (403) las peticiones
 * sin un User-Agent propio, tanto en la API como en las imagenes (upload/thumb.wikimedia.org).
 */
class UserAgentInterceptor(private val userAgent: String = USER_AGENT) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response =
        chain.proceed(chain.request().newBuilder().header("User-Agent", userAgent).build())

    companion object {
        const val USER_AGENT = "MpazPro2/1.0 (app educativa Android; proyecto academico DSY1105)"
    }
}
