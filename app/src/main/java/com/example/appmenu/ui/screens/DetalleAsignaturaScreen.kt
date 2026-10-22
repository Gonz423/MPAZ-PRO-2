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
import com.example.appmenu.ui.componentes.TarjetaEjercicioItem
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

    val asignatura = ConfiguracionEducativa.asignaturas.find {
        it.id.equals(materiaId, ignoreCase = true)
    } ?: ConfiguracionEducativa.asignaturas.first()

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
                BannerAsignatura(
                    asignatura = asignatura,
                    esDocente = esDocente,
                    ejerciciosCompletados = ejerciciosCompletados,
                    totalEjercicios = ejerciciosMateria.size,
                    progresoCalculado = progresoCalculado,
                    onAgregarUnidad = {
                        nuevaUnidadTitulo = ""
                        nuevaUnidadOa = ""
                        errorModal = null
                        mostrarDialogoNuevaUnidad = true
                    },
                    onAgregarActividad = {
                        nuevoEjercicioTitulo = ""
                        nuevoEjercicioOa = ""
                        nuevoEjercicioCodigo = ""
                        unidadSeleccionadaId = unidadesMateria.firstOrNull()?.id ?: ""
                        errorModal = null
                        mostrarDialogoNuevoEjercicio = true
                    },
                    onAbrirFormulario = { onNavegarFormulario(asignatura.nombre) }
                )
            }

            // 2. cabecera de seccion unidades
            item {
                CabeceraUnidades(
                    cantidadUnidades = unidadesMateria.size,
                    cantidadEjercicios = ejerciciosMateria.size
                )
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

            // 3. lista de unidades con acordeon
            items(items = unidadesMateria, key = { it.id }) { unidad ->
                val actividadesDeEstaUnidad = remember(ejerciciosMateria, unidad.id) {
                    ejerciciosMateria.filter { it.unidadId == unidad.id }
                }
                val estaExpandida = unidadesExpandidas.contains(unidad.id)

                TarjetaUnidad(
                    unidad = unidad,
                    colorAsignatura = asignatura.color,
                    estaExpandida = estaExpandida,
                    actividadesDeEstaUnidad = actividadesDeEstaUnidad,
                    esDocente = esDocente,
                    onToggleExpansion = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        unidadesExpandidas = if (estaExpandida) {
                            unidadesExpandidas - unidad.id
                        } else {
                            unidadesExpandidas + unidad.id
                        }
                    },
                    onRealizarActividad = { id -> onAbrirActividad(id) },
                    onToggleCompletado = { ejercicio ->
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

    // dialogos modales del docente
    if (mostrarDialogoNuevaUnidad) {
        DialogoNuevaUnidad(
            asignaturaNombre = asignatura.nombre,
            colorAsignatura = asignatura.color,
            titulo = nuevaUnidadTitulo,
            oa = nuevaUnidadOa,
            errorModal = errorModal,
            onTituloChange = { nuevaUnidadTitulo = it },
            onOaChange = { nuevaUnidadOa = it },
            onCrear = {
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
            onCancelar = { mostrarDialogoNuevaUnidad = false }
        )
    }

    if (mostrarDialogoNuevoEjercicio) {
        DialogoNuevoEjercicio(
            colorAsignatura = asignatura.color,
            unidadesMateria = unidadesMateria,
            titulo = nuevoEjercicioTitulo,
            oa = nuevoEjercicioOa,
            codigo = nuevoEjercicioCodigo,
            unidadSeleccionadaId = unidadSeleccionadaId,
            errorModal = errorModal,
            onTituloChange = { nuevoEjercicioTitulo = it },
            onOaChange = { nuevoEjercicioOa = it },
            onCodigoChange = { if (it.all { c -> c.isDigit() }) nuevoEjercicioCodigo = it },
            onUnidadChange = { unidadSeleccionadaId = it },
            onGuardar = {
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
            onCancelar = { mostrarDialogoNuevoEjercicio = false }
        )
    }
}

// --- COMPOSABLES PRIVADOS ---

// banner superior: identidad de la asignatura + barra de progreso + botones docente
@Composable
private fun BannerAsignatura(
    asignatura: com.example.appmenu.model.TarjetaAsignatura,
    esDocente: Boolean,
    ejerciciosCompletados: Int,
    totalEjercicios: Int,
    progresoCalculado: Float,
    onAgregarUnidad: () -> Unit,
    onAgregarActividad: () -> Unit,
    onAbrirFormulario: () -> Unit
) {
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

            // barra de progreso
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Progreso en la materia", fontSize = 12.sp, color = GrisTexto, fontWeight = FontWeight.Medium)
                    Text(
                        text = "$ejerciciosCompletados de $totalEjercicios completados",
                        fontSize = 12.sp,
                        color = asignatura.color,
                        fontWeight = FontWeight.Bold
                    )
                }
                LinearProgressIndicator(
                    progress = { progresoCalculado },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                    color = asignatura.color,
                    trackColor = Color(0xFFE0E0E0)
                )
            }

            // botones del docente
            if (esDocente) {
                HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 1.dp)
                Text(text = "Acciones del Docente", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextoOscuro)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onAgregarUnidad,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = asignatura.color)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("+ Unidad", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = onAgregarActividad,
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
                    onClick = onAbrirFormulario,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Abrir Formulario Oficial de Registro", fontSize = 12.sp)
                }
            }
        }
    }
}

// cabecera de la seccion de unidades
@Composable
private fun CabeceraUnidades(cantidadUnidades: Int, cantidadEjercicios: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Unidades Didácticas ($cantidadUnidades)",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = TextoOscuro
        )
        Text(text = "$cantidadEjercicios actividades", fontSize = 13.sp, color = GrisTexto)
    }
}

