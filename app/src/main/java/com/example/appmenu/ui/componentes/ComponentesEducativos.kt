package com.example.appmenu.ui.componentes

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appmenu.model.MetricaDocente
import com.example.appmenu.model.ProgresoAsignatura
import com.example.appmenu.model.TarjetaAsignatura
import com.example.appmenu.ui.theme.GrisTexto
import com.example.appmenu.ui.theme.TextoOscuro

// COMPONENTES MODULARES Y REUTILIZABLES - ESTILO MPAZ PRO 2.º

// Banner de Saludo para el Estudiante (avatar + mensaje motivacional)

@Composable
fun BannerSaludoEstudiante(
    nombre: String = "Martina",
    mensaje: String = "¿Qué aprenderemos hoy?",
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar infantil
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFE0B2)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Face,
                    contentDescription = "Avatar de $nombre",
                    tint = Color(0xFFE65100),
                    modifier = Modifier.size(34.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = "¡Hola, $nombre!",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoOscuro
                )
                Text(
                    text = mensaje,
                    fontSize = 14.sp,
                    color = GrisTexto
                )
            }
        }
    }
}

// Tarjeta Modular de Asignatura (Lenguaje, Matematica, Ciencias, Historia)
// Su color, icono y texto vienen de ModeloAsignatura

@Composable
fun TarjetaAsignaturaItem(
    tarjeta: TarjetaAsignatura,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(130.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = tarjeta.color),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Contenedor semitransparente para el ícono
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = tarjeta.icono,
                    contentDescription = tarjeta.nombre,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = tarjeta.nombre,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = tarjeta.actividadesTexto,
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 12.sp
                )
            }
        }
    }
}

// Tarjeta de metrica para panel de docentes

@Composable
fun TarjetaMetricaItem(
    metrica: MetricaDocente,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(86.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = metrica.fondoColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = metrica.valor,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = metrica.textoColor
            )
            Text(
                text = metrica.etiqueta,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = metrica.textoColor.copy(alpha = 0.8f)
            )
        }
    }
}

// Fila con barra de progreso animada por asignatura

@Composable
fun FilaProgresoAsignatura(
    progreso: ProgresoAsignatura,
    modifier: Modifier = Modifier
) {
    val animProgress by animateFloatAsState(
        targetValue = progreso.porcentaje / 100f,
        label = "Progreso ${progreso.asignatura}"
    )

    Column(modifier = modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = progreso.asignatura,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextoOscuro
            )
            Text(
                text = "${progreso.porcentaje}%",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = GrisTexto
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { animProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = progreso.color,
            trackColor = progreso.color.copy(alpha = 0.2f)
        )
    }
}
