package com.example.appmenu.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appmenu.data.CamaraUriFactory
import com.example.appmenu.ui.componentes.ImagenInteligente
import com.example.appmenu.ui.theme.AzulBarraSuperior
import com.example.appmenu.ui.theme.BlancoTarjeta
import com.example.appmenu.ui.theme.FondoApp
import com.example.appmenu.ui.theme.GrisTexto
import com.example.appmenu.ui.theme.TextoOscuro
import com.example.appmenu.viewmodel.PerfilViewModel

// PANTALLA: PERFIL Y FOTO DE CREDENCIAL ESCOLAR (CAMARA/GALERIA)

@Composable
fun PerfilScreen(
    perfilViewModel: PerfilViewModel = viewModel()
) {
    val context = LocalContext.current
    val imagenUri by perfilViewModel.imagenUri.collectAsState()

    // Launcher para seleccionar imagen existente desde la galeria
    val galeriaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uriSeleccionado ->
        perfilViewModel.actualizarImagenDesdeGaleria(uriSeleccionado)
    }

    // Launcher para capturar foto con la aplicación de camara del dispositivo
    val camaraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { exito ->
        if (exito) {
            perfilViewModel.actualizarImagenDesdeCamara(true)
            Toast.makeText(context, "¡Fotografía capturada y guardada!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Captura de fotografía cancelada.", Toast.LENGTH_SHORT).show()
        }
    }

    // Launcher para solicitar permiso camara si el usuario no lo ha otorgado
    val permisoCamaraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) {
            try {
                val uri = CamaraUriFactory.crearUri(context)
                perfilViewModel.uriPendienteCamara = uri
                camaraLauncher.launch(uri)
            } catch (e: Exception) {
                Toast.makeText(context, "Error al preparar la cámara: ${e.message}", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(context, "Debes permitir el acceso a la cámara para tomar la fotografía.", Toast.LENGTH_LONG).show()
        }
    }

    // Funcion de activacion de la camara
    fun abrirCamara() {
        val permisoConcedido = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (permisoConcedido) {
            try {
                val uri = CamaraUriFactory.crearUri(context)
                perfilViewModel.uriPendienteCamara = uri
                camaraLauncher.launch(uri)
            } catch (e: Exception) {
                Toast.makeText(context, "Error al preparar la cámara: ${e.message}", Toast.LENGTH_LONG).show()
            }
        } else {
            permisoCamaraLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // Interfaz Visual
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoApp)
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Cabecera
        Text(
            text = "Perfil del Estudiante",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = TextoOscuro
        )

        Text(
            text = "Fotografía para credencial escolar · Escuela Marcela Paz (2.° Básico)",
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            color = GrisTexto
        )

        // Tarjeta con visor e interactores
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = BlancoTarjeta),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Componente modular de visualizacion de foto
                ImagenInteligente(imagenUri = imagenUri)

                Text(
                    text = if (imagenUri == null) {
                        "Sin fotografía asignada"
                    } else {
                        "Fotografía de credencial guardada"
                    },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextoOscuro
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Boton de la camara
                Button(
                    onClick = { abrirCamara() },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AzulBarraSuperior)
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = "Cámara",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Tomar fotografía con cámara")
                }

                // Boton Galeria
                OutlinedButton(
                    onClick = { galeriaLauncher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoLibrary,
                        contentDescription = "Galería",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Elegir desde galería")
                }
            }
        }
    }
}