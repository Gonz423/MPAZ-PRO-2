package com.example.appmenu.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appmenu.room.Note
import com.example.appmenu.ui.theme.*
import com.example.appmenu.viewmodel.NotasViewModel
import kotlinx.coroutines.delay

// pantalla: cuaderno de tareas y observaciones room + validaciones + filtro dinamico

@Composable
fun NotasScreen(
    viewModel: NotasViewModel = viewModel(),
    onVolver: (() -> Unit)? = null
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    // estados de validacion visual del formulario 2
    var errorTitulo by remember { mutableStateOf<String?>(null) }
    var errorContenido by remember { mutableStateOf<String?>(null) }

    // estado del filtro dinamico bonus track d
    var filtroBusqueda by remember { mutableStateOf("") }

    // feedback haptico nativo
    val haptic = LocalHapticFeedback.current

    // flujo reactivo observable desde room
    val notes by viewModel.notas.collectAsState()

    // lista filtrada dinamicamente segun la busqueda
    val notasFiltradas = remember(notes, filtroBusqueda) {
        if (filtroBusqueda.isBlank()) {
            notes
        } else {
            notes.filter {
                it.title.contains(filtroBusqueda, ignoreCase = true) ||
                it.content.contains(filtroBusqueda, ignoreCase = true)
            }
        }
    }

    // ocultar mensaje tras 3 segundos
    LaunchedEffect(message) {
        if (message.isNotBlank()) {
            delay(3000)
            message = ""
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoApp)
            .padding(16.dp)
    ) {
        // cabecera
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (onVolver != null) {
                IconButton(onClick = onVolver) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = TextoOscuro
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
            }
            Column {
                Text(
                    text = "Cuaderno de Tareas y Avances",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoOscuro
                )
                Text(
                    text = "Escuela Marcela Paz · Observaciones generales de asignaturas y cursos",
                    fontSize = 12.sp,
                    color = GrisTexto
                )
            }
        }
        Spacer(Modifier.height(14.dp))

        // formulario 2: ingreso con validacion visual por campo
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = BlancoTarjeta),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // campo titulo con validacion visual
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        if (errorTitulo != null) errorTitulo = null
                    },
                    label = { Text("Título de la Tarea / Estudiante") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = errorTitulo != null,
                    trailingIcon = {
                        if (errorTitulo != null) {
                            Icon(Icons.Default.Error, contentDescription = "Error", tint = MaterialTheme.colorScheme.error)
                        }
                    },
                    supportingText = {
                        errorTitulo?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                    }
                )

                // campo contenido con validacion visual
                OutlinedTextField(
                    value = content,
                    onValueChange = {
                        content = it
                        if (errorContenido != null) errorContenido = null
                    },
                    label = { Text("Observación o Avance del Objetivo (OA)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    isError = errorContenido != null,
                    trailingIcon = {
                        if (errorContenido != null) {
                            Icon(Icons.Default.Error, contentDescription = "Error", tint = MaterialTheme.colorScheme.error)
                        }
                    },
                    supportingText = {
                        errorContenido?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                    }
                )

                Button(
                    onClick = {
                        // validacion de campos
                        var esValido = true
                        if (title.isBlank()) {
                            errorTitulo = "El título o estudiante es obligatorio"
                            esValido = false
                        } else if (title.trim().length < 3) {
                            errorTitulo = "El título debe tener al menos 3 caracteres"
                            esValido = false
                        }

                        if (content.isBlank()) {
                            errorContenido = "La observación pedagógica es obligatoria"
                            esValido = false
                        } else if (content.trim().length < 5) {
                            errorContenido = "La observación debe tener al menos 5 caracteres"
                            esValido = false
                        }

                        if (esValido) {
                            viewModel.guardarNota(title, content) { exito, msg ->
                                if (exito) {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    message = msg
                                    title = ""
                                    content = ""
                                    errorTitulo = null
                                    errorContenido = null
                                } else {
                                    message = msg
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(46.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AzulBarraSuperior)
                ) {
                    Icon(Icons.Default.NoteAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Guardar Registro")
                }

                // animacion de feedback de confirmacion
                AnimatedVisibility(
                    visible = message.isNotBlank(),
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Text(
                        text = message,
                        color = Color(0xFF2E7D32),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        // barra de busqueda y filtro dinamico bonus track d
        OutlinedTextField(
            value = filtroBusqueda,
            onValueChange = { filtroBusqueda = it },
            placeholder = { Text("Filtrar registros en tiempo real...") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Buscar", tint = AzulBarraSuperior)
            },
            trailingIcon = {
                if (filtroBusqueda.isNotBlank()) {
                    IconButton(onClick = { filtroBusqueda = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "Limpiar filtro")
                    }
                }
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = BlancoTarjeta,
                unfocusedContainerColor = BlancoTarjeta
            )
        )

        Spacer(Modifier.height(10.dp))

        // encabezado de la lista con conteo
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (filtroBusqueda.isBlank()) {
                    "Registros guardados (${notes.size})"
                } else {
                    "Resultados (${notasFiltradas.size} de ${notes.size})"
                },
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextoOscuro
            )
        }
        Spacer(Modifier.height(6.dp))

        // lista reactiva de room
        if (notes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Aún no hay registros en la base de datos.\nIngresa el primero con el formulario de arriba.",
                    color = GrisTexto,
                    fontSize = 14.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        } else if (notasFiltradas.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No se encontraron coincidencias para \"$filtroBusqueda\".",
                    color = GrisTexto,
                    fontSize = 14.sp
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(items = notasFiltradas, key = { it.id }) { note ->
                    ItemNotaCard(
                        note = note,
                        onEliminar = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            viewModel.eliminarNota(note)
                            message = "Registro eliminado correctamente."
                        }
                    )
                }
            }
        }
    }
}

// componente modular para mostrar cada tarjeta de nota individual
@Composable
fun ItemNotaCard(
    note: Note,
    onEliminar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = BlancoTarjeta),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = note.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoOscuro
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = note.content,
                    fontSize = 13.sp,
                    color = GrisTexto
                )
            }

            IconButton(onClick = onEliminar) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = Color(0xFFEF5350)
                )
            }
        }
    }
}