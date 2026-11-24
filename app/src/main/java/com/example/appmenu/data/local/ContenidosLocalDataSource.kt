package com.example.appmenu.data.local

import com.example.appmenu.domain.model.EjercicioEducativo
import com.example.appmenu.domain.model.UnidadEducativa
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Fuente de datos LOCAL del catalogo de unidades y ejercicios (en memoria).
 *
 * Antes era el objeto global ActividadesManager al que las pantallas accedian directamente.
 * Ahora solo la usa ContenidosRepository, que ademas la sincroniza con ms-contenidos.
 * Incluye el catalogo inicial para que la app funcione aunque el backend no este disponible.
 */
class ContenidosLocalDataSource(
    private val generarId: () -> Long = { System.currentTimeMillis() }
) {

    // unidades pedagogicas iniciales 1 por materia, ampliable por el docente
    private val _unidades = MutableStateFlow(
        listOf(
            UnidadEducativa(
                id = "uni_len_1",
                materiaId = "lenguaje",
                numero = 1,
                titulo = "Lectura y comprensión",
                objetivoAprendizaje = "OA 3, OA 6 · Comprensión de textos, fábulas y valores"
            ),
            UnidadEducativa(
                id = "uni_mat_1",
                materiaId = "matematica",
                numero = 1,
                titulo = "Números y Operaciones hasta 100",
                objetivoAprendizaje = "OA 1, OA 3 · Conteo de 2 en 2, sumas y restas con material concreto"
            ),
            UnidadEducativa(
                id = "uni_cie_1",
                materiaId = "ciencias",
                numero = 1,
                titulo = "Cuerpo Humano y Hábitats",
                objetivoAprendizaje = "OA 1, OA 3 · Función de órganos vitales y animales nativos"
            ),
            UnidadEducativa(
                id = "uni_his_1",
                materiaId = "historia",
                numero = 1,
                titulo = "Mi Entorno y Pueblos Originarios",
                objetivoAprendizaje = "OA 1, OA 3 · Lectura de planos y tradiciones de Chile"
            )
        )
    )
    val unidades: StateFlow<List<UnidadEducativa>> = _unidades.asStateFlow()

    // exactamente 2 actividades didacticas por materia asociadas a sus unidades
    private val _ejercicios = MutableStateFlow(
        listOf(
            // lenguaje 2 actividades
            EjercicioEducativo(
                id = "len_1",
                materiaId = "lenguaje",
                unidadId = "uni_len_1",
                titulo = "Lectura Guiada: El Zorro y la Cigüeña",
                objetivoAprendizaje = "OA 3: Demostrar comprensión de narraciones extrayendo información e identificando valores.",
                codigoGuia = "1001",
                completado = true,
                pregunta = "¿Qué valor importante aprendemos al compartir con nuestros amigos?",
                opciones = listOf(
                    "Compartir y jugar juntos con respeto",
                    "Quedarse solo sin hablar con nadie",
                    "Pelear y discutir con los compañeros"
                ),
                respuestaCorrectaIndex = 0
            ),
            EjercicioEducativo(
                id = "len_2",
                materiaId = "lenguaje",
                unidadId = "uni_len_1",
                titulo = "Juego de Rimas y Sonidos de Palabras",
                objetivoAprendizaje = "OA 6: Reconocer rimas, aliteraciones y juegos sonoros de la lengua oral.",
                codigoGuia = "1002",
                completado = false,
                pregunta = "¿Qué palabra rima con la palabra 'CANCION'?",
                opciones = listOf(
                    "Mesa",
                    "Corazón",
                    "Árbol"
                ),
                respuestaCorrectaIndex = 1
            ),

            // matematica 2 actividades
            EjercicioEducativo(
                id = "mat_1",
                materiaId = "matematica",
                unidadId = "uni_mat_1",
                titulo = "Conteo Divertido de 2 en 2 hasta 20",
                objetivoAprendizaje = "OA 1: Contar números del 0 al 100 de 2 en 2 partiendo desde cualquier número.",
                codigoGuia = "2001",
                completado = true,
                pregunta = "Si contamos de 2 en 2: 2, 4, 6... ¿cuál número viene a continuación?",
                opciones = listOf(
                    "7",
                    "8",
                    "9"
                ),
                respuestaCorrectaIndex = 1
            ),
            EjercicioEducativo(
                id = "mat_2",
                materiaId = "matematica",
                unidadId = "uni_mat_1",
                titulo = "Sumas con Manzanas y Frutas",
                objetivoAprendizaje = "OA 3: Demostrar que comprenden la adición con material concreto en ámbito 0-20.",
                codigoGuia = "2002",
                completado = false,
                pregunta = "En un canasto hay 3 manzanas y agregamos 4 manzanas más, ¿cuántas hay en total?",
                opciones = listOf(
                    "6 manzanas",
                    "7 manzanas",
                    "8 manzanas"
                ),
                respuestaCorrectaIndex = 1
            ),

            // ciencias naturales 2 actividades
            EjercicioEducativo(
                id = "cie_1",
                materiaId = "ciencias",
                unidadId = "uni_cie_1",
                titulo = "El Corazón y los Pulmones",
                objetivoAprendizaje = "OA 1: Identificar la ubicación y función del corazón, los pulmones y el estómago.",
                codigoGuia = "3001",
                completado = true,
                pregunta = "¿Cuál es la función principal de nuestro corazón en el cuerpo humano?",
                opciones = listOf(
                    "Bombear la sangre a todo nuestro cuerpo",
                    "Ayudarnos a respirar el aire fresco",
                    "Sostener la cabeza erguida"
                ),
                respuestaCorrectaIndex = 0
            ),
            EjercicioEducativo(
                id = "cie_2",
                materiaId = "ciencias",
                unidadId = "uni_cie_1",
                titulo = "Hábitats de los Animales Chilenos",
                objetivoAprendizaje = "OA 3: Observar e identificar animales nativos de Chile y sus ambientes.",
                codigoGuia = "3002",
                completado = false,
                pregunta = "¿En qué lugar de Chile vive y nada naturalmente el Pingüino de Humboldt?",
                opciones = listOf(
                    "En las costas rocosas del mar chileno",
                    "En las copas de los árboles de la selva",
                    "En la arena seca del desierto"
                ),
                respuestaCorrectaIndex = 0
            ),

            // historia 2 actividades
            EjercicioEducativo(
                id = "his_1",
                materiaId = "historia",
                unidadId = "uni_his_1",
                titulo = "Mi Escuela y mi Barrio en el Plano",
                objetivoAprendizaje = "OA 1: Leer planos simples de su entorno utilizando simbología básica y puntos de referencia.",
                codigoGuia = "4001",
                completado = true,
                pregunta = "¿Para qué sirve un plano en nuestra escuela Marcela Paz?",
                opciones = listOf(
                    "Para ubicarnos y saber dónde están las salas y patios",
                    "Para ver películas los fines de semana",
                    "Para comprar útiles escolares"
                ),
                respuestaCorrectaIndex = 0
            ),
            EjercicioEducativo(
                id = "his_2",
                materiaId = "historia",
                unidadId = "uni_his_1",
                titulo = "Pueblos Originarios y Tradiciones de Chile",
                objetivoAprendizaje = "OA 3: Reconocer modos de vida y costumbres de los pueblos originarios de Chile.",
                codigoGuia = "4002",
                completado = false,
                pregunta = "¿Qué pueblo originario del sur de Chile construía sus tradicionales viviendas llamadas rucas?",
                opciones = listOf(
                    "El pueblo Mapuche",
                    "Los Aztecas",
                    "Los Romanos"
                ),
                respuestaCorrectaIndex = 0
            )
        )
    )
    val ejercicios: StateFlow<List<EjercicioEducativo>> = _ejercicios.asStateFlow()

    fun unidadesActuales(): List<UnidadEducativa> = _unidades.value

    fun ejerciciosActuales(): List<EjercicioEducativo> = _ejercicios.value

    /** El docente agrega una unidad; se numera a continuacion de las existentes de la materia. */
    fun agregarUnidad(materiaId: String, titulo: String, oa: String): UnidadEducativa {
        val unidadesMateria = _unidades.value.filter { it.materiaId.equals(materiaId, ignoreCase = true) }
        val nuevoNumero = unidadesMateria.size + 1
        val nueva = UnidadEducativa(
            id = "uni_${materiaId.lowercase()}_${generarId()}",
            materiaId = materiaId.lowercase(),
            numero = nuevoNumero,
            titulo = "Unidad $nuevoNumero - ${titulo.trim()}",
            objetivoAprendizaje = oa.trim()
        )
        _unidades.update { it + nueva }
        return nueva
    }

    /** El docente agrega un ejercicio asociado a una unidad. */
    fun agregarEjercicio(
        materiaId: String,
        unidadId: String,
        titulo: String,
        oa: String,
        codigoGuia: String,
        pregunta: String = "¿Cuál es la opción correcta?",
        opciones: List<String> = listOf("Opción A", "Opción B", "Opción C"),
        respuestaCorrectaIndex: Int = 0,
        disponible: Boolean = true,
        esTarea: Boolean = false,
        fechaEntrega: Long? = null
    ): EjercicioEducativo {
        val nuevo = EjercicioEducativo(
            id = "ej_${generarId()}",
            materiaId = materiaId.lowercase(),
            unidadId = unidadId,
            titulo = titulo.trim(),
            objetivoAprendizaje = oa.trim(),
            codigoGuia = codigoGuia.trim(),
            completado = false,
            disponibleParaEstudiantes = disponible,
            esTarea = esTarea,
            fechaEntrega = fechaEntrega,
            pregunta = if (pregunta.isBlank()) "¿Cuál es la respuesta correcta?" else pregunta.trim(),
            opciones = if (opciones.isEmpty()) listOf("Opción 1", "Opción 2", "Opción 3") else opciones,
            respuestaCorrectaIndex = respuestaCorrectaIndex.coerceIn(0, 2)
        )
        _ejercicios.update { it + nuevo }
        return nuevo
    }

    /** Agrega una unidad que vino del servidor y no existia localmente. */
    fun insertarUnidad(unidad: UnidadEducativa) {
        _unidades.update { it + unidad }
    }

    /** Agrega un ejercicio que vino del servidor y no existia localmente. */
    fun insertarEjercicio(ejercicio: EjercicioEducativo) {
        _ejercicios.update { it + ejercicio }
    }

    fun actualizarUnidad(id: String, cambio: (UnidadEducativa) -> UnidadEducativa) {
        _unidades.update { lista -> lista.map { if (it.id == id) cambio(it) else it } }
    }

    fun actualizarEjercicio(id: String, cambio: (EjercicioEducativo) -> EjercicioEducativo) {
        _ejercicios.update { lista -> lista.map { if (it.id == id) cambio(it) else it } }
    }

    fun eliminarEjercicio(id: String) {
        _ejercicios.update { lista -> lista.filterNot { it.id == id } }
    }

    /** Marca como completado o pendiente. */
    fun alternarCompletado(ejercicioId: String) {
        actualizarEjercicio(ejercicioId) { ej ->
            ej.copy(completado = !ej.completado, nota = if (!ej.completado) 10 else 0)
        }
    }

    fun marcarCompletado(ejercicioId: String, nota: Int = 10) {
        actualizarEjercicio(ejercicioId) { ej ->
            ej.copy(completado = true, nota = nota, intentos = ej.intentos + 1)
        }
    }
}
