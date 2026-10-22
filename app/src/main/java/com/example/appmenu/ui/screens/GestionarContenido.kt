package com.example.appmenu.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import com.example.appmenu.model.ActividadesManager
import com.example.appmenu.model.EjercicioEducativo
import com.example.appmenu.ui.theme.*

// modelo de datos para representar a los alumnos del curso
data class EstudianteCurso(
    val id: String,
    val nombre: String,
    val run: String,
    val curso: String = "2.° Básico - A",
    val guiasCompletadas: Int,
    val totalGuias: Int = 8,
    val promedio: Double
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionarContenidosScreen(
    onVolver: () -> Unit = {},
    onCrearNuevaGuia: () -> Unit = {},
    onVerInformeEstudiante: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    // observamos los ejercicios y guias del sistema en tiempo real
    val todosLosEjercicios by ActividadesManager.ejercicios.collectAsState()

    // 0: pestaña administrar guias, 1: pestaña alumnos del curso
    var tabSeleccionado by remember { mutableIntStateOf(0) }
    var filtroTexto by remember { mutableStateOf("") }

    // [modificado]: clasificacion de curso: "todos", "2.° basico - a", "2.° basico - b"
    var cursoFiltro by remember { mutableStateOf("Todos") }

    // [modificado]: nomina oficial de alumnos con segundo a y segundo b
    val nominaAlumnos = remember {
        listOf(
            // --- segundo basico a ---
            EstudianteCurso("1", "Martina González P.", "24.120.453-2", "2.° Básico - A", 7, 8, 6.7),
            EstudianteCurso("2", "Lucas Silva Morales", "24.231.890-K", "2.° Básico - A", 6, 8, 6.2),
            EstudianteCurso("3", "Sofía Morales Castro", "24.189.764-5", "2.° Básico - A", 8, 8, 7.0),
            EstudianteCurso("4", "Tomás Pérez Araya", "24.345.122-8", "2.° Básico - A", 5, 8, 5.8),
            EstudianteCurso("5", "Valentina Rojas Soto", "24.298.541-1", "2.° Básico - A", 7, 8, 6.5),
            EstudianteCurso("6", "Mateo Araya Fuentes", "24.112.789-3", "2.° Básico - A", 4, 8, 5.2),
            EstudianteCurso("7", "Isidora Campos Vera", "24.312.445-9", "2.° Básico - A", 8, 8, 6.9),
            EstudianteCurso("8", "Agustín Muñoz Tapia", "24.254.887-6", "2.° Básico - A", 6, 8, 6.0),

            // --- segundo basico b ---
            EstudianteCurso("9", "Benjamín Castillo R.", "24.412.331-4", "2.° Básico - B", 7, 8, 6.6),
            EstudianteCurso("10", "Florencia Herrera M.", "24.389.102-7", "2.° Básico - B", 8, 8, 6.8),
            EstudianteCurso("11", "Joaquín Valenzuela C.", "24.290.871-3", "2.° Básico - B", 5, 8, 5.9),
            EstudianteCurso("12", "Emilia Navarrete S.", "24.512.980-K", "2.° Básico - B", 8, 8, 7.0),
            EstudianteCurso("13", "Gaspar Sepúlveda T.", "24.198.654-2", "2.° Básico - B", 6, 8, 6.1),
            EstudianteCurso("14", "Maite Fuentes Ortiz", "24.321.765-8", "2.° Básico - B", 7, 8, 6.4),
            EstudianteCurso("15", "Vicente Carrasco L.", "24.443.210-5", "2.° Básico - B", 5, 8, 5.5),
            EstudianteCurso("16", "Antonella Miranda G.", "24.567.891-1", "2.° Básico - B", 8, 8, 6.7)
        )
    }

    Scaffold(
        floatingActionButton = {
            if (tabSeleccionado == 0) {
                ExtendedFloatingActionButton(
                    onClick = onCrearNuevaGuia,
                    containerColor = Color(0xFF8E24AA),
                    contentColor = Color.White,
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("Nueva Guía", fontWeight = FontWeight.Bold) }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(FondoApp)
                .padding(paddingValues)
        ) {
            // selector de pestañas superiores
            val totalAlumnosFiltro = if (cursoFiltro == "Todos") nominaAlumnos.size
            else nominaAlumnos.count { it.curso == cursoFiltro }

            PrimaryTabRow(
                selectedTabIndex = tabSeleccionado,
                containerColor = Color.White,
                contentColor = Color(0xFF8E24AA)
            ) {
                Tab(
                    selected = tabSeleccionado == 0,
                    onClick = { tabSeleccionado = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Guías (${todosLosEjercicios.size})", fontWeight = FontWeight.Bold)
                        }
                    }
                )
                Tab(
                    selected = tabSeleccionado == 1,
                    onClick = { tabSeleccionado = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.People, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Alumnos ($totalAlumnosFiltro)", fontWeight = FontWeight.Bold)
                        }
                    }
                )
            }

            // [modificado]: chips de clasificacion entre segundo a y segundo b
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = cursoFiltro == "Todos",
                    onClick = { cursoFiltro = "Todos" },
                    label = { Text("Todos", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF8E24AA),
                        selectedLabelColor = Color.White
                    )
                )
                FilterChip(
                    selected = cursoFiltro == "2.° Básico - A",
                    onClick = { cursoFiltro = "2.° Básico - A" },
                    label = { Text("2.° Básico A", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF8E24AA),
                        selectedLabelColor = Color.White
                    )
                )
                FilterChip(
                    selected = cursoFiltro == "2.° Básico - B",
                    onClick = { cursoFiltro = "2.° Básico - B" },
                    label = { Text("2.° Básico B", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF8E24AA),
                        selectedLabelColor = Color.White
                    )
                )
            }

            // barra de busqueda rapida
            OutlinedTextField(
                value = filtroTexto,
                onValueChange = { filtroTexto = it },
                placeholder = {
                    Text(if (tabSeleccionado == 0) "Buscar por título o código de guía..." else "Buscar alumno por nombre o RUT...")
                },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = GrisTexto)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Color(0xFF8E24AA),
                    unfocusedBorderColor = Color(0xFFE0E0E0)
                )
            )

            // contenido dinamico segun la pestaña activa
            if (tabSeleccionado == 0) {
                // ==========================================
                // vista: administrar guias creadas
                // ==========================================
                val guiasFiltradas = remember(todosLosEjercicios, filtroTexto) {
                    if (filtroTexto.isBlank()) todosLosEjercicios
                    else todosLosEjercicios.filter {
                        it.titulo.contains(filtroTexto, ignoreCase = true) ||
                                it.codigoGuia.contains(filtroTexto, ignoreCase = true) ||
                                it.objetivoAprendizaje.contains(filtroTexto, ignoreCase = true)
                    }
                }

                if (guiasFiltradas.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No se encontraron guías registradas.",
                            color = GrisTexto,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 80.dp, top = 4.dp)
                    ) {
                        items(guiasFiltradas, key = { it.id }) { guia ->
                            ItemGuiaDocente(
                                guia = guia,
                                cursoActivo = cursoFiltro,
                                onToggleVisibilidad = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    Toast.makeText(
                                        context,
                                        if (guia.disponibleParaEstudiantes) "Guía #${guia.codigoGuia} ocultada a estudiantes"
                                        else "Guía #${guia.codigoGuia} activada y visible",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            )
                        }
                    }
                }
            } else {
                // ==========================================
                // vista: alumnos del curso
                // ==========================================
                // [modificado]: filtra segun el texto y el curso todos, 2.° a o 2.° b
                val alumnosFiltrados = remember(nominaAlumnos, filtroTexto, cursoFiltro) {
                    nominaAlumnos.filter { alumno ->
                        val coincideCurso = (cursoFiltro == "Todos" || alumno.curso == cursoFiltro)
                        val coincideTexto = filtroTexto.isBlank() ||
                                alumno.nombre.contains(filtroTexto, ignoreCase = true) ||
                                alumno.run.contains(filtroTexto, ignoreCase = true)
                        coincideCurso && coincideTexto
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 20.dp, top = 4.dp)
                ) {
                    items(alumnosFiltrados, key = { it.id }) { alumno ->
                        ItemEstudianteCurso(
                            estudiante = alumno,
                            onClickVerInforme = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onVerInformeEstudiante(alumno.nombre)
                            }
                        )
                    }
                }
            }
        }
    }
}

