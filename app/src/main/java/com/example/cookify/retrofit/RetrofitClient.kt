package com.example.cookify.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "https://coockify-docker-5-0-1.onrender.com/"
    //private const val BASE_URL = "http://192.168.0.82:8080/"

    private val logger = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logger)
        .connectTimeout(30, TimeUnit.SECONDS) // Tiempo de espera para la conexión
        .readTimeout(30, TimeUnit.SECONDS)    // Tiempo de espera para la lectura
        .writeTimeout(30, TimeUnit.SECONDS)   // Tiempo de espera para la escritura
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    val usuarioApi: UsuarioApi by lazy {
        retrofit.create(UsuarioApi::class.java)
    }

    val postApi: PostApi by lazy {
        retrofit.create(PostApi::class.java)
    }
}
