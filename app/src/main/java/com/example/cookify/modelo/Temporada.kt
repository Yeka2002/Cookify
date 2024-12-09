package com.example.cookify.modelo

import java.io.Serializable

data class Temporada(
    val idCategory: String,
    val strCategory: String,
    val strCategoryThumb: String,
    val strCategoryDescription: String
) : Serializable