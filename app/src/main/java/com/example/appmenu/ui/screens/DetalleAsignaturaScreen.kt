package com.example.appmenu.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.RadioButtonUnchecked
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
import com.example.appmenu.model.ConfiguracionEducativa
import com.example.appmenu.model.EjercicioEducativo
import com.example.appmenu.model.UnidadEducativa
import com.example.appmenu.ui.theme.*

// pantalla: detalle de asignatura - unidades asociadas y actividades didacticas

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleAsignaturaScreen(
    materiaId: String,
    esDocente: Boolean,
    onVolver: () -> Unit,
    onNavegarFormulario: (String) -> Unit = {},
    onAbrirLeccion: (String, String) -> Unit = { _, _ -> },
    onAbrirActividad: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    // buscar configuracion de la materia
    val asignatura = ConfiguracionEducativa.asignaturas.find {
        it.id.equals(materiaId, ignoreCase = true)
    } ?: ConfiguracionEducativa.asignaturas.first()

    // observar unidades y ejercicios reactivos desde actividadesmanager
    val todasLasUnidades by ActividadesManager.unidades.collectAsState()
    val todosLosEjercicios by ActividadesManager.ejercicios.collectAsState()

    val unidadesMateria = remember(todasLasUnidades, materiaId) {
        todasLasUnidades.filter { it.materiaId.equals(materiaId, ignoreCase = true) }
    }
    val ejerciciosMateria = remember(todosLosEjercicios, materiaId) {
        todosLosEjercicios.filter { it.materiaId.equals(materiaId, ignoreCase = true) }
    }

    val ejerciciosCompletados = ejerciciosMateria.count { it.completado }
    val progresoCalculado = if (ejerciciosMateria.isNotEmpty()) {
        ejerciciosCompletados.toFloat() / ejerciciosMateria.size.toFloat()
    } else 0f

    // control de unidades desplegadas acordeon expandible
    var unidadesExpandidas by remember(unidadesMateria) {
        mutableStateOf(unidadesMateria.map { it.id }.toSet())
    }

    // estados para dialogos del docente
    var mostrarDialogoNuevaUnidad by remember { mutableStateOf(false) }
    var nuevaUnidadTitulo by remember { mutableStateOf("") }
    var nuevaUnidadOa by remember { mutableStateOf("") }

    var mostrarDialogoNuevoEjercicio by remember { mutableStateOf(false) }
    var nuevoEjercicioTitulo by remember { mutableStateOf("") }
    var nuevoEjercicioOa by remember { mutableStateOf("") }
    var nuevoEjercicioCodigo by remember { mutableStateOf("") }
    var nuevoEjercicioPregunta by remember { mutableStateOf("") }
    var nuevaOpcion1 by remember { mutableStateOf("") }
    var nuevaOpcion2 by remember { mutableStateOf("") }
    var nuevaOpcion3 by remember { mutableStateOf("") }
    var unidadSeleccionadaId by remember { mutableStateOf("") }
    var errorModal by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Asignatura: ${asignatura.nombre}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver a Inicio",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = asignatura.color,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(FondoApp)
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. banner superior con identidad y progreso
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = BlancoTarjeta),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .background(asignatura.color.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = asignatura.icono,
                                    contentDescription = null,
                                    tint = asignatura.color,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${asignatura.nombre} · 2.° Básico",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextoOscuro
                                )
                                Text(
                                    text = if (esDocente) "Panel de Gestión Pedagógica (Docente)" else "Tus Unidades y Actividades Didácticas",
                                    fontSize = 13.sp,
                                    color = GrisTexto
                                )
                            }
                        }

                        // barra de progreso interactiva
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Progreso en la materia",
                                    fontSize = 12.sp,
                                    color = GrisTexto,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "$ejerciciosCompletados de ${ejerciciosMateria.size} completados",
                                    fontSize = 12.sp,
                                    color = asignatura.color,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            LinearProgressIndicator(
                                progress = { progresoCalculado },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = asignatura.color,
                                trackColor = Color(0xFFE0E0E0)
                            )
                        }

                        // herramientas de gestion para el docente
                        if (esDocente) {
                            HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 1.dp)
                            Text(
                                text = "Acciones del Docente",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextoOscuro
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        nuevaUnidadTitulo = ""
                                        nuevaUnidadOa = ""
                                        errorModal = null
                                        mostrarDialogoNuevaUnidad = true
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = asignatura.color)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("+ Unidad", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = {
                                        nuevoEjercicioTitulo = ""
                                        nuevoEjercicioOa = ""
                                        nuevoEjercicioCodigo = ""
                                        nuevoEjercicioPregunta = ""
                                        nuevaOpcion1 = ""
                                        nuevaOpcion2 = ""
                                        nuevaOpcion3 = ""
                                        unidadSeleccionadaId = unidadesMateria.firstOrNull()?.id ?: ""
                                        errorModal = null
                                        mostrarDialogoNuevoEjercicio = true
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = AzulBarraSuperior)
                                ) {
                                    Icon(Icons.Default.PostAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("+ Actividad", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            OutlinedButton(
                                onClick = { onNavegarFormulario(asignatura.nombre) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Abrir Formulario Oficial de Registro", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // 2. seccion unidades pedagogicas con sus actividades asociadas
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Unidades Didácticas (${unidadesMateria.size})",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextoOscuro
                    )
                    Text(
                        text = "${ejerciciosMateria.size} actividades",
                        fontSize = 13.sp,
                        color = GrisTexto
                    )
                }
            }

            // si no hay unidades todavia
            if (unidadesMateria.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = BlancoTarjeta)
                    ) {
                        Text(
                            text = "No hay unidades configuradas para ${asignatura.nombre}.\nPulsa '+ Unidad' para agregar la primera.",
                            color = GrisTexto,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(20.dp)
                        )
                    }
                }
            }

            // iteracion por cada unidad y sus actividades asociadas
            items(items = unidadesMateria, key = { it.id }) { unidad ->
                val actividadesDeEstaUnidad = remember(ejerciciosMateria, unidad.id) {
                    ejerciciosMateria.filter { it.unidadId == unidad.id }
                }

                val estaExpandida = unidadesExpandidas.contains(unidad.id)

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (estaExpandida) BlancoTarjeta else Color(0xFFFBFBFB)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = if (estaExpandida) 2.dp else 1.dp),
                    border = if (estaExpandida) androidx.compose.foundation.BorderStroke(1.dp, asignatura.color.copy(alpha = 0.35f)) else null
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // cabecera de la unidad interactiva click despliega / esconde los ejercicios
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    unidadesExpandidas = if (estaExpandida) {
                                        unidadesExpandidas - unidad.id
                                    } else {
                                        unidadesExpandidas + unidad.id
                                    }
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(asignatura.color.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Bookmark,
                                    contentDescription = null,
                                    tint = asignatura.color,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = unidad.titulo,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextoOscuro
                                )
                                Text(
                                    text = unidad.objetivoAprendizaje,
                                    fontSize = 12.sp,
                                    color = GrisTexto
                                )
                            }

                            // icono de acordeon expandible / colapsable
                            Surface(
                                shape = CircleShape,
                                color = if (estaExpandida) asignatura.color.copy(alpha = 0.12f) else Color(0xFFEEEEEE),
                                modifier = Modifier.size(32.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = if (estaExpandida) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                        contentDescription = if (estaExpandida) "Esconder ejercicios" else "Mostrar ejercicios",
                                        tint = if (estaExpandida) asignatura.color else GrisTexto,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }

                        // cuerpo animado con los ejercicios de la unidad
                        AnimatedVisibility(
                            visible = estaExpandida,
                            enter = expandVertically() + fadeIn(),
                            exit = shrinkVertically() + fadeOut()
                        ) {
                            Column(
                                modifier = Modifier.padding(top = 4.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 1.dp)

                                // sub-cabecera de actividades de esta unidad
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Ejercicios Didácticos (${actividadesDeEstaUnidad.size})",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = asignatura.color
                                    )
                                    Surface(
                                        color = asignatura.color.copy(alpha = 0.10f),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "${actividadesDeEstaUnidad.count { it.completado }}/${actividadesDeEstaUnidad.size} listos",
                                            color = asignatura.color,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                // lista de actividades asociadas a esta unidad
                                if (actividadesDeEstaUnidad.isEmpty()) {
                                    Text(
                                        text = "Aún no se han asignado actividades a esta unidad.",
                                        fontSize = 12.sp,
                                        color = GrisTexto,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )
                                } else {
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        actividadesDeEstaUnidad.forEach { ejercicio ->
                                            TarjetaEjercicioItem(
                                                ejercicio = ejercicio,
                                                colorAsignatura = asignatura.color,
                                                esDocente = esDocente,
                                                onRealizar = {
                                                    onAbrirActividad(ejercicio.id)
                                                },
                                                onToggleCompletado = {
                                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                    ActividadesManager.alternarCompletado(ejercicio.id)
                                                    val nuevoEstado = !ejercicio.completado
                                                    Toast.makeText(
                                                        context,
                                                        if (nuevoEstado) "¡Felicitaciones! Actividad completada 🎉" else "Actividad marcada como pendiente",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
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

    // dialogo modal: nueva unidad docente
    if (mostrarDialogoNuevaUnidad) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoNuevaUnidad = false },
            title = {
                Text("Crear Nueva Unidad en ${asignatura.nombre}", fontWeight = FontWeight.Bold, fontSize = 17.sp)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "El sistema numerará la unidad automáticamente y la asociará a ${asignatura.nombre}.",
                        fontSize = 12.sp,
                        color = GrisTexto
                    )
                    OutlinedTextField(
                        value = nuevaUnidadTitulo,
                        onValueChange = { nuevaUnidadTitulo = it },
                        label = { Text("Título de la Unidad") },
                        placeholder = { Text("Ej: Comprensión Poética") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = nuevaUnidadOa,
                        onValueChange = { nuevaUnidadOa = it },
                        label = { Text("Objetivo de Aprendizaje (OA)") },
                        placeholder = { Text("Ej: OA 5, OA 8 · Poemas y canciones") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                    errorModal?.let {
                        Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (nuevaUnidadTitulo.isBlank() || nuevaUnidadOa.isBlank()) {
                            errorModal = "Debes ingresar título y objetivo de aprendizaje."
                        } else {
                            ActividadesManager.agregarUnidad(
                                materiaId = materiaId,
                                titulo = nuevaUnidadTitulo,
                                oa = nuevaUnidadOa
                            )
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            Toast.makeText(context, "¡Nueva unidad creada exitosamente!", Toast.LENGTH_SHORT).show()
                            mostrarDialogoNuevaUnidad = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = asignatura.color)
                ) {
                    Text("Crear Unidad")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoNuevaUnidad = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // dialogo modal: nueva actividad asociada a una unidad docente
    if (mostrarDialogoNuevoEjercicio) {
        var dropdownExpandido by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { mostrarDialogoNuevoEjercicio = false },
            title = {
                Text("Nueva Actividad Didáctica", fontWeight = FontWeight.Bold, fontSize = 17.sp)
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // selector de unidad
                    Text("Unidad asociada:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = { dropdownExpandido = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            val unidadActual = unidadesMateria.find { it.id == unidadSeleccionadaId }
                            Text(unidadActual?.titulo ?: "Seleccionar Unidad", maxLines = 1)
                        }
                        DropdownMenu(
                            expanded = dropdownExpandido,
                            onDismissRequest = { dropdownExpandido = false }
                        ) {
                            unidadesMateria.forEach { uni ->
                                DropdownMenuItem(
                                    text = { Text(uni.titulo) },
                                    onClick = {
                                        unidadSeleccionadaId = uni.id
                                        dropdownExpandido = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = nuevoEjercicioTitulo,
                        onValueChange = { nuevoEjercicioTitulo = it },
                        label = { Text("Título de la Actividad") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = nuevoEjercicioOa,
                        onValueChange = { nuevoEjercicioOa = it },
                        label = { Text("Objetivo de Aprendizaje (OA)") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                    OutlinedTextField(
                        value = nuevoEjercicioCodigo,
                        onValueChange = { if (it.all { c -> c.isDigit() }) nuevoEjercicioCodigo = it },
                        label = { Text("Código de Guía (ej. 1045)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    errorModal?.let {
                        Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (nuevoEjercicioTitulo.isBlank() || nuevoEjercicioOa.isBlank()) {
                            errorModal = "Debes ingresar título y objetivo de aprendizaje."
                        } else if (unidadSeleccionadaId.isBlank()) {
                            errorModal = "Debes seleccionar una unidad asociada."
                        } else {
                            val cod = if (nuevoEjercicioCodigo.isBlank()) "100" + (1..9).random() else nuevoEjercicioCodigo
                            ActividadesManager.agregarEjercicio(
                                materiaId = materiaId,
                                unidadId = unidadSeleccionadaId,
                                titulo = nuevoEjercicioTitulo,
                                oa = nuevoEjercicioOa,
                                codigoGuia = cod
                            )
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            Toast.makeText(context, "¡Actividad agregada a la unidad!", Toast.LENGTH_SHORT).show()
                            mostrarDialogoNuevoEjercicio = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = asignatura.color)
                ) {
                    Text("Guardar Actividad")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoNuevoEjercicio = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

// componente: tarjeta de cada ejercicio individual
@Composable
fun TarjetaEjercicioItem(
    ejercicio: EjercicioEducativo,
    colorAsignatura: Color,
    esDocente: Boolean,
    onRealizar: () -> Unit,
    onToggleCompletado: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (ejercicio.completado) Color(0xFFF1F8E9) else Color(0xFFFAFAFA)
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (ejercicio.completado) Color(0xFFC8E6C9) else Color(0xFFE5E7EB))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // codigo de guia
                SuggestionChip(
                    onClick = {},
                    label = { Text("Guía #${ejercicio.codigoGuia}", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = colorAsignatura.copy(alpha = 0.12f),
                        labelColor = colorAsignatura
                    )
                )

                // badge de estado
                Surface(
                    color = if (ejercicio.completado) Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (ejercicio.completado) "Completado" else "Pendiente",
                        color = if (ejercicio.completado) Color(0xFF2E7D32) else Color(0xFFE65100),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            // titulo
            Text(
                text = ejercicio.titulo,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextoOscuro
            )

            // objetivo pedagogico
            Text(
                text = ejercicio.objetivoAprendizaje,
                fontSize = 12.sp,
                color = GrisTexto
            )

            Spacer(modifier = Modifier.height(2.dp))

            // botones de accion
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // boton realizar actividad
                Button(
                    onClick = onRealizar,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colorAsignatura)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (esDocente) "Probar Actividad" else "Realizar",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // boton marcar estado
                OutlinedButton(
                    onClick = onToggleCompletado,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = if (ejercicio.completado) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                        contentDescription = null,
                        tint = if (ejercicio.completado) Color(0xFF2E7D32) else GrisTexto,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
