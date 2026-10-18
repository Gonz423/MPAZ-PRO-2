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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appmenu.ui.theme.*
import com.example.appmenu.viewmodel.FormularioViewModel
import java.util.Date
import java.util.Locale

// PANTALLA: FORMULARIO DE REGISTRO DE ACTIVIDADES

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioScreen(
    viewModel: FormularioViewModel = viewModel()
) {
    val context = LocalContext.current
    val formulario by viewModel.formulario.collectAsState()
    val errores by viewModel.errores.collectAsState()

    var mostrarDatePicker by remember { mutableStateOf(false) }
    var categoriaExpandida by remember { mutableStateOf(false) }

    // Lista modular de asignaturas
    val categorias = listOf(
        "Lenguaje y Comunicación",
        "Matemática",
        "Ciencias Naturales",
        "Historia y Geografía",
        "Artes / Música"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoApp)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Cabecera
        Text(
            text = "Registro de Actividad Didáctica",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = TextoOscuro
        )
        Text(
            text = "Escuela Marcela Paz · Formulario Oficial 2.° Básico",
            fontSize = 13.sp,
            color = GrisTexto
        )

        // Tarjeta contenedora del formulario
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
                // Nombre de la actividad
                OutlinedTextField(
                    value = formulario.nombre,
                    onValueChange = { viewModel.actualizarNombre(it) },
                    label = { Text("Nombre de la Actividad / Guía") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = errores.nombre != null,
                    supportingText = { errores.nombre?.let { Text(it) } }
                )

                // Descripcion / Objetivo de Aprendizaje
                OutlinedTextField(
                    value = formulario.descripcion,
                    onValueChange = { viewModel.actualizarDescripcion(it) },
                    label = { Text("Objetivo de Aprendizaje (OA)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    isError = errores.descripcion != null,
                    supportingText = { errores.descripcion?.let { Text(it) } }
                )

                // Cantidad / Codigo numerico
                OutlinedTextField(
                    value = formulario.cantidad,
                    onValueChange = { viewModel.actualizarCantidad(it) },
                    label = { Text("Codigo de Guia (1000 - 10000)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = errores.cantidad != null,
                    supportingText = {
                        Text(errores.cantidad ?: "Ingrese un código numérico entre 1000 y 10000")
                    }
                )

                // Selector de Asignatura
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
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
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

                // Selector de Fecha
                val fechaTexto = formulario.fecha?.let {
                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(it))
                } ?: "Seleccionar fecha"

                OutlinedTextField(
                    value = fechaTexto,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Fecha de Aplicación") },
                    trailingIcon = {
                        IconButton(onClick = { mostrarDatePicker = true }) {
                            Icon(Icons.Default.CalendarMonth, contentDescription = "Elegir fecha", tint = AzulBarraSuperior)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    isError = errores.fecha != null,
                    supportingText = { errores.fecha?.let { Text(it) } }
                )

                // Checkbox
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = formulario.aceptaTerminos,
                        onCheckedChange = { viewModel.actualizarAceptaTerminos(it) },
                        colors = CheckboxDefaults.colors(checkedColor = AzulBarraSuperior)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Actividad revisada y aprobada para 2.° Básico",
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

                // Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Guía disponible para estudiantes",
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

                // Boton Guardar
                Button(
                    onClick = {
                        val exito = viewModel.guardar()
                        if (exito) {
                            Toast.makeText(context, "¡Actividad guardada exitosamente!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AzulBarraSuperior)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Guardar Actividad Pedagógica", fontSize = 15.sp)
                }
            }
        }
    }

    // Modal de DatePicker
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
}
