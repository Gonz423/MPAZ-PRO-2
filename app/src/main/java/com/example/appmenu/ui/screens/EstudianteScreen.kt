package com.example.appmenu.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.appmenu.di.AppViewModelProvider
import com.example.appmenu.viewmodel.ContenidosViewModel
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appmenu.ui.model.ConfiguracionEducativa
import com.example.appmenu.ui.componentes.BannerSaludoEstudiante
import com.example.appmenu.ui.componentes.TarjetaAsignaturaItem
import com.example.appmenu.ui.theme.*
import com.example.appmenu.viewmodel.EstadoViewModel
import kotlinx.coroutines.delay

// pantalla principal del estudiante (tarea del dia, asignaturas y desafio cientifico)

@Composable
fun EstudianteScreen(
    onSeleccionarAsignatura: (String) -> Unit = {},
    onAbrirTarea: (String) -> Unit = {},
    onNavegarTrivia: () -> Unit = {},
    contenidosViewModel: ContenidosViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val context = LocalContext.current

    val sesion by contenidosViewModel.sesion.collectAsStateWithLifecycle()
    val nombreEstudiante = if (sesion.estaAutenticado && sesion.nombreCompleto.isNotBlank()) {
        sesion.nombreCompleto.split(" ").firstOrNull() ?: sesion.nombreCompleto
    } else {
        "Martina"
    }

    val todosLosEjercicios by contenidosViewModel.ejercicios.collectAsStateWithLifecycle()
    val tareaLen2Completada = remember(todosLosEjercicios) {
        todosLosEjercicios.find { it.id == "len_2" }?.completado == true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoApp)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // banner de saludo
        BannerSaludoEstudiante(
            nombre = nombreEstudiante,
            mensaje = "¿Qué aprenderemos hoy?"
        )

        // ===================== TAREA DEL DIA =====================
        // card destacada con la tarea pendiente o completada de hoy
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onAbrirTarea("len_2")
                },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (tareaLen2Completada) Color(0xFFE8F5E9) else Color(0xFFFFF3E0)
            ),
            border = BorderStroke(1.dp, if (tareaLen2Completada) Color(0xFFA5D6A7) else Color(0xFFFFCC80)),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // icono de tarea
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (tareaLen2Completada) Color(0xFFC8E6C9) else Color(0xFFFFCC80)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (tareaLen2Completada) Icons.Default.CheckCircle else Icons.Default.Assignment,
                        contentDescription = null,
                        tint = if (tareaLen2Completada) Color(0xFF2E7D32) else Color(0xFFE65100),
                        modifier = Modifier.size(26.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                // textos de la tarea
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (tareaLen2Completada) "🎉 ¡Tarea del día completada!" else "📌 Tarea pendiente de hoy",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (tareaLen2Completada) Color(0xFF2E7D32) else Color(0xFFE65100)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Juego de Rimas y Sonidos de Palabras · Lenguaje",
                        fontSize = 13.sp,
                        color = TextoOscuro,
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                // badge
                Surface(
                    color = if (tareaLen2Completada) Color(0xFF2E7D32) else Color(0xFFE65100),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (tareaLen2Completada) "¡10/10!" else "¡Vence hoy!",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                    )
                }
            }
        }
        // ===================== FIN TAREA DEL DIA =================

        // titulo de asignaturas y estado
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Tus Asignaturas",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextoOscuro
            )
            Text(
                text = "2.° Básico",
                fontSize = 13.sp,
                color = GrisTexto,
                fontWeight = FontWeight.Medium
            )
        }

        // grilla modular de 4 asignaturas con progreso dinamico y reactivo
        val listaAsignaturas = remember(todosLosEjercicios) {
            ConfiguracionEducativa.asignaturas.map { asig ->
                val ejercicios = todosLosEjercicios.filter { it.materiaId.equals(asig.id, ignoreCase = true) }
                val completadas = ejercicios.count { it.completado }
                val total = ejercicios.size
                val pct = if (total > 0) ((completadas.toFloat() / total.toFloat()) * 100).toInt() else 0
                asig.copy(
                    actividadesTexto = "$completadas de $total listas ($pct%)",
                    progreso = if (total > 0) completadas.toFloat() / total.toFloat() else 0f
                )
            }
        }

        // fila 1
        if (listaAsignaturas.size >= 2) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                TarjetaAsignaturaItem(
                    tarjeta = listaAsignaturas[0],
                    onClick = {
                        Toast.makeText(context, "Abriendo ${listaAsignaturas[0].nombre}", Toast.LENGTH_SHORT).show()
                        onSeleccionarAsignatura(listaAsignaturas[0].id)
                    },
                    modifier = Modifier.weight(1f)
                )
                TarjetaAsignaturaItem(
                    tarjeta = listaAsignaturas[1],
                    onClick = {
                        Toast.makeText(context, "Abriendo ${listaAsignaturas[1].nombre}", Toast.LENGTH_SHORT).show()
                        onSeleccionarAsignatura(listaAsignaturas[1].id)
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // fila 2
        if (listaAsignaturas.size >= 4) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                TarjetaAsignaturaItem(
                    tarjeta = listaAsignaturas[2],
                    onClick = {
                        Toast.makeText(context, "Abriendo ${listaAsignaturas[2].nombre}", Toast.LENGTH_SHORT).show()
                        onSeleccionarAsignatura(listaAsignaturas[2].id)
                    },
                    modifier = Modifier.weight(1f)
                )
                TarjetaAsignaturaItem(
                    tarjeta = listaAsignaturas[3],
                    onClick = {
                        Toast.makeText(context, "Abriendo ${listaAsignaturas[3].nombre}", Toast.LENGTH_SHORT).show()
                        onSeleccionarAsignatura(listaAsignaturas[3].id)
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // ===================== DESAFÍO CIENTÍFICO =====================
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavegarTrivia() },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F2F1)),
            border = BorderStroke(1.dp, Color(0xFF80CBC4)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF00897B)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Science,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "🔬 Desafío Científico · Curiosidades",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00695C)
                    )
                    Text(
                        text = "¿Cuánto sabes de la naturaleza? ¡Responde la trivia diaria!",
                        fontSize = 12.sp,
                        color = GrisTexto
                    )
                }
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = Color(0xFF00897B)
                )
            }
        }
        // ===================== FIN DESAFÍO CIENTÍFICO =================

    }
}

