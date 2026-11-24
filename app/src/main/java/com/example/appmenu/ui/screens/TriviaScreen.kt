package com.example.appmenu.ui.screens

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.appmenu.di.AppViewModelProvider
import com.example.appmenu.domain.model.PreguntaTrivia
import com.example.appmenu.ui.theme.*
import com.example.appmenu.viewmodel.TriviaViewModel

// PANTALLA: DESAFIO CIENTIFICO · CURIOSIDADES (API externa: Wikipedia en español) - mpaz pro 2.°

@Composable
fun TriviaScreen(
    triviaViewModel: TriviaViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val pregunta  by triviaViewModel.pregunta.collectAsState()
    val cargando  by triviaViewModel.cargando.collectAsState()
    val error     by triviaViewModel.error.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoApp)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
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
                    text = "Desafío Científico",
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

// tarjeta con la pista, la imagen y las alternativas de respuesta
@Composable
private fun TarjetaPreguntaTrivia(
    pregunta: PreguntaTrivia,
    onNuevaPregunta: () -> Unit
) {
    // mezclar las opciones: 1 correcta + 3 incorrectas en orden aleatorio
    val opcionesMezcladas = remember(pregunta) {
        (pregunta.respuestasIncorrectas + pregunta.respuestaCorrecta).shuffled()
    }

    // el estado de la respuesta se reinicia con cada pregunta nueva
    var seleccionada by remember(pregunta) { mutableStateOf<String?>(null) }
    val mostrarResultado = seleccionada != null

    // tarjeta de la pista
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
            Surface(
                color = ColorCiencias.copy(alpha = 0.12f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = pregunta.categoria,
                    color = ColorCiencias,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }

            // imagen del tema (apoyo visual para estudiantes que aun no leen con fluidez)
            if (pregunta.imagenUrl != null) {
                // la foto se muestra completa (Fit) en un recuadro con su misma proporcion:
                // sin recortes y sin bandas vacias; las fotos verticales se limitan a 280 dp de alto
                val proporcion = (pregunta.imagenProporcion ?: (4f / 3f)).coerceIn(0.5f, 2.5f)
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    AsyncImage(
                        model = pregunta.imagenUrl,
                        contentDescription = "Imagen de apoyo para adivinar",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .heightIn(max = 280.dp)
                            .aspectRatio(proporcion)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFFE8F5E9))
                    )
                }
            }

            Text(
                text = "🔍 Pista",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = GrisTexto
            )
            Text(
                text = pregunta.pista,
                fontSize = 15.sp,
                color = TextoOscuro,
                lineHeight = 21.sp
            )
            Text(
                text = pregunta.pregunta,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = TextoOscuro
            )
        }
    }

    // opciones de respuesta como botones
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        opcionesMezcladas.forEach { opcion ->
            val esCorrecta = opcion == pregunta.respuestaCorrecta
            val estaElegida = opcion == seleccionada

            val colorBoton = when {
                !mostrarResultado -> BlancoTarjeta
                esCorrecta -> Color(0xFF2E7D32)
                estaElegida -> Color(0xFFC62828)
                else -> BlancoTarjeta
            }
            val colorTexto = if (mostrarResultado && (esCorrecta || estaElegida)) Color.White else TextoOscuro

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = colorBoton),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                onClick = { if (!mostrarResultado) seleccionada = opcion }
            ) {
                Text(
                    text = opcion,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = colorTexto,
                    modifier = Modifier.padding(14.dp)
                )
            }
        }
    }

    // resultado + dato completo
    if (mostrarResultado) {
        val esCorrecto = seleccionada == pregunta.respuestaCorrecta
        Surface(
            color = if (esCorrecto) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = if (esCorrecto) "✅ ¡Excelente! Era: ${pregunta.respuestaCorrecta}"
                    else "❌ ¡Casi! La respuesta correcta era: ${pregunta.respuestaCorrecta}",
                    color = if (esCorrecto) Color(0xFF2E7D32) else Color(0xFFC62828),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "💡 ¿Sabías que...? ${pregunta.datoCompleto}",
                    color = TextoOscuro,
                    fontSize = 13.sp,
                    lineHeight = 19.sp
                )
                Text(text = "Fuente: Wikipedia en español", color = GrisTexto, fontSize = 11.sp)
            }
        }
    }

    // boton nueva pregunta
    Button(
        onClick = onNuevaPregunta,
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
