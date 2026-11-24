package com.example.appmenu.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appmenu.data.repository.ContenidosRepository
import com.example.appmenu.domain.model.FormularioModel
import com.example.appmenu.domain.model.Materias
import com.example.appmenu.domain.model.NuevoEjercicio
import com.example.appmenu.domain.model.PreguntaFormulario
import com.example.appmenu.domain.model.UnidadEducativa
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FormularioErrores(
    val nombre: String? = null,
    val descripcion: String? = null,
    val cantidad: String? = null,
    val aceptaTerminos: String? = null,
    val fecha: String? = null,
    val categoria: String? = null,
    val unidad: String? = null
)

class FormularioViewModel(private val contenidosRepository: ContenidosRepository) : ViewModel() {

    private val _formulario = MutableStateFlow(FormularioModel())
    val formulario: StateFlow<FormularioModel> = _formulario.asStateFlow()

    private val _errores = MutableStateFlow(FormularioErrores())
    val errores: StateFlow<FormularioErrores> = _errores.asStateFlow()

    /** Unidades del catalogo, para el selector de unidad. */
    val unidades: StateFlow<List<UnidadEducativa>> = contenidosRepository.unidades

    // ---------------- cambios de campos ----------------

    fun actualizarNombre(valor: String) {
        _formulario.update { it.copy(nombre = valor) }
        if (valor.isNotBlank()) _errores.update { it.copy(nombre = null) }
    }

    fun actualizarDescripcion(valor: String) {
        _formulario.update { it.copy(descripcion = valor) }
        if (valor.isNotBlank()) _errores.update { it.copy(descripcion = null) }
    }

    /** Solo acepta digitos. */
    fun actualizarCantidad(valor: String) {
        if (!valor.all { it.isDigit() }) return
        _formulario.update { it.copy(cantidad = valor) }
        if (valor.isNotBlank()) _errores.update { it.copy(cantidad = null) }
    }

    fun actualizarAceptaTerminos(valor: Boolean) {
        _formulario.update { it.copy(aceptaTerminos = valor) }
        if (valor) _errores.update { it.copy(aceptaTerminos = null) }
    }

    fun actualizarActivo(valor: Boolean) = _formulario.update { it.copy(activo = valor) }

    fun actualizarFecha(valor: Long?) {
        _formulario.update { it.copy(fecha = valor) }
        if (valor != null) _errores.update { it.copy(fecha = null) }
    }

    fun actualizarCategoria(valor: String) {
        _formulario.update { it.copy(categoria = valor) }
        _errores.update { it.copy(categoria = null) }
    }

    /** Recibe el nombre de asignatura desde otra pantalla y lo ajusta al texto del selector. */
    fun aplicarCategoriaInicial(categoria: String?) {
        if (!categoria.isNullOrBlank()) actualizarCategoria(Materias.normalizarCategoria(categoria))
    }

    fun actualizarEsTarea(valor: Boolean) = _formulario.update { it.copy(esTarea = valor) }

    fun actualizarUnidadId(valor: String) {
        _formulario.update { it.copy(unidadId = valor) }
        _errores.update { it.copy(unidad = null) }
    }

    // ---------------- validacion ----------------

    fun validarFormulario(): Boolean {
        val datos = _formulario.value

        val nombreError = when {
            datos.nombre.isBlank() ->
                if (datos.esTarea) "El título de la tarea es obligatorio" else "El nombre de la actividad es obligatorio"
            datos.nombre.length < 3 -> "El título debe tener al menos 3 caracteres"
            else -> null
        }
        val descripcionError = when {
            datos.descripcion.isBlank() ->
                if (datos.esTarea) "Las instrucciones de la tarea son obligatorias" else "La descripción / OA es obligatoria"
            datos.descripcion.length < 5 -> "Debe tener al menos 5 caracteres"
            else -> null
        }
        val cantidad = datos.cantidad.toIntOrNull()
        val cantidadError = when {
            cantidad == null -> "Debe ingresar un número"
            cantidad < 1000 || cantidad > 10000 -> "El valor debe estar entre 1000 y 10000"
            else -> null
        }
        val terminosError = if (!datos.aceptaTerminos) "Debe aceptar y aprobar el registro pedagógico" else null
        val fechaError = if (datos.fecha == null) {
            if (datos.esTarea) "Debe seleccionar una fecha límite de entrega" else "Debe seleccionar una fecha"
        } else null
        val categoriaError = if (datos.categoria.isBlank()) "Debe seleccionar una asignatura" else null

        val nuevosErrores = FormularioErrores(
            nombre = nombreError,
            descripcion = descripcionError,
            cantidad = cantidadError,
            aceptaTerminos = terminosError,
            fecha = fechaError,
            categoria = categoriaError
        )
        _errores.value = nuevosErrores
        return nuevosErrores == FormularioErrores()
    }

