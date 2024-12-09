package com.example.cookify.servicios

import com.example.cookify.modelo.ListaCategorias
import com.example.cookify.modelo.ListaRecetaCompleta
import com.example.cookify.modelo.ListaRecetas
import com.example.cookify.retrofit.RetrofitExternalClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ServicioTemporada {

    companion object {

        fun getTemporada(onSuccess: (ListaCategorias) -> Unit) {

            val call = RetrofitExternalClient.api.getTemporadas()

            call.enqueue(object : Callback<ListaCategorias> {
                override fun onResponse(call: Call<ListaCategorias>, response: Response<ListaCategorias>) {
                    if (response.isSuccessful) {
                        val temps = response.body()
                        if (temps != null) {
                            onSuccess(temps)
                        }
                    }
                }

                override fun onFailure(call: Call<ListaCategorias>, t: Throwable) {
                    t.printStackTrace()
                }
            })

        }

        fun getRecetasPorCategoria(categoria: String, onSuccess: (ListaRecetas) -> Unit) {

            val call = RetrofitExternalClient.api.getRecetasPorCategoria(categoria)

            call.enqueue(object : Callback<ListaRecetas> {
                override fun onResponse(call: Call<ListaRecetas>, response: Response<ListaRecetas>) {
                    if (response.isSuccessful) {
                        val listaRecetas = response.body()
                        if (listaRecetas != null) {
                            onSuccess(listaRecetas)
                        }
                    }
                }

                override fun onFailure(call: Call<ListaRecetas>, t: Throwable) {
                    t.printStackTrace()
                }
            })
        }

        fun getRecetaPorId(id : String, onSuccess: (ListaRecetaCompleta) -> Unit){

            val call = RetrofitExternalClient.api.getRecetaPorId(id)

            call.enqueue(object : Callback<ListaRecetaCompleta> {
                override fun onResponse(call: Call<ListaRecetaCompleta>, response: Response<ListaRecetaCompleta>) {
                    if (response.isSuccessful) {
                        val listaRecetas = response.body()
                        if (listaRecetas != null) {
                            onSuccess(listaRecetas)
                        }
                    } else {
                        // Handle the error
                    }
                }

                override fun onFailure(call: Call<ListaRecetaCompleta>, t: Throwable) {
                    t.printStackTrace()
                }
            })
        }

    }

}