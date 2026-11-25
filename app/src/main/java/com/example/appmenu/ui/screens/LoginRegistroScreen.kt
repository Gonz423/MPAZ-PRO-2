package com.example.appmenu.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appmenu.di.AppViewModelProvider
import com.example.appmenu.ui.theme.*
import com.example.appmenu.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginRegistroScreen(
    authViewModel: AuthViewModel = viewModel(factory = AppViewModelProvider.Factory),
    onLoginExitoso: (rol: String) -> Unit,
    onAccesoRapidoEstudiante: () -> Unit,
    onAccesoRapidoDocente: () -> Unit
) {
    val state by authViewModel.uiState.collectAsState()
    var tabSeleccionado by remember { mutableIntStateOf(0) } // 0 = Iniciar Sesion, 1 = Registrarse
    var passwordVisible by remember { mutableStateOf(false) }

    // Texto negro en los campos: con el tema oscuro del sistema salia blanco sobre la tarjeta blanca
    val coloresCampo = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.Black,
        unfocusedTextColor = Color.Black,
        cursorColor = Color.Black,
        focusedLabelColor = AzulBarraSuperior,
        unfocusedLabelColor = GrisTexto,
        focusedPlaceholderColor = GrisTexto,
        unfocusedPlaceholderColor = GrisTexto
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoApp)
            // respeta barra de estado, barra de gestos y teclado (la app es edge-to-edge)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(36.dp))

        // Encabezado institucional
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "MPAZ PRO 2.º",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = AzulBarraSuperior
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = Color(0xFFFFB300),
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Plataforma Educativa Digital",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextoOscuro
        )
        Text(
            text = "Escuela Escritora Marcela Paz · San Bernardo",
            fontSize = 12.sp,
            color = GrisTexto
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Selector de Pestañas (Login / Registro)
        TabRow(
            selectedTabIndex = tabSeleccionado,
            containerColor = Color.White,
            contentColor = AzulBarraSuperior,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
        ) {
            Tab(
                selected = tabSeleccionado == 0,
                onClick = { tabSeleccionado = 0 },
                text = {
                    Text(
                        text = "Iniciar Sesión",
                        fontWeight = if (tabSeleccionado == 0) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 14.sp
                    )
                },
                icon = {
                    Icon(imageVector = Icons.Default.Login, contentDescription = null, modifier = Modifier.size(20.dp))
                }
            )
            Tab(
                selected = tabSeleccionado == 1,
                onClick = { tabSeleccionado = 1 },
                text = {
                    Text(
                        text = "Crear Cuenta",
                        fontWeight = if (tabSeleccionado == 1) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 14.sp
                    )
                },
                icon = {
                    Icon(imageVector = Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(20.dp))
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Contenido según pestaña
        if (tabSeleccionado == 0) {
            // ================= PESTAÑA 1: INICIAR SESIÓN =================
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "¡Bienvenido de vuelta!",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextoOscuro
                    )
                    Text(
                        text = "Ingresa con tu correo o nombre de usuario",
                        fontSize = 13.sp,
                        color = GrisTexto
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Campo Correo o Usuario
                    OutlinedTextField(
                        value = state.loginIdentificador,
                        onValueChange = { authViewModel.onLoginIdentificadorChanged(it) },
                        label = { Text("Correo o Usuario") },
                        placeholder = { Text("ej: alumno@marcelapaz.cl") },
                        leadingIcon = {
                            Icon(Icons.Default.Email, contentDescription = null, tint = AzulBarraSuperior)
                        },
                        isError = state.loginErrorIdentificador != null,
                        supportingText = {
                            if (state.loginErrorIdentificador != null) {
                                Text(state.loginErrorIdentificador!!, color = MaterialTheme.colorScheme.error)
                            }
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        colors = coloresCampo,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Campo Contraseña
                    OutlinedTextField(
                        value = state.loginPassword,
                        onValueChange = { authViewModel.onLoginPasswordChanged(it) },
                        label = { Text("Contraseña") },
                        placeholder = { Text("••••••••") },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = AzulBarraSuperior)
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = if (passwordVisible) "Ocultar" else "Mostrar",
                                    tint = GrisTexto
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        isError = state.loginErrorPassword != null,
                        supportingText = {
                            if (state.loginErrorPassword != null) {
                                Text(state.loginErrorPassword!!, color = MaterialTheme.colorScheme.error)
                            }
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        colors = coloresCampo,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Mensaje de error general
                    AnimatedVisibility(
                        visible = state.loginMensajeError != null,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ErrorOutline,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = state.loginMensajeError ?: "",
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Botón Iniciar Sesión
                    Button(
                        onClick = { authViewModel.iniciarSesion(onExito = onLoginExitoso) },
                        enabled = !state.loginCargando,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AzulBarraSuperior)
                    ) {
                        if (state.loginCargando) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Entrar a la Plataforma", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Acceso rápido alternativo
            Text(
                text = "— O entra rápidamente como invitado —",
                fontSize = 13.sp,
                color = GrisTexto,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onAccesoRapidoEstudiante,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.5.dp)
                ) {
                    Icon(Icons.Default.School, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color(0xFF1E88E5))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Estudiante", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }

                OutlinedButton(
                    onClick = onAccesoRapidoDocente,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.5.dp)
                ) {
                    Icon(Icons.Default.Fingerprint, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color(0xFF43A047))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Docente", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        } else {
            // ================= PESTAÑA 2: REGISTRO =================
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Crear nueva cuenta",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextoOscuro
                    )
                    Text(
                        text = "Regístrate para guardar tu progreso educativo",
                        fontSize = 13.sp,
                        color = GrisTexto
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Nombre Completo
                    OutlinedTextField(
                        value = state.regNombreCompleto,
                        onValueChange = { authViewModel.onRegNombreChanged(it) },
                        label = { Text("Nombre Completo") },
                        placeholder = { Text("ej: Sofía Morales") },
                        leadingIcon = {
                            Icon(Icons.Default.Badge, contentDescription = null, tint = AzulBarraSuperior)
                        },
                        isError = state.regErrorNombre != null,
                        supportingText = {
                            if (state.regErrorNombre != null) {
                                Text(state.regErrorNombre!!, color = MaterialTheme.colorScheme.error)
                            }
                        },
                        singleLine = true,
                        colors = coloresCampo,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Correo electrónico
                    OutlinedTextField(
                        value = state.regEmail,
                        onValueChange = { authViewModel.onRegEmailChanged(it) },
                        label = { Text("Correo electrónico") },
                        placeholder = { Text("sofia@marcelapaz.cl") },
                        leadingIcon = {
                            Icon(Icons.Default.Email, contentDescription = null, tint = AzulBarraSuperior)
                        },
                        isError = state.regErrorEmail != null,
                        supportingText = {
                            if (state.regErrorEmail != null) {
                                Text(state.regErrorEmail!!, color = MaterialTheme.colorScheme.error)
                            }
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        colors = coloresCampo,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Contraseña
                    OutlinedTextField(
                        value = state.regPassword,
                        onValueChange = { authViewModel.onRegPasswordChanged(it) },
                        label = { Text("Contraseña (mín. 4 caracteres)") },
                        placeholder = { Text("••••••••") },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = AzulBarraSuperior)
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = if (passwordVisible) "Ocultar" else "Mostrar",
                                    tint = GrisTexto
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        isError = state.regErrorPassword != null,
                        supportingText = {
                            if (state.regErrorPassword != null) {
                                Text(state.regErrorPassword!!, color = MaterialTheme.colorScheme.error)
                            }
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        colors = coloresCampo,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Selector de Perfil / Rol
                    Text(
                        text = "Tipo de perfil:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextoOscuro
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        val esEst = state.regRol == "ESTUDIANTE"
                        val esDoc = state.regRol == "DOCENTE"

                        // Tarjeta Estudiante
                        Surface(
                            onClick = { authViewModel.onRegRolChanged("ESTUDIANTE") },
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .border(
                                    width = if (esEst) 2.dp else 1.dp,
                                    color = if (esEst) AzulBarraSuperior else Color(0xFFE0E0E0),
                                    shape = RoundedCornerShape(14.dp)
                                ),
                            color = if (esEst) AzulBarraSuperior.copy(alpha = 0.08f) else Color.White
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.School,
                                    contentDescription = null,
                                    tint = if (esEst) AzulBarraSuperior else GrisTexto,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Estudiante",
                                    fontSize = 13.sp,
                                    fontWeight = if (esEst) FontWeight.Bold else FontWeight.Normal,
                                    color = if (esEst) AzulBarraSuperior else TextoOscuro
                                )
                            }
                        }

                        // Tarjeta Docente
                        Surface(
                            onClick = { authViewModel.onRegRolChanged("DOCENTE") },
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .border(
                                    width = if (esDoc) 2.dp else 1.dp,
                                    color = if (esDoc) Color(0xFF2E7D32) else Color(0xFFE0E0E0),
                                    shape = RoundedCornerShape(14.dp)
                                ),
                            color = if (esDoc) Color(0xFF2E7D32).copy(alpha = 0.08f) else Color.White
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.MenuBook,
                                    contentDescription = null,
                                    tint = if (esDoc) Color(0xFF2E7D32) else GrisTexto,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Docente",
                                    fontSize = 13.sp,
                                    fontWeight = if (esDoc) FontWeight.Bold else FontWeight.Normal,
                                    color = if (esDoc) Color(0xFF2E7D32) else TextoOscuro
                                )
                            }
                        }
                    }

                    // Campo curso si es estudiante
                    if (state.regRol == "ESTUDIANTE") {
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = state.regCurso,
                            onValueChange = { authViewModel.onRegCursoChanged(it) },
                            label = { Text("Curso") },
                            leadingIcon = {
                                Icon(Icons.Default.Class, contentDescription = null, tint = AzulBarraSuperior)
                            },
                            singleLine = true,
                            colors = coloresCampo,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Mensaje de error general de registro
                    AnimatedVisibility(
                        visible = state.regMensajeError != null,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = state.regMensajeError ?: "", color = MaterialTheme.colorScheme.onErrorContainer, fontSize = 13.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Botón Registrarse
                    Button(
                        onClick = { authViewModel.registrarse(onExito = onLoginExitoso) },
                        enabled = !state.regCargando,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                    ) {
                        if (state.regCargando) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Crear Cuenta y Entrar", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(36.dp))
    }
}
