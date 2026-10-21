package com.example.appmenu.ui.componentes

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

// componente modular: visor de foto de credencial/perfil


@Composable
fun ImagenInteligente(
    imagenUri: Uri?,
    modifier: Modifier = Modifier,
    descripcion: String = "Fotografía de credencial escolar"
) {
    Box(
        modifier = modifier
            .size(180.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(3.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.65f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (imagenUri != null) {
            AsyncImage(
                model = imagenUri,
                contentDescription = descripcion,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = "Sin imagen seleccionada",
                modifier = Modifier.size(88.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
