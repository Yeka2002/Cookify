package com.example.cookify.servicios

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AlertDialog
import com.example.cookify.modelo.ImagenDefectoCodificada
import com.example.cookify.modelo.Usuario
import com.example.cookify.retrofit.RetrofitClient
import com.example.cookify.ui.LoginActivity
import com.google.firebase.auth.FirebaseUser
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ServicioRegistro {

    companion object {

        /**
         * Método que registra a un nuevo usuario en el backend de Cookify.
         */
        @JvmStatic
        fun enviarDatosAlBackend(token: String, usuario: Usuario, destino: Context, currentUser: FirebaseUser) {
            val retrofitService = RetrofitClient.usuarioApi

            usuario.photoUrl = ImagenDefectoCodificada.getImagenDefecto()

            val call = retrofitService.registerUser("Bearer $token", usuario)

            call.enqueue(object : Callback<Usuario> {
                override fun onResponse(call: Call<Usuario>, response: Response<Usuario>) {
                    if (response.isSuccessful) {
                        // Registro en el backend exitoso
                        val intent = Intent(destino, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK // Añadir esta línea
                        destino.startActivity(intent)
                    } else {
                        // Registro en el backend fallido
                        currentUser.delete()
                    }
                }

                override fun onFailure(call: Call<Usuario>, t: Throwable) {
                    currentUser.delete()
                }
            })
        }

    }
}
