package com.example.appmenu.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Museum
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.appmenu.ui.theme.*

// modelo modular de asignaturas y paneles - mpaz pro 2.º

data class TarjetaAsignatura(
    val id: String,
    val nombre: String,
    val actividadesTexto: String,
    val color: Color,
    val icono: ImageVector,
    val progreso: Float = 0.5f
)

data class MetricaDocente(
    val valor: String,
    val etiqueta: String,
    val fondoColor: Color,
    val textoColor: Color
)

data class ProgresoAsignatura(
    val asignatura: String,
    val porcentaje: Int,
    val color: Color
)

object ConfiguracionEducativa {

    // lista de asignaturas del estudiante
    // cambia el orden de los elementos aqui para alterar el orden en pantalla

    val asignaturas = listOf(
        // 1. lenguaje
        TarjetaAsignatura(
            id = "lenguaje",
            nombre = "Lenguaje",
            actividadesTexto = "2 actividades",
            color = ColorLenguaje,
            icono = Icons.AutoMirrored.Filled.MenuBook,
            progreso = 0.50f
        ),
        // 2. matematica
        TarjetaAsignatura(
            id = "matematica",
            nombre = "Matemática",
            actividadesTexto = "2 actividades",
            color = ColorMatematica,
            icono = Icons.Default.Calculate,
            progreso = 0.50f
        ),
        // 3. ciencias
        TarjetaAsignatura(
            id = "ciencias",
            nombre = "Ciencias",
            actividadesTexto = "2 actividades",
            color = ColorCiencias,
            icono = Icons.Default.Eco,
            progreso = 0.50f
        ),
        // 4. historia
        TarjetaAsignatura(
            id = "historia",
            nombre = "Historia",
            actividadesTexto = "2 actividades",
            color = ColorHistoria,
            icono = Icons.Default.Museum,
            progreso = 0.50f
        )
    )

    // metricas del panel docente
    val metricasDocente = listOf(
        MetricaDocente(
            valor = "28",
            etiqueta = "Alumnos",
            fondoColor = FondoMetricaAzul,
            textoColor = TextoMetricaAzul
        ),
        MetricaDocente(
            valor = "24",
            etiqueta = "Activos",
            fondoColor = FondoMetricaVerde,
            textoColor = TextoMetricaVerde
        )
    )

    // progreso general del curso por asignatura
    val progresoCurso = listOf(
        ProgresoAsignatura(asignatura = "Lenguaje", porcentaje = 72, color = ColorLenguaje),
        ProgresoAsignatura(asignatura = "Matemática", porcentaje = 64, color = ColorMatematica),
        ProgresoAsignatura(asignatura = "Ciencias", porcentaje = 58, color = ColorCiencias),
        ProgresoAsignatura(asignatura = "Historia", porcentaje = 70, color = ColorHistoria)
    )
}
