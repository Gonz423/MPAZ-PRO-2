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
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appmenu.ui.theme.*

// pantalla: informe de rendimiento del estudiante mockup screen 13 mpaz pro 2.º

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InformeEstudianteScreen(
    onVolver: () -> Unit = {}
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Informe de estudiante",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        Toast.makeText(context, "Opciones de reporte pedagógico", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Más", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AzulBarraSuperior,
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
            // tarjeta de estudiante
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
                            text = "2.° Básico - A · Escuela Marcela Paz",
                            fontSize = 13.sp,
                            color = GrisTexto
                        )
                    }
                }
            }

            // rendimiento general circular 78% puntaje global
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = BlancoTarjeta),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Rendimiento general",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextoOscuro
                    )

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(120.dp)
                    ) {
                        CircularProgressIndicator(
                            progress = { 0.78f },
                            modifier = Modifier.fillMaxSize(),
                            color = Color(0xFF2E7D32),
                            strokeWidth = 10.dp,
                            trackColor = Color(0xFFE0E0E0)
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "78%",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextoOscuro
                            )
                            Text(
                                text = "Puntaje global",
                                fontSize = 11.sp,
                                color = GrisTexto
                            )
                        }
                    }
                }
            }

            // detalle por asignatura
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
                        text = "Detalle por asignatura",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextoOscuro
                    )

                    BarraAsignaturaProgreso("Lenguaje y Comunicación", 85, ColorLenguaje)
                    BarraAsignaturaProgreso("Matemática", 72, ColorMatematica)
                    BarraAsignaturaProgreso("Ciencias Naturales", 68, ColorCiencias)
                    BarraAsignaturaProgreso("Historia, Geografía y Cs. Sociales", 80, ColorHistoria)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // boton descargar informe
            Button(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    Toast.makeText(context, "Generando PDF del informe de Martina González...", Toast.LENGTH_LONG).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AzulBarraSuperior)
            ) {
                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Descargar informe pedagógico", fontWeight = FontWeight.Bold)
            }
        }
    }
}
