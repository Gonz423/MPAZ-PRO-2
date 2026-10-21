package com.example.appmenu.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appmenu.model.ConfiguracionEducativa
import com.example.appmenu.model.ProgresoAsignatura
import com.example.appmenu.ui.componentes.FilaProgresoAsignatura
import com.example.appmenu.ui.theme.*

// pantalla: panel docente con progreso por asignatura integrado en cada curso mpaz pro 2.º

@Composable
fun DocenteScreen(
    onNavegarFormulario: () -> Unit = {},
    onNavegarNotas: () -> Unit = {},
    onNavegarInformeEstudiante: () -> Unit = {},
    onSeleccionarAsignatura: (String) -> Unit = {}
) {
    val context = LocalContext.current

    // estado del curso desplegado para ver su progreso por asignatura
    var cursoDesplegado by remember { mutableStateOf<String?>("A") }

    // datos pedagogicos por curso
    val progresoCursoA = listOf(
        ProgresoAsignatura(asignatura = "Lenguaje y Comunicación", porcentaje = 78, color = ColorLenguaje),
        ProgresoAsignatura(asignatura = "Matemática", porcentaje = 72, color = ColorMatematica),
        ProgresoAsignatura(asignatura = "Ciencias Naturales", porcentaje = 65, color = ColorCiencias),
        ProgresoAsignatura(asignatura = "Historia y Geografía", porcentaje = 84, color = ColorHistoria)
    )

    val progresoCursoB = listOf(
        ProgresoAsignatura(asignatura = "Lenguaje y Comunicación", porcentaje = 68, color = ColorLenguaje),
        ProgresoAsignatura(asignatura = "Matemática", porcentaje = 60, color = ColorMatematica),
        ProgresoAsignatura(asignatura = "Ciencias Naturales", porcentaje = 55, color = ColorCiencias),
        ProgresoAsignatura(asignatura = "Historia y Geografía", porcentaje = 76, color = ColorHistoria)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoApp)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // cabecera del panel docente
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Panel Docente",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoOscuro
                )
                Text(
                    text = "Escuela Marcela Paz · 2.° Básico",
                    fontSize = 13.sp,
                    color = GrisTexto
                )
            }

            Surface(
                color = AzulBarraSuperior.copy(alpha = 0.12f),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = "Prof. Andrea",
                    color = AzulBarraSuperior,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }

        // tarjetas de accion 2x2
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 1. cursos asignados azul
                TarjetaAccionDocente(
                    titulo = "Cursos asignados",
                    color = AzulBarraSuperior,
                    icono = Icons.Default.School,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        Toast.makeText(context, "Mostrando cursos: 2.° Básico A y B", Toast.LENGTH_SHORT).show()
                    }
                )

                // 2. gestionar contenidos morado -> abre formulario
                TarjetaAccionDocente(
                    titulo = "Gestionar contenidos",
                    color = Color(0xFF8E24AA),
                    icono = Icons.Default.Settings,
                    modifier = Modifier.weight(1f),
                    onClick = onNavegarFormulario
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 3. rendimiento de estudiantes verde -> abre informeestudiantescreen
                TarjetaAccionDocente(
                    titulo = "Rendimiento general",
                    color = Color(0xFF43A047),
                    icono = Icons.AutoMirrored.Filled.TrendingUp,
                    modifier = Modifier.weight(1f),
                    onClick = onNavegarInformeEstudiante
                )

                // 4. asignar tarea escolar naranja -> abre formulario en modo tarea
                TarjetaAccionDocente(
                    titulo = "Asignar Tarea Escolar",
                    color = Color(0xFFFB8C00),
                    icono = Icons.Default.PostAdd,
                    modifier = Modifier.weight(1f),
                    onClick = onNavegarFormulario
                )
            }
        }

        // seccion: cursos asignados con progreso por asignatura al apretar cada curso
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = BlancoTarjeta),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Cursos asignados",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextoOscuro
                    )
                    Text(
                        text = "Toca para ver progreso",
                        fontSize = 11.sp,
                        color = GrisTexto
                    )
                }

                // ================= curso 2.° basico - a =================
                val estaExpandidoA = cursoDesplegado == "A"
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (estaExpandidoA) AzulBarraSuperior.copy(alpha = 0.05f) else Color(0xFFFAFAFA),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (estaExpandidoA) AzulBarraSuperior.copy(alpha = 0.3f) else Color(0xFFEEEEEE)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    cursoDesplegado = if (estaExpandidoA) null else "A"
                                },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(AzulBarraSuperior.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.School, contentDescription = null, tint = AzulBarraSuperior, modifier = Modifier.size(20.dp))
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("2.° Básico - A", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextoOscuro)
                                    Text("28 estudiantes · Prof. Andrea Jefe", fontSize = 12.sp, color = GrisTexto)
                                }
                            }
                            Icon(
                                imageVector = if (estaExpandidoA) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = AzulBarraSuperior
                            )
                        }

                        // progreso por asignatura de 2.° basico - a
                        AnimatedVisibility(
                            visible = estaExpandidoA,
                            enter = expandVertically() + fadeIn(),
                            exit = shrinkVertically() + fadeOut()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 10.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                HorizontalDivider(color = Color(0xFFE2E8F0))
                                Text(
                                    text = "Progreso por asignatura en 2.° Básico - A:",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AzulBarraSuperior
                                )

                                progresoCursoA.forEach { progreso ->
                                    val idMateria = when {
                                        progreso.asignatura.contains("Lenguaje", ignoreCase = true) -> "lenguaje"
                                        progreso.asignatura.contains("Matemática", ignoreCase = true) -> "matematica"
                                        progreso.asignatura.contains("Ciencias", ignoreCase = true) -> "ciencias"
                                        progreso.asignatura.contains("Historia", ignoreCase = true) -> "historia"
                                        else -> "lenguaje"
                                    }
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { onSeleccionarAsignatura(idMateria) }
                                            .padding(vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(modifier = Modifier.weight(1f)) {
                                            FilaProgresoAsignatura(progreso = progreso)
                                        }
                                        Icon(
                                            imageVector = Icons.Default.ChevronRight,
                                            contentDescription = "Ver guías",
                                            tint = GrisTexto,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // ================= curso 2.° basico - b =================
                val estaExpandidoB = cursoDesplegado == "B"
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (estaExpandidoB) Color(0xFF8E24AA).copy(alpha = 0.05f) else Color(0xFFFAFAFA),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (estaExpandidoB) Color(0xFF8E24AA).copy(alpha = 0.3f) else Color(0xFFEEEEEE)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    cursoDesplegado = if (estaExpandidoB) null else "B"
                                },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF8E24AA).copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.School, contentDescription = null, tint = Color(0xFF8E24AA), modifier = Modifier.size(20.dp))
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("2.° Básico - B", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextoOscuro)
                                    Text("24 estudiantes · Asignatura compartida", fontSize = 12.sp, color = GrisTexto)
                                }
                            }
                            Icon(
                                imageVector = if (estaExpandidoB) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = Color(0xFF8E24AA)
                            )
                        }

                        // progreso por asignatura de 2.° basico - b
                        AnimatedVisibility(
                            visible = estaExpandidoB,
                            enter = expandVertically() + fadeIn(),
                            exit = shrinkVertically() + fadeOut()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 10.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                HorizontalDivider(color = Color(0xFFE2E8F0))
                                Text(
                                    text = "Progreso por asignatura en 2.° Básico - B:",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF8E24AA)
                                )

                                progresoCursoB.forEach { progreso ->
                                    val idMateria = when {
                                        progreso.asignatura.contains("Lenguaje", ignoreCase = true) -> "lenguaje"
                                        progreso.asignatura.contains("Matemática", ignoreCase = true) -> "matematica"
                                        progreso.asignatura.contains("Ciencias", ignoreCase = true) -> "ciencias"
                                        progreso.asignatura.contains("Historia", ignoreCase = true) -> "historia"
                                        else -> "lenguaje"
                                    }
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { onSeleccionarAsignatura(idMateria) }
                                            .padding(vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(modifier = Modifier.weight(1f)) {
                                            FilaProgresoAsignatura(progreso = progreso)
                                        }
                                        Icon(
                                            imageVector = Icons.Default.ChevronRight,
                                            contentDescription = "Ver guías",
                                            tint = GrisTexto,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TarjetaAccionDocente(
    titulo: String,
    color: Color,
    icono: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(115.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            Text(
                text = titulo,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 16.sp
            )
        }
    }
}
