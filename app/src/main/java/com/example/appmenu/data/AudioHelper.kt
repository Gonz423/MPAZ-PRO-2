package com.example.appmenu.data

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

// helper nativo de audio y sintesis de voz text-to-speech para 2.° basico
object AudioHelper {
    private var tts: TextToSpeech? = null
    private var inicializado = false

    fun inicializar(context: Context) {
        if (tts == null) {
            tts = TextToSpeech(context.applicationContext) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    val resultado = tts?.setLanguage(Locale("es", "CL"))
                    if (resultado == TextToSpeech.LANG_MISSING_DATA || resultado == TextToSpeech.LANG_NOT_SUPPORTED) {
                        tts?.setLanguage(Locale("es", "ES"))
                    }
                    inicializado = true
                }
            }
        }
    }

    fun hablar(texto: String) {
        if (inicializado && tts != null) {
            tts?.speak(texto, TextToSpeech.QUEUE_FLUSH, null, "MPAZ_TTS_ID")
        }
    }

    fun detener() {
        tts?.stop()
    }
}
