package com.example.appmenu.ui.screens

import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appmenu.viewmodel.FormularioViewModel
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioScreen(
    viewModel: FormularioViewModel = viewModel()
) {
    val formulario by
    viewModel.formulario.collectAsState()

    val errores by
    viewModel.errores.collectAsState()

    var mostrarDatePicker by
    remember {mutableStateOf(false)
    }

    val categorias = listOf("Electrónica","Hogar","Deportes","Ropa","Otros")
    var categoriaExpandida by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth() .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "Formulario",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(
            modifier = Modifier.height(10.dp)
        )
        OutlinedTextField(
            value = formulario.nombre,
            onValueChange = { viewModel.actualizarNombre(it) },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = errores.nombre != null,
            supportingText = { errores.nombre?.let { Text(it) } }
        )
        OutlinedTextField(
            value = formulario.descripcion,
            onValueChange = { viewModel.actualizarDescripcion(it) },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            isError = errores.descripcion != null,
            supportingText = { errores.descripcion?.let { Text(it) }}
        )

        OutlinedTextField(
            value = formulario.cantidad,
            onValueChange = { viewModel.actualizarCantidad(it) },
            label = { Text("Cantidad") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions( keyboardType = KeyboardType.Number ),
            isError = errores.cantidad != null,
            supportingText = {
                if (errores.cantidad != null) { Text(errores.cantidad!! )
                } else {
                    Text("Ingrese un valor entre 1000 y 10000" ) }
            }
        )

        Row(
            verticalAlignment = Alignment.CenterVertically ) {
            Checkbox( checked = formulario.aceptaTerminos,
                onCheckedChange = { viewModel.actualizarAceptaTerminos(it) } )
            Text( text = "Acepto los términos y condiciones"
            )
        }

        if (errores.aceptaTerminos != null) {
            Text( text = errores.aceptaTerminos!!,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding( start = 12.dp )
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text( text = "Registro activo" )
            Switch( checked = formulario.activo,
                onCheckedChange = { viewModel.actualizarActivo(it) }
            )
        }

        val fechaTexto = formulario.fecha?.let { SimpleDateFormat(
            "dd/MM/yyyy",
            Locale.getDefault()
        ).format(Date(it)) } ?: "Seleccionar fecha"
        /*
                val fechaTexto = formulario.fecha?.let {
                    SimpleDateFormat(
                        "dd/MM/yyyy",
                        Locale.getDefault()
                    ).apply {
                        timeZone = java.util.TimeZone.getTimeZone("UTC")
                    }.format(Date(it))
                } ?: "Seleccionar fecha"
        */
        OutlinedTextField(
            value = fechaTexto,
            onValueChange = {},
            readOnly = true,
            label = { Text("Fecha") },
            modifier = Modifier.fillMaxWidth(),
            isError = errores.fecha != null
        )
        Button(
            onClick = { mostrarDatePicker = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Seleccionar fecha")
        }

        /*
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = fechaTexto,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Fecha") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = errores.fecha != null
                    )
                    TextButton(
                        onClick = { mostrarDatePicker = true },
                        modifier = Modifier.matchParentSize()
                    ) {
                    }
                }
        */
        if (errores.fecha != null) {
            Text( text = errores.fecha!!,
                color = MaterialTheme.colorScheme.error )
        }

        ExposedDropdownMenuBox( expanded = categoriaExpandida,
            onExpandedChange = { categoriaExpandida = !categoriaExpandida } )
        {
            OutlinedTextField( value = formulario.categoria,
                onValueChange = {},
                readOnly = true,
                label = { Text("Categoría") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoriaExpandida) },
                modifier = Modifier.fillMaxWidth(),
                isError = errores.categoria != null )

            ExposedDropdownMenu( expanded = categoriaExpandida,
                onDismissRequest = { categoriaExpandida = false })
            {
                categorias.forEach { categoria -> DropdownMenuItem(text = { Text(categoria) },
                    onClick = { viewModel.actualizarCategoria(categoria )
                        categoriaExpandida = false } )
                }
            }
        }

        if (errores.categoria != null) {
            Text( text = errores.categoria!!, color = MaterialTheme.colorScheme.error )
        }

        Spacer( modifier = Modifier.height(10.dp) )

        Button( onClick = { viewModel.guardar() },
            modifier = Modifier.fillMaxWidth() )
        {
            Text("Guardar" )
        }
    }

    if (mostrarDatePicker) {
        val datePickerState = rememberDatePickerState( initialSelectedDateMillis = formulario.fecha )
        DatePickerDialog( onDismissRequest = { mostrarDatePicker = false },
            confirmButton = { TextButton( onClick = { viewModel.actualizarFecha(datePickerState.selectedDateMillis)
                mostrarDatePicker = false } )
            {
                Text("Aceptar")
            }
            },

            dismissButton = { TextButton( onClick = { mostrarDatePicker = false } ) {
                Text("Cancelar" )
            }
            } ) {
            DatePicker(
                state = datePickerState
            )
        }
    }
}
