package com.example.appmenu.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appmenu.model.ActividadesManager
import com.example.appmenu.ui.theme.*

// Modelo interno para preguntas y alternativas barajadas
private data class ItemPregunta(
    val enunciado: String,
    val opciones: List<String>,
    val respuestaCorrectaIndex: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActividadInteractivaScreen(
    ejercicioId: String? = null,
    onVolver: () -> Unit = {},
    onFinalizarActividad: () -> Unit = {}
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val todosLosEjercicios by ActividadesManager.ejercicios.collectAsState()

    val ejercicio = remember(todosLosEjercicios, ejercicioId) {
        todosLosEjercicios.find { it.id == ejercicioId } ?: todosLosEjercicios.firstOrNull()
    }

    // [CORRECCIÓN DUPLICADOS]: Extraemos todas las preguntas empaquetadas en este ejercicio único
    // y aplicamos orden ALEATORIO (.shuffled()) a las preguntas y a sus alternativas
    val listaPreguntas = remember(ejercicio?.id) {
        val textoPregunta = ejercicio?.pregunta ?: ""
        if (textoPregunta.contains(";;;")) {
            // Viene del formulario dinámico con múltiples preguntas
            val items = textoPregunta.split("|||").mapNotNull { bloque ->
                val partes = bloque.split(";;;")
                if (partes.size >= 4) {
                    val enunciado = partes[0]
                    val correcta = partes[1]
                    val listaOpciones = listOf(partes[1], partes[2], partes[3]).shuffled()
                    ItemPregunta(
                        enunciado = enunciado,
                        opciones = listaOpciones,
                        respuestaCorrectaIndex = listaOpciones.indexOf(correcta)
                    )
                } else null
            }
            if (items.isNotEmpty()) items.shuffled() else listOf(
                ItemPregunta(
                    enunciado = ejercicio?.pregunta ?: "¿Cuál es la respuesta correcta?",
                    opciones = ejercicio?.opciones ?: listOf("Opción 1", "Opción 2", "Opción 3"),
                    respuestaCorrectaIndex = ejercicio?.respuestaCorrectaIndex ?: 0
                )
            )
        } else {
            // Ejercicio estándar predefinido
            listOf(
                ItemPregunta(
                    enunciado = ejercicio?.pregunta ?: "¿Cuál es la respuesta correcta?",
                    opciones = ejercicio?.opciones ?: listOf("Opción 1", "Opción 2", "Opción 3"),
                    respuestaCorrectaIndex = ejercicio?.respuestaCorrectaIndex ?: 0
                )
            )
        }
    }

    // Control de avance por cada pregunta barajada
    var indicePregunta by remember(ejercicio?.id) { mutableIntStateOf(0) }
    val preguntaActual = listaPreguntas.getOrNull(indicePregunta) ?: listaPreguntas.firstOrNull()

    val colorMateria = when (ejercicio?.materiaId) {
        "matematica" -> ColorMatematica
        "ciencias"   -> ColorCiencias
        "historia"   -> ColorHistoria
        else         -> ColorLenguaje
    }

    var opcionSeleccionada by remember(indicePregunta) { mutableStateOf<Int?>(null) }
    var mostrarExito by remember { mutableStateOf(false) }
    var mensajeError by remember(indicePregunta) { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Guía #${ejercicio?.codigoGuia ?: "1001"}",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorMateria,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(FondoApp)
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Barra de progreso según avance de preguntas
                val totalPreguntas = listaPreguntas.size.coerceAtLeast(1)
                val progreso = if (mostrarExito) 1f else ((indicePregunta + 1).toFloat() / totalPreguntas.toFloat())
                LinearProgressIndicator(
                    progress = { progreso },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = colorMateria,
                    trackColor = Color(0xFFE0E0E0)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = BlancoTarjeta),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = ejercicio?.titulo ?: "Actividad Interactiva",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextoOscuro
                            )
                            if (totalPreguntas > 1) {
                                Surface(
                                    color = colorMateria.copy(alpha = 0.12f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "${indicePregunta + 1} de $totalPreguntas",
                                        color = colorMateria,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            text = ejercicio?.objetivoAprendizaje ?: "Objetivo de Aprendizaje",
                            fontSize = 12.sp,
                            color = GrisTexto
                        )
                    }
                }

                // Enunciado aleatorio actual
                Text(
                    text = preguntaActual?.enunciado ?: "¿Cuál es la respuesta correcta?",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoOscuro
                )

                Text(
                    text = "Selecciona una opción y presiona 'Responder':",
                    fontSize = 13.sp,
                    color = GrisTexto
                )

                // Renderiza dinámicamente las alternativas barajadas de la pregunta
                val opciones = preguntaActual?.opciones ?: listOf("Opción 1", "Opción 2", "Opción 3")

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    opciones.forEachIndexed { index, opcionTexto ->
                        val seleccionado = opcionSeleccionada == index
                        val interactionSource = remember { MutableInteractionSource() }
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    interactionSource = interactionSource,
                                    indication = null
                                ) {
                                    opcionSeleccionada = index
                                    mensajeError = null
                                }
                                .border(
                                    width = if (seleccionado) 2.dp else 1.dp,
                                    color = if (seleccionado) colorMateria else Color(0xFFE2E8F0),
                                    shape = RoundedCornerShape(14.dp)
                                ),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (seleccionado) colorMateria.copy(alpha = 0.08f) else BlancoTarjeta
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = seleccionado,
                                    onClick = {
                                        opcionSeleccionada = index
                                        mensajeError = null
                                    },
                                    colors = RadioButtonDefaults.colors(selectedColor = colorMateria)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = opcionTexto,
                                    fontSize = 14.sp,
                                    fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Normal,
                                    color = TextoOscuro
                                )
                            }
                        }
                    }
                }

                mensajeError?.let { msg ->
                    Surface(
                        color = Color(0xFFFFEBEE),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = msg,
                            color = Color(0xFFC62828),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(12.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        if (opcionSeleccionada == null) {
                            mensajeError = "Por favor selecciona una alternativa antes de responder."
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        } else {
                            val indiceCorrecto = preguntaActual?.respuestaCorrectaIndex ?: 0
                            if (opcionSeleccionada == indiceCorrecto) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                mensajeError = null

                                if (indicePregunta + 1 < listaPreguntas.size) {
                                    Toast.makeText(context, "¡Correcto! Siguiente pregunta...", Toast.LENGTH_SHORT).show()
                                    indicePregunta++
                                    opcionSeleccionada = null
                                } else {
                                    ejercicio?.let { ActividadesManager.marcarCompletado(it.id) }
                                    mostrarExito = true
                                }
                            } else {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                mensajeError = "¡Casi! Revisa bien la pregunta e inténtalo otra vez."
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colorMateria)
                ) {
                    Text("Responder", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }

            AnimatedVisibility(
                visible = mostrarExito,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut(),
                modifier = Modifier.align(Alignment.Center)
            ) {
                AlertDialog(
                    onDismissRequest = {},
                    title = null,
                    text = {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(70.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFFF9C4)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Estrella",
                                    tint = Color(0xFFFBC02D),
                                    modifier = Modifier.size(46.dp)
                                )
                            }

                            Text(
                                text = "¡Muy bien!",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextoOscuro
                            )

                            Text(
                                text = "Has completado la actividad con éxito.\n¡Sigue así!",
                                fontSize = 14.sp,
                                color = GrisTexto,
                                textAlign = TextAlign.Center
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                mostrarExito = false
                                onFinalizarActividad()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = colorMateria)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Continuar", fontWeight = FontWeight.Bold)
                        }
                    },
                    shape = RoundedCornerShape(20.dp),
                    containerColor = Color.White
                )
            }
        }
    }
}