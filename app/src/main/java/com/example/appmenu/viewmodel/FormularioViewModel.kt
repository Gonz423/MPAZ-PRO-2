package com.example.appmenu.viewmodel

import androidx.lifecycle.ViewModel
import com.example.appmenu.model.FormularioModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class FormularioErrores(
    val nombre: String? = null,
    val descripcion: String? = null,
    val cantidad: String? = null,
    val aceptaTerminos: String? = null,
    val fecha: String? = null,
    val categoria: String? = null
)

class FormularioViewModel : ViewModel() {
    private val _formulario = MutableStateFlow(FormularioModel())
    val formulario: StateFlow<FormularioModel> = _formulario.asStateFlow()
    private val _errores = MutableStateFlow(FormularioErrores())
    val errores: StateFlow<FormularioErrores> = _errores.asStateFlow()

    fun actualizarNombre(valor: String) {
        _formulario.value = _formulario.value.copy(nombre = valor)
    }
    fun actualizarDescripcion(valor: String) {
        _formulario.value =_formulario.value.copy(descripcion = valor)
    }
    fun actualizarCantidad(valor: String) {
        if (valor.all { it.isDigit() }) {
            _formulario.value =_formulario.value.copy(cantidad = valor)
        }
    }
    fun actualizarAceptaTerminos(valor: Boolean) {
        _formulario.value = _formulario.value.copy(aceptaTerminos = valor)
    }
    fun actualizarActivo(valor: Boolean) {
        _formulario.value = _formulario.value.copy(activo = valor)
    }
    fun actualizarFecha(valor: Long?) {
        _formulario.value = _formulario.value.copy(fecha = valor)
    }
    fun actualizarCategoria(valor: String) {
        _formulario.value = _formulario.value.copy(valor)
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
            nombreError = "El nombre es obligatorio"
        } else if (datos.nombre.length < 3) {
            nombreError = "El nombre debe tener al menos 3 caracteres"
        }
        if (datos.descripcion.isBlank()) {
            descripcionError = "La descripción es obligatoria"
        } else if (datos.descripcion.length < 5) {
            descripcionError = "La descripción debe tener al menos 5 caracteres"
        }
        val cantidad = datos.cantidad.toIntOrNull()
        if (cantidad == null) {
            cantidadError = "Debe ingresar un número"
        } else if (cantidad < 1000 || cantidad > 10000) {
            cantidadError = "El valor debe estar entre 1000 y 10000"
        }
        if (!datos.aceptaTerminos) {
            terminosError ="Debe aceptar los términos y condiciones"
        }
        if (datos.fecha == null) {
            fechaError ="Debe seleccionar una fecha"
        }
        if (datos.categoria.isBlank()) {
            categoriaError ="Debe seleccionar una categoría"
        }

        val nuevosErrores =
            FormularioErrores(
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
        println("Formulario guardado: ${_formulario.value}")
        return true
    }
}
