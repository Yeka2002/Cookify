package com.example.cookify.servicios

import android.annotation.SuppressLint
import android.content.Context
import com.example.cookify.modelo.Comentario
import com.example.cookify.modelo.ImagenPost
import com.example.cookify.modelo.Post
import com.example.cookify.modelo.PostLike
import com.example.cookify.modelo.PostResponse
import com.example.cookify.modelo.Reporte
import com.example.cookify.modelo.Usuario
import com.example.cookify.retrofit.RetrofitClient
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Date

class PostService {

    companion object {

        private var auth: FirebaseAuth = FirebaseAuth.getInstance()

        fun getUserUid() : String {
            return auth.uid.toString();
        }

        fun getToken(onTokenReceived: (String?) -> Unit, context : Context) {
            val user = auth.currentUser
            user?.getIdToken(true)?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result?.token

                    //Guardo el token en mis preferencias para mantener la sesión.
                    context.let {
                        if (token != null) {
                            TokenManagerService(it).saveUserToken(token)
                        }
                    }

                    onTokenReceived(token)
                } else {
                    // Manejar el error al obtener el token
                    onTokenReceived(null)
                }
            }
        }

        fun crearPost(token: String, post: Post, onSuccess: (Post?) -> Unit, onFailure: (Throwable) -> Unit) {
            RetrofitClient.postApi.createPost("Bearer $token", post).enqueue(object : Callback<Post> {
                override fun onResponse(call: Call<Post>, response: Response<Post>) {
                    if (response.isSuccessful) {
                        val createdPost = response.body()
                        onSuccess(createdPost)
                    } else {
                        onFailure(Exception("Error en la respuesta del servidor"))
                    }
                }

                override fun onFailure(call: Call<Post>, t: Throwable) {
                    onFailure(Exception("Error en la respuesta del servidor"))
                }
            })
        }

        @SuppressLint("UseSwitchCompatOrMaterialCode")
        fun postCreado(nombre : String,  descripcion : String, usuarioUid : String, usuarioNombre: String, imagenes : List<ImagenPost>, fecha : String, categoria : String, receta : String) : Post{
            return Post(0,nombre,descripcion,usuarioUid,usuarioNombre,imagenes, fecha, categoria, receta, listOf(), listOf(), listOf())
        }

        fun getAllPosts(page: Int, size: Int, onSuccess: (List<Post>?) -> Unit, onFailure: (Throwable) -> Unit) {
            RetrofitClient.postApi.getAllPosts(page, size).enqueue(object : Callback<PostResponse> {
                override fun onResponse(call: Call<PostResponse>, response: Response<PostResponse>) {
                    if (response.isSuccessful) {
                        val posts = response.body()
                        onSuccess(posts?.content)
                    } else {
                        onFailure(Exception("Error fetching all posts"))
                    }
                }

                override fun onFailure(call: Call<PostResponse>, t: Throwable) {
                    onFailure(t)
                }
            })
        }

        fun getUserPosts(token: String, onSuccess: (List<Post>?) -> Unit, onFailure: (Throwable) -> Unit) {
            RetrofitClient.postApi.getUserPosts("Bearer $token").enqueue(object : Callback<List<Post>> {
                override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                    if (response.isSuccessful) {
                        val userPosts = response.body()
                        onSuccess(userPosts)
                    } else {
                        onFailure(Exception("Error fetching user posts"))
                    }
                }

                override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                    onFailure(t)
                }
            })
        }

        fun getUserPosts(token: String, nombre : String, onSuccess: (List<Post>?) -> Unit, onFailure: (Throwable) -> Unit) {
            RetrofitClient.postApi.getUserPosts("Bearer $token",nombre).enqueue(object : Callback<List<Post>> {
                override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                    if (response.isSuccessful) {
                        val userPosts = response.body()
                        onSuccess(userPosts)
                    } else {
                        onFailure(Exception("Error fetching user posts"))
                    }
                }

                override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                    onFailure(t)
                }
            })
        }

        fun deleteUserPost(token: String, postId: Long, onSuccess: () -> Unit, onFailure: (Throwable) -> Unit) {
            RetrofitClient.postApi.deletePost(postId,"Bearer $token").enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        onSuccess()
                    } else {
                        onFailure(Exception("Error deleting post"))
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    onFailure(t)
                }
            })
        }


        // Nuevo método para obtener un post por su ID
        fun getPostById(token: String, postId: Long, onSuccess: (Post?) -> Unit, onFailure: (Throwable) -> Unit) {
            RetrofitClient.postApi.getPostById("Bearer $token", postId).enqueue(object : Callback<Post> {
                override fun onResponse(call: Call<Post>, response: Response<Post>) {
                    if (response.isSuccessful) {
                        val post = response.body()
                        onSuccess(post)
                    } else {
                        onFailure(Exception("Error fetching post by ID"))
                    }
                }

                override fun onFailure(call: Call<Post>, t: Throwable) {
                    onFailure(t)
                }
            })
        }

        fun addComentarioToPost(
            postId: Long,
            token: String,
            comentario: Comentario,
            onSuccess: () -> Unit,
            onFailure: (Throwable) -> Unit
        ) {
            RetrofitClient.postApi.addComentarioToPost(postId, "Bearer $token", comentario)
                .enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            onSuccess()
                        } else {
                            onFailure(Exception("Error al agregar comentario"))
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        onFailure(t)
                    }
                })
        }

        // Método para obtener comentarios de un post por su ID
        fun getComentariosByPostId(postId: Long, onSuccess: (List<Comentario>?) -> Unit, onFailure: (Throwable) -> Unit) {
            RetrofitClient.postApi.getComentariosByPostId(postId)
                .enqueue(object : Callback<List<Comentario>> {
                    override fun onResponse(call: Call<List<Comentario>>, response: Response<List<Comentario>>) {
                        if (response.isSuccessful) {
                            val comentarios = response.body()
                            onSuccess(comentarios)
                        } else {
                            onFailure(Exception("Error al obtener comentarios"))
                        }
                    }

                    override fun onFailure(call: Call<List<Comentario>>, t: Throwable) {
                        onFailure(t)
                    }
                })
        }

        fun enviarDenuncia(token: String, postId: Long, denuncia: Reporte, onSuccess: () -> Unit, onFailure: (String) -> Unit) {

            val call = RetrofitClient.postApi.addDenunciaToPost("Bearer $token",postId,denuncia)

            call.enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        onSuccess()
                    } else {
                        onFailure("Error al enviar la denuncia")
                    }
                }
                override fun onFailure(call: Call<Void>, t: Throwable) {
                    onFailure("Error de red: ${t.message}")
                }
            })
        }

    }

}