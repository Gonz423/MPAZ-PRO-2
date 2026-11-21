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
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appmenu.model.ConfiguracionEducativa
import com.example.appmenu.ui.componentes.BannerSaludoEstudiante
import com.example.appmenu.ui.componentes.TarjetaAsignaturaItem
import com.example.appmenu.ui.theme.*
import com.example.appmenu.viewmodel.EstadoViewModel
import kotlinx.coroutines.delay

// pantalla principal del estudiante (dashboard con asignaturas y asistencia de lectura)

@Composable
fun EstudianteScreen(
    estadoViewModel: EstadoViewModel = viewModel(),
    onSeleccionarAsignatura: (String) -> Unit = {},
    onAbrirTarea: (String) -> Unit = {},
    onNavegarTrivia: () -> Unit = {}
) {
    val context = LocalContext.current

    // estados del viewmodel
    val estadoGuardado by estadoViewModel.estadoActivado.collectAsState()
    val cargando by estadoViewModel.cargando.collectAsState()
    val guardadoCorrectamente by estadoViewModel.guardadoCorrectamente.collectAsState()
    val mensajeError by estadoViewModel.mensajeError.collectAsState()

    var estadoActivado by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(cargando, estadoGuardado) {
        if (!cargando) {
            estadoActivado = estadoGuardado
        }
    }

    LaunchedEffect(guardadoCorrectamente) {
        if (guardadoCorrectamente) {
            delay(2000)
            estadoViewModel.ocultarMensajeGuardado()
        }
    }

    // color animado para el boton de modo guiado
    val colorBotonModo by animateColorAsState(
        targetValue = if (estadoActivado) Color(0xFF2E7D32) else AzulBarraSuperior,
        label = "ColorBotonModo"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoApp)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // banner de saludo
        BannerSaludoEstudiante(
            nombre = "Martina",
            mensaje = "¿Qué aprenderemos hoy?"
        )

        // ===================== TAREA DEL DIA =====================
        // card destacada con la tarea pendiente de hoy
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onAbrirTarea("len_2")
                },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
            border = BorderStroke(1.dp, Color(0xFFFFCC80)),
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
                        .background(Color(0xFFFFCC80)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Assignment,
                        contentDescription = null,
                        tint = Color(0xFFE65100),
                        modifier = Modifier.size(26.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                // textos de la tarea
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "📌 Tarea pendiente de hoy",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE65100)
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
                // badge "¡Vence hoy!"
                Surface(
                    color = Color(0xFFE65100),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "¡Vence hoy!",
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

        // grilla modular de 4 asignaturas
        // el orden y colores se cambian directamente en moduloseducativos.kt
        val listaAsignaturas = ConfiguracionEducativa.asignaturas

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

        // control apoyo didactico
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = BlancoTarjeta),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (estadoActivado) Icons.Default.Hearing else Icons.Default.MenuBook,
                        contentDescription = null,
                        tint = if (estadoActivado) Color(0xFF2E7D32) else AzulBarraSuperior
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Lectura Guiada y Apoyo con Audio",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextoOscuro
                    )
                }

                Text(
                    text = if (estadoActivado) {
                        "Modo Asistido ACTIVO: Instrucciones habladas y apoyos visuales activados para 2.° Básico."
                    } else {
                        "Modo Estándar: Lectura directa sin asistencia sonora."
                    },
                    fontSize = 13.sp,
                    color = GrisTexto
                )

                Button(
                    onClick = {
                        val nuevoEstado = !estadoActivado
                        estadoActivado = nuevoEstado
                        estadoViewModel.guardarEstado(nuevoEstado)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colorBotonModo),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (estadoActivado) "Desactivar Asistencia" else "Activar Asistencia Guiada",
                        fontWeight = FontWeight.Bold
                    )
                }

                AnimatedVisibility(
                    visible = guardadoCorrectamente,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Text(
                        text = "¡Preferencia guardada en el dispositivo!",
                        color = Color(0xFF2E7D32),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                AnimatedVisibility(
                    visible = mensajeError != null,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Text(
                        text = mensajeError ?: "",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

// compatibilidad hacia atras por si se llama con el nombre antiguo
@Composable
fun PantallaPrincipal(
    estadoViewModel: EstadoViewModel = viewModel(),
    onSeleccionarAsignatura: (String) -> Unit = {}
) {
    EstudianteScreen(estadoViewModel, onSeleccionarAsignatura)
}
