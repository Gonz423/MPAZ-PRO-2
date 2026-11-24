package com.example.appmenu.ui.navigation

import android.net.Uri

/**
 * Rutas de Navigation Compose. El prefijo indica el rol: "est/" estudiante, "doc/" docente.
 * Las pantallas con barra superior e inferior son las "pestañas" de cada rol.
 */
object Rutas {
    const val SPLASH = "splash"
    const val LOGIN = "login"

    // flujo estudiante (mockups 3, 10, 11)
    const val EST_INICIO = "est/inicio"
    const val EST_PROGRESO = "est/progreso"
    const val EST_CUADERNO = "est/cuaderno"
    const val EST_PERFIL = "est/perfil"
    const val EST_TRIVIA = "est/trivia"

    // flujo docente (mockups 12, 13)
    const val DOC_PANEL = "doc/panel"
    const val DOC_GESTION = "doc/gestion"
    const val DOC_FORMULARIO = "doc/formulario?categoria={categoria}"
    const val DOC_CUADERNO = "doc/cuaderno"
    const val DOC_INFORME = "doc/informe?estudiante={estudiante}"
    const val DOC_PERFIL = "doc/perfil"
    const val DOC_TRIVIA = "doc/trivia"

    // pantallas completas (sin barras), compartidas por ambos roles
    const val ASIGNATURA = "asignatura/{materiaId}?docente={docente}"
    const val LECCION = "leccion?materia={materia}&titulo={titulo}&oa={oa}"
    const val ACTIVIDAD = "actividad?ejercicioId={ejercicioId}"
    const val FEEDBACK = "feedback?aciertos={aciertos}&total={total}"

    // ---------------- constructores de rutas con argumentos ----------------

    fun formulario(categoria: String? = null) =
        if (categoria == null) "doc/formulario" else "doc/formulario?categoria=${Uri.encode(categoria)}"

    fun informe(estudiante: String? = null) =
        if (estudiante == null) "doc/informe" else "doc/informe?estudiante=${Uri.encode(estudiante)}"

    fun asignatura(materiaId: String, docente: Boolean) = "asignatura/${Uri.encode(materiaId)}?docente=$docente"

    fun leccion(materia: String, titulo: String, oa: String) =
        "leccion?materia=${Uri.encode(materia)}&titulo=${Uri.encode(titulo)}&oa=${Uri.encode(oa)}"

    fun actividad(ejercicioId: String?) =
        if (ejercicioId == null) "actividad" else "actividad?ejercicioId=${Uri.encode(ejercicioId)}"

    fun feedback(aciertos: Int, total: Int) = "feedback?aciertos=$aciertos&total=$total"

    // ---------------- clasificacion ----------------

    val pestanasEstudiante = listOf(EST_INICIO, EST_PROGRESO, EST_CUADERNO, EST_PERFIL, EST_TRIVIA)
    val pestanasDocente = listOf(DOC_PANEL, DOC_GESTION, DOC_FORMULARIO, DOC_CUADERNO, DOC_INFORME, DOC_PERFIL, DOC_TRIVIA)

    fun tieneBarras(ruta: String?) = ruta in pestanasEstudiante || ruta in pestanasDocente

    fun esEstudiante(ruta: String?) = ruta?.startsWith("est/") == true

    fun esRaiz(ruta: String?) = ruta == EST_INICIO || ruta == DOC_PANEL
}
