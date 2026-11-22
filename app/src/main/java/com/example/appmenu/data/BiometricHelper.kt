package com.example.appmenu.data

import android.content.Context
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

// helper de autenticacion biometrica reconocimiento facial y huella - bonus track
object BiometricHelper {

    fun autenticarDocente(
        activity: FragmentActivity,
        onExito: () -> Unit,
        onError: (String) -> Unit = {}
    ) {
        val biometricManager = BiometricManager.from(activity)
        val autenticacionDisponible = biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or 
            BiometricManager.Authenticators.BIOMETRIC_WEAK or 
            BiometricManager.Authenticators.DEVICE_CREDENTIAL
        )

        when (autenticacionDisponible) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                // el dispositivo tiene biometria activa rostro o huella
                lanzarPrompt(activity, onExito, onError)
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                // no hay rostro ni huella configurados en el celular
                Toast.makeText(
                    activity,
                    "Dispositivo sin biometría registrada. Acceso alternativo concedido.",
                    Toast.LENGTH_LONG
                ).show()
                onExito()
            }
            else -> {
                // emulador o dispositivo sin hardware biometrico
                Toast.makeText(
                    activity,
                    "Hardware biométrico no disponible. Acceso docente concedido.",
                    Toast.LENGTH_SHORT
                ).show()
                onExito()
            }
        }
    }

    private fun lanzarPrompt(
        activity: FragmentActivity,
        onExito: () -> Unit,
        onError: (String) -> Unit
    ) {
        val executor = ContextCompat.getMainExecutor(activity)

        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                Toast.makeText(activity, "¡Identidad verificada exitosamente!", Toast.LENGTH_SHORT).show()
                onExito()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                if (errorCode != BiometricPrompt.ERROR_USER_CANCELED && 
                    errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                    Toast.makeText(activity, "Error biométrico: $errString", Toast.LENGTH_SHORT).show()
                }
                onError(errString.toString())
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Toast.makeText(activity, "Rostro o huella no reconocidos. Intente nuevamente.", Toast.LENGTH_SHORT).show()
            }
        }

        val prompt = BiometricPrompt(activity, executor, callback)

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Acceso Seguro al Panel Docente")
            .setSubtitle("Autenticación Biométrica (Reconocimiento Facial / Huella)")
            .setDescription("Verifique su identidad como docente de la Escuela Marcela Paz para consultar registros pedagógicos.")
            .setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or 
                BiometricManager.Authenticators.BIOMETRIC_WEAK or 
                BiometricManager.Authenticators.DEVICE_CREDENTIAL
            )
            .build()

        prompt.authenticate(promptInfo)
    }
}
