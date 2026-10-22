package com.example.appmenu.ui.componentes

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appmenu.model.EjercicioEducativo
import com.example.appmenu.room.Note
import com.example.appmenu.ui.theme.*

// componentes compartidos reutilizables en múltiples pantallas - mpaz pro 2.°

// tarjeta de accion en el panel del docente (colores por modulo)
@Composable
fun TarjetaAccionDocente(
    titulo: String,
    color: Color,
    icono: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(115.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
            Text(
                text = titulo,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 16.sp
            )
        }
    }
}

// tarjeta de nota/observacion en el cuaderno (room)
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
                Text(text = note.title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextoOscuro)
                Spacer(Modifier.height(4.dp))
                Text(text = note.content, fontSize = 13.sp, color = GrisTexto)
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

// fila de dato en el perfil (etiqueta + valor con icono)
@Composable
fun FilaDatoPerfil(
    etiqueta: String,
    valor: String,
    icono: ImageVector
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icono, contentDescription = null, tint = GrisTexto, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(etiqueta, fontSize = 13.sp, color = GrisTexto)
        }
        Text(valor, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextoOscuro)
    }
}

// barra de progreso animada por asignatura (pantalla de progreso del estudiante)
@Composable
fun BarraAsignaturaProgreso(
    nombre: String,
    porcentaje: Int,
    color: Color
) {
    val animProgress by animateFloatAsState(
        targetValue = porcentaje / 100f,
        label = "Progreso $nombre"
    )
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(nombre, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextoOscuro)
            Text("$porcentaje%", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = color)
        }
        LinearProgressIndicator(
            progress = { animProgress },
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = color.copy(alpha = 0.15f)
        )
    }
}

// fila de ultima actividad con estado completado/pendiente
@Composable
fun FilaUltimaActividad(
    titulo: String,
    puntaje: String,
    completado: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (completado) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                contentDescription = null,
                tint = if (completado) Color(0xFF2E7D32) else GrisTexto,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(titulo, fontSize = 14.sp, color = TextoOscuro)
        }
        Text(
            text = puntaje,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = if (completado) Color(0xFF2E7D32) else GrisTexto
        )
    }
}

// tarjeta de ejercicio individual en detalle de asignatura
@Composable
fun TarjetaEjercicioItem(
    ejercicio: EjercicioEducativo,
    colorAsignatura: Color,
    esDocente: Boolean,
    onRealizar: () -> Unit,
    onToggleCompletado: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (ejercicio.completado) Color(0xFFF1F8E9) else Color(0xFFFAFAFA)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (ejercicio.completado) Color(0xFFC8E6C9) else Color(0xFFE5E7EB)
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SuggestionChip(
                    onClick = {},
                    label = { Text("Guía #${ejercicio.codigoGuia}", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = colorAsignatura.copy(alpha = 0.12f),
                        labelColor = colorAsignatura
                    )
                )
                Surface(
                    color = if (ejercicio.completado) Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (ejercicio.completado) "Completado" else "Pendiente",
                        color = if (ejercicio.completado) Color(0xFF2E7D32) else Color(0xFFE65100),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
            Text(text = ejercicio.titulo, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextoOscuro)
            Text(text = ejercicio.objetivoAprendizaje, fontSize = 12.sp, color = GrisTexto)
            Spacer(modifier = Modifier.height(2.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onRealizar,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colorAsignatura)
                ) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (esDocente) "Probar Actividad" else "Realizar",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                OutlinedButton(onClick = onToggleCompletado, shape = RoundedCornerShape(8.dp)) {
                    Icon(
                        imageVector = if (ejercicio.completado) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                        contentDescription = null,
                        tint = if (ejercicio.completado) Color(0xFF2E7D32) else GrisTexto,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