    // ---------------- guardado ----------------

    /** Guarda una TAREA. Devuelve false si el formulario tiene errores. */
    fun guardar(): Boolean {
        if (!validarFormulario()) return false
        val datos = _formulario.value
        val titulo = (if (datos.esTarea && !datos.nombre.startsWith("Tarea:", ignoreCase = true)) "Tarea: " else "") + datos.nombre
        crear(
            NuevoEjercicio(
                materiaId = materiaActual(),
                unidadId = unidadFinal(),
                titulo = titulo,
                objetivoAprendizaje = datos.descripcion,
                codigoGuia = datos.cantidad,
                disponible = datos.activo,
                esTarea = datos.esTarea,
                fechaEntrega = datos.fecha
            )
        )
        // reinicia el formulario manteniendo la ultima categoria elegida
        _formulario.value = FormularioModel(categoria = datos.categoria, esTarea = datos.esTarea)
        _errores.value = FormularioErrores()
        return true
    }

    /**
     * Guarda una GUIA didactica con sus preguntas. Cada pregunta se empaqueta como
     * "enunciado;;;op1;;;op2;;;op3" y las preguntas se separan con "|||".
     */
    fun guardarGuia(preguntas: List<PreguntaFormulario>): Boolean {
        if (!validarFormulario()) return false
        val datos = _formulario.value
        val paquete = preguntas.joinToString("|||") { p ->
            val texto = p.enunciado.ifBlank { "¿Cuál es la respuesta correcta?" }
            "$texto;;;${p.opcion1.ifBlank { "Alternativa A " }};;;${p.opcion2.ifBlank { "Alternativa B" }};;;${p.opcion3.ifBlank { "Alternativa C" }}"
        }
        val primera = preguntas.firstOrNull()
        crear(
            NuevoEjercicio(
                materiaId = materiaActual(),
                unidadId = unidadFinal(),
                titulo = datos.nombre,
                objetivoAprendizaje = datos.descripcion,
                codigoGuia = datos.cantidad,
                pregunta = paquete,
                opciones = listOf(
                    primera?.opcion1?.ifBlank { "Opción 1" } ?: "Opción 1",
                    primera?.opcion2?.ifBlank { "Opción 2" } ?: "Opción 2",
                    primera?.opcion3?.ifBlank { "Opción 3" } ?: "Opción 3"
                ),
                respuestaCorrectaIndex = 0,
                disponible = datos.activo,
                esTarea = false,
                fechaEntrega = datos.fecha
            )
        )
        _formulario.update { it.copy(nombre = "", descripcion = "", cantidad = "") }
        return true
    }

    /**
     * Crea una unidad para la asignatura elegida y la deja seleccionada.
     * Devuelve el mensaje de error de validacion, o null si se creo.
     */
    fun crearUnidad(titulo: String, oa: String): String? {
        if (titulo.isBlank() || oa.isBlank()) return "Ingrese título y objetivo de aprendizaje."
        viewModelScope.launch {
            val nueva = contenidosRepository.crearUnidad(materiaActual(), titulo, oa)
            actualizarUnidadId(nueva.id)
        }
        return null
    }

    private fun crear(nuevo: NuevoEjercicio) {
        viewModelScope.launch { contenidosRepository.crearEjercicio(nuevo) }
    }

    private fun materiaActual(): String = Materias.idDesdeCategoria(_formulario.value.categoria)

    private fun unidadFinal(): String {
        val elegida = _formulario.value.unidadId
        if (elegida.isNotBlank()) return elegida
        val materia = materiaActual()
        return unidades.value.firstOrNull { it.materiaId == materia }?.id ?: "uni_${materia}_1"
    }
}
