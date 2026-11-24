package com.example.appmenu.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appmenu.ui.theme.*

// PANTALLA: FEEDBACK ANIMADO AL COMPLETAR UNA ACTIVIDAD - mpaz pro 2.°

@Composable
fun FeedbackActividadScreen(
    aciertos: Int = 2,
    total: Int = 3,
    tituloActividad: String = "Actividad",
    onContinuar: () -> Unit = {}
) {
    // calculo de nota y color de fondo segun puntaje
    val esBuenPuntaje = aciertos >= (total * 0.6).toInt().coerceAtLeast(1)
    val colorFondo = if (esBuenPuntaje) Color(0xFFE8F5E9) else Color(0xFFFFFDE7)
    val colorIcono  = if (esBuenPuntaje) Color(0xFF2E7D32) else Color(0xFFF9A825)
    val mensajePrincipal = if (esBuenPuntaje) "¡Muy bien!" else "¡Sigue practicando!"
    val nota = if (total > 0) (aciertos * 10) / total else 0

    // animacion de escala del icono (0f -> 1f al entrar)
    var animacionIniciada by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { animacionIniciada = true }

    val escala by animateFloatAsState(
        targetValue  = if (animacionIniciada) 1f else 0f,
        animationSpec = tween(durationMillis = 600),
        label = "EscalaIcono"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorFondo)
            .windowInsetsPadding(WindowInsets.safeDrawing),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
        ) {
            // icono animado grande con trofeo
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .scale(escala)
                    .clip(CircleShape)
                    .background(colorIcono.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = "Resultado",
                    tint = colorIcono,
                    modifier = Modifier.size(72.dp)
                )
            }

            // mensaje principal segun puntaje
            Text(
                text = mensajePrincipal,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (esBuenPuntaje) Color(0xFF1B5E20) else Color(0xFFF57F17),
                textAlign = TextAlign.Center
            )

            // titulo de la actividad completada
            Text(
                text = tituloActividad,
                fontSize = 15.sp,
                color = GrisTexto,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium
            )

            // texto de aciertos
            Text(
                text = "Respondiste $aciertos de $total correctamente",
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextoOscuro,
                textAlign = TextAlign.Center
            )

            // badge circular con la nota
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(colorIcono),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$nota/10",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Text(
                        text = "Nota",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // boton continuar
            Button(
                onClick = onContinuar,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (esBuenPuntaje) Color(0xFF2E7D32) else Color(0xFFF9A825)
                )
            ) {
                Text(
                    text = "Continuar",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
