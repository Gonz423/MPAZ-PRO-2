package com.example.appmenu

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.appmenu.data.BiometricHelper
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.School
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.Alignment
import kotlinx.coroutines.delay
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appmenu.model.ConfiguracionEducativa
import com.example.appmenu.ui.componentes.TabNavItem
import com.example.appmenu.ui.screens.*
import com.example.appmenu.ui.theme.AppMenuTheme
import com.example.appmenu.ui.theme.AzulBarraSuperior

// rutas de navegacion de la aplicacion mockups mpaz pro 2.º
sealed class Ruta(val id: String) {
    // portada inicial splash 3 segundos
    object Splash      : Ruta("splash")

    // login inicial
    object Selector : Ruta("selector")

    // flujo estudiante mockup screens 3, 10, 11
    object EstudianteInicio    : Ruta("est_inicio")
    object EstudianteProgreso  : Ruta("est_progreso")
    object EstudiantePerfil    : Ruta("est_perfil")
    object EstudianteCuaderno  : Ruta("est_cuaderno")

    // flujo docente mockup screens 12, 13
    object DocentePanel        : Ruta("doc_panel")
    object DocenteActividad    : Ruta("doc_actividad")
    object DocenteCuaderno     : Ruta("doc_cuaderno")
    object DocenteInforme      : Ruta("doc_informe")
    object DocentePerfil       : Ruta("doc_perfil")
}

class MainActivity : FragmentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppMenuTheme {

                // ruta y estados de navegacion inicia con portada 3 segundos
                var ruta by remember { mutableStateOf<Ruta>(Ruta.Splash) }
                var materiaSeleccionada by remember { mutableStateOf<String?>(null) }
                var verLeccion by remember { mutableStateOf(false) }
                var leccionTitulo by remember { mutableStateOf("Unidad 1 - Lectura y comprensión") }
                var leccionOa by remember { mutableStateOf("El valor de la amistad") }
                var verActividadInteractiva by remember { mutableStateOf(false) }
                var actividadSeleccionadaId by remember { mutableStateOf<String?>(null) }
                var categoriaFormulario by remember { mutableStateOf<String?>(null) }

                // 0. pantalla de bienvenida / portada splash 3 segundos
                if (ruta == Ruta.Splash) {
                    SplashScreen(
                        onTiempoTerminado = {
                            ruta = Ruta.Selector
                        }
                    )
                    return@AppMenuTheme
                }

                // 1. pantalla de login / bienvenida mockup screens 1 y 2
                if (ruta == Ruta.Selector) {
                    materiaSeleccionada = null
                    verLeccion = false
                    verActividadInteractiva = false

                    SelectorPerfilScreen(
                        onSeleccionarEstudiante = { ruta = Ruta.EstudianteInicio },
                        onSeleccionarDocente    = {
                            BiometricHelper.autenticarDocente(
                                activity = this@MainActivity,
                                onExito = { ruta = Ruta.DocentePanel }
                            )
                        }
                    )
                    return@AppMenuTheme
                }

                val esEstudiante = ruta.id.startsWith("est_")
                val tituloPerfil = if (esEstudiante) "MPAZ PRO 2.º · Estudiante" else "MPAZ PRO 2.º · Docente"

                // control del boton atras del sistema no salir del perfil
                BackHandler(enabled = ruta != Ruta.Selector && ruta != Ruta.Splash) {
                    when {
                        verActividadInteractiva -> verActividadInteractiva = false
                        verLeccion -> verLeccion = false
                        materiaSeleccionada != null -> materiaSeleccionada = null
                        esEstudiante && ruta != Ruta.EstudianteInicio -> ruta = Ruta.EstudianteInicio
                        !esEstudiante && ruta != Ruta.DocentePanel -> ruta = Ruta.DocentePanel
                        // si ya esta en la vista principal de su perfil, no hace nada para mantenerse en el perfil
                    }
                }
                if (verActividadInteractiva) {
                    ActividadInteractivaScreen(
                        ejercicioId = actividadSeleccionadaId,
                        onVolver = { verActividadInteractiva = false },
                        onFinalizarActividad = {
                            verActividadInteractiva = false
                            verLeccion = false
                        }
                    )
                    return@AppMenuTheme
                }

                // 3. overlay: detalle de leccion y contenido didactico mockup screen 5
                if (verLeccion) {
                    val colorMateria = ConfiguracionEducativa.asignaturas.find {
                        it.id.equals(materiaSeleccionada, ignoreCase = true)
                    }?.color ?: AzulBarraSuperior

                    LeccionScreen(
                        tituloUnidad = leccionTitulo,
                        tituloContenido = leccionOa,
                        colorMateria = colorMateria,
                        onVolver = { verLeccion = false },
                        onIrAActividad = {
                            actividadSeleccionadaId = null
                            verActividadInteractiva = true
                        }
                    )
                    return@AppMenuTheme
                }

                // 4. overlay: detalle de asignatura y unidades mockup screen 4
                if (materiaSeleccionada != null) {
                    DetalleAsignaturaScreen(
                        materiaId = materiaSeleccionada!!,
                        esDocente = !esEstudiante,
                        onVolver = { materiaSeleccionada = null },
                        onAbrirLeccion = { titulo, oa ->
                            leccionTitulo = titulo
                            leccionOa = oa
                            verLeccion = true
                        },
                        onAbrirActividad = { idEjercicio ->
                            actividadSeleccionadaId = idEjercicio
                            verActividadInteractiva = true
                        },
                        onNavegarFormulario = { nombreAsignatura ->
                            categoriaFormulario = nombreAsignatura
                            materiaSeleccionada = null
                            ruta = if (esEstudiante) Ruta.EstudianteInicio else Ruta.DocenteActividad
                        }
                    )
                    return@AppMenuTheme
                }

