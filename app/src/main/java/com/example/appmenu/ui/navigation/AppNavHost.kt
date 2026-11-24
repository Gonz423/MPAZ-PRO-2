package com.example.appmenu.ui.navigation

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.appmenu.data.device.BiometricHelper
import com.example.appmenu.di.AppViewModelProvider
import com.example.appmenu.ui.componentes.TabNavItem
import com.example.appmenu.ui.model.ConfiguracionEducativa
import com.example.appmenu.ui.screens.ActividadInteractivaScreen
import com.example.appmenu.ui.screens.DetalleAsignaturaScreen
import com.example.appmenu.ui.screens.DocenteScreen
import com.example.appmenu.ui.screens.EstudianteScreen
import com.example.appmenu.ui.screens.FeedbackActividadScreen
import com.example.appmenu.ui.screens.FormularioScreen
import com.example.appmenu.ui.screens.GestionarContenidosScreen
import com.example.appmenu.ui.screens.InformeEstudianteScreen
import com.example.appmenu.ui.screens.LeccionScreen
import com.example.appmenu.ui.screens.LoginRegistroScreen
import com.example.appmenu.ui.screens.NotasScreen
import com.example.appmenu.ui.screens.PerfilScreen
import com.example.appmenu.ui.screens.ProgresoScreen
import com.example.appmenu.ui.screens.SplashScreen
import com.example.appmenu.ui.screens.TriviaScreen
import com.example.appmenu.ui.theme.AzulBarraSuperior
import com.example.appmenu.viewmodel.SesionViewModel

