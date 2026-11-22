package com.example.appmenu.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.appmenu.R
import kotlinx.coroutines.delay

// pantalla: portada splash screen con imagen portada.png durante 3 segundos

@Composable
fun SplashScreen(
    onTiempoTerminado: () -> Unit
) {
    LaunchedEffect(Unit) {
        delay(3000)
        onTiempoTerminado()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .clickable { onTiempoTerminado() },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.portada),
            contentDescription = "Portada MPAZ PRO",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}
