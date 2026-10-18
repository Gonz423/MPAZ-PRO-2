package com.example.appmenu.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appmenu.room.Note
import com.example.appmenu.ui.theme.*
import com.example.appmenu.viewmodel.NotasViewModel

// PANTALLA: CUADERNO DE TAREAS Y OBSERVACIONES

@Composable
fun NotasScreen(
    viewModel: NotasViewModel = viewModel()
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    // Flujo reactivo observable desde Room
    val notes by viewModel.notas.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoApp)
            .padding(16.dp)
    ) {
        // Cabecera
        Text(
            text = "Cuaderno de Tareas y Avances",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = TextoOscuro
        )
        Text(
            text = "Escuela Marcela Paz · Registro Pedagógico Local (Room)",
            fontSize = 13.sp,
            color = GrisTexto
        )
        Spacer(Modifier.height(14.dp))

        // Formulario de ingreso rapido de nota
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
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título de la Tarea / Estudiante") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Observación o Avance del Objetivo (OA)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )

                Button(
                    onClick = {
                        viewModel.guardarNota(title, content) { exito, msg ->
                            message = msg
                            if (exito) {
                                title = ""
                                content = ""
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

                if (message.isNotBlank()) {
                    Text(
                        text = message,
                        color = AzulBarraSuperior,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Lista de notas persistidas en Room
        Text(
            text = "Registros guardados (${notes.size})",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = TextoOscuro
        )
        Spacer(Modifier.height(8.dp))

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
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(items = notes, key = { it.id }) { note ->
                    ItemNotaCard(
                        note = note,
                        onEliminar = {
                            viewModel.eliminarNota(note)
                            message = "Registro eliminado."
                        }
                    )
                }
            }
        }
    }
}

// Componente modular para mostrar cada tarjeta de nota individual
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