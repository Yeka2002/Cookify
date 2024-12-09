package com.example.cookify.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cookify.R
import com.example.cookify.modelo.Receta
import com.example.cookify.ui.inicio.DetalleRecetaActivity

class RecetaApiAdapter(private val listaRecetas: List<Receta>) : RecyclerView.Adapter<RecetaApiAdapter.RecetaViewHolder>() {

    // ViewHolder que contiene las vistas de cada elemento
    class RecetaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivRecetaApi: ImageView = itemView.findViewById(R.id.ivImagenUsuario)
        val tvRecetaApi: TextView = itemView.findViewById(R.id.ivNombreUsuario)
        val ivVerRecetaApi: ImageView = itemView.findViewById(R.id.ivPrivacidadUsuario)
    }

    // Crear nuevas vistas (invocado por el layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecetaViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.receta_api_item, parent, false)
        return RecetaViewHolder(itemView)
    }

    // Reemplazar el contenido de una vista (invocado por el layout manager)
    override fun onBindViewHolder(holder: RecetaViewHolder, position: Int) {
        val receta = listaRecetas[position]
        holder.tvRecetaApi.text = receta.strMeal

        // Utiliza Glide para cargar la imagen
        Glide.with(holder.itemView.context)
            .load(receta.strMealThumb)
            .into(holder.ivRecetaApi)

        // Configurar el listener para el botón
        holder.ivVerRecetaApi.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DetalleRecetaActivity::class.java).apply {
                putExtra("idMeal", receta.idMeal)
            }
            context.startActivity(intent)
        }
    }

    // Devuelve el tamaño de tu conjunto de datos (invocado por el layout manager)
    override fun getItemCount() = listaRecetas.size
}