package com.example.appmenu.ui.componentes

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun NuevoMenu(isExpanded: Boolean,onItemClick:(String)-> Unit,onDismiss:()->Unit){
    val opciones=listOf("Ingreso","Modificacion","Eliminacion")
    DropdownMenu(expanded = isExpanded, onDismissRequest = onDismiss) {
        opciones.forEach {opcion->
            DropdownMenuItem(text={
                Text(text=opcion)
            }, onClick = {onDismiss()})

        }
    }

}