// componente para cada guia didactica en la lista docente
@Composable
private fun ItemGuiaDocente(
    guia: EjercicioEducativo,
    cursoActivo: String = "Todos",
    onToggleVisibilidad: () -> Unit
) {
    val colorMateria = when (guia.materiaId) {
        "matematica" -> ColorMatematica
        "ciencias"   -> ColorCiencias
        "historia"   -> ColorHistoria
        else         -> ColorLenguaje
    }

    val cantidadPreguntas = remember(guia.pregunta) {
        if (guia.pregunta.contains(";;;")) guia.pregunta.split("|||").size else 1
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = BlancoTarjeta),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // chip de codigo, materia y curso asignado
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Surface(
                        color = colorMateria.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "Guía #${guia.codigoGuia}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorMateria,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    // [modificado]: etiqueta del curso correspondiente
                    Surface(
                        color = Color(0xFF8E24AA).copy(alpha = 0.1f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = if (cursoActivo == "Todos") "2.° A y B" else cursoActivo.replace("2.° Básico - ", "2.° "),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF8E24AA),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    Surface(
                        color = Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "$cantidadPreguntas pregunta(s)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextoOscuro,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                // switch / estado de visibilidad para los alumnos
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onToggleVisibilidad() }
                ) {
                    Icon(
                        imageVector = if (guia.disponibleParaEstudiantes) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = null,
                        tint = if (guia.disponibleParaEstudiantes) Color(0xFF2E7D32) else GrisTexto,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (guia.disponibleParaEstudiantes) "Visible" else "Oculta",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (guia.disponibleParaEstudiantes) Color(0xFF2E7D32) else GrisTexto
                    )
                }
            }

            // titulo de la guia
            Text(
                text = guia.titulo,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextoOscuro
            )

            // objetivo pedagogico
            Text(
                text = guia.objetivoAprendizaje,
                fontSize = 12.sp,
                color = GrisTexto,
                maxLines = 2
            )
        }
    }
}

