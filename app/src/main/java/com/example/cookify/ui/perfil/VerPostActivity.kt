package com.example.cookify.ui.perfil

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.cookify.R
import com.example.cookify.adapters.CarruselMostrarPostAdapter
import com.example.cookify.componentes.ComponenteImagenRedondeada
import com.example.cookify.componentes.ComponentesComentarios
import com.example.cookify.controladores.ControladorCarga
import com.example.cookify.modelo.Post
import com.example.cookify.modelo.Reporte
import com.example.cookify.servicios.PostService
import com.example.cookify.servicios.ServicioBase64
import com.example.cookify.ui.buscar.PerfilBuscadoActivity

class VerPostActivity : AppCompatActivity() {

    private lateinit var layout: ViewGroup
    private lateinit var postImageView: ComponenteImagenRedondeada
    private lateinit var postTitleTextView: TextView
    private lateinit var postDescriptionTextView: TextView
    private lateinit var postUserTextView: TextView
    private lateinit var postCategoryTextView: TextView
    private lateinit var postSubCategoryTextView: TextView
    private lateinit var postViewPager: ViewPager2
    private lateinit var ivCerrarPost: ImageView
    private lateinit var ivComnentar: ImageView
    private lateinit var ivDenunciar: ImageView
    private lateinit var pbCarga: ProgressBar
    private lateinit var postDenunciar: Post


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        setContentView(R.layout.activity_ver_post)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        layout = findViewById(R.id.main)
        postImageView = findViewById(R.id.componenteImagenRedondeadaPost)
        postTitleTextView = findViewById(R.id.textView5)
        postDescriptionTextView = findViewById(R.id.textView6)
        postUserTextView = findViewById(R.id.textView2)
        postCategoryTextView = findViewById(R.id.textView3)
        postSubCategoryTextView = findViewById(R.id.textView4)
        postViewPager = findViewById(R.id.postCarrusel)
        ivCerrarPost = findViewById(R.id.ivCerrarPost)
        ivComnentar = findViewById(R.id.ivComentar)
        ivDenunciar = findViewById(R.id.ivDenunciar)
        pbCarga = findViewById(R.id.progressBar)


        ivCerrarPost.setOnClickListener {
            finish()
        }

        val id = intent.getIntExtra("post", 0)
        val parseado = id.toLong()

        //Eventos
        ivComnentar.setOnClickListener {
            val activity = it.context as FragmentActivity
            val comentariosBottomSheet = ComponentesComentarios.newInstance(parseado)
            comentariosBottomSheet.show(activity.supportFragmentManager, "ComponentesComentarios")
        }

        ivDenunciar.setOnClickListener {
            val builder = AlertDialog.Builder(it.context)
            builder.setTitle("Denuncia")
            builder.setMessage("¿Consideras que este post no es comida?")
            builder.setPositiveButton("Confirmar") { dialog, _ ->
                denunciarPost(postDenunciar)
                dialog.dismiss()
            }
            builder.setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            builder.show()
        }

        ControladorCarga.showLoading(true, layout, pbCarga)
        getPost(parseado)
    }

    private fun denunciarPost(post: Post){
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

    private fun getPost(postId: Long) {
        PostService.getToken({ token ->
            token?.let {

                PostService.getPostById(it, postId, {post->

                    postUserTextView.text = post?.usuarioNombre
                    postCategoryTextView.text = post?.categoria
                    postSubCategoryTextView.text = post?.receta
                    postTitleTextView.text = post?.nombre
                    postDescriptionTextView.text = post?.descripcion

                    postDenunciar = post!!

                    // Lista para almacenar los bitmaps de las imágenes base64
                    val bitmaps = ArrayList<Bitmap>()

                    // Convertir cada imagen base64 a bitmap y agregarla a la lista
                    if (post != null) {
                        for (imagenBase64 in post.imagenes) {
                            val bitmap = ServicioBase64.base64ToBitmap(imagenBase64.imagenBase64)
                            if (bitmap != null) {
                                bitmaps.add(bitmap)
                            }
                        }
                    }

                    // Configurar el adaptador del ViewPager con la lista de bitmaps
                    postViewPager.adapter = CarruselMostrarPostAdapter(bitmaps)

                    if (post != null) {
                        UserService.searchUsers(post.usuarioNombre, { usuarios ->
                            // Configura el RecyclerView
                            val btmp = usuarios[0]?.let { ServicioBase64.base64ToBitmap(it.photoUrl) }
                            postImageView.imagenEditable.setImageBitmap(btmp)
                            ControladorCarga.showLoading(false, layout, pbCarga)

                        }, {
                            // Maneja el error
                        })
                    }

                }, {})

            }
        }, baseContext)
    }

}