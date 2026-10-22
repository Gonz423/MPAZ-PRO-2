package com.example.appmenu.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.RadioButtonUnchecked
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
import com.example.appmenu.ui.theme.*

// pantalla: mi progreso mockup screen 10 mpaz pro 2.º

@Composable
fun ProgresoScreen(
    onAbrirCuaderno: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoApp)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // cabecera del estudiante
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = BlancoTarjeta),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFE0B2)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Face,
                        contentDescription = "Avatar",
                        tint = Color(0xFFE65100),
                        modifier = Modifier.size(34.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = "Martina González",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextoOscuro
                    )
                    Text(
                        text = "2.° Básico · Escuela Marcela Paz",
                        fontSize = 13.sp,
                        color = GrisTexto
                    )
                }
            }
        }

        // barras de progreso por asignatura
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = BlancoTarjeta),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Progreso por Asignatura",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoOscuro
                )

                BarraAsignaturaProgreso("Lenguaje y Comunicación", 75, ColorLenguaje)
                BarraAsignaturaProgreso("Matemática", 60, ColorMatematica)
                BarraAsignaturaProgreso("Ciencias Naturales", 50, ColorCiencias)
                BarraAsignaturaProgreso("Historia, Geografía y Cs. Sociales", 80, ColorHistoria)
            }
        }

        // ultimas actividades realizadas
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = BlancoTarjeta),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Últimas actividades",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoOscuro
                )

                FilaUltimaActividad("Lectura y comprensión", "10/10", completado = true)
                FilaUltimaActividad("Números hasta 100", "8/10", completado = true)
                FilaUltimaActividad("Los seres vivos", "6/10", completado = false)
            }
        }

        // acceso a cuaderno de tareas y notas room
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onAbrirCuaderno() },
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBBDEFB))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(AzulBarraSuperior),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Book, contentDescription = null, tint = Color.White)
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Mi Cuaderno de Tareas",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = AzulBarraSuperior
                    )
                    Text(
                        text = "Observaciones y notas de asignaturas y cursos",
                        fontSize = 12.sp,
                        color = GrisTexto
                    )
                }
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = AzulBarraSuperior)
            }
        }
    }
}

@Composable
fun BarraAsignaturaProgreso(
    nombre: String,
    porcentaje: Int,
    color: Color
) {
    val animProgress by animateFloatAsState(
        targetValue = porcentaje / 100f,
        label = "Progreso $nombre"
    )

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(nombre, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextoOscuro)
            Text("$porcentaje%", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = color)
        }
        LinearProgressIndicator(
            progress = { animProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = color.copy(alpha = 0.15f)
        )
    }
}

@Composable
fun FilaUltimaActividad(
    titulo: String,
    puntaje: String,
    completado: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (completado) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                contentDescription = null,
                tint = if (completado) Color(0xFF2E7D32) else GrisTexto,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(titulo, fontSize = 14.sp, color = TextoOscuro)
        }
        Text(
            text = puntaje,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = if (completado) Color(0xFF2E7D32) else GrisTexto
        )
    }
}
