package com.example.appmenu.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appmenu.model.ConfiguracionEducativa
import com.example.appmenu.ui.componentes.BannerSaludoEstudiante
import com.example.appmenu.ui.componentes.TarjetaAsignaturaItem
import com.example.appmenu.ui.theme.*
import com.example.appmenu.viewmodel.EstadoViewModel
import kotlinx.coroutines.delay

// PANTALLA PRINCIPAL: PERFIL ESTUDIANTE (ESTILO MOCKUP MPAZ PRO 2.º)

@Composable
fun PantallaPrincipal(
    estadoViewModel: EstadoViewModel = viewModel(),
    onSeleccionarAsignatura: (String) -> Unit = {}
) {
    val context = LocalContext.current

    // Estados del ViewModel
    val estadoGuardado by estadoViewModel.estadoActivado.collectAsState()
    val cargando by estadoViewModel.cargando.collectAsState()
    val guardadoCorrectamente by estadoViewModel.guardadoCorrectamente.collectAsState()
    val mensajeError by estadoViewModel.mensajeError.collectAsState()

    var estadoActivado by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(cargando, estadoGuardado) {
        if (!cargando) {
            estadoActivado = estadoGuardado
        }
    }

    LaunchedEffect(guardadoCorrectamente) {
        if (guardadoCorrectamente) {
            delay(2000)
            estadoViewModel.ocultarMensajeGuardado()
        }
    }

    // Color animado para el boton de modo guiado
    val colorBotonModo by animateColorAsState(
        targetValue = if (estadoActivado) Color(0xFF2E7D32) else AzulBarraSuperior,
        label = "ColorBotonModo"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoApp)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // BANNER DE SALUDO
        BannerSaludoEstudiante(
            nombre = "Martina",
            mensaje = "¿Qué aprenderemos hoy?"
        )

        // TÍTULO DE ASIGNATURAS Y ESTADO
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Tus Asignaturas",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextoOscuro
            )
            Text(
                text = "2.° Básico",
                fontSize = 13.sp,
                color = GrisTexto,
                fontWeight = FontWeight.Medium
            )
        }

        // GRILLA MODULAR DE 4 ASIGNATURAS
        // El orden y colores se cambian directamente en ModulosEducativos.kt
        val listaAsignaturas = ConfiguracionEducativa.asignaturas

        // FILA 1
        if (listaAsignaturas.size >= 2) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                TarjetaAsignaturaItem(
                    tarjeta = listaAsignaturas[0],
                    onClick = {
                        Toast.makeText(context, "Abriendo ${listaAsignaturas[0].nombre}", Toast.LENGTH_SHORT).show()
                        onSeleccionarAsignatura(listaAsignaturas[0].id)
                    },
                    modifier = Modifier.weight(1f)
                )
                TarjetaAsignaturaItem(
                    tarjeta = listaAsignaturas[1],
                    onClick = {
                        Toast.makeText(context, "Abriendo ${listaAsignaturas[1].nombre}", Toast.LENGTH_SHORT).show()
                        onSeleccionarAsignatura(listaAsignaturas[1].id)
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // FILA 2
        if (listaAsignaturas.size >= 4) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                TarjetaAsignaturaItem(
                    tarjeta = listaAsignaturas[2],
                    onClick = {
                        Toast.makeText(context, "Abriendo ${listaAsignaturas[2].nombre}", Toast.LENGTH_SHORT).show()
                        onSeleccionarAsignatura(listaAsignaturas[2].id)
                    },
                    modifier = Modifier.weight(1f)
                )
                TarjetaAsignaturaItem(
                    tarjeta = listaAsignaturas[3],
                    onClick = {
                        Toast.makeText(context, "Abriendo ${listaAsignaturas[3].nombre}", Toast.LENGTH_SHORT).show()
                        onSeleccionarAsignatura(listaAsignaturas[3].id)
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // CONTROL APOYO DIDACTICO
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
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (estadoActivado) Icons.Default.Hearing else Icons.Default.MenuBook,
                        contentDescription = null,
                        tint = if (estadoActivado) Color(0xFF2E7D32) else AzulBarraSuperior
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Modo de Lectura y Audio (DataStore)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextoOscuro
                    )
                }

                Text(
                    text = if (estadoActivado) {
                        "Modo Asistido ACTIVO: Instrucciones habladas y apoyos visuales activados para 2.° Básico."
                    } else {
                        "Modo Estándar: Lectura directa sin asistencia sonora."
                    },
                    fontSize = 13.sp,
                    color = GrisTexto
                )

                Button(
                    onClick = {
                        val nuevoEstado = !estadoActivado
                        estadoActivado = nuevoEstado
                        estadoViewModel.guardarEstado(nuevoEstado)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colorBotonModo),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (estadoActivado) "Desactivar Asistencia" else "Activar Asistencia Guiada",
                        fontWeight = FontWeight.Bold
                    )
                }

                AnimatedVisibility(
                    visible = guardadoCorrectamente,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Text(
                        text = "¡Preferencia guardada en el dispositivo!",
                        color = Color(0xFF2E7D32),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                AnimatedVisibility(
                    visible = mensajeError != null,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Text(
                        text = mensajeError ?: "",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}