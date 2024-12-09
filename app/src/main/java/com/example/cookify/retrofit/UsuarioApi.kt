package com.example.cookify.retrofit

import com.example.cookify.modelo.Usuario
import retrofit2.Call
import retrofit2.http.*

interface UsuarioApi {
    @POST("api/usuarios/register")
    fun registerUser(@Header("Authorization") token: String, @Body request: Usuario): Call<Usuario>

    @GET("api/usuarios/profile")
    fun getProfile(@Header("Authorization") token: String): Call<Usuario>

    @PUT("api/usuarios/profile")
    fun updateProfile(@Header("Authorization") token: String, @Body request: Usuario): Call<Usuario>

    @GET("api/usuarios/search/{name}")
    fun searchUsers(@Path("name") name: String): Call<List<Usuario>>

}