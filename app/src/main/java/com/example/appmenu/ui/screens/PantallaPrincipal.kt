package com.example.appmenu.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.animation.animateColorAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appmenu.viewmodel.EstadoViewModel
import kotlinx.coroutines.delay



@Composable
fun PantallaPrincipal(
    estadoViewModel: EstadoViewModel = viewModel()
) {
    val estadoGuardado by
    estadoViewModel.estadoActivado.collectAsState()

    val cargando by
    estadoViewModel.cargando.collectAsState()

    val guardadoCorrectamente by
    estadoViewModel.guardadoCorrectamente.collectAsState()

    val mensajeError by
    estadoViewModel.mensajeError.collectAsState()

    var estadoActivado by rememberSaveable {
        mutableStateOf(false)
    }

    LaunchedEffect(cargando, estadoGuardado) {
        if (!cargando) {
            estadoActivado = estadoGuardado
        }
    }

    LaunchedEffect(guardadoCorrectamente) {
        if (guardadoCorrectamente) {
            delay(2000)
            estadoViewModel.ocultarMensajeGuardado()
        }
    }

    val textoEstado by remember {
        derivedStateOf {
            if (estadoActivado) {
                "Modo especial activado"
            } else {
                "Modo especial desactivado"
            }
        }
    }

    val colorBoton by animateColorAsState(
        targetValue = if (estadoActivado) {
            Color(0xFF2E7D32)
        } else {
            Color(0xFF1565C0)
        },
        label = "Color del botón"
    )

    Scaffold { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            if (cargando) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )

                    Text(
                        text = "Cargando estado...",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Control de modo especial",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(24.dp)
                    )

                    Text(
                        text = textoEstado,
                        style = MaterialTheme.typography.titleMedium,
                        color = if (estadoActivado) {
                            Color(0xFF2E7D32)
                        } else {
                            MaterialTheme.colorScheme.onBackground
                        }
                    )

                    Spacer(
                        modifier = Modifier.height(24.dp)
                    )

                    Button(
                        onClick = {
                            val nuevoEstado = !estadoActivado
                            estadoActivado = nuevoEstado
                            estadoViewModel.guardarEstado(nuevoEstado)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorBoton
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.width(220.dp)
                    ) {
                        Text(
                            text = if (estadoActivado) {
                                "Desactivar modo"
                            } else {
                                "Activar modo"
                            }
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(24.dp)
                    )

                    AnimatedVisibility(
                        visible = guardadoCorrectamente,
                        enter = fadeIn() +
                                expandVertically(),
                        exit = fadeOut() +
                                shrinkVertically()
                    ) {
                        Text(
                            text = "¡Estado guardado correctamente!",
                            color = Color(0xFF2E7D32),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    AnimatedVisibility(
                        visible = mensajeError != null,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Text(
                            text = mensajeError ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}


/*
package com.example.miapp.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.miapp.viewmodel.EstadoViewModel
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.sin
private val FondoOscuro = Color(0xFF050816)
private val AzulNeon = Color(0xFF00CFFF)
private val VerdeNeon = Color(0xFF00FF9C)
private val VioletaNeon = Color(0xFF8A5CFF)
@Composable
fun PantallaPrincipal(estadoViewModel: EstadoViewModel = viewModel()) {
    val estadoGuardado by estadoViewModel.estadoActivado.collectAsState()
    val cargando by estadoViewModel.cargando.collectAsState()
    val guardadoCorrectamente by estadoViewModel.guardadoCorrectamente.collectAsState()
    val mensajeError by estadoViewModel.mensajeError.collectAsState()
    var estadoActivado by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(cargando, estadoGuardado) {
        if (!cargando) estadoActivado = estadoGuardado
    }
    LaunchedEffect(guardadoCorrectamente) {
        if (guardadoCorrectamente) {
            delay(2500)
            estadoViewModel.ocultarMensajeGuardado()
        }
    }
    val colorPrincipal by animateColorAsState(
        targetValue = if (estadoActivado) VerdeNeon else AzulNeon,
        animationSpec = tween(600),
        label = "Color neón"
    )
    val transicion = rememberInfiniteTransition(label = "Animaciones futuristas")
    val pulso by transicion.animateFloat(
        initialValue = 0.88f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Pulso luminoso"
    )
    val rotacion by transicion.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(animation = tween(7000)),
        label = "Rotación de anillos"
    )
    val movimientoParticulas by transicion.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(animation = tween(4000)),
        label = "Movimiento de partículas"
    )
    val escalaBoton by animateFloatAsState(
        targetValue = if (estadoActivado) 1.06f else 1f,
        animationSpec = tween(300),
        label = "Escala del botón"
    )
    Scaffold(containerColor = FondoOscuro) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Brush.verticalGradient(listOf(Color(0xFF10183A), FondoOscuro, Color(0xFF10102A))))
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val ancho = size.width
                val alto = size.height
                for (i in 0 until 38) {
                    val xBase = (i * 137.5f) % ancho
                    val yBase = (i * 83.7f) % alto
                    val desplazamiento = movimientoParticulas * alto * 0.22f
                    val x = ((xBase + sin((movimientoParticulas * 2.0 * PI + i).toFloat()) * 18f) % ancho + ancho) % ancho
                    val y = (yBase - desplazamiento + alto) % alto
                    drawCircle(
                        color = if (i % 2 == 0) AzulNeon.copy(alpha = 0.35f) else VioletaNeon.copy(alpha = 0.35f),
                        radius = if (i % 4 == 0) 3.5f else 1.8f,
                        center = Offset(x, y)
                    )
                }
            }
            Column(
                modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("N E X U S", color = colorPrincipal, fontSize = 15.sp, fontWeight = FontWeight.Bold, letterSpacing = 5.sp)
                Spacer(modifier = Modifier.height(10.dp))
                Text("CONTROL DE ENERGÍA", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(8.dp))
                Text("SISTEMA DE ACTIVACIÓN", color = Color.LightGray.copy(alpha = 0.7f), fontSize = 11.sp, letterSpacing = 2.sp)
                Spacer(modifier = Modifier.height(36.dp))
                Box(modifier = Modifier.size(250.dp), contentAlignment = Alignment.Center) {
                    Box(
                        modifier = Modifier.size(230.dp).graphicsLayer { scaleX = pulso * 0.98f; scaleY = pulso * 0.98f }.border(1.dp, colorPrincipal.copy(alpha = 0.4f), CircleShape)
                    )
                    Box(
                        modifier = Modifier.size(200.dp).graphicsLayer { scaleX = 1.02f - (pulso - 1f) * 0.4f; scaleY = 1.02f - (pulso - 1f) * 0.4f }.border(2.dp, VioletaNeon.copy(alpha = 0.7f), CircleShape)
                    )
                    Canvas(modifier = Modifier.size(178.dp)) {
                        drawArc(
                            color = colorPrincipal,
                            startAngle = rotacion,
                            sweepAngle = 100f,
                            useCenter = false,
                            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                        )
                        drawArc(
                            color = VioletaNeon,
                            startAngle = rotacion + 180f,
                            sweepAngle = 75f,
                            useCenter = false,
                            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                    Box(
                        modifier = Modifier.size(135.dp)
                            .graphicsLayer { scaleX = pulso; scaleY = pulso }
                            .shadow(35.dp, CircleShape, ambientColor = colorPrincipal, spotColor = colorPrincipal)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(
                                        Color.White.copy(alpha = 0.95f),
                                        colorPrincipal,
                                        if (estadoActivado) Color(0xFF007A62) else Color(0xFF123C91),
                                        FondoOscuro
                                    )
                                )
                            )
                            .border(2.dp, Color.White.copy(alpha = 0.8f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(if (estadoActivado) "ON" else "OFF", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Black, letterSpacing = 3.sp)
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                AnimatedContent(
                    targetState = estadoActivado,
                    transitionSpec = {
                        (fadeIn(tween(350)) + scaleIn(initialScale = 0.7f)) togetherWith
                            (fadeOut(tween(200)) + scaleOut(targetScale = 1.2f))
                    },
                    label = "Cambio de estado"
                ) { activado ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (activado) "¡ENERGÍA ACTIVADA!" else "SISTEMA EN ESPERA",
                            color = if (activado) VerdeNeon else AzulNeon,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (activado) "Todos los sistemas están activos" else "Listo para iniciar",
                            color = Color.White.copy(alpha = 0.65f),
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                Spacer(modifier = Modifier.height(30.dp))
                Button(
                    onClick = {
                        val nuevoEstado = !estadoActivado
                        estadoActivado = nuevoEstado
                        estadoViewModel.guardarEstado(nuevoEstado)
                    },
                    modifier = Modifier.width(230.dp).height(58.dp).graphicsLayer {
                        scaleX = escalaBoton
                        scaleY = escalaBoton
                    }.shadow(18.dp, RoundedCornerShape(18.dp), ambientColor = colorPrincipal, spotColor = colorPrincipal),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colorPrincipal, contentColor = FondoOscuro)
                ) {
                    Text(
                        text = if (estadoActivado) "⏻  DESACTIVAR" else "⚡  ACTIVAR SISTEMA",
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                AnimatedVisibility(
                    visible = guardadoCorrectamente,
                    enter = fadeIn() + expandVertically() + scaleIn(initialScale = 0.8f),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Surface(
                        color = VerdeNeon.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.border(1.dp, VerdeNeon.copy(alpha = 0.65f), RoundedCornerShape(12.dp))
                    ) {
                        Text(
                            text = "✓  ¡ESTADO GUARDADO!",
                            color = VerdeNeon,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                        )
                    }
                }
                AnimatedVisibility(
                    visible = mensajeError != null,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Text(
                        text = mensajeError ?: "",
                        color = Color(0xFFFF5370),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "● SISTEMA ${if (cargando) "CONECTANDO" else "EN LÍNEA"}",
                    color = if (cargando) Color(0xFFFFC857) else VerdeNeon.copy(alpha = 0.75f),
                    fontSize = 10.sp,
                    letterSpacing = 2.sp
                )
            }
            AnimatedVisibility(
                visible = cargando,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier.fillMaxSize().background(FondoOscuro.copy(alpha = 0.88f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = AzulNeon, strokeWidth = 4.dp, modifier = Modifier.size(54.dp))
                        Spacer(modifier = Modifier.height(20.dp))
                        Text("INICIALIZANDO SISTEMA...", color = AzulNeon, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                    }
                }
            }
        }
    }
}
 */