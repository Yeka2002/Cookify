package com.example.cookify.ui.buscar
import UserService
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cookify.R
import com.example.cookify.adapters.MisPostsAdapter
import com.example.cookify.componentes.ComponenteImagenRedondeada
import com.example.cookify.controladores.ControladorCarga
import com.example.cookify.servicios.PostService
import com.example.cookify.servicios.ServicioBase64
import kotlin.properties.Delegates

class PerfilBuscadoActivity : AppCompatActivity() {

    private lateinit var layout : ViewGroup
    private lateinit var tvNombre : TextView
    private lateinit var cpImagen : ComponenteImagenRedondeada
    private lateinit var recyclerViewPosts : RecyclerView
    private lateinit var tvNoRecetas : TextView
    private lateinit var tvNPublicaciones : TextView
    private lateinit var ivNoRecetas : ImageView
    private lateinit var ivCerrarPerfil : ImageView
    private lateinit var pbCarga : ProgressBar

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        setContentView(R.layout.activity_perfil_buscado)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        layout = findViewById(R.id.main)
        tvNombre = findViewById(R.id.textNombrePerfil)
        cpImagen = findViewById(R.id.componenteImagenRedondeada)
        recyclerViewPosts = findViewById(R.id.recyclerViewPosts)
        tvNoRecetas = findViewById(R.id.tvNoRecetas)
        ivNoRecetas = findViewById(R.id.ivNoRecetas)
        tvNPublicaciones = findViewById(R.id.textNumPublicaciones)
        ivCerrarPerfil = findViewById(R.id.ivCerrarPerfil)
        pbCarga = findViewById(R.id.progressBar)
        recyclerViewPosts.layoutManager = GridLayoutManager(baseContext, 3)

        ivCerrarPerfil.setOnClickListener {
            finish()
        }

        ControladorCarga.showLoading(true,layout,pbCarga)
        val value = intent.getStringExtra("usuario")
        cargarUsuario(value.toString())

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun cargarUsuario(query: String) {
        UserService.searchUsers(query, { usuarios ->
            // Configura el RecyclerView
            tvNombre.text = usuarios[0]?.name
            val btmp = usuarios[0]?.let { ServicioBase64.base64ToBitmap(it.photoUrl) }
            cpImagen.imagenEditable.setImageBitmap(btmp)
            cargarPostsUsuario(query)
        }, {
            // Maneja el error
        })
    }

    private fun cargarPostsUsuario(nombre:String){
        baseContext?.let {
            PostService.getToken({ token ->
                // Aquí puedes manejar el token recibido
                if (token != null) {
                    PostService.getUserPosts(token,nombre,{posts->
                        ControladorCarga.showLoading(false,layout,pbCarga)
                        tvNPublicaciones.text = posts?.size.toString()
                        recyclerViewPosts.adapter = posts?.let { it1 -> MisPostsAdapter(it1, baseContext) }
                        ivNoRecetas.visibility = View.GONE
                        tvNoRecetas.visibility = View.GONE
                    },{})
                } else {
                    Toast.makeText(baseContext, "Algo ha ido mal...", Toast.LENGTH_LONG).show()
                }
            }, it)
        }
    }


}