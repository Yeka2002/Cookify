package com.example.cookify.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitExternalClient {
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://www.themealdb.com/api/json/v1/1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: RecetaApi = retrofit.create(RecetaApi::class.java)
}
