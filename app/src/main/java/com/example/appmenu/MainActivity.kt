package com.example.appmenu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.appmenu.ui.screens.DocenteScreen
import com.example.appmenu.ui.screens.FormularioScreen
import com.example.appmenu.ui.screens.NotasScreen
import com.example.appmenu.ui.screens.PantallaPrincipal
import com.example.appmenu.ui.screens.PerfilScreen
import com.example.appmenu.ui.screens.SelectorPerfilScreen
import com.example.appmenu.ui.theme.AppMenuTheme
import com.example.appmenu.ui.theme.AzulBarraSuperior
import com.example.appmenu.ui.componentes.TabNavItem

// Flujo de navegación por perfiles

// Pestaña activa dentro del perfil
sealed class Ruta(val id: String) {
    // Pantalla inicial de elección de perfil
    object Selector : Ruta("selector")

    // Rutas del perfil Estudiante
    object EstudianteInicio    : Ruta("est_inicio")
    object EstudianteActividad : Ruta("est_actividad")
    object EstudianteCuaderno  : Ruta("est_cuaderno")
    object EstudiantePerfil    : Ruta("est_perfil")

    // Rutas del perfil Docente
    object DocentePanel        : Ruta("doc_panel")
    object DocenteActividad    : Ruta("doc_actividad")
    object DocenteCuaderno     : Ruta("doc_cuaderno")
    object DocentePerfil       : Ruta("doc_perfil")
}

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppMenuTheme {

                // Ruta actual 
                var ruta by remember { mutableStateOf<Ruta>(Ruta.Selector) }

                // Pantalla de selección de perfil
                if (ruta == Ruta.Selector) {
                    SelectorPerfilScreen(
                        onSeleccionarEstudiante = { ruta = Ruta.EstudianteInicio },
                        onSeleccionarDocente    = { ruta = Ruta.DocentePanel }
                    )
                    return@AppMenuTheme
                }

                // Determina si estamos en el flujo Estudiante o Docente
                val esEstudiante = ruta.id.startsWith("est_")
                val tituloPerfil = if (esEstudiante) "Perfil Estudiante" else "Panel Docente"

                Scaffold(
                    modifier = Modifier.fillMaxSize(),

                    // barra de navegacion superior
                    topBar = {
                        TopAppBar(
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = AzulBarraSuperior,
                                titleContentColor = Color.White,
                                navigationIconContentColor = Color.White
                            ),
                            navigationIcon = {
                                // Volver al selector de perfil
                                IconButton(onClick = { ruta = Ruta.Selector }) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowBack,
                                        contentDescription = "Cambiar perfil"
                                    )
                                }
                            },
                            title = {
                                Text(text = tituloPerfil, fontWeight = FontWeight.Bold)
                            }
                        )
                    },

                    // Barra de navegacion inferior
                    bottomBar = {
                        NavigationBar(
                            containerColor = Color.White,
                            tonalElevation = 6.dp
                        ) {
                            if (esEstudiante) {
                                // Tabs del Estudiante
                                TabNavItem(
                                    seleccionado = ruta == Ruta.EstudianteInicio,
                                    icono = Icons.Default.School,
                                    etiqueta = "Inicio",
                                    onClick = { ruta = Ruta.EstudianteInicio }
                                )
                                TabNavItem(
                                    seleccionado = ruta == Ruta.EstudianteActividad,
                                    icono = Icons.Default.EditNote,
                                    etiqueta = "Actividad",
                                    onClick = { ruta = Ruta.EstudianteActividad }
                                )
                                TabNavItem(
                                    seleccionado = ruta == Ruta.EstudianteCuaderno,
                                    icono = Icons.Default.Book,
                                    etiqueta = "Cuaderno",
                                    onClick = { ruta = Ruta.EstudianteCuaderno }
                                )
                                TabNavItem(
                                    seleccionado = ruta == Ruta.EstudiantePerfil,
                                    icono = Icons.Default.AccountCircle,
                                    etiqueta = "Mi Perfil",
                                    onClick = { ruta = Ruta.EstudiantePerfil }
                                )
                            } else {
                                // Tabs del Docente
                                TabNavItem(
                                    seleccionado = ruta == Ruta.DocentePanel,
                                    icono = Icons.Default.Analytics,
                                    etiqueta = "Panel",
                                    onClick = { ruta = Ruta.DocentePanel }
                                )
                                TabNavItem(
                                    seleccionado = ruta == Ruta.DocenteActividad,
                                    icono = Icons.Default.EditNote,
                                    etiqueta = "Actividad",
                                    onClick = { ruta = Ruta.DocenteActividad }
                                )
                                TabNavItem(
                                    seleccionado = ruta == Ruta.DocenteCuaderno,
                                    icono = Icons.Default.Book,
                                    etiqueta = "Cuaderno",
                                    onClick = { ruta = Ruta.DocenteCuaderno }
                                )
                                TabNavItem(
                                    seleccionado = ruta == Ruta.DocentePerfil,
                                    icono = Icons.Default.AccountCircle,
                                    etiqueta = "Perfil Clase",
                                    onClick = { ruta = Ruta.DocentePerfil }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        // ── Renderizado de pantalla según ruta ──
                        when (ruta) {

                            // FLUJO ESTUDIANTE
                            Ruta.EstudianteInicio -> PantallaPrincipal(
                                onSeleccionarAsignatura = { ruta = Ruta.EstudianteActividad }
                            )
                            Ruta.EstudianteActividad -> FormularioScreen()
                            Ruta.EstudianteCuaderno  -> NotasScreen()
                            Ruta.EstudiantePerfil    -> PerfilScreen()

                            // FLUJO DOCENTE
                            Ruta.DocentePanel -> DocenteScreen(
                                onNavegarFormulario = { ruta = Ruta.DocenteActividad },
                                onNavegarNotas      = { ruta = Ruta.DocenteCuaderno }
                            )
                            Ruta.DocenteActividad -> FormularioScreen()
                            Ruta.DocenteCuaderno  -> NotasScreen()
                            Ruta.DocentePerfil    -> PerfilScreen()

                            else -> SelectorPerfilScreen(
                                onSeleccionarEstudiante = { ruta = Ruta.EstudianteInicio },
                                onSeleccionarDocente    = { ruta = Ruta.DocentePanel }
                            )
                        }
                    }
                }
            }
        }
    }
}

