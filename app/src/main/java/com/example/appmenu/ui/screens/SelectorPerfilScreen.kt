package com.example.appmenu.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appmenu.ui.theme.*

// pantalla: seleccion de perfil de acceso directo estilos mpaz pro 2.º

@Composable
fun SelectorPerfilScreen(
    onSeleccionarEstudiante: () -> Unit,
    onSeleccionarDocente: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoApp)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // logo e identidad
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "MPAZ PRO 2.º",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = AzulBarraSuperior
            )
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = Color(0xFFFFB300),
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Aprender también es una aventura",
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = GrisTexto
        )
        Text(
            text = "Escuela Escritora Marcela Paz · San Bernardo",
            fontSize = 12.sp,
            color = GrisTexto.copy(alpha = 0.8f)
        )

        Spacer(modifier = Modifier.height(36.dp))

        Text(
            text = "Acceso rápido por perfil:",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = TextoOscuro
        )

        Spacer(modifier = Modifier.height(18.dp))

        // tarjeta perfil estudiante
        Card(
            onClick = onSeleccionarEstudiante,
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = BlancoTarjeta),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(AzulBarraSuperior.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = "Estudiante",
                        tint = AzulBarraSuperior,
                        modifier = Modifier.size(30.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Estudiante",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextoOscuro
                    )
                    Text(
                        text = "2.° Básico · Martina González",
                        fontSize = 13.sp,
                        color = GrisTexto
                    )
                }

                FilledTonalButton(
                    onClick = onSeleccionarEstudiante,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = AzulBarraSuperior,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text("Ingresar", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // tarjeta perfil docente
        Card(
            onClick = onSeleccionarDocente,
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = BlancoTarjeta),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF6A1B9A).copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Face,
                        contentDescription = "Docente",
                        tint = Color(0xFF6A1B9A),
                        modifier = Modifier.size(30.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Docente / Educador",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextoOscuro
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Fingerprint,
                            contentDescription = null,
                            tint = Color(0xFF6A1B9A),
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Acceso con Biometría",
                            fontSize = 13.sp,
                            color = Color(0xFF6A1B9A),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                FilledTonalButton(
                    onClick = onSeleccionarDocente,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = Color(0xFF6A1B9A),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text("Ingresar", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // tarjeta ilustrativa del colegio marcela paz
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(AzulBarraSuperior),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.School, contentDescription = null, tint = Color.White)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Escuela Escritora Marcela Paz",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = AzulBarraSuperior
                    )
                    Text(
                        text = "San Bernardo · DSY1105 Duoc UC",
                        fontSize = 11.sp,
                        color = GrisTexto
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}