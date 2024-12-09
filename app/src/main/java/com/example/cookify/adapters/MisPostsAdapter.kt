package com.example.cookify.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cookify.R
import com.example.cookify.modelo.Post
import com.example.cookify.servicios.PostService
import com.example.cookify.ui.buscar.PerfilBuscadoActivity
import com.example.cookify.ui.perfil.VerPostActivity

class MisPostsAdapter(private val postList: List<Post>, private val context: Context) : RecyclerView.Adapter<MisPostsAdapter.PostViewHolder>() {

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageViewPost: ImageView = itemView.findViewById(R.id.imageViewPost)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.mi_post_item, parent, false)
        return PostViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = postList[position]
        if (post.imagenes.isNotEmpty()) {
            // Decodificar la imagen base64 y cargarla usando Glide
            val imageByteArray = Base64.decode(post.imagenes[0].imagenBase64, Base64.DEFAULT)
            Glide.with(holder.itemView.context)
                .load(imageByteArray)
                .into(holder.imageViewPost)
        }

        holder.imageViewPost.setOnLongClickListener {
            showDeleteConfirmationDialog(post.id.toLong())
            true
        }

        holder.imageViewPost.setOnClickListener {
            val context = holder.imageViewPost.context
            val intent = Intent(context, VerPostActivity::class.java).apply {
                putExtra("post", post.id)
            }
            context.startActivity(intent)
        }

    }

    override fun getItemCount() = postList.size

    private fun showDeleteConfirmationDialog(postId: Long) {
        AlertDialog.Builder(context)
            .setTitle("Confirmar Eliminación")
            .setMessage("¿Estás seguro de que deseas eliminar este post?")
            .setPositiveButton("Sí") { dialog, _ ->
                deletePost(postId)
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun deletePost(postId: Long) {
        PostService.getToken({ token ->
            token?.let {
                PostService.deleteUserPost(it, postId, {
                    // Actualiza la lista de posts y notifica al adaptador
                    (postList as MutableList).removeAll { post -> post.id.toLong() == postId }
                    notifyDataSetChanged()
                }, {})
            }
        }, context)
    }
}