/** Grafo de navegacion de la app con las barras superior e inferior de cada rol. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost(
    activity: FragmentActivity,
    navController: NavHostController = rememberNavController(),
    sesionViewModel: SesionViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val context = LocalContext.current
    val entradaActual by navController.currentBackStackEntryAsState()
    val ruta = entradaActual?.destination?.route

    // si el backend rechaza el token (401) se vuelve al login
    LaunchedEffect(Unit) {
        sesionViewModel.irAlLogin.collect { mensaje ->
            Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show()
            navController.irAlLogin()
        }
    }

    val conBarras = Rutas.tieneBarras(ruta)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (conBarras) {
                val (color, titulo) = encabezado(ruta, entradaActual)
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = color,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    ),
                    navigationIcon = {
                        IconButton(onClick = {
                            if (Rutas.esRaiz(ruta)) {
                                // "Cambiar usuario": cierra la sesion y vuelve al login
                                sesionViewModel.cerrarSesion { navController.irAlLogin() }
                            } else if (!navController.popBackStack()) {
                                navController.navigate(if (Rutas.esEstudiante(ruta)) Rutas.EST_INICIO else Rutas.DOC_PANEL)
                            }
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = if (Rutas.esRaiz(ruta)) "Cambiar usuario" else "Volver"
                            )
                        }
                    },
                    title = {
                        Text(
                            text = titulo,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                )
            }
        },
        bottomBar = {
            if (conBarras) BarraInferior(ruta, navController)
        }
    ) { innerPadding ->
        // las pantallas completas (detalle, leccion, actividad...) manejan sus propios margenes
        Box(
            modifier = if (conBarras) {
                Modifier.fillMaxSize()
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding)
                    .imePadding() // el teclado no tapa los formularios
            } else Modifier.fillMaxSize()
        ) {
            NavHost(navController = navController, startDestination = Rutas.SPLASH) {

                composable(Rutas.SPLASH) {
                    SplashScreen(onTiempoTerminado = {
                        navController.navigate(Rutas.LOGIN) { popUpTo(Rutas.SPLASH) { inclusive = true } }
                    })
                }

                composable(Rutas.LOGIN) {
                    LoginRegistroScreen(
                        onLoginExitoso = { rol ->
                            val esDocente = rol.equals("DOCENTE", true) || rol.equals("UTP", true)
                            navController.entrarComo(if (esDocente) Rutas.DOC_PANEL else Rutas.EST_INICIO)
                        },
                        onAccesoRapidoEstudiante = { navController.entrarComo(Rutas.EST_INICIO) },
                        onAccesoRapidoDocente = {
                            BiometricHelper.autenticarDocente(
                                activity = activity,
                                onExito = { navController.entrarComo(Rutas.DOC_PANEL) }
                            )
                        }
                    )
                }

                // ---------------- estudiante ----------------
                composable(Rutas.EST_INICIO) {
                    EstudianteScreen(
                        onSeleccionarAsignatura = { navController.navigate(Rutas.asignatura(it, docente = false)) },
                        onAbrirTarea = { navController.navigate(Rutas.actividad(it)) },
                        onNavegarTrivia = { navController.navigate(Rutas.EST_TRIVIA) }
                    )
                }
                composable(Rutas.EST_PROGRESO) { ProgresoScreen() }
                composable(Rutas.EST_CUADERNO) { NotasScreen() }
                composable(Rutas.EST_PERFIL) {
                    PerfilScreen(esDocente = false, onCerrarSesion = { navController.irAlLogin() })
                }
                composable(Rutas.EST_TRIVIA) { TriviaScreen() }

                // ---------------- docente ----------------
                composable(Rutas.DOC_PANEL) {
                    DocenteScreen(
                        onNavegarGestionContenidos = { navController.navigate(Rutas.DOC_GESTION) },
                        onNavegarAsignarTarea = { navController.navigate(Rutas.formulario()) },
                        onNavegarNotas = { navController.navigate(Rutas.DOC_CUADERNO) },
                        onNavegarInformeEstudiante = { navController.navigate(Rutas.informe()) },
                        onSeleccionarAsignatura = { navController.navigate(Rutas.asignatura(it, docente = true)) },
                        onNavegarTrivia = { navController.navigate(Rutas.DOC_TRIVIA) }
                    )
                }
                composable(Rutas.DOC_GESTION) {
                    GestionarContenidosScreen(
                        onVolver = { navController.popBackStack() },
                        onCrearNuevaGuia = { navController.navigate(Rutas.formulario()) },
                        onVerInformeEstudiante = { navController.navigate(Rutas.informe(it)) }
                    )
                }
                composable(
                    Rutas.DOC_FORMULARIO,
                    arguments = listOf(argumentoTextoOpcional("categoria"))
                ) { entrada ->
                    FormularioScreen(categoriaInicial = entrada.arguments?.getString("categoria"))
                }
                composable(Rutas.DOC_CUADERNO) { NotasScreen() }
                composable(
                    Rutas.DOC_INFORME,
                    arguments = listOf(argumentoTextoOpcional("estudiante"))
                ) { entrada ->
                    InformeEstudianteScreen(
                        nombreEstudiante = entrada.arguments?.getString("estudiante"),
                        onVolver = { navController.popBackStack() }
                    )
                }
                composable(Rutas.DOC_PERFIL) {
                    PerfilScreen(esDocente = true, onCerrarSesion = { navController.irAlLogin() })
                }
                composable(Rutas.DOC_TRIVIA) { TriviaScreen() }

                // ---------------- pantallas completas ----------------
                composable(
                    Rutas.ASIGNATURA,
                    arguments = listOf(
                        navArgument("materiaId") { type = NavType.StringType },
                        navArgument("docente") { type = NavType.BoolType; defaultValue = false }
                    )
                ) { entrada ->
                    val materiaId = entrada.arguments?.getString("materiaId").orEmpty()
                    val esDocente = entrada.arguments?.getBoolean("docente") ?: false
                    DetalleAsignaturaScreen(
                        materiaId = materiaId,
                        esDocente = esDocente,
                        onVolver = { navController.popBackStack() },
                        onAbrirLeccion = { titulo, oa -> navController.navigate(Rutas.leccion(materiaId, titulo, oa)) },
                        onAbrirActividad = { navController.navigate(Rutas.actividad(it)) },
                        onNavegarFormulario = { nombreAsignatura ->
                            if (esDocente) {
                                navController.navigate(Rutas.formulario(nombreAsignatura)) {
                                    popUpTo(Rutas.ASIGNATURA) { inclusive = true }
                                }
                            } else {
                                navController.popBackStack()
                            }
                        }
                    )
                }
                composable(
                    Rutas.LECCION,
                    arguments = listOf(
                        argumentoTextoOpcional("materia"),
                        argumentoTextoOpcional("titulo"),
                        argumentoTextoOpcional("oa")
                    )
                ) { entrada ->
                    val materia = entrada.arguments?.getString("materia")
                    LeccionScreen(
                        tituloUnidad = entrada.arguments?.getString("titulo") ?: "Unidad 1 - Lectura y comprensión",
                        tituloContenido = entrada.arguments?.getString("oa") ?: "El valor de la amistad",
                        colorMateria = ConfiguracionEducativa.asignaturas
                            .find { it.id.equals(materia, ignoreCase = true) }?.color ?: AzulBarraSuperior,
                        onVolver = { navController.popBackStack() },
                        onIrAActividad = { navController.navigate(Rutas.actividad(null)) }
                    )
                }
                composable(
                    Rutas.ACTIVIDAD,
                    arguments = listOf(argumentoTextoOpcional(AppViewModelProvider.ARG_EJERCICIO_ID))
                ) {
                    ActividadInteractivaScreen(
                        onVolver = { navController.popBackStack() },
                        onFinalizarActividad = { aciertos, total ->
                            // al terminar se cierran la actividad y la leccion, y se muestra el feedback
                            navController.popBackStack()
                            if (navController.currentDestination?.route == Rutas.LECCION) navController.popBackStack()
                            navController.navigate(Rutas.feedback(aciertos, total))
                        }
                    )
                }
                composable(
                    Rutas.FEEDBACK,
                    arguments = listOf(
                        navArgument("aciertos") { type = NavType.IntType; defaultValue = 1 },
                        navArgument("total") { type = NavType.IntType; defaultValue = 1 }
                    )
                ) { entrada ->
                    FeedbackActividadScreen(
                        aciertos = entrada.arguments?.getInt("aciertos") ?: 1,
                        total = entrada.arguments?.getInt("total") ?: 1,
                        tituloActividad = "Actividad completada",
                        onContinuar = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}

// ---------------- barras ----------------

@Composable
private fun BarraInferior(ruta: String?, navController: NavHostController) {
    NavigationBar(containerColor = Color.White, tonalElevation = 6.dp) {
        val pestanas = if (Rutas.esEstudiante(ruta)) {
            listOf(
                Triple(Rutas.EST_INICIO, Icons.Default.School, "Inicio"),
                Triple(Rutas.EST_PROGRESO, Icons.AutoMirrored.Filled.TrendingUp, "Progreso"),
                Triple(Rutas.EST_CUADERNO, Icons.Default.Book, "Observaciones"),
                Triple(Rutas.EST_PERFIL, Icons.Default.AccountCircle, "Perfil")
            )
        } else {
            listOf(
                Triple(Rutas.DOC_PANEL, Icons.Default.Analytics, "Inicio"),
                Triple(Rutas.DOC_GESTION, Icons.AutoMirrored.Filled.Assignment, "Gestionar"),
                Triple(Rutas.DOC_CUADERNO, Icons.Default.Book, "Observaciones"),
                Triple(Rutas.DOC_PERFIL, Icons.Default.AccountCircle, "Perfil")
            )
        }
        pestanas.forEach { (destino, icono, etiqueta) ->
            TabNavItem(
                seleccionado = ruta == destino,
                icono = icono,
                etiqueta = etiqueta,
                onClick = { navController.irAPestana(destino) }
            )
        }
    }
}

/** Color y titulo de la barra superior segun la pantalla (igual que la version anterior). */
private fun encabezado(ruta: String?, entrada: NavBackStackEntry?): Pair<Color, String> = when (ruta) {
    Rutas.DOC_PANEL -> AzulBarraSuperior to "MPAZ PRO 2.º · Docente"
    Rutas.DOC_GESTION -> Color(0xFF8E24AA) to "Gestión de Contenidos y Alumnos"
    Rutas.DOC_INFORME -> {
        val estudiante = entrada?.arguments?.getString("estudiante")
        Color(0xFF43A047) to (if (estudiante != null) "Informe: $estudiante" else "Rendimiento General del Curso")
    }
    Rutas.DOC_FORMULARIO -> Color(0xFFFB8C00) to "Asignar Tarea Escolar"
    Rutas.DOC_CUADERNO, Rutas.EST_CUADERNO -> Color(0xFF2E7D32) to "Observaciones"
    Rutas.DOC_PERFIL -> AzulBarraSuperior to "Perfil Docente"
    Rutas.DOC_TRIVIA, Rutas.EST_TRIVIA -> Color(0xFF00897B) to "Desafío de Ciencias · Trivia"
    Rutas.EST_INICIO -> AzulBarraSuperior to "MPAZ PRO 2.º · Estudiante"
    Rutas.EST_PROGRESO -> AzulBarraSuperior to "Progreso Académico"
    Rutas.EST_PERFIL -> AzulBarraSuperior to "Perfil del Estudiante"
    else -> AzulBarraSuperior to "MPAZ PRO 2.º"
}

// ---------------- utilidades de navegacion ----------------

private fun argumentoTextoOpcional(nombre: String) = navArgument(nombre) {
    type = NavType.StringType
    nullable = true
    defaultValue = null
}

/** Entra al inicio de un rol borrando el login de la pila (atras no vuelve al login). */
private fun NavHostController.entrarComo(inicio: String) {
    navigate(inicio) { popUpTo(Rutas.LOGIN) { inclusive = true } }
}

/** Vuelve al login borrando toda la pila. */
private fun NavHostController.irAlLogin() {
    navigate(Rutas.LOGIN) { popUpTo(graph.id) { inclusive = true } }
}

/** Cambia de pestaña: la pila queda siempre como [inicio del rol] -> [pestaña]. */
private fun NavHostController.irAPestana(destino: String) {
    val raiz = if (Rutas.esEstudiante(destino)) Rutas.EST_INICIO else Rutas.DOC_PANEL
    navigate(destino) {
        popUpTo(raiz) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
