package com.example.cookify.retrofit

import com.example.cookify.modelo.Comentario
import com.example.cookify.modelo.Post
import com.example.cookify.modelo.PostLike
import com.example.cookify.modelo.PostResponse
import com.example.cookify.modelo.Reporte
import retrofit2.Call
import retrofit2.http.*

interface PostApi {
    @POST("api/posts")
    fun createPost(@Header("Authorization") token: String, @Body post: Post): Call<Post>

    @GET("api/posts/user")
    fun getUserPosts(@Header("Authorization") token: String): Call<List<Post>>

    @GET("api/posts")
    fun getAllPosts(@Query("page") page: Int, @Query("size") size: Int): Call<PostResponse>

    @DELETE("/api/posts/{postId}")
    fun deletePost(@Path("postId") postId: Long, @Header("Authorization") token: String): Call<Void>

    @GET("/api/posts/user/{nombre}")
    fun getUserPosts(@Header("Authorization") token: String, @Path("nombre") nombre: String): Call<List<Post>>

    @GET("/api/posts/{postId}")
    fun getPostById(@Header("Authorization") token: String, @Path("postId") postId: Long): Call<Post>

    @POST("/api/posts/{postId}/comentarios")
    fun addComentarioToPost(@Path("postId") postId: Long, @Header("Authorization") token: String, @Body comentario: Comentario): Call<Void>

    // Endpoint para obtener comentarios de un post
    @GET("/api/posts/{postId}/comentarios")
    fun getComentariosByPostId(@Path("postId") postId: Long): Call<List<Comentario>>

    @POST("/api/posts/{postId}/denuncias")
    fun addDenunciaToPost(@Header("Authorization") token: String, @Path("postId") postId: Long, @Body denuncia: Reporte): Call<Void>

}