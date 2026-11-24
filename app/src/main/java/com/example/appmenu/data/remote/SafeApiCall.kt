package com.example.appmenu.data.remote

import com.example.appmenu.domain.model.ErrorRed
import com.google.gson.Gson
import com.google.gson.JsonObject
import retrofit2.HttpException
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

/**
 * Ejecuta una llamada de red y la convierte en Result, clasificando el error en ErrorRed.
 * Nunca deja escapar excepciones de red hacia el ViewModel o la UI.
 * CancellationException se relanza para no romper la cancelacion de corrutinas.
 */
suspend fun <T> safeApiCall(llamada: suspend () -> T): Result<T> = try {
    Result.success(llamada())
} catch (e: CancellationException) {
    throw e
} catch (e: ErrorRed) {
    Result.failure(e)
} catch (e: IOException) {
    Result.failure(ErrorRed.SinConexion())
} catch (e: HttpException) {
    val detalle = leerDetalle(e)
    Result.failure(
        if (e.code() == 401) ErrorRed.NoAutorizado(detalle) else ErrorRed.Http(e.code(), detalle)
    )
} catch (e: Exception) {
    Result.failure(ErrorRed.Desconocido(e))
}

/** Extrae el campo "detail" del ProblemDetail (RFC 7807) que devuelven los microservicios. */
private fun leerDetalle(e: HttpException): String? = try {
    val cuerpo = e.response()?.errorBody()?.string()
    if (cuerpo.isNullOrBlank()) null
    else Gson().fromJson(cuerpo, JsonObject::class.java)?.get("detail")?.takeIf { !it.isJsonNull }?.asString
} catch (_: Exception) {
    null
}
