package com.example.cookify.ui.inicio

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cookify.R
import com.example.cookify.adapters.RecetaApiAdapter
import com.example.cookify.modelo.Temporada
import com.example.cookify.servicios.ServicioTemporada

class DetalleCategoriaActivity : AppCompatActivity() {

    private lateinit var ivCerrar : ImageView
    private lateinit var categoria : Temporada
    private lateinit var ivImagen : ImageView
    private lateinit var tvNombre : TextView
    private lateinit var rvRecetasApi : RecyclerView
    private lateinit var recetaAdapter: RecetaApiAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_categoria)
        supportActionBar?.hide()

        // Obtener el nombre de la categoría desde el Intent
        @Suppress("DEPRECATION")
        categoria = intent.getSerializableExtra("categoria") as Temporada

        ivCerrar = findViewById(R.id.ivCerrarRecetas)
        ivImagen = findViewById(R.id.ivCategoria)
        tvNombre = findViewById(R.id.tvTitulo)
        rvRecetasApi = findViewById(R.id.rvRecetasApi)

        Glide.with(this).load(categoria.strCategoryThumb).into(ivImagen)
        tvNombre.text = categoria.strCategory

        ivCerrar.setOnClickListener {
            finish()
        }

        cargarRecetas()

    }

    private fun cargarRecetas(){
        ServicioTemporada.getRecetasPorCategoria(categoria.strCategory) {recetas->
            // Configura el RecyclerView
            rvRecetasApi.layoutManager = LinearLayoutManager(baseContext)
            recetaAdapter = RecetaApiAdapter(recetas.meals)
            rvRecetasApi.adapter = recetaAdapter
        }
    }

}