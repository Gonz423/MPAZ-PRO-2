package com.example.appmenu.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appmenu.ui.componentes.TarjetaPerfilSelector
import com.example.appmenu.ui.theme.AzulBarraSuperior

// PANTALLA DE SELECCION DE PERFIL

@Composable
fun SelectorPerfilScreen(
    onSeleccionarEstudiante: () -> Unit,
    onSeleccionarDocente: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(AzulBarraSuperior, Color(0xFF1565C0))
                )
            )
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Cabecera: nombre e institucion
        Text(
            text = "MPAZ PRO",
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White
        )
        Text(
            text = "Escuela Marcela Paz - 2 Basico",
            fontSize = 14.sp,
            color = Color.White.copy(alpha = 0.8f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = "Quien eres?",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White.copy(alpha = 0.9f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Tarjeta Estudiante
        TarjetaPerfilSelector(
            icono = Icons.Default.School,
            titulo = "Soy Estudiante",
            descripcion = "Accede a tus asignaturas y actividades",
            onClick = onSeleccionarEstudiante
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Tarjeta Docente
        TarjetaPerfilSelector(
            icono = Icons.Default.AccountCircle,
            titulo = "Soy Docente",
            descripcion = "Panel de seguimiento y registros",
            onClick = onSeleccionarDocente
        )
    }
}