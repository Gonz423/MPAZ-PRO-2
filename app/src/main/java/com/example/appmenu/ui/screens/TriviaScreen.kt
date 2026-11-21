package com.example.appmenu.ui.screens

import android.os.Build
import android.text.Html
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appmenu.network.TriviaQuestion
import com.example.appmenu.ui.theme.*
import com.example.appmenu.viewmodel.TriviaViewModel

// PANTALLA: TRIVIA DE CIENCIAS NATURALES (OPEN TRIVIA DB) - mpaz pro 2.°

@Composable
fun TriviaScreen(
    triviaViewModel: TriviaViewModel = viewModel()
) {
    val pregunta  by triviaViewModel.pregunta.collectAsState()
    val cargando  by triviaViewModel.cargando.collectAsState()
    val error     by triviaViewModel.error.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoApp)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // cabecera
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Science,
                    contentDescription = null,
                    tint = ColorCiencias,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Dato del día · Ciencias",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoOscuro
                )
            }
            // boton recargar
            IconButton(onClick = { triviaViewModel.cargarPregunta() }) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Nueva pregunta",
                    tint = ColorCiencias
                )
            }
        }

        when {
            // estado de carga
            cargando -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = ColorCiencias)
                }
            }

            // estado de error
            error != null -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "⚠️ ${error ?: "Error desconocido"}",
                            fontSize = 14.sp,
                            color = Color(0xFFC62828),
                            textAlign = TextAlign.Center
                        )
                        Button(
                            onClick = { triviaViewModel.cargarPregunta() },
                            colors = ButtonDefaults.buttonColors(containerColor = ColorCiencias),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Reintentar", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // pregunta cargada
            pregunta != null -> {
                TarjetaPreguntaTrivia(
                    pregunta = pregunta!!,
                    onNuevaPregunta = { triviaViewModel.cargarPregunta() }
                )
            }
        }
    }
}

// tarjeta con la pregunta y las opciones de respuesta
@Composable
private fun TarjetaPreguntaTrivia(
    pregunta: TriviaQuestion,
    onNuevaPregunta: () -> Unit
) {
    // decodificar HTML entities del texto
    @Suppress("DEPRECATION")
    fun decodificarHtml(texto: String): String =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            Html.fromHtml(texto, Html.FROM_HTML_MODE_LEGACY).toString()
        else
            Html.fromHtml(texto).toString()

    // mezclar las opciones: 1 correcta + 3 incorrectas en orden aleatorio
    val opcionesMezcladas = remember(pregunta) {
        (pregunta.incorrectAnswers + pregunta.correctAnswer).shuffled()
    }

    var seleccionada     by remember { mutableStateOf<String?>(null) }
    var mostrarResultado by remember { mutableStateOf(false) }

    // badge de dificultad
    val colorDificultad = when (pregunta.difficulty) {
        "easy"   -> Color(0xFF2E7D32)
        "medium" -> Color(0xFFF9A825)
        else     -> Color(0xFFC62828)
    }
    val textoDificultad = when (pregunta.difficulty) {
        "easy"   -> "Fácil"
        "medium" -> "Medio"
        else     -> "Difícil"
    }

    // tarjeta de pregunta
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = BlancoTarjeta),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // fila de metadatos: categoria y dificultad
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = decodificarHtml(pregunta.category),
                    fontSize = 11.sp,
                    color = GrisTexto,
                    fontWeight = FontWeight.Medium
                )
                Surface(
                    color = colorDificultad.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = textoDificultad,
                        color = colorDificultad,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            // texto de la pregunta
            Text(
                text = decodificarHtml(pregunta.question),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextoOscuro,
                lineHeight = 22.sp
            )
        }
    }

    // opciones de respuesta como botones
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        opcionesMezcladas.forEach { opcion ->
            val esCorrecta   = opcion == pregunta.correctAnswer
            val estaElegida  = opcion == seleccionada

            val colorBoton = when {
                !mostrarResultado       -> if (estaElegida) ColorCiencias else BlancoTarjeta
                esCorrecta              -> Color(0xFF2E7D32)
                estaElegida             -> Color(0xFFC62828)
                else                    -> BlancoTarjeta
            }
            val colorTexto = when {
                !mostrarResultado && estaElegida -> Color.White
                mostrarResultado && (esCorrecta || estaElegida) -> Color.White
                else -> TextoOscuro
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = colorBoton),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                onClick = {
                    if (!mostrarResultado) {
                        seleccionada = opcion
                        mostrarResultado = true
                    }
                }
            ) {
                Text(
                    text = decodificarHtml(opcion),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = colorTexto,
                    modifier = Modifier.padding(14.dp)
                )
            }
        }
    }

    // mensaje de resultado
    if (mostrarResultado) {
        val esCorrecto = seleccionada == pregunta.correctAnswer
        Surface(
            color = if (esCorrecto) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (esCorrecto) "✅ ¡Correcto! Muy bien." else "❌ Incorrecto. La correcta era: ${Html.fromHtml(pregunta.correctAnswer, Html.FROM_HTML_MODE_LEGACY)}",
                color = if (esCorrecto) Color(0xFF2E7D32) else Color(0xFFC62828),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(14.dp)
            )
        }
    }

    // boton nueva pregunta
    Button(
        onClick = {
            seleccionada = null
            mostrarResultado = false
            onNuevaPregunta()
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = ColorCiencias)
    ) {
        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text("Nueva pregunta", fontSize = 15.sp, fontWeight = FontWeight.Bold)
    }
}
