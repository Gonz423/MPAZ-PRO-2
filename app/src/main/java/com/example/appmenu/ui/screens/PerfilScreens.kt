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
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appmenu.data.CamaraUriFactory
import com.example.appmenu.ui.componentes.ImagenInteligente
import com.example.appmenu.ui.theme.*
import com.example.appmenu.viewmodel.EstadoViewModel
import com.example.appmenu.viewmodel.PerfilViewModel

// pantalla: perfil del estudiante y configuracion mockup screen 11 mpaz pro 2.º

@Composable
fun PerfilScreen(
    perfilViewModel: PerfilViewModel = viewModel(),
    estadoViewModel: EstadoViewModel = viewModel(),
    onCerrarSesion: () -> Unit = {}
) {
    val context = LocalContext.current
    val imagenUri by perfilViewModel.imagenUri.collectAsState()

    // configuracion desde datastore
    val modoAudioActivo by estadoViewModel.estadoActivado.collectAsState()
    var notificacionesActivas by remember { mutableStateOf(true) }
    var modoOscuroActivo by remember { mutableStateOf(false) }

    // launcher para galeria
    val galeriaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uriSeleccionado ->
        perfilViewModel.actualizarImagenDesdeGaleria(uriSeleccionado)
    }

    // launcher para camara
    val camaraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { exito ->
        if (exito) {
            perfilViewModel.actualizarImagenDesdeCamara(true)
            Toast.makeText(context, "¡Fotografía de credencial guardada!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Captura cancelada.", Toast.LENGTH_SHORT).show()
        }
    }

    // launcher para permiso de camara
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
            Toast.makeText(context, "Permiso de cámara denegado.", Toast.LENGTH_LONG).show()
        }
    }

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
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        } else {
            permisoCamaraLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoApp)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // tarjeta de cabecera y foto mockup screen 11
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
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // foto con bordes redondeados y soporte de camara nativa
                ImagenInteligente(imagenUri = imagenUri)

                Text(
                    text = "Martina González",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoOscuro
                )

                Text(
                    text = "Editar fotografía de credencial",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = AzulBarraSuperior
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { abrirCamara() },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AzulBarraSuperior)
                    ) {
                        Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Cámara", fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = { galeriaLauncher.launch("image/*") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Galería", fontSize = 12.sp)
                    }
                }
            }
        }

        // seccion: informacion del estudiante mockup screen 11
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = BlancoTarjeta),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Información",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoOscuro
                )

                FilaDatoPerfil("Curso", "2.° Básico", Icons.Default.School)
                FilaDatoPerfil("Usuario", "martina", Icons.Default.Person)
                FilaDatoPerfil("Contraseña", "••••••••", Icons.Default.Lock)
            }
        }

        // seccion: configuracion mockup screen 11
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = BlancoTarjeta),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Configuración",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoOscuro
                )

                // 1. audio de instrucciones datastore preferences
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.VolumeUp, contentDescription = null, tint = AzulBarraSuperior, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Audio de instrucciones", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextoOscuro)
                            Text("Soporte sonoro (DataStore)", fontSize = 11.sp, color = GrisTexto)
                        }
                    }
                    Switch(
                        checked = modoAudioActivo,
                        onCheckedChange = { estadoViewModel.guardarEstado(it) }
                    )
                }

                HorizontalDivider(color = Color(0xFFF1F5F9))

                // 2. notificaciones
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Notifications, contentDescription = null, tint = AzulBarraSuperior, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Notificaciones de tareas", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextoOscuro)
                    }
                    Switch(
                        checked = notificacionesActivas,
                        onCheckedChange = { notificacionesActivas = it }
                    )
                }

                HorizontalDivider(color = Color(0xFFF1F5F9))

                // 3. modo oscuro
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Modo oscuro", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextoOscuro)
                    Switch(
                        checked = modoOscuroActivo,
                        onCheckedChange = { modoOscuroActivo = it }
                    )
                }
            }
        }

        // boton cerrar sesion / cambiar de perfil
        OutlinedButton(
            onClick = onCerrarSesion,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.ExitToApp, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Cerrar sesión / Cambiar de perfil")
        }
    }
}

@Composable
fun FilaDatoPerfil(
    etiqueta: String,
    valor: String,
    icono: androidx.compose.ui.graphics.vector.ImageVector
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