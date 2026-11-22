package com.example.appmenu.ui.screens

import android.icu.text.SimpleDateFormat
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appmenu.model.ActividadesManager
import com.example.appmenu.ui.theme.*
import com.example.appmenu.viewmodel.FormularioViewModel
import java.util.Date
import java.util.Locale

data class PreguntaFormulario(
    val id: Int,
    var enunciado: String = "",
    var opcion1: String = "",
    var opcion2: String = "",
    var opcion3: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioScreen(
    categoriaInicial: String? = null,
    viewModel: FormularioViewModel = viewModel()
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val formulario by viewModel.formulario.collectAsState()
    val errores by viewModel.errores.collectAsState()

    val todasLasUnidades by ActividadesManager.unidades.collectAsState()

    LaunchedEffect(categoriaInicial) {
        if (!categoriaInicial.isNullOrBlank()) {
            val categoriaNormalizada = when {
                categoriaInicial.contains("Lenguaje", ignoreCase = true) -> "Lenguaje y Comunicación"
                categoriaInicial.contains("Matemática", ignoreCase = true) || categoriaInicial.contains("Matematica", ignoreCase = true) -> "Matemática"
                categoriaInicial.contains("Ciencias", ignoreCase = true) -> "Ciencias Naturales"
                categoriaInicial.contains("Historia", ignoreCase = true) -> "Historia y Geografía"
                else -> categoriaInicial
            }
            viewModel.actualizarCategoria(categoriaNormalizada)
        }
    }

    var mostrarDatePicker by remember { mutableStateOf(false) }
    var categoriaExpandida by remember { mutableStateOf(false) }
    var unidadExpandida by remember { mutableStateOf(false) }
    val preguntasDinamicas = remember { mutableStateListOf(PreguntaFormulario(id = 1)) }
    var mostrarDialogoNuevaUnidad by remember { mutableStateOf(false) }
    var nuevaUnidadTitulo by remember { mutableStateOf("") }
    var nuevaUnidadOa by remember { mutableStateOf("") }
    var errorModalUnidad by remember { mutableStateOf<String?>(null) }

    val categorias = listOf(
        "Lenguaje y Comunicación", "Matemática",
        "Ciencias Naturales", "Historia y Geografía", "Artes / Música"
    )

    val materiaIdActual = remember(formulario.categoria) {
        ActividadesManager.mapearCategoriaAId(formulario.categoria)
    }
    val unidadesDeMateria = remember(todasLasUnidades, materiaIdActual) {
        todasLasUnidades.filter { it.materiaId == materiaIdActual }
    }

    LaunchedEffect(unidadesDeMateria) {
        if (formulario.unidadId.isBlank() && unidadesDeMateria.isNotEmpty()) {
            viewModel.actualizarUnidadId(unidadesDeMateria.first().id)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoApp)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // encabezado de la pantalla
        EncabezadoFormulario(esTarea = formulario.esTarea)

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = BlancoTarjeta),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // selector tipo: guia o tarea
                SelectorTipoRecurso(
                    esTarea = formulario.esTarea,
                    onSeleccionarGuia = { viewModel.actualizarEsTarea(false) },
                    onSeleccionarTarea = { viewModel.actualizarEsTarea(true) }
                )

                HorizontalDivider(color = Color(0xFFF1F5F9))

                // selector de asignatura
                SelectorAsignatura(
                    categoriaActual = formulario.categoria,
                    categoriaExpandida = categoriaExpandida,
                    categorias = categorias,
                    errorCategoria = errores.categoria,
                    onExpandir = { categoriaExpandida = !categoriaExpandida },
                    onCerrar = { categoriaExpandida = false },
                    onSeleccionar = { viewModel.actualizarCategoria(it); categoriaExpandida = false }
                )

                // selector de unidad tematica
                SelectorUnidad(
                    formularioUnidadId = formulario.unidadId,
                    unidadesDeMateria = unidadesDeMateria,
                    unidadExpandida = unidadExpandida,
                    onExpandir = { unidadExpandida = !unidadExpandida },
                    onCerrar = { unidadExpandida = false },
                    onSeleccionar = { viewModel.actualizarUnidadId(it); unidadExpandida = false },
                    onAgregarUnidad = {
                        nuevaUnidadTitulo = ""
                        nuevaUnidadOa = ""
                        errorModalUnidad = null
                        mostrarDialogoNuevaUnidad = true
                    }
                )

                // campos de texto principales
                CamposTextoFormulario(
                    formularioNombre = formulario.nombre,
                    formularioDescripcion = formulario.descripcion,
                    esTarea = formulario.esTarea,
                    errorNombre = errores.nombre,
                    errorDescripcion = errores.descripcion,
                    onNombreChange = { viewModel.actualizarNombre(it) },
                    onDescripcionChange = { viewModel.actualizarDescripcion(it) }
                )

                // seccion de preguntas dinamicas (solo guia)
                if (!formulario.esTarea) {
                    HorizontalDivider(color = Color(0xFFF1F5F9))
                    SeccionPreguntas(
                        preguntas = preguntasDinamicas,
                        onAgregarPregunta = {
                            val nuevoId = (preguntasDinamicas.maxOfOrNull { it.id } ?: 0) + 1
                            preguntasDinamicas.add(PreguntaFormulario(id = nuevoId))
                        },
                        onEliminarPregunta = { index -> preguntasDinamicas.removeAt(index) },
                        onEditarPregunta = { index, nueva -> preguntasDinamicas[index] = nueva }
                    )
                }

                // campo codigo
                CampoCodigo(
                    valor = formulario.cantidad,
                    esTarea = formulario.esTarea,
                    error = errores.cantidad,
                    onCambio = { viewModel.actualizarCantidad(it) }
                )

                // selector de fecha
                CampoFecha(
                    fecha = formulario.fecha,
                    esTarea = formulario.esTarea,
                    error = errores.fecha,
                    onAbrirDatePicker = { mostrarDatePicker = true }
                )

                // checkbox aprobacion
                CheckboxAprobacion(
                    aceptaTerminos = formulario.aceptaTerminos,
                    esTarea = formulario.esTarea,
                    error = errores.aceptaTerminos,
                    onCambio = { viewModel.actualizarAceptaTerminos(it) }
                )

                // switch visibilidad
                SwitchVisibilidad(
                    activo = formulario.activo,
                    esTarea = formulario.esTarea,
                    onCambio = { viewModel.actualizarActivo(it) }
                )

                Spacer(modifier = Modifier.height(6.dp))

                // boton guardar
                BotonGuardar(
                    esTarea = formulario.esTarea,
                    onGuardar = {
                        if (formulario.esTarea) {
                            val exito = viewModel.guardar()
                            if (exito) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                Toast.makeText(context, "¡Tarea escolar creada exitosamente!", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            val esValido = viewModel.validarFormulario()
                            if (esValido) {
                                val matId = ActividadesManager.mapearCategoriaAId(formulario.categoria)
                                val unidadIdFinal = if (formulario.unidadId.isNotBlank()) formulario.unidadId
                                else ActividadesManager.obtenerUnidadesPorMateria(matId).firstOrNull()?.id ?: "uni_${matId}_1"

                                val paquetePreguntas = preguntasDinamicas.joinToString("|||") { p ->
                                    val texto = p.enunciado.ifBlank { "¿Cuál es la respuesta correcta?" }
                                    val o1 = p.opcion1.ifBlank { "Alternativa A " }
                                    val o2 = p.opcion2.ifBlank { "Alternativa B" }
                                    val o3 = p.opcion3.ifBlank { "Alternativa C" }
                                    "$texto;;;$o1;;;$o2;;;$o3"
                                }
                                val primera = preguntasDinamicas.firstOrNull()
                                ActividadesManager.agregarEjercicio(
                                    materiaId = matId,
                                    unidadId = unidadIdFinal,
                                    titulo = formulario.nombre,
                                    oa = formulario.descripcion,
                                    codigoGuia = formulario.cantidad,
                                    pregunta = paquetePreguntas,
                                    opciones = listOf(
                                        primera?.opcion1?.ifBlank { "Opción 1" } ?: "Opción 1",
                                        primera?.opcion2?.ifBlank { "Opción 2" } ?: "Opción 2",
                                        primera?.opcion3?.ifBlank { "Opción 3" } ?: "Opción 3"
                                    ),
                                    respuestaCorrectaIndex = 0,
                                    disponible = formulario.activo,
                                    esTarea = false,
                                    fechaEntrega = formulario.fecha
                                )
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                Toast.makeText(context, "¡Guía guardada exitosamente!", Toast.LENGTH_SHORT).show()
                                viewModel.actualizarNombre("")
                                viewModel.actualizarDescripcion("")
                                viewModel.actualizarCantidad("")
                                preguntasDinamicas.clear()
                                preguntasDinamicas.add(PreguntaFormulario(id = 1))
                            }
                        }
                    }
                )
            }
        }
    }

    // date picker dialog
    if (mostrarDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = formulario.fecha)
        DatePickerDialog(
            onDismissRequest = { mostrarDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.actualizarFecha(datePickerState.selectedDateMillis)
                    mostrarDatePicker = false
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDatePicker = false }) { Text("Cancelar") }
            }
        ) { DatePicker(state = datePickerState) }
    }

    // dialogo nueva unidad
    if (mostrarDialogoNuevaUnidad) {
        DialogoNuevaUnidadFormulario(
            asignaturaNombre = formulario.categoria.ifBlank { "Asignatura" },
            titulo = nuevaUnidadTitulo,
            oa = nuevaUnidadOa,
            errorModal = errorModalUnidad,
            onTituloChange = { nuevaUnidadTitulo = it },
            onOaChange = { nuevaUnidadOa = it },
            onCrear = {
                if (nuevaUnidadTitulo.isBlank() || nuevaUnidadOa.isBlank()) {
                    errorModalUnidad = "Ingrese título y objetivo de aprendizaje."
                } else {
                    val matId = ActividadesManager.mapearCategoriaAId(formulario.categoria)
                    val nuevaUnidad = ActividadesManager.agregarUnidad(matId, nuevaUnidadTitulo, nuevaUnidadOa)
                    viewModel.actualizarUnidadId(nuevaUnidad.id)
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    Toast.makeText(context, "¡Unidad creada y asociada!", Toast.LENGTH_SHORT).show()
                    mostrarDialogoNuevaUnidad = false
                }
            },
            onCancelar = { mostrarDialogoNuevaUnidad = false }
        )
    }
}

