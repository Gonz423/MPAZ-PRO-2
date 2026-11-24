package com.example.appmenu

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.FragmentActivity
import com.example.appmenu.ui.navigation.AppNavHost
import com.example.appmenu.ui.theme.AppMenuTheme

/**
 * Unica Activity de la app. Es FragmentActivity porque la autenticacion biometrica
 * (acceso rapido docente) lo requiere. Toda la navegacion vive en AppNavHost.
 */
class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppMenuTheme {
                AppNavHost(activity = this)
            }
        }
    }
}
