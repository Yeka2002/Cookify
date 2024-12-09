package com.example.cookify.modelo

data class Post(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val usuarioUid: String,
    val usuarioNombre: String,
    val imagenes: List<ImagenPost>,
    val fechaDeCreacion: String,
    val categoria: String,
    val receta: String,
    val comentarios: List<Comentario>,
    val likes: List<PostLike>,
    val reportes: List<Reporte>
)