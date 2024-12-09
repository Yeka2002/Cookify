package com.example.cookify.servicios

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

data class Traductor (
    val q: String,
    val source: String,
    val target: String,
    val format: String = "text"
)

data class TranslateResponse(
    val translatedText: String
)

interface LibreTranslateService {
    @Headers("Content-Type: application/json")
    @POST("translate")
    fun translate(@Body request: Traductor): Call<TranslateResponse>
}