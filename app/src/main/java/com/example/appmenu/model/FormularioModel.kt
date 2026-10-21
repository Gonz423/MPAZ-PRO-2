package com.example.appmenu.model

data class FormularioModel(
    val nombre: String = "",
    val descripcion: String = "",
    val cantidad: String = "",
    val aceptaTerminos: Boolean = false,
    val activo: Boolean = false,
    val fecha: Long? = null,
    val categoria: String = "",
    val esTarea: Boolean = false,
    val unidadId: String = ""
)
