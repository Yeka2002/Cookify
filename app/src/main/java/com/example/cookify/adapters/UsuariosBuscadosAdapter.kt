package com.example.cookify.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cookify.R
import com.example.cookify.componentes.ComponenteImagenRedondeada
import com.example.cookify.modelo.Usuario
import com.example.cookify.servicios.ServicioBase64
import com.example.cookify.ui.buscar.PerfilBuscadoActivity

class UsuariosBuscadosAdapter (private val listaUsuarios: List<Usuario?>) : RecyclerView.Adapter<UsuariosBuscadosAdapter.RecetaViewHolder>() {

    // ViewHolder que contiene las vistas de cada elemento
    class RecetaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivImagenUsuario: ComponenteImagenRedondeada = itemView.findViewById(R.id.ivImagenUsuario)
        val tvNombreUsuario: TextView = itemView.findViewById(R.id.ivNombreUsuario)
        val ivNombreUsuario: ImageView = itemView.findViewById(R.id.ivPrivacidadUsuario)
    }

    // Crear nuevas vistas (invocado por el layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecetaViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.usuario_buscar_item, parent, false)
        return RecetaViewHolder(itemView)
    }

    // Reemplazar el contenido de una vista (invocado por el layout manager)
    override fun onBindViewHolder(holder: RecetaViewHolder, position: Int) {
        val usuario = listaUsuarios[position]
        holder.tvNombreUsuario.text = usuario?.name

        val btmp = usuario?.let { ServicioBase64.base64ToBitmap(it.photoUrl) }
        holder.ivImagenUsuario.imagenEditable.setImageBitmap(btmp)

        // Configurar el listener para el botón
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, PerfilBuscadoActivity::class.java).apply {
                putExtra("usuario", usuario?.name)
            }
            context.startActivity(intent)
        }
    }

    // Devuelve el tamaño de tu conjunto de datos (invocado por el layout manager)
    override fun getItemCount() = listaUsuarios.size
}