// tarjeta de cada unidad con acordeon expandible
@Composable
private fun TarjetaUnidad(
    unidad: UnidadEducativa,
    colorAsignatura: Color,
    estaExpandida: Boolean,
    actividadesDeEstaUnidad: List<EjercicioEducativo>,
    esDocente: Boolean,
    onToggleExpansion: () -> Unit,
    onRealizarActividad: (String) -> Unit,
    onToggleCompletado: (EjercicioEducativo) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (estaExpandida) BlancoTarjeta else Color(0xFFFBFBFB)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (estaExpandida) 2.dp else 1.dp),
        border = if (estaExpandida) androidx.compose.foundation.BorderStroke(1.dp, colorAsignatura.copy(alpha = 0.35f)) else null
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // cabecera clickable de la unidad
            Row(
                modifier = Modifier.fillMaxWidth().clickable { onToggleExpansion() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(42.dp).clip(CircleShape).background(colorAsignatura.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.Bookmark, contentDescription = null, tint = colorAsignatura, modifier = Modifier.size(24.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = unidad.titulo, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextoOscuro)
                    Text(text = unidad.objetivoAprendizaje, fontSize = 12.sp, color = GrisTexto)
                }
                Surface(
                    shape = CircleShape,
                    color = if (estaExpandida) colorAsignatura.copy(alpha = 0.12f) else Color(0xFFEEEEEE),
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (estaExpandida) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = if (estaExpandida) "Esconder ejercicios" else "Mostrar ejercicios",
                            tint = if (estaExpandida) colorAsignatura else GrisTexto,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // cuerpo animado con ejercicios
            AnimatedVisibility(
                visible = estaExpandida,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(modifier = Modifier.padding(top = 4.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 1.dp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Ejercicios Didácticos (${actividadesDeEstaUnidad.size})",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorAsignatura
                        )
                        Surface(color = colorAsignatura.copy(alpha = 0.10f), shape = RoundedCornerShape(6.dp)) {
                            Text(
                                text = "${actividadesDeEstaUnidad.count { it.completado }}/${actividadesDeEstaUnidad.size} listos",
                                color = colorAsignatura,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

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
                                    colorAsignatura = colorAsignatura,
                                    esDocente = esDocente,
                                    onRealizar = { onRealizarActividad(ejercicio.id) },
                                    onToggleCompletado = { onToggleCompletado(ejercicio) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// dialogo: crear nueva unidad
@Composable
private fun DialogoNuevaUnidad(
    asignaturaNombre: String,
    colorAsignatura: Color,
    titulo: String,
    oa: String,
    errorModal: String?,
    onTituloChange: (String) -> Unit,
    onOaChange: (String) -> Unit,
    onCrear: () -> Unit,
    onCancelar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Crear Nueva Unidad en $asignaturaNombre", fontWeight = FontWeight.Bold, fontSize = 17.sp) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "El sistema numerará la unidad automáticamente y la asociará a $asignaturaNombre.",
                    fontSize = 12.sp,
                    color = GrisTexto
                )
                OutlinedTextField(
                    value = titulo,
                    onValueChange = onTituloChange,
                    label = { Text("Título de la Unidad") },
                    placeholder = { Text("Ej: Comprensión Poética") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = oa,
                    onValueChange = onOaChange,
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
            Button(onClick = onCrear, colors = ButtonDefaults.buttonColors(containerColor = colorAsignatura)) {
                Text("Crear Unidad")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) { Text("Cancelar") }
        }
    )
}

// dialogo: crear nueva actividad asociada a una unidad
@Composable
private fun DialogoNuevoEjercicio(
    colorAsignatura: Color,
    unidadesMateria: List<UnidadEducativa>,
    titulo: String,
    oa: String,
    codigo: String,
    unidadSeleccionadaId: String,
    errorModal: String?,
    onTituloChange: (String) -> Unit,
    onOaChange: (String) -> Unit,
    onCodigoChange: (String) -> Unit,
    onUnidadChange: (String) -> Unit,
    onGuardar: () -> Unit,
    onCancelar: () -> Unit
) {
    var dropdownExpandido by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Nueva Actividad Didáctica", fontWeight = FontWeight.Bold, fontSize = 17.sp) },
        text = {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
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
                    DropdownMenu(expanded = dropdownExpandido, onDismissRequest = { dropdownExpandido = false }) {
                        unidadesMateria.forEach { uni ->
                            DropdownMenuItem(
                                text = { Text(uni.titulo) },
                                onClick = { onUnidadChange(uni.id); dropdownExpandido = false }
                            )
                        }
                    }
                }
                OutlinedTextField(
                    value = titulo, onValueChange = onTituloChange,
                    label = { Text("Título de la Actividad") },
                    modifier = Modifier.fillMaxWidth(), singleLine = true
                )
                OutlinedTextField(
                    value = oa, onValueChange = onOaChange,
                    label = { Text("Objetivo de Aprendizaje (OA)") },
                    modifier = Modifier.fillMaxWidth(), minLines = 2
                )
                OutlinedTextField(
                    value = codigo, onValueChange = onCodigoChange,
                    label = { Text("Código de Guía (ej. 1045)") },
                    modifier = Modifier.fillMaxWidth(), singleLine = true
                )
                errorModal?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            Button(onClick = onGuardar, colors = ButtonDefaults.buttonColors(containerColor = colorAsignatura)) {
                Text("Guardar Actividad")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) { Text("Cancelar") }
        }
    )
}
