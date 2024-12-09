package com.example.cookify.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cookify.R
import com.example.cookify.modelo.Comentario

class ComentariosAdapter(private var comentarios: List<Comentario>) : RecyclerView.Adapter<ComentariosAdapter.ComentarioViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComentarioViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.comentario_item, parent, false)
        return ComentarioViewHolder(view)
    }

    override fun onBindViewHolder(holder: ComentarioViewHolder, position: Int) {
        val comentario = comentarios[position]
        holder.bind(comentario)
    }

    override fun getItemCount(): Int {
        return comentarios.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun actualizarComentarios(nuevosComentarios: List<Comentario>) {
        comentarios = nuevosComentarios
        notifyDataSetChanged()
    }

    class ComentarioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvUsuario: TextView = itemView.findViewById(R.id.tvUsuario)
        private val tvContenido: TextView = itemView.findViewById(R.id.tvContenido)

        fun bind(comentario: Comentario) {
            tvUsuario.text = comentario.usuarioNombre
            tvContenido.text = comentario.contenido
        }
    }
}
