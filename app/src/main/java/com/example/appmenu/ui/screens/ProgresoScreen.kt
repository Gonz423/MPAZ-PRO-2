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
import com.example.appmenu.model.ActividadesManager
import com.example.appmenu.network.BackendClient
import com.example.appmenu.network.ResultadoDto
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
    onAbrirCuaderno: () -> Unit = {}
) {
    // observamos en tiempo real todos los ejercicios y actividades
    val todosLosEjercicios by ActividadesManager.ejercicios.collectAsState()

    // estado sincronizado con el microservicio ms-progreso
    var resultadosBackend by remember { mutableStateOf<List<ResultadoDto>>(emptyList()) }
    var backendConectado by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        try {
            val lista = BackendClient.progresoApi.listarResultados(estudianteId = 1L)
            if (lista.isNotEmpty()) {
                resultadosBackend = lista
                backendConectado = true
                // Sincronizar hacia ActividadesManager los resultados del backend
                lista.forEach { res ->
                    todosLosEjercicios.find {
                        it.materiaId.equals(res.materiaId, ignoreCase = true) ||
                        (it.codigoGuia.toLongOrNull() == res.ejercicioId)
                    }?.let { ej ->
                        if (!ej.completado) {
                            ActividadesManager.marcarCompletado(ej.id, res.nota)
                        }
                    }
                }
            }
        } catch (_: Exception) {
            backendConectado = false
        }
    }

    // calculo dinamico reactivo del porcentaje de progreso por asignatura
    fun calcularPorcentaje(materiaId: String): Int {
        val ejercicios = todosLosEjercicios.filter { it.materiaId.equals(materiaId, ignoreCase = true) }
        if (ejercicios.isEmpty()) return 0
        val completados = ejercicios.count { it.completado }
        return ((completados.toFloat() / ejercicios.size.toFloat()) * 100).toInt()
    }

    val pctLenguaje = remember(todosLosEjercicios) { calcularPorcentaje("lenguaje") }
    val pctMatematica = remember(todosLosEjercicios) { calcularPorcentaje("matematica") }
    val pctCiencias = remember(todosLosEjercicios) { calcularPorcentaje("ciencias") }
    val pctHistoria = remember(todosLosEjercicios) { calcularPorcentaje("historia") }
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
                        text = "Observaciones",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = AzulBarraSuperior
                    )
                    Text(
                        text = "Notas y observaciones de asignaturas y cursos",
                        fontSize = 12.sp,
                        color = GrisTexto
                    )
                }
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = AzulBarraSuperior)
            }
        }
    }
}