// --- COMPOSABLES PRIVADOS ---

@Composable
private fun EncabezadoFormulario(esTarea: Boolean) {
    Text(
        text = if (esTarea) "Registro de Tarea" else "Registro de Actividad Didáctica",
        fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextoOscuro
    )
    Text(
        text = "Escuela Marcela Paz · Formulario Oficial 2.° Básico",
        fontSize = 13.sp, color = GrisTexto
    )
}

@Composable
private fun SelectorTipoRecurso(
    esTarea: Boolean,
    onSeleccionarGuia: () -> Unit,
    onSeleccionarTarea: () -> Unit
) {
    Text(text = "Tipo de recurso a registrar:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextoOscuro)
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        val esGuia = !esTarea
        Surface(
            onClick = onSeleccionarGuia,
            modifier = Modifier.weight(1f).height(44.dp),
            shape = RoundedCornerShape(12.dp),
            color = if (esGuia) Color(0xFF8E24AA).copy(alpha = 0.12f) else Color(0xFFF5F5F5),
            border = androidx.compose.foundation.BorderStroke(
                width = if (esGuia) 2.dp else 1.dp,
                color = if (esGuia) Color(0xFF8E24AA) else Color(0xFFE0E0E0)
            )
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "📘 Guía de Ejercicios", fontSize = 13.sp,
                    fontWeight = if (esGuia) FontWeight.Bold else FontWeight.Medium,
                    color = if (esGuia) Color(0xFF8E24AA) else TextoOscuro
                )
            }
        }
        Surface(
            onClick = onSeleccionarTarea,
            modifier = Modifier.weight(1f).height(44.dp),
            shape = RoundedCornerShape(12.dp),
            color = if (esTarea) Color(0xFFFB8C00).copy(alpha = 0.12f) else Color(0xFFF5F5F5),
            border = androidx.compose.foundation.BorderStroke(
                width = if (esTarea) 2.dp else 1.dp,
                color = if (esTarea) Color(0xFFFB8C00) else Color(0xFFE0E0E0)
            )
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "📝 Tarea Escolar", fontSize = 13.sp,
                    fontWeight = if (esTarea) FontWeight.Bold else FontWeight.Medium,
                    color = if (esTarea) Color(0xFFE65100) else TextoOscuro
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectorAsignatura(
    categoriaActual: String,
    categoriaExpandida: Boolean,
    categorias: List<String>,
    errorCategoria: String?,
    onExpandir: () -> Unit,
    onCerrar: () -> Unit,
    onSeleccionar: (String) -> Unit
) {
    ExposedDropdownMenuBox(expanded = categoriaExpandida, onExpandedChange = { onExpandir() }) {
        OutlinedTextField(
            value = categoriaActual, onValueChange = {}, readOnly = true,
            label = { Text("Asignatura") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoriaExpandida) },
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
            isError = errorCategoria != null,
            supportingText = { errorCategoria?.let { Text(it) } }
        )
        ExposedDropdownMenu(expanded = categoriaExpandida, onDismissRequest = onCerrar) {
            categorias.forEach { categoria ->
                DropdownMenuItem(text = { Text(categoria) }, onClick = { onSeleccionar(categoria) })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectorUnidad(
    formularioUnidadId: String,
    unidadesDeMateria: List<com.example.appmenu.model.UnidadEducativa>,
    unidadExpandida: Boolean,
    onExpandir: () -> Unit,
    onCerrar: () -> Unit,
    onSeleccionar: (String) -> Unit,
    onAgregarUnidad: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Unidad Temática", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextoOscuro)
            TextButton(onClick = onAgregarUnidad, contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Nueva Unidad", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
        val unidadSeleccionada = unidadesDeMateria.find { it.id == formularioUnidadId }
        ExposedDropdownMenuBox(expanded = unidadExpandida, onExpandedChange = { onExpandir() }) {
            OutlinedTextField(
                value = unidadSeleccionada?.titulo ?: "Seleccionar unidad",
                onValueChange = {}, readOnly = true,
                label = { Text("Unidad de Aprendizaje") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = unidadExpandida) },
                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = unidadExpandida, onDismissRequest = onCerrar) {
                if (unidadesDeMateria.isEmpty()) {
                    DropdownMenuItem(
                        text = { Text("No hay unidades creadas. Pulsa '+ Nueva Unidad'") },
                        onClick = onCerrar
                    )
                } else {
                    unidadesDeMateria.forEach { uni ->
                        DropdownMenuItem(text = { Text(uni.titulo) }, onClick = { onSeleccionar(uni.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun CamposTextoFormulario(
    formularioNombre: String,
    formularioDescripcion: String,
    esTarea: Boolean,
    errorNombre: String?,
    errorDescripcion: String?,
    onNombreChange: (String) -> Unit,
    onDescripcionChange: (String) -> Unit
) {
    OutlinedTextField(
        value = formularioNombre, onValueChange = onNombreChange,
        label = { Text(if (esTarea) "Título de la Tarea Escolar" else "Nombre de la Actividad / Guía") },
        placeholder = { Text(if (esTarea) "Ej: Lectura en casa cap. 3" else "Ej: Lectura Guiada y Preguntas") },
        modifier = Modifier.fillMaxWidth(), singleLine = true,
        isError = errorNombre != null, supportingText = { errorNombre?.let { Text(it) } }
    )
    OutlinedTextField(
        value = formularioDescripcion, onValueChange = onDescripcionChange,
        label = { Text(if (esTarea) "Instrucciones de la Tarea" else "Objetivo de Aprendizaje (OA)") },
        placeholder = { Text(if (esTarea) "Describa lo que el estudiante debe realizar en casa..." else "Ej: OA 3: Demostrar comprensión...") },
        modifier = Modifier.fillMaxWidth(), minLines = 2,
        isError = errorDescripcion != null, supportingText = { errorDescripcion?.let { Text(it) } }
    )
}

@Composable
private fun SeccionPreguntas(
    preguntas: List<PreguntaFormulario>,
    onAgregarPregunta: () -> Unit,
    onEliminarPregunta: (Int) -> Unit,
    onEditarPregunta: (Int, PreguntaFormulario) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(Icons.Default.HelpOutline, contentDescription = null, tint = AzulBarraSuperior, modifier = Modifier.size(18.dp))
            Text(text = "Preguntas de la Guía (${preguntas.size})", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextoOscuro)
        }
        TextButton(onClick = onAgregarPregunta, contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Añadir Pregunta", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
    preguntas.forEachIndexed { index, pregunta ->
        TarjetaPregunta(
            index = index,
            pregunta = pregunta,
            puedeBorrarse = preguntas.size > 1,
            onEliminar = { onEliminarPregunta(index) },
            onEditar = { nueva -> onEditarPregunta(index, nueva) }
        )
    }
}

@Composable
private fun TarjetaPregunta(
    index: Int,
    pregunta: PreguntaFormulario,
    puedeBorrarse: Boolean,
    onEliminar: () -> Unit,
    onEditar: (PreguntaFormulario) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Pregunta #${index + 1}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AzulBarraSuperior)
                if (puedeBorrarse) {
                    IconButton(onClick = onEliminar, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar pregunta", tint = Color.Red, modifier = Modifier.size(16.dp))
                    }
                }
            }
            OutlinedTextField(
                value = pregunta.enunciado,
                onValueChange = { onEditar(pregunta.copy(enunciado = it)) },
                label = { Text("Enunciado de la Pregunta") },
                placeholder = { Text("Ej: ¿Qué valor importante aprendemos...?") },
                modifier = Modifier.fillMaxWidth(), singleLine = true
            )
            OutlinedTextField(
                value = pregunta.opcion1,
                onValueChange = { onEditar(pregunta.copy(opcion1 = it)) },
                label = { Text("Alternativa 1 (Escriba aqui la alternativa correcta)") },
                placeholder = { Text("Ej: Compartir con respeto") },
                modifier = Modifier.fillMaxWidth(), singleLine = true
            )
            OutlinedTextField(
                value = pregunta.opcion2,
                onValueChange = { onEditar(pregunta.copy(opcion2 = it)) },
                label = { Text("Alternativa 2") },
                placeholder = { Text("Ej: Quedarse solo") },
                modifier = Modifier.fillMaxWidth(), singleLine = true
            )
            OutlinedTextField(
                value = pregunta.opcion3,
                onValueChange = { onEditar(pregunta.copy(opcion3 = it)) },
                label = { Text("Alternativa 3") },
                placeholder = { Text("Ej: Pelear con compañeros") },
                modifier = Modifier.fillMaxWidth(), singleLine = true
            )
        }
    }
}

@Composable
private fun CampoCodigo(valor: String, esTarea: Boolean, error: String?, onCambio: (String) -> Unit) {
    OutlinedTextField(
        value = valor, onValueChange = onCambio,
        label = { Text(if (esTarea) "Código de Tarea (1000 - 10000)" else "Código de Guía (1000 - 10000)") },
        modifier = Modifier.fillMaxWidth(), singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        isError = error != null,
        supportingText = { Text(error ?: "Ingrese un código numérico entre 1000 y 10000") }
    )
}

@Composable
private fun CampoFecha(fecha: Long?, esTarea: Boolean, error: String?, onAbrirDatePicker: () -> Unit) {
    val fechaTexto = fecha?.let { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(it)) } ?: "Seleccionar fecha"
    OutlinedTextField(
        value = fechaTexto, onValueChange = {}, readOnly = true,
        label = { Text(if (esTarea) "Fecha Límite de Entrega" else "Fecha de Aplicación") },
        trailingIcon = {
            IconButton(onClick = onAbrirDatePicker) {
                Icon(Icons.Default.CalendarMonth, contentDescription = "Seleccionar fecha")
            }
        },
        modifier = Modifier.fillMaxWidth(),
        isError = error != null,
        supportingText = { error?.let { Text(it) } }
    )
}

@Composable
private fun CheckboxAprobacion(aceptaTerminos: Boolean, esTarea: Boolean, error: String?, onCambio: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
            checked = aceptaTerminos, onCheckedChange = onCambio,
            colors = CheckboxDefaults.colors(checkedColor = AzulBarraSuperior)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = if (esTarea) "Tarea aprobada para enviar a 2.° Básico" else "Actividad pedagógica aprobada para 2.° Básico",
            fontSize = 13.sp, color = TextoOscuro
        )
    }
    if (error != null) {
        Text(text = error, color = MaterialTheme.colorScheme.error, fontSize = 12.sp, modifier = Modifier.padding(start = 12.dp))
    }
}

@Composable
private fun SwitchVisibilidad(activo: Boolean, esTarea: Boolean, onCambio: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = if (esTarea) "Tarea visible en el portal de estudiantes" else "Guía disponible para estudiantes",
            fontSize = 13.sp, color = TextoOscuro
        )
        Switch(
            checked = activo, onCheckedChange = onCambio,
            colors = SwitchDefaults.colors(checkedThumbColor = AzulBarraSuperior)
        )
    }
}

@Composable
private fun BotonGuardar(esTarea: Boolean, onGuardar: () -> Unit) {
    Button(
        onClick = onGuardar,
        modifier = Modifier.fillMaxWidth().height(48.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (esTarea) Color(0xFFFB8C00) else Color(0xFF8E24AA)
        )
    ) {
        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = if (esTarea) "Guardar Tarea Escolar" else "Guardar Actividad Didáctica",
            fontSize = 15.sp, fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun DialogoNuevaUnidadFormulario(
    asignaturaNombre: String,
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
        title = { Text("Nueva Unidad para $asignaturaNombre", fontWeight = FontWeight.Bold, fontSize = 17.sp) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(text = "Crea una nueva unidad temática y se asociará inmediatamente a este registro.", fontSize = 12.sp, color = GrisTexto)
                OutlinedTextField(value = titulo, onValueChange = onTituloChange, label = { Text("Título de la Unidad") }, placeholder = { Text("Ej: Comprensión y Fábulas") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = oa, onValueChange = onOaChange, label = { Text("Objetivo de Aprendizaje (OA)") }, placeholder = { Text("Ej: OA 2, OA 5") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
                errorModal?.let { Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp) }
            }
        },
        confirmButton = {
            Button(onClick = onCrear, colors = ButtonDefaults.buttonColors(containerColor = AzulBarraSuperior)) { Text("Crear Unidad") }
        },
        dismissButton = { TextButton(onClick = onCancelar) { Text("Cancelar") } }
    )
}