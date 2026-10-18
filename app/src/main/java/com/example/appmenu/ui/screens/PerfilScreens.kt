package com.example.appmenu.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appmenu.data.CamaraUriFactory
import com.example.appmenu.ui.componentes.ImagenInteligente
import com.example.appmenu.viewmodel.PerfilViewModel

@Composable
fun PerfilScreen(
    perfilViewModel: PerfilViewModel = viewModel()
) {
    val context = LocalContext.current
    val imagenUri by perfilViewModel.imagenUri.collectAsState()

    var uriPendienteCamara by remember {
        mutableStateOf<Uri?>(null)
    }

    val galeriaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uriSeleccionado ->
        perfilViewModel.actualizarImagenDesdeGaleria(
            uriSeleccionado
        )
    }

    val camaraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { exito ->
        perfilViewModel.actualizarImagenDesdeCamara(
            uriPendienteCamara,
            exito
        )

        if (!exito) {
            uriPendienteCamara = null
        }
    }

    // Solicita permiso si todavía no está concedido.
    val permisoCamaraLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { concedido ->
            if (concedido) {
                try {
                    val uri = CamaraUriFactory.crearUri(context)
                    uriPendienteCamara = uri
                    camaraLauncher.launch(uri)
                } catch (e: Exception) {
                    Toast.makeText(
                        context,
                        "Error al abrir la cámara: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else {
                Toast.makeText(
                    context,
                    "Debes conceder el permiso de cámara en Ajustes.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    // Comprueba el permiso antes de abrir la cámara.
    fun tomarFotografia() {
        val permisoConcedido =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED

        if (permisoConcedido) {
            try {
                val uri = CamaraUriFactory.crearUri(context)
                uriPendienteCamara = uri
                camaraLauncher.launch(uri)
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    "Error al abrir la cámara: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        } else {
            permisoCamaraLauncher.launch(
                Manifest.permission.CAMERA
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                horizontal = 24.dp,
                vertical = 28.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Mi perfil",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Personaliza tu imagen de perfil",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 3.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ImagenInteligente(imagenUri = imagenUri)

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = if (imagenUri == null) {
                        "Aún no has seleccionado una imagen"
                    } else {
                        "Imagen de perfil actualizada"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        galeriaLauncher.launch("image/*")
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Filled.Share,
                        contentDescription = "Galería"
                    )
                    Text("  Elegir desde galería")
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = {
                        tomarFotografia()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Filled.Share,
                        contentDescription = "Cámara"
                    )
                    Text("  Tomar fotografía")
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Puedes cambiar la imagen cuando quieras.",
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}