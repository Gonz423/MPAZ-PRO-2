package com.example.appmenu.ui.componentes

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appmenu.ui.theme.AzulBarraSuperior
import com.example.appmenu.ui.theme.TextoOscuro

// componente reutilizable: tarjeta de seleccion de perfil

@Composable
fun TarjetaPerfilSelector(
    icono: ImageVector,
    titulo: String,
    descripcion: String,
    onClick: () -> Unit
) {
    var presionado by remember { mutableStateOf(false) }

    // animacion de escala al presionar efecto tactil
    val escala by animateFloatAsState(
        targetValue = if (presionado) 0.97f else 1f,
        animationSpec = tween(100),
        label = "escala_tarjeta"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(escala)
            .clickable {
                presionado = true
                onClick()
            },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // icono con fondo redondeado
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = AzulBarraSuperior.copy(alpha = 0.12f),
                modifier = Modifier.size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icono,
                        contentDescription = titulo,
                        tint = AzulBarraSuperior,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }

            // texto: titulo y descripcion del perfil
            Column {
                Text(
                    text = titulo,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoOscuro
                )
                Text(
                    text = descripcion,
                    fontSize = 13.sp,
                    color = Color(0xFF78909C)
                )
            }
        }
    }
}