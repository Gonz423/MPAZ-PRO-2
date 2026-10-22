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
import androidx.compose.material.icons.filled.School
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appmenu.ui.componentes.BarraAsignaturaProgreso
import com.example.appmenu.ui.theme.*

// pantalla: informe de rendimiento del estudiante mockup screen 13 mpaz pro 2.º

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InformeEstudianteScreen(
    nombreEstudiante: String? = null,
    onVolver: () -> Unit = {}
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    // [modificado]: estado para clasificar rendimiento general entre 2.° a y 2.° b
    var cursoSeleccionadoRendimiento by remember { mutableStateOf("2.° Básico - A") }

    // metricas dinamicas segun el curso seleccionado
    val esCursoA = cursoSeleccionadoRendimiento == "2.° Básico - A"
    val porcentajeGlobal = if (esCursoA) 78 else 82
    val progresoFloat = if (esCursoA) 0.78f else 0.82f
    val cantidadEstudiantes = if (esCursoA) 28 else 26

    val pctLenguaje = if (esCursoA) 85 else 80
    val pctMatematica = if (esCursoA) 72 else 84
    val pctCiencias = if (esCursoA) 68 else 79
    val pctHistoria = if (esCursoA) 80 else 85

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoApp)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column {
            Text(
                text = if (nombreEstudiante != null) "Informe de Estudiante" else "Rendimiento General del Curso",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextoOscuro
            )
            Text(
                text = if (nombreEstudiante != null) "Escuela Marcela Paz · Reporte individual del alumno" else "Escuela Marcela Paz · Métricas pedagógicas 2.° Básico",
                fontSize = 13.sp,
                color = GrisTexto
            )
        }
            // [modificado]: selector de clasificacion segundo a y segundo b solo en vista general
            if (nombreEstudiante == null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FilterChip(
                        modifier = Modifier.weight(1f),
                        selected = cursoSeleccionadoRendimiento == "2.° Básico - A",
                        onClick = { cursoSeleccionadoRendimiento = "2.° Básico - A" },
                        label = {
                            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                Text("2.° Básico A", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF43A047),
                            selectedLabelColor = Color.White
                        )
                    )
                    FilterChip(
                        modifier = Modifier.weight(1f),
                        selected = cursoSeleccionadoRendimiento == "2.° Básico - B",
                        onClick = { cursoSeleccionadoRendimiento = "2.° Básico - B" },
                        label = {
                            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                Text("2.° Básico B", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF43A047),
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            // tarjeta de estudiante o del curso general seleccionado
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
                            .background(if (nombreEstudiante != null) Color(0xFFFFE0B2) else Color(0xFFE8F5E9)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (nombreEstudiante != null) Icons.Default.Face else Icons.Default.School,
                            contentDescription = "Avatar",
                            tint = if (nombreEstudiante != null) Color(0xFFE65100) else Color(0xFF2E7D32),
                            modifier = Modifier.size(34.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        // [modificado]: muestra 2.° basico a o b segun el chip seleccionado
                        Text(
                            text = nombreEstudiante ?: cursoSeleccionadoRendimiento,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextoOscuro
                        )
                        Text(
                            text = if (nombreEstudiante != null) "2.° Básico · Escuela Marcela Paz"
                            else "Promedio general · $cantidadEstudiantes estudiantes",
                            fontSize = 13.sp,
                            color = GrisTexto
                        )
                    }
                }
            }

            // rendimiento circular con puntaje dinamico
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
                        text = if (nombreEstudiante != null) "Rendimiento del estudiante" else "Rendimiento general ($cursoSeleccionadoRendimiento)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextoOscuro
                    )

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(120.dp)
                    ) {
                        CircularProgressIndicator(
                            progress = { progresoFloat },
                            modifier = Modifier.fillMaxSize(),
                            color = Color(0xFF2E7D32),
                            strokeWidth = 10.dp,
                            trackColor = Color(0xFFE0E0E0)
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$porcentajeGlobal%",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextoOscuro
                            )
                            Text(
                                text = if (nombreEstudiante != null) "Puntaje alumno" else "Puntaje global",
                                fontSize = 11.sp,
                                color = GrisTexto
                            )
                        }
                    }
                }
            }

            // detalle por asignatura dinamico segun el curso clasificado
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

                    BarraAsignaturaProgreso("Lenguaje y Comunicación", pctLenguaje, ColorLenguaje)
                    BarraAsignaturaProgreso("Matemática", pctMatematica, ColorMatematica)
                    BarraAsignaturaProgreso("Ciencias Naturales", pctCiencias, ColorCiencias)
                    BarraAsignaturaProgreso("Historia, Geografía y Cs. Sociales", pctHistoria, ColorHistoria)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // boton descargar informe
            Button(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    val mensaje = if (nombreEstudiante != null) {
                        "Generando PDF del informe de $nombreEstudiante..."
                    } else {
                        "Generando PDF del informe general de $cursoSeleccionadoRendimiento..."
                    }
                    Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF43A047))
            ) {
                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Descargar informe pedagógico", fontWeight = FontWeight.Bold)
            }
        }
    }