package com.example.cookify.retrofit

import com.example.cookify.modelo.ListaCategorias
import com.example.cookify.modelo.ListaRecetaCompleta
import com.example.cookify.modelo.ListaRecetas
import retrofit2.Call
import retrofit2.http.*

interface RecetaApi {

    @GET("categories.php")
    fun getTemporadas(): Call<ListaCategorias>

    @GET("filter.php")
    fun getRecetasPorCategoria(@Query("c") categoria: String): Call<ListaRecetas>

    @GET("lookup.php")
    fun getRecetaPorId(@Query("i") id: String): Call<ListaRecetaCompleta>
}
