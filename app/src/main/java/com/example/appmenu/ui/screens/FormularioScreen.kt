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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.graphics.Color
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

    val preguntasDinamicas = remember {
        mutableStateListOf(PreguntaFormulario(id = 1))
    }

    var mostrarDialogoNuevaUnidad by remember { mutableStateOf(false) }
    var nuevaUnidadTitulo by remember { mutableStateOf("") }
    var nuevaUnidadOa by remember { mutableStateOf("") }
    var errorModalUnidad by remember { mutableStateOf<String?>(null) }

    val categorias = listOf(
        "Lenguaje y Comunicación",
        "Matemática",
        "Ciencias Naturales",
        "Historia y Geografía",
        "Artes / Música"
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
        Text(
            text = if (formulario.esTarea) "Registro de Tarea" else "Registro de Actividad Didáctica",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = TextoOscuro
        )
        Text(
            text = "Escuela Marcela Paz · Formulario Oficial 2.° Básico",
            fontSize = 13.sp,
            color = GrisTexto
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = BlancoTarjeta),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Tipo de recurso a registrar:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoOscuro
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val esGuia = !formulario.esTarea
                    Surface(
                        onClick = { viewModel.actualizarEsTarea(false) },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = if (esGuia) AzulBarraSuperior.copy(alpha = 0.12f) else Color(0xFFF5F5F5),
                        border = androidx.compose.foundation.BorderStroke(
                            width = if (esGuia) 2.dp else 1.dp,
                            color = if (esGuia) AzulBarraSuperior else Color(0xFFE0E0E0)
                        )
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "📘 Guía de Ejercicios",
                                fontSize = 13.sp,
                                fontWeight = if (esGuia) FontWeight.Bold else FontWeight.Medium,
                                color = if (esGuia) AzulBarraSuperior else TextoOscuro
                            )
                        }
                    }

                    val esTarea = formulario.esTarea
                    Surface(
                        onClick = { viewModel.actualizarEsTarea(true) },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = if (esTarea) Color(0xFFFB8C00).copy(alpha = 0.12f) else Color(0xFFF5F5F5),
                        border = androidx.compose.foundation.BorderStroke(
                            width = if (esTarea) 2.dp else 1.dp,
                            color = if (esTarea) Color(0xFFFB8C00) else Color(0xFFE0E0E0)
                        )
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "📝 Tarea Escolar",
                                fontSize = 13.sp,
                                fontWeight = if (esTarea) FontWeight.Bold else FontWeight.Medium,
                                color = if (esTarea) Color(0xFFE65100) else TextoOscuro
                            )
                        }
                    }
                }

                HorizontalDivider(color = Color(0xFFF1F5F9))

                ExposedDropdownMenuBox(
                    expanded = categoriaExpandida,
                    onExpandedChange = { categoriaExpandida = !categoriaExpandida }
                ) {
                    OutlinedTextField(
                        value = formulario.categoria,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Asignatura") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoriaExpandida) },
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
                        isError = errores.categoria != null,
                        supportingText = { errores.categoria?.let { Text(it) } }
                    )

                    ExposedDropdownMenu(
                        expanded = categoriaExpandida,
                        onDismissRequest = { categoriaExpandida = false }
                    ) {
                        categorias.forEach { categoria ->
                            DropdownMenuItem(
                                text = { Text(categoria) },
                                onClick = {
                                    viewModel.actualizarCategoria(categoria)
                                    categoriaExpandida = false
                                }
                            )
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Unidad Temática",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextoOscuro
                        )
                        TextButton(
                            onClick = {
                                nuevaUnidadTitulo = ""
                                nuevaUnidadOa = ""
                                errorModalUnidad = null
                                mostrarDialogoNuevaUnidad = true
                            },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Nueva Unidad", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    val unidadSeleccionada = unidadesDeMateria.find { it.id == formulario.unidadId }
                    ExposedDropdownMenuBox(
                        expanded = unidadExpandida,
                        onExpandedChange = { unidadExpandida = !unidadExpandida }
                    ) {
                        OutlinedTextField(
                            value = unidadSeleccionada?.titulo ?: "Seleccionar unidad",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Unidad de Aprendizaje") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = unidadExpandida) },
                            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                        )

                        ExposedDropdownMenu(
                            expanded = unidadExpandida,
                            onDismissRequest = { unidadExpandida = false }
                        ) {
                            if (unidadesDeMateria.isEmpty()) {
                                DropdownMenuItem(
                                    text = { Text("No hay unidades creadas. Pulsa '+ Nueva Unidad'") },
                                    onClick = { unidadExpandida = false }
                                )
                            } else {
                                unidadesDeMateria.forEach { uni ->
                                    DropdownMenuItem(
                                        text = { Text(uni.titulo) },
                                        onClick = {
                                            viewModel.actualizarUnidadId(uni.id)
                                            unidadExpandida = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = formulario.nombre,
                    onValueChange = { viewModel.actualizarNombre(it) },
                    label = { Text(if (formulario.esTarea) "Título de la Tarea Escolar" else "Nombre de la Actividad / Guía") },
                    placeholder = { Text(if (formulario.esTarea) "Ej: Lectura en casa cap. 3" else "Ej: Lectura Guiada y Preguntas") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = errores.nombre != null,
                    supportingText = { errores.nombre?.let { Text(it) } }
                )

                OutlinedTextField(
                    value = formulario.descripcion,
                    onValueChange = { viewModel.actualizarDescripcion(it) },
                    label = { Text(if (formulario.esTarea) "Instrucciones de la Tarea" else "Objetivo de Aprendizaje (OA)") },
                    placeholder = { Text(if (formulario.esTarea) "Describa lo que el estudiante debe realizar en casa..." else "Ej: OA 3: Demostrar comprensión...") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    isError = errores.descripcion != null,
                    supportingText = { errores.descripcion?.let { Text(it) } }
                )

                if (!formulario.esTarea) {
                    HorizontalDivider(color = Color(0xFFF1F5F9))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.HelpOutline, contentDescription = null, tint = AzulBarraSuperior, modifier = Modifier.size(18.dp))
                            Text(
                                text = "Preguntas de la Guía (${preguntasDinamicas.size})",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextoOscuro
                            )
                        }
                        TextButton(
                            onClick = {
                                val nuevoId = (preguntasDinamicas.maxOfOrNull { it.id } ?: 0) + 1
                                preguntasDinamicas.add(PreguntaFormulario(id = nuevoId))
                            },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Añadir Pregunta", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    preguntasDinamicas.forEachIndexed { index, pregunta ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Pregunta #${index + 1}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AzulBarraSuperior
                                    )
                                    if (preguntasDinamicas.size > 1) {
                                        IconButton(
                                            onClick = { preguntasDinamicas.removeAt(index) },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(Icons.Default.Delete, contentDescription = "Eliminar pregunta", tint = Color.Red, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }

                                OutlinedTextField(
                                    value = pregunta.enunciado,
                                    onValueChange = { nuevoValor ->
                                        preguntasDinamicas[index] = pregunta.copy(enunciado = nuevoValor)
                                    },
                                    label = { Text("Enunciado de la Pregunta") },
                                    placeholder = { Text("Ej: ¿Qué valor importante aprendemos...?") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )

                                OutlinedTextField(
                                    value = pregunta.opcion1,
                                    onValueChange = { nuevoValor ->
                                        preguntasDinamicas[index] = pregunta.copy(opcion1 = nuevoValor)
                                    },
                                    label = { Text("Alternativa 1 (Escriba aqui la alternativa correcta)") },
                                    placeholder = { Text("Ej: Compartir con respeto") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )

                                OutlinedTextField(
                                    value = pregunta.opcion2,
                                    onValueChange = { nuevoValor ->
                                        preguntasDinamicas[index] = pregunta.copy(opcion2 = nuevoValor)
                                    },
                                    label = { Text("Alternativa 2") },
                                    placeholder = { Text("Ej: Quedarse solo") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )

                                OutlinedTextField(
                                    value = pregunta.opcion3,
                                    onValueChange = { nuevoValor ->
                                        preguntasDinamicas[index] = pregunta.copy(opcion3 = nuevoValor)
                                    },
                                    label = { Text("Alternativa 3") },
                                    placeholder = { Text("Ej: Pelear con compañeros") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = formulario.cantidad,
                    onValueChange = { viewModel.actualizarCantidad(it) },
                    label = { Text(if (formulario.esTarea) "Código de Tarea (1000 - 10000)" else "Código de Guía (1000 - 10000)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = errores.cantidad != null,
                    supportingText = {
                        Text(errores.cantidad ?: "Ingrese un código numérico entre 1000 y 10000")
                    }
                )

                val fechaTexto = formulario.fecha?.let {
                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(it))
                } ?: "Seleccionar fecha"

                OutlinedTextField(
                    value = fechaTexto,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(if (formulario.esTarea) "Fecha Límite de Entrega" else "Fecha de Aplicación") },
                    trailingIcon = {
                        IconButton(onClick = { mostrarDatePicker = true }) {
                            Icon(Icons.Default.CalendarMonth, contentDescription = "Seleccionar fecha")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    isError = errores.fecha != null,
                    supportingText = { errores.fecha?.let { Text(it) } }
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = formulario.aceptaTerminos,
                        onCheckedChange = { viewModel.actualizarAceptaTerminos(it) },
                        colors = CheckboxDefaults.colors(checkedColor = AzulBarraSuperior)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (formulario.esTarea) "Tarea aprobada para enviar a 2.° Básico" else "Actividad pedagógica aprobada para 2.° Básico",
                        fontSize = 13.sp,
                        color = TextoOscuro
                    )
                }
                if (errores.aceptaTerminos != null) {
                    Text(
                        text = errores.aceptaTerminos!!,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (formulario.esTarea) "Tarea visible en el portal de estudiantes" else "Guía disponible para estudiantes",
                        fontSize = 13.sp,
                        color = TextoOscuro
                    )
                    Switch(
                        checked = formulario.activo,
                        onCheckedChange = { viewModel.actualizarActivo(it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = AzulBarraSuperior)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Button(
                    onClick = {
                        if (formulario.esTarea) {
                            val exito = viewModel.guardar()
                            if (exito) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                Toast.makeText(
                                    context,
                                    "¡Tarea escolar creada exitosamente!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            // [CORRECCIÓN DUPLICADOS]: Guardamos EXACTAMENTE UNA tarjeta de actividad para la guía
                            val esValido = viewModel.validarFormulario()
                            if (esValido) {
                                val materiaId = ActividadesManager.mapearCategoriaAId(formulario.categoria)
                                val unidadIdFinal = if (formulario.unidadId.isNotBlank()) {
                                    formulario.unidadId
                                } else {
                                    val unidadExistente = ActividadesManager.obtenerUnidadesPorMateria(materiaId).firstOrNull()
                                    unidadExistente?.id ?: "uni_${materiaId}_1"
                                }

                                // Empaquetamos todas las preguntas dinámicas en una cadena estructurada para que la guía sea 1 sola actividad
                                val paquetePreguntas = preguntasDinamicas.joinToString("|||") { p ->
                                    val texto = p.enunciado.ifBlank { "¿Cuál es la respuesta correcta?" }
                                    val o1 = p.opcion1.ifBlank { "Alternativa A " }
                                    val o2 = p.opcion2.ifBlank { "Alternativa B" }
                                    val o3 = p.opcion3.ifBlank { "Alternativa C" }
                                    "$texto;;;$o1;;;$o2;;;$o3"
                                }

                                // Opciones de la primera pregunta para compatibilidad directa
                                val primeraPregunta = preguntasDinamicas.firstOrNull()
                                val op1 = primeraPregunta?.opcion1?.ifBlank { "Opción 1" } ?: "Opción 1"
                                val op2 = primeraPregunta?.opcion2?.ifBlank { "Opción 2" } ?: "Opción 2"
                                val op3 = primeraPregunta?.opcion3?.ifBlank { "Opción 3" } ?: "Opción 3"

                                // Se agrega UNA SOLA actividad a la unidad (así no se duplica en la lista)
                                ActividadesManager.agregarEjercicio(
                                    materiaId = materiaId,
                                    unidadId = unidadIdFinal,
                                    titulo = formulario.nombre,
                                    oa = formulario.descripcion,
                                    codigoGuia = formulario.cantidad,
                                    pregunta = paquetePreguntas,
                                    opciones = listOf(op1, op2, op3),
                                    respuestaCorrectaIndex = 0,
                                    disponible = formulario.activo,
                                    esTarea = false,
                                    fechaEntrega = formulario.fecha
                                )

                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                Toast.makeText(
                                    context,
                                    "¡Guía guardada exitosamente!",
                                    Toast.LENGTH_SHORT
                                ).show()

                                // Reiniciamos campos
                                viewModel.actualizarNombre("")
                                viewModel.actualizarDescripcion("")
                                viewModel.actualizarCantidad("")
                                preguntasDinamicas.clear()
                                preguntasDinamicas.add(PreguntaFormulario(id = 1))
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (formulario.esTarea) Color(0xFFFB8C00) else AzulBarraSuperior
                    )
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (formulario.esTarea) "Guardar Tarea Escolar" else "Guardar Actividad Didáctica",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    if (mostrarDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = formulario.fecha)
        DatePickerDialog(
            onDismissRequest = { mostrarDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.actualizarFecha(datePickerState.selectedDateMillis)
                    mostrarDatePicker = false
                }) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (mostrarDialogoNuevaUnidad) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoNuevaUnidad = false },
            title = {
                Text("Nueva Unidad para ${formulario.categoria.ifBlank { "Asignatura" }}", fontWeight = FontWeight.Bold, fontSize = 17.sp)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Crea una nueva unidad temática y se asociará inmediatamente a este registro.",
                        fontSize = 12.sp,
                        color = GrisTexto
                    )
                    OutlinedTextField(
                        value = nuevaUnidadTitulo,
                        onValueChange = { nuevaUnidadTitulo = it },
                        label = { Text("Título de la Unidad") },
                        placeholder = { Text("Ej: Comprensión y Fábulas") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = nuevaUnidadOa,
                        onValueChange = { nuevaUnidadOa = it },
                        label = { Text("Objetivo de Aprendizaje (OA)") },
                        placeholder = { Text("Ej: OA 2, OA 5") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                    errorModalUnidad?.let {
                        Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (nuevaUnidadTitulo.isBlank() || nuevaUnidadOa.isBlank()) {
                            errorModalUnidad = "Ingrese título y objetivo de aprendizaje."
                        } else {
                            val matId = ActividadesManager.mapearCategoriaAId(formulario.categoria)
                            val nuevaUnidad = ActividadesManager.agregarUnidad(
                                materiaId = matId,
                                titulo = nuevaUnidadTitulo,
                                oa = nuevaUnidadOa
                            )
                            viewModel.actualizarUnidadId(nuevaUnidad.id)
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            Toast.makeText(context, "¡Unidad creada y asociada!", Toast.LENGTH_SHORT).show()
                            mostrarDialogoNuevaUnidad = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AzulBarraSuperior)
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
}