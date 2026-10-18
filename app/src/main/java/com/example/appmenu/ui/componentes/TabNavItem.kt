package com.example.appmenu.ui.componentes

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.appmenu.ui.theme.AzulBarraSuperior

// Helper reutilizable para items de NavigationBar
// Necesita RowScope porque NavigationBarItem es una extension function de RowScope

@Composable
fun RowScope.TabNavItem(
    seleccionado: Boolean,
    icono: ImageVector,
    etiqueta: String,
    onClick: () -> Unit
) {
    NavigationBarItem(
        selected = seleccionado,
        onClick = onClick,
        icon = { Icon(icono, contentDescription = etiqueta) },
        label = { Text(etiqueta) },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = AzulBarraSuperior,
            selectedTextColor = AzulBarraSuperior,
            indicatorColor = AzulBarraSuperior.copy(alpha = 0.15f)
        )
    )
}
