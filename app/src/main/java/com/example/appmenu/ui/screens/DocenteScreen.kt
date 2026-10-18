package com.example.appmenu.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appmenu.model.ConfiguracionEducativa
import com.example.appmenu.ui.componentes.FilaProgresoAsignatura
import com.example.appmenu.ui.componentes.TarjetaMetricaItem
import com.example.appmenu.ui.theme.*

// PANTALLA: PANEL DOCENTE (ESTILO MOCKUP MPAZ PRO 2.º)


@Composable
fun DocenteScreen(
    onNavegarFormulario: () -> Unit = {},
    onNavegarNotas: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoApp)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Cabecera del panel docente
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Panel docente",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoOscuro
                )
                Text(
                    text = "Buenos días, Profesora",
                    fontSize = 14.sp,
                    color = GrisTexto
                )
            }
            IconButton(onClick = { /* Notificaciones */ }) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notificaciones",
                    tint = AzulBarraSuperior
                )
            }
        }

        // Tarjetas de Métricas (28 Alumnos / 24 Activos)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ConfiguracionEducativa.metricasDocente.forEach { metrica ->
                TarjetaMetricaItem(
                    metrica = metrica,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Progreso del curso por asignatura
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = BlancoTarjeta),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Progreso del curso",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoOscuro
                )
                Spacer(modifier = Modifier.height(4.dp))

                // Lista modular de barras de progreso
                ConfiguracionEducativa.progresoCurso.forEach { progreso ->
                    FilaProgresoAsignatura(progreso = progreso)
                }
            }
        }

        // Botones de accion
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = onNavegarNotas,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AzulBarraSuperior)
            ) {
                Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Ver Registros y Cuaderno (Room)", fontSize = 15.sp)
            }

            OutlinedButton(
                onClick = onNavegarFormulario,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Assignment, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Registrar Nueva Actividad / Guía", fontSize = 15.sp)
            }
        }
    }
}
