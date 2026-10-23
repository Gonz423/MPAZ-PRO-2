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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
    object DocenteGestion      : Ruta("doc_gestion")
    object DocenteFormulario   : Ruta("doc_formulario")
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
                var estudianteSeleccionadoInforme by remember { mutableStateOf<String?>(null) }

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
                            ruta = if (esEstudiante) Ruta.EstudianteInicio else Ruta.DocenteFormulario
                        }
                    )
                    return@AppMenuTheme
                }

                // 5. scaffold principal con topbar y bottom navigation mockup screens 3, 10, 11, 12
                val (colorBarra, tituloBarra) = when (ruta) {
                    Ruta.DocentePanel -> Pair(AzulBarraSuperior, "MPAZ PRO 2.º · Docente")
                    Ruta.DocenteGestion -> Pair(Color(0xFF8E24AA), "Gestión de Contenidos y Alumnos")
                    Ruta.DocenteInforme -> Pair(
                        Color(0xFF43A047),
                        if (estudianteSeleccionadoInforme != null) "Informe: $estudianteSeleccionadoInforme" else "Rendimiento General del Curso"
                    )
                    Ruta.DocenteFormulario -> Pair(Color(0xFFFB8C00), "Asignar Tarea Escolar")
                    Ruta.DocenteCuaderno -> Pair(Color(0xFF2E7D32), "Cuaderno de Tareas y Observaciones")
                    Ruta.DocentePerfil -> Pair(AzulBarraSuperior, "Perfil Docente")

                    Ruta.EstudianteInicio -> Pair(AzulBarraSuperior, "MPAZ PRO 2.º · Estudiante")
                    Ruta.EstudianteProgreso -> Pair(AzulBarraSuperior, "Progreso Académico")
                    Ruta.EstudianteCuaderno -> Pair(Color(0xFF2E7D32), "Mi Cuaderno de Tareas")
                    Ruta.EstudiantePerfil -> Pair(AzulBarraSuperior, "Perfil del Estudiante")
                    else -> Pair(AzulBarraSuperior, tituloPerfil)
                }

                val esPantallaRaiz = ruta == Ruta.EstudianteInicio || ruta == Ruta.DocentePanel

                Scaffold(
                    modifier = Modifier.fillMaxSize(),

                    topBar = {
                        TopAppBar(
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = colorBarra,
                                titleContentColor = Color.White,
                                navigationIconContentColor = Color.White
                            ),
                            navigationIcon = {
                                IconButton(onClick = {
                                    when (ruta) {
                                        Ruta.EstudianteInicio, Ruta.DocentePanel -> ruta = Ruta.Selector
                                        Ruta.EstudianteProgreso, Ruta.EstudianteCuaderno, Ruta.EstudiantePerfil -> ruta = Ruta.EstudianteInicio
                                        Ruta.DocenteInforme -> {
                                            if (estudianteSeleccionadoInforme != null) ruta = Ruta.DocenteGestion
                                            else ruta = Ruta.DocentePanel
                                        }
                                        else -> ruta = Ruta.DocentePanel
                                    }
                                }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = if (esPantallaRaiz) "Cambiar usuario" else "Volver"
                                    )
                                }
                            },
                            title = {
                                Text(
                                    text = tituloBarra,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
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
                                    seleccionado = ruta == Ruta.DocenteGestion,
                                    icono = Icons.AutoMirrored.Filled.Assignment,
                                    etiqueta = "Gestionar",
                                    onClick = { materiaSeleccionada = null; ruta = Ruta.DocenteGestion }
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
                            Ruta.EstudianteInicio -> EstudianteScreen(
                                onSeleccionarAsignatura = { idMateria -> materiaSeleccionada = idMateria }
                            )
                            Ruta.EstudianteProgreso -> ProgresoScreen(
                                onAbrirCuaderno = { ruta = Ruta.EstudianteCuaderno }
                            )
                            Ruta.EstudianteCuaderno -> NotasScreen()
                            Ruta.EstudiantePerfil   -> PerfilScreen(
                                esDocente = false,
                                onCerrarSesion = { ruta = Ruta.Selector }
                            )

                            // flujo docente
                            Ruta.DocentePanel -> DocenteScreen(
                                onNavegarGestionContenidos = { ruta = Ruta.DocenteGestion },
                                onNavegarAsignarTarea      = { categoriaFormulario = null; ruta = Ruta.DocenteFormulario },
                                onNavegarNotas             = { ruta = Ruta.DocenteCuaderno },
                                onNavegarInformeEstudiante = {
                                    estudianteSeleccionadoInforme = null
                                    ruta = Ruta.DocenteInforme
                                },
                                onSeleccionarAsignatura    = { idMateria -> materiaSeleccionada = idMateria }
                            )
                            Ruta.DocenteGestion -> GestionarContenidosScreen(
                                onVolver = { ruta = Ruta.DocentePanel },
                                onCrearNuevaGuia = {
                                    categoriaFormulario = null
                                    ruta = Ruta.DocenteFormulario
                                },
                                onVerInformeEstudiante = { nombreEstudiante ->
                                    estudianteSeleccionadoInforme = nombreEstudiante
                                    ruta = Ruta.DocenteInforme
                                }
                            )
                            Ruta.DocenteFormulario -> FormularioScreen(categoriaInicial = categoriaFormulario)
                            Ruta.DocenteCuaderno  -> NotasScreen()
                            Ruta.DocenteInforme   -> InformeEstudianteScreen(
                                nombreEstudiante = estudianteSeleccionadoInforme,
                                onVolver = {
                                    if (estudianteSeleccionadoInforme != null) ruta = Ruta.DocenteGestion
                                    else ruta = Ruta.DocentePanel
                                }
                            )
                            Ruta.DocentePerfil    -> PerfilScreen(
                                esDocente = true,
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
