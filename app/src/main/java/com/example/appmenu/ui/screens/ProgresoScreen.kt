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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appmenu.di.AppViewModelProvider
import com.example.appmenu.viewmodel.ProgresoViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appmenu.ui.componentes.BarraAsignaturaProgreso
import com.example.appmenu.ui.componentes.FilaUltimaActividad
import com.example.appmenu.ui.theme.*

// pantalla: mi progreso mockup screen 10 mpaz pro 2.º

@Composable
fun ProgresoScreen(
    viewModel: ProgresoViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    // el ViewModel combina el avance local con los resultados guardados en ms-progreso
    val todosLosEjercicios by viewModel.ejercicios.collectAsStateWithLifecycle()
    val porcentajes by viewModel.porcentajes.collectAsStateWithLifecycle()

    val pctLenguaje = porcentajes["lenguaje"] ?: 0
    val pctMatematica = porcentajes["matematica"] ?: 0
    val pctCiencias = porcentajes["ciencias"] ?: 0
    val pctHistoria = porcentajes["historia"] ?: 0
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoApp)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
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

                BarraAsignaturaProgreso("Lenguaje y Comunicación", pctLenguaje, ColorLenguaje)
                BarraAsignaturaProgreso("Matemática", pctMatematica, ColorMatematica)
                BarraAsignaturaProgreso("Ciencias Naturales", pctCiencias, ColorCiencias)
                BarraAsignaturaProgreso("Historia, Geografía y Cs. Sociales", pctHistoria, ColorHistoria)
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Últimas actividades",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextoOscuro
                    )
                    Surface(
                        color = Color(0xFFE8F5E9),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "⭐ Cuaderno al día",
                            color = Color(0xFF2E7D32),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                val ejerciciosCompletados = remember(todosLosEjercicios) {
                    todosLosEjercicios.filter { it.completado }
                }
                if (ejerciciosCompletados.isNotEmpty()) {
                    ejerciciosCompletados.takeLast(4).reversed().forEach { ej ->
                        val (nombreMateria, colorMateria) = when (ej.materiaId) {
                            "matematica" -> Pair("Matemática", ColorMatematica)
                            "ciencias"   -> Pair("Ciencias Naturales", ColorCiencias)
                            "historia"   -> Pair("Historia y Geografía", ColorHistoria)
                            else         -> Pair("Lenguaje y Comunicación", ColorLenguaje)
                        }
                        val notaTexto = if (ej.nota > 0) "${ej.nota}/10" else "10/10"
                        FilaUltimaActividad(
                            titulo = ej.titulo,
                            asignatura = nombreMateria,
                            colorAsignatura = colorMateria,
                            nota = notaTexto,
                            completado = true
                        )
                    }
                } else {
                    FilaUltimaActividad(
                        titulo = "Sin actividades completadas aún",
                        asignatura = "2.° Básico",
                        colorAsignatura = GrisTexto,
                        nota = "0/10",
                        completado = false
                    )
                }
            }
        }
    }
}