                // 5. scaffold principal con topbar y bottom navigation mockup screens 3, 10, 11, 12
                Scaffold(
                    modifier = Modifier.fillMaxSize(),

                    // barra superior
                    topBar = {
                        TopAppBar(
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = AzulBarraSuperior,
                                titleContentColor = Color.White,
                                navigationIconContentColor = Color.White
                            ),
                            navigationIcon = {
                                IconButton(onClick = { ruta = Ruta.Selector }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Cambiar usuario"
                                    )
                                }
                            },
                            title = {
                                Text(text = tituloPerfil, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                            }
                        )
                    },

                    // barra de navegacion inferior adaptada al rol 3 pestañas del mockup
                    bottomBar = {
                        NavigationBar(
                            containerColor = Color.White,
                            tonalElevation = 6.dp
                        ) {
                            if (esEstudiante) {
                                // tabs del estudiante inicio, progreso, perfil
                                TabNavItem(
                                    seleccionado = ruta == Ruta.EstudianteInicio,
                                    icono = Icons.Default.School,
                                    etiqueta = "Inicio",
                                    onClick = { materiaSeleccionada = null; ruta = Ruta.EstudianteInicio }
                                )
                                TabNavItem(
                                    seleccionado = ruta == Ruta.EstudianteProgreso,
                                    icono = Icons.AutoMirrored.Filled.TrendingUp,
                                    etiqueta = "Progreso",
                                    onClick = { materiaSeleccionada = null; ruta = Ruta.EstudianteProgreso }
                                )
                                TabNavItem(
                                    seleccionado = ruta == Ruta.EstudiantePerfil,
                                    icono = Icons.Default.AccountCircle,
                                    etiqueta = "Perfil",
                                    onClick = { materiaSeleccionada = null; ruta = Ruta.EstudiantePerfil }
                                )
                            } else {
                                // tabs del docente inicio, gestionar, perfil
                                TabNavItem(
                                    seleccionado = ruta == Ruta.DocentePanel,
                                    icono = Icons.Default.Analytics,
                                    etiqueta = "Inicio",
                                    onClick = { materiaSeleccionada = null; ruta = Ruta.DocentePanel }
                                )
                                TabNavItem(
                                    seleccionado = ruta == Ruta.DocenteActividad,
                                    icono = Icons.AutoMirrored.Filled.Assignment,
                                    etiqueta = "Gestionar",
                                    onClick = { materiaSeleccionada = null; ruta = Ruta.DocenteActividad }
                                )
                                TabNavItem(
                                    seleccionado = ruta == Ruta.DocentePerfil,
                                    icono = Icons.Default.AccountCircle,
                                    etiqueta = "Perfil",
                                    onClick = { materiaSeleccionada = null; ruta = Ruta.DocentePerfil }
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
                        when (ruta) {
                            // flujo estudiante
                            Ruta.EstudianteInicio -> PantallaPrincipal(
                                onSeleccionarAsignatura = { idMateria -> materiaSeleccionada = idMateria }
                            )
                            Ruta.EstudianteProgreso -> ProgresoScreen(
                                onAbrirCuaderno = { ruta = Ruta.EstudianteCuaderno }
                            )
                            Ruta.EstudianteCuaderno -> NotasScreen()
                            Ruta.EstudiantePerfil   -> PerfilScreen(
                                onCerrarSesion = { ruta = Ruta.Selector }
                            )

                            // flujo docente
                            Ruta.DocentePanel -> DocenteScreen(
                                onNavegarFormulario = { ruta = Ruta.DocenteActividad },
                                onNavegarNotas      = { ruta = Ruta.DocenteCuaderno },
                                onNavegarInformeEstudiante = { ruta = Ruta.DocenteInforme },
                                onSeleccionarAsignatura = { idMateria -> materiaSeleccionada = idMateria }
                            )
                            Ruta.DocenteActividad -> FormularioScreen(categoriaInicial = categoriaFormulario)
                            Ruta.DocenteCuaderno  -> NotasScreen()
                            Ruta.DocenteInforme   -> InformeEstudianteScreen(
                                onVolver = { ruta = Ruta.DocentePanel }
                            )
                            Ruta.DocentePerfil    -> PerfilScreen(
                                onCerrarSesion = { ruta = Ruta.Selector }
                            )

                            else -> SelectorPerfilScreen(
                                onSeleccionarEstudiante = { ruta = Ruta.EstudianteInicio },
                                onSeleccionarDocente    = {
                                    BiometricHelper.autenticarDocente(
                                        activity = this@MainActivity,
                                        onExito = { ruta = Ruta.DocentePanel }
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

// componente: pantalla de portada splash screen con imagen portada.png
@Composable
fun SplashScreen(
    onTiempoTerminado: () -> Unit
) {
    LaunchedEffect(Unit) {
        delay(3000)
        onTiempoTerminado()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .clickable { onTiempoTerminado() },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.portada),
            contentDescription = "Portada MPAZ PRO",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}
