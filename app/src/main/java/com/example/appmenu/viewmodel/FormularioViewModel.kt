package com.example.appmenu.viewmodel

import androidx.lifecycle.ViewModel
import com.example.appmenu.model.FormularioModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.appmenu.model.ActividadesManager

data class FormularioErrores(
    val nombre: String? = null,
    val descripcion: String? = null,
    val cantidad: String? = null,
    val aceptaTerminos: String? = null,
    val fecha: String? = null,
    val categoria: String? = null,
    val unidad: String? = null
)

class FormularioViewModel : ViewModel() {
    private val _formulario = MutableStateFlow(FormularioModel())
    val formulario: StateFlow<FormularioModel> = _formulario.asStateFlow()
    private val _errores = MutableStateFlow(FormularioErrores())
    val errores: StateFlow<FormularioErrores> = _errores.asStateFlow()

    fun actualizarNombre(valor: String) {
        _formulario.value = _formulario.value.copy(nombre = valor)
        if (_errores.value.nombre != null && valor.isNotBlank()) {
            _errores.value = _errores.value.copy(nombre = null)
        }
    }

    fun actualizarDescripcion(valor: String) {
        _formulario.value = _formulario.value.copy(descripcion = valor)
        if (_errores.value.descripcion != null && valor.isNotBlank()) {
            _errores.value = _errores.value.copy(descripcion = null)
        }
    }

    fun actualizarCantidad(valor: String) {
        if (valor.all { it.isDigit() }) {
            _formulario.value = _formulario.value.copy(cantidad = valor)
            if (_errores.value.cantidad != null && valor.isNotBlank()) {
                _errores.value = _errores.value.copy(cantidad = null)
            }
        }
    }

    fun actualizarAceptaTerminos(valor: Boolean) {
        _formulario.value = _formulario.value.copy(aceptaTerminos = valor)
        if (valor) {
            _errores.value = _errores.value.copy(aceptaTerminos = null)
        }
    }

    fun actualizarActivo(valor: Boolean) {
        _formulario.value = _formulario.value.copy(activo = valor)
    }

    fun actualizarFecha(valor: Long?) {
        _formulario.value = _formulario.value.copy(fecha = valor)
        if (valor != null) {
            _errores.value = _errores.value.copy(fecha = null)
        }
    }

    fun actualizarCategoria(valor: String) {
        _formulario.value = _formulario.value.copy(categoria = valor)
        _errores.value = _errores.value.copy(categoria = null)
    }

    fun actualizarEsTarea(valor: Boolean) {
        _formulario.value = _formulario.value.copy(esTarea = valor)
    }

    fun actualizarUnidadId(valor: String) {
        _formulario.value = _formulario.value.copy(unidadId = valor)
        _errores.value = _errores.value.copy(unidad = null)
    }

    fun validarFormulario(): Boolean {
        val datos = _formulario.value
        var nombreError: String? = null
        var descripcionError: String? = null
        var cantidadError: String? = null
        var terminosError: String? = null
        var fechaError: String? = null
        var categoriaError: String? = null

        if (datos.nombre.isBlank()) {
            nombreError = if (datos.esTarea) "El título de la tarea es obligatorio" else "El nombre de la actividad es obligatorio"
        } else if (datos.nombre.length < 3) {
            nombreError = "El título debe tener al menos 3 caracteres"
        }

        if (datos.descripcion.isBlank()) {
            descripcionError = if (datos.esTarea) "Las instrucciones de la tarea son obligatorias" else "La descripción / OA es obligatoria"
        } else if (datos.descripcion.length < 5) {
            descripcionError = "Debe tener al menos 5 caracteres"
        }

        val cantidad = datos.cantidad.toIntOrNull()
        if (cantidad == null) {
            cantidadError = "Debe ingresar un número"
        } else if (cantidad < 1000 || cantidad > 10000) {
            cantidadError = "El valor debe estar entre 1000 y 10000"
        }

        if (!datos.aceptaTerminos) {
            terminosError = "Debe aceptar y aprobar el registro pedagógico"
        }

        if (datos.fecha == null) {
            fechaError = if (datos.esTarea) "Debe seleccionar una fecha límite de entrega" else "Debe seleccionar una fecha"
        }

        if (datos.categoria.isBlank()) {
            categoriaError = "Debe seleccionar una asignatura"
        }

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

    fun guardar(): Boolean {
        if (!validarFormulario()) {
            return false
        }
        val datos = _formulario.value
        val materiaId = ActividadesManager.mapearCategoriaAId(datos.categoria)
        
        val unidadIdFinal = if (datos.unidadId.isNotBlank()) {
            datos.unidadId
        } else {
            val unidadExistente = ActividadesManager.obtenerUnidadesPorMateria(materiaId).firstOrNull()
            unidadExistente?.id ?: "uni_${materiaId}_1"
        }

        ActividadesManager.agregarEjercicio(
            materiaId = materiaId,
            unidadId = unidadIdFinal,
            titulo = (if (datos.esTarea && !datos.nombre.startsWith("Tarea:", ignoreCase = true)) "Tarea: " else "") + datos.nombre,
            oa = datos.descripcion,
            codigoGuia = datos.cantidad,
            disponible = datos.activo,
            esTarea = datos.esTarea,
            fechaEntrega = datos.fecha
        )

        // reiniciar formulario manteniendo la ultima categoria elegida
        _formulario.value = FormularioModel(categoria = datos.categoria, esTarea = datos.esTarea)
        _errores.value = FormularioErrores()
        return true
    }
}