// componente para cada alumno del curso en la nomina
@Composable
private fun ItemEstudianteCurso(
    estudiante: EstudianteCurso,
    onClickVerInforme: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClickVerInforme() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = BlancoTarjeta),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // avatar
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF8E24AA).copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Face,
                    contentDescription = null,
                    tint = Color(0xFF8E24AA),
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = estudiante.nombre,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoOscuro
                )
                Text(
                    text = "${estudiante.curso} · RUN: ${estudiante.run}",
                    fontSize = 12.sp,
                    color = GrisTexto
                )

                Spacer(modifier = Modifier.height(4.dp))

                // progreso de guias del alumno
                val progreso = estudiante.guiasCompletadas.toFloat() / estudiante.totalGuias.toFloat()
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LinearProgressIndicator(
                        progress = { progreso },
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = if (progreso >= 0.75f) Color(0xFF2E7D32) else Color(0xFFFB8C00),
                        trackColor = Color(0xFFEEEEEE)
                    )
                    Text(
                        text = "${estudiante.guiasCompletadas}/${estudiante.totalGuias} guías",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextoOscuro
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // nota / promedio del estudiante
            Surface(
                color = if (estudiante.promedio >= 6.0) Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "${estudiante.promedio}",
                    color = if (estudiante.promedio >= 6.0) Color(0xFF2E7D32) else Color(0xFFE65100),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}