package com.example.cookify.adapters

import UserService
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.cookify.R
import com.example.cookify.componentes.ComponenteImagenRedondeada
import com.example.cookify.componentes.ComponentesComentarios
import com.example.cookify.controladores.ControladorCarga
import com.example.cookify.modelo.Post
import com.example.cookify.modelo.Reporte
import com.example.cookify.servicios.PostService
import com.example.cookify.servicios.ServicioBase64
import com.example.cookify.ui.buscar.PerfilBuscadoActivity
import com.google.firebase.auth.FirebaseAuth

class PostAdapter (private val posts: MutableList<Post>) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.post_item, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.bind(post)
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    class PostViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val postImageView: ComponenteImagenRedondeada = view.findViewById(R.id.componenteImagenRedondeadaPost)
        private val postTitleTextView: TextView = view.findViewById(R.id.textView5)
        private val postDescriptionTextView: TextView = view.findViewById(R.id.textView6)
        private val postUserTextView: TextView = view.findViewById(R.id.textView2)
        private val postCategoryTextView: TextView = view.findViewById(R.id.textView3)
        private val postSubCategoryTextView: TextView = view.findViewById(R.id.textView4)
        private val postViewPager: ViewPager2 = view.findViewById(R.id.postCarrusel)

        private val ivComentar: ImageView = view.findViewById(R.id.ivComentar)
        private val ivDenunciar: ImageView = view.findViewById(R.id.ivDenunciar)

        fun bind(post: Post) {

            UserService.searchUsers(post.usuarioNombre, { usuarios ->
                // Configura el RecyclerView
                val btmp = usuarios[0]?.let { ServicioBase64.base64ToBitmap(it.photoUrl) }
                postImageView.imagenEditable.setImageBitmap(btmp)

                postImageView.setOnClickListener{
                    val context = itemView.context
                    val intent = Intent(context, PerfilBuscadoActivity::class.java).apply {
                        putExtra("usuario", usuarios[0]?.name)
                    }
                    context.startActivity(intent)
                }

            }, {})


            postUserTextView.text = post.usuarioNombre
            postCategoryTextView.text = post.categoria
            postSubCategoryTextView.text = post.receta
            postTitleTextView.text = post.nombre
            postDescriptionTextView.text = post.descripcion

            //Eventos
            ivComentar.setOnClickListener {
                val activity = it.context as FragmentActivity
                val comentariosBottomSheet = ComponentesComentarios.newInstance((post.id).toLong())
                comentariosBottomSheet.show(activity.supportFragmentManager, "ComponentesComentarios")
            }
            ivDenunciar.setOnClickListener {
                val builder = AlertDialog.Builder(it.context)
                builder.setTitle("Denuncia")
                builder.setMessage("¿Consideras que este post no es comida?")
                builder.setPositiveButton("Confirmar") { dialog, _ ->
                    denunciarPost(post)
                    dialog.dismiss()
                }
                builder.setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.dismiss()
                }
                builder.show()
            }

            // Lista para almacenar los bitmaps de las imágenes base64
            val bitmaps = ArrayList<Bitmap>()

            // Convertir cada imagen base64 a bitmap y agregarla a la lista
            for (imagenBase64 in post.imagenes) {
                val bitmap = ServicioBase64.base64ToBitmap(imagenBase64.imagenBase64)
                if (bitmap != null) {
                    bitmaps.add(bitmap)
                }
            }

            // Configurar el adaptador del ViewPager con la lista de bitmaps
            postViewPager.adapter = CarruselMostrarPostAdapter(bitmaps)
        }

        private fun denunciarPost(post:Post){
            val denuncia = Reporte(
                id = 0,
                post = post,
                usuarioNombre = post.usuarioNombre,
                contenido = "No es comida."
            )
            this.let {
                PostService.getToken({ token ->
                    // Aquí puedes manejar el token recibido
                    if (token != null) {
                        denuncia.post?.id?.toLong()?.let { PostService.enviarDenuncia(token,it,denuncia,{},{}) }
                    } else {
                        Toast.makeText(this.postViewPager.context, "Algo ha ido mal...", Toast.LENGTH_LONG).show()
                    }
                }, this.postViewPager.context)
            }
        }
    }


    fun addPosts(newPosts: List<Post>) {
        val startPosition = posts.size
        posts.addAll(newPosts)
        notifyItemRangeInserted(startPosition, newPosts.size)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearPosts() {
        posts.clear()
        notifyDataSetChanged()
    }

}