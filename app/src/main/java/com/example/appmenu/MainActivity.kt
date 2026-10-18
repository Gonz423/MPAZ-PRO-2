package com.example.appmenu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.appmenu.ui.theme.AppMenuTheme
import com.example.appmenu.ui.theme.Green
import com.example.appmenu.ui.theme.Negro
import com.example.appmenu.ui.screens.PantallaPrincipal
import com.example.appmenu.ui.screens.FormularioScreen
import com.example.appmenu.ui.screens.PerfilScreen
import com.example.appmenu.ui.screens.NotasScreen


class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppMenuTheme {
                var pantallaActual by remember { mutableStateOf("estado") }
                var menuAbierto by remember { mutableStateOf(false) }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = Green,
                                titleContentColor = Negro
                            ),
                            title = { Text(text = "Menu") },
                            actions = {
                                Box {
                                    IconButton(onClick = { menuAbierto = true }) {
                                        Icon(
                                            imageVector = Icons.Filled.Menu,
                                            contentDescription = "Opciones Menu"
                                        )
                                    }
                                    DropdownMenu(
                                        expanded = menuAbierto,
                                        onDismissRequest = { menuAbierto = false }
                                    ) {
                                        DropdownMenuItem(
                                            text = { Text("Estado / DataStore") },
                                            onClick = {
                                                pantallaActual = "estado"
                                                menuAbierto = false
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("Formulario") },
                                            onClick = {
                                                pantallaActual = "formulario"
                                                menuAbierto = false
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("Cámara / Perfil") },
                                            onClick = {
                                                pantallaActual = "camara"
                                                menuAbierto = false
                                            }
                                        )
                                        // Añadimos la opción de Room aquí:
                                        DropdownMenuItem(
                                            text = { Text("Base de Datos (Room)") },
                                            onClick = {
                                                pantallaActual = "room"
                                                menuAbierto = false
                                            }
                                        )
                                    }
                                }
                            }
                        )
                    }
                ) { _ ->
                    // Y aquí en el when enlazamos la pantalla:
                    when (pantallaActual) {
                        "formulario" -> FormularioScreen()
                        "camara" -> PerfilScreen()
                        "room" -> NotasScreen()
                        else -> PantallaPrincipal()
                    }
                }
            }
        }
    }
}

// Esto de aqui abajo es lo del profe, le pedí a la ia que me lo modificara lo menos posible (El codigo de arriba) para que se pudieran conectar los menus y formularios

/* package com.example.appmenu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.appmenu.ui.componentes.NuevoMenu
import com.example.appmenu.ui.theme.AppMenuTheme
import com.example.appmenu.ui.theme.*
import com.example.appmenu.ui.screens.PantallaPrincipal
import com.example.appmenu.ui.screens.FormularioScreen

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppMenuTheme {

                Scaffold(modifier = Modifier.fillMaxSize(),
                    topBar={
                        TopAppBar(colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Green,
                            titleContentColor = Negro
                        ),title={ Text(text="Menu") },
                            actions= {
                                var isMenuOpened by remember { mutableStateOf(false) }
                                IconButton(onClick = { isMenuOpened = true }) {
                                    Icon(
                                        imageVector = Icons.Filled.Menu,
                                        contentDescription = "Opciones Menu"
                                    )
                                    NuevoMenu(isExpanded = isMenuOpened, onItemClick = {}) {
                                        isMenuOpened=false
                                    }
                            }
                            })
                    }) { _ ->
                    if (pantallaActual == "formulario") {
                        FormularioScreen()
                    } else {
                        PantallaPrincipal()
                    }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        color=Negro,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AppMenuTheme {
        Greeting("Android")
    }
}   */
