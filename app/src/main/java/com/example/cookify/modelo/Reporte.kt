package com.example.cookify.modelo

data class Reporte (
    val id: Long?,
    val post: Post?,  // Puede que necesites ajustar la referencia al post según tu modelo
    val usuarioNombre: String?,
    val contenido: String?
)