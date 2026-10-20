package com.example.appmenu.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appmenu.data.AudioHelper
import com.example.appmenu.ui.theme.*

// pantalla: detalle de leccion y contenido didactico mockup screen 5 mpaz pro 2.º

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeccionScreen(
    tituloUnidad: String = "Unidad 1 - Lectura y comprensión",
    tituloContenido: String = "El valor de la amistad",
    colorMateria: Color = ColorLenguaje,
    onVolver: () -> Unit = {},
    onIrAActividad: () -> Unit = {}
) {
    val context = LocalContext.current
    var formatoSeleccionado by remember { mutableStateOf("texto") }

    // inicializar sintesis de voz nativa
    LaunchedEffect(Unit) {
        AudioHelper.inicializar(context)
    }

    val textoDidactico = "La amistad nos ayuda a compartir, aprender y crecer en la Escuela Marcela Paz. Es importante respetar, escuchar y apoyar siempre a nuestros compañeros de curso."

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = tituloUnidad,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(FondoApp)
                .padding(paddingValues)
                .padding(18.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // tarjeta ilustrativa del contenido
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = colorMateria.copy(alpha = 0.1f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .background(colorMateria),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = tituloContenido,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextoOscuro
                    )
                    Text(
                        text = "Contenido Didáctico Adaptado · 2.° Básico",
                        fontSize = 12.sp,
                        color = GrisTexto
                    )
                }
            }

            // selector de formato didactico texto / audio / imagen
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // opcion texto
                FilterChip(
                    selected = formatoSeleccionado == "texto",
                    onClick = { formatoSeleccionado = "texto" },
                    label = { Text("Texto") },
                    leadingIcon = { Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    modifier = Modifier.weight(1f)
                )

                // opcion audio sintetizador nativo text-to-speech
                FilterChip(
                    selected = formatoSeleccionado == "audio",
                    onClick = {
                        formatoSeleccionado = "audio"
                        AudioHelper.hablar(textoDidactico)
                        Toast.makeText(context, "Reproduciendo audio con sintetizador nativo...", Toast.LENGTH_SHORT).show()
                    },
                    label = { Text("Audio") },
                    leadingIcon = { Icon(Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    modifier = Modifier.weight(1f)
                )

                // opcion imagen
                FilterChip(
                    selected = formatoSeleccionado == "imagen",
                    onClick = { formatoSeleccionado = "imagen" },
                    label = { Text("Imagen") },
                    leadingIcon = { Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    modifier = Modifier.weight(1f)
                )
            }

            // contenedor de lectura / explicacion
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
                    Text(
                        text = "Lectura Guiada",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorMateria
                    )

                    Text(
                        text = textoDidactico,
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        color = TextoOscuro
                    )

                    // boton para escuchar con tts nativo
                    FilledTonalButton(
                        onClick = {
                            AudioHelper.hablar(textoDidactico)
                            Toast.makeText(context, "Escuchando narración hablada...", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Hearing, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Escuchar explicación en audio")
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // boton siguiente a la actividad
            Button(
                onClick = onIrAActividad,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colorMateria)
            ) {
                Text("Siguiente a la Actividad", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
            }
        }
    }
}
