package com.example.cookify.ui.inicio

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.cookify.R
import com.example.cookify.modelo.Receta
import com.example.cookify.servicios.ServicioTemporada
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

class DetalleRecetaActivity : AppCompatActivity() {

    private lateinit var ivReceta: ImageView
    private lateinit var videoYoutube: YouTubePlayerView
    private lateinit var tvTituloReceta: TextView
    private lateinit var tvElaboracion: TextView
    private lateinit var ivCerrarReceta: ImageView

    private lateinit var tvI1: TextView
    private lateinit var tvI2: TextView
    private lateinit var tvI3: TextView
    private lateinit var tvI4: TextView
    private lateinit var tvI5: TextView
    private lateinit var tvI6: TextView
    private lateinit var tvI7: TextView
    private lateinit var tvI8: TextView
    private lateinit var tvI9: TextView
    private lateinit var tvI10: TextView
    private lateinit var tvI11: TextView
    private lateinit var tvI12: TextView
    private lateinit var tvI13: TextView
    private lateinit var tvI14: TextView
    private lateinit var tvI15: TextView
    private lateinit var tvI16: TextView
    private lateinit var tvI17: TextView
    private lateinit var tvI18: TextView
    private lateinit var tvI19: TextView
    private lateinit var tvI20: TextView

    private lateinit var tvC1: TextView
    private lateinit var tvC2: TextView
    private lateinit var tvC3: TextView
    private lateinit var tvC4: TextView
    private lateinit var tvC5: TextView
    private lateinit var tvC6: TextView
    private lateinit var tvC7: TextView
    private lateinit var tvC8: TextView
    private lateinit var tvC9: TextView
    private lateinit var tvC10: TextView
    private lateinit var tvC11: TextView
    private lateinit var tvC12: TextView
    private lateinit var tvC13: TextView
    private lateinit var tvC14: TextView
    private lateinit var tvC15: TextView
    private lateinit var tvC16: TextView
    private lateinit var tvC17: TextView
    private lateinit var tvC18: TextView
    private lateinit var tvC19: TextView
    private lateinit var tvC20: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_receta)
        supportActionBar?.hide()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tvTituloReceta = findViewById(R.id.tvTituloReceta)
        ivReceta = findViewById(R.id.ivReceta)
        videoYoutube = findViewById(R.id.youtubePlayerView)
        ivCerrarReceta = findViewById(R.id.ivCerrarReceta)
        tvElaboracion = findViewById(R.id.tvElaboracion)

        tvI1 = findViewById(R.id.i1)
        tvI2 = findViewById(R.id.i2)
        tvI3 = findViewById(R.id.i3)
        tvI4 = findViewById(R.id.i4)
        tvI5 = findViewById(R.id.i5)
        tvI6 = findViewById(R.id.i6)
        tvI7 = findViewById(R.id.i7)
        tvI8 = findViewById(R.id.i8)
        tvI9 = findViewById(R.id.i9)
        tvI10 = findViewById(R.id.i10)
        tvI11 = findViewById(R.id.i11)
        tvI12 = findViewById(R.id.i12)
        tvI13 = findViewById(R.id.i13)
        tvI14 = findViewById(R.id.i14)
        tvI15 = findViewById(R.id.i15)
        tvI16 = findViewById(R.id.i16)
        tvI17 = findViewById(R.id.i17)
        tvI18 = findViewById(R.id.i18)
        tvI19 = findViewById(R.id.i19)
        tvI20 = findViewById(R.id.i20)

        tvC1 = findViewById(R.id.c1)
        tvC2 = findViewById(R.id.c2)
        tvC3 = findViewById(R.id.c3)
        tvC4 = findViewById(R.id.c4)
        tvC5 = findViewById(R.id.c5)
        tvC6 = findViewById(R.id.c6)
        tvC7 = findViewById(R.id.c7)
        tvC8 = findViewById(R.id.c8)
        tvC9 = findViewById(R.id.c9)
        tvC10 = findViewById(R.id.c10)
        tvC11 = findViewById(R.id.c11)
        tvC12 = findViewById(R.id.c12)
        tvC13 = findViewById(R.id.c13)
        tvC14 = findViewById(R.id.c14)
        tvC15 = findViewById(R.id.c15)
        tvC16 = findViewById(R.id.c16)
        tvC17 = findViewById(R.id.c17)
        tvC18 = findViewById(R.id.c18)
        tvC19 = findViewById(R.id.c19)
        tvC20 = findViewById(R.id.c20)

        val recetaId = intent.getStringExtra("idMeal")
        cargarReceta(recetaId.toString())

        ivCerrarReceta.setOnClickListener {
            finish()
        }

    }

    private fun cargarReceta(recetaId : String){
        ServicioTemporada.getRecetaPorId(recetaId) { recetas ->
            val receta = recetas.meals[0]

            tvTituloReceta.text = receta.strMeal
            tvElaboracion.text = receta.strInstructions

            if (!receta.strIngredient1.isNullOrEmpty()) tvI1.text = receta.strIngredient1
            if (!receta.strIngredient2.isNullOrEmpty()) tvI2.text = receta.strIngredient2
            if (!receta.strIngredient3.isNullOrEmpty()) tvI3.text = receta.strIngredient3
            if (!receta.strIngredient4.isNullOrEmpty()) tvI4.text = receta.strIngredient4
            if (!receta.strIngredient5.isNullOrEmpty()) tvI5.text = receta.strIngredient5
            if (!receta.strIngredient6.isNullOrEmpty()) tvI6.text = receta.strIngredient6
            if (!receta.strIngredient7.isNullOrEmpty()) tvI7.text = receta.strIngredient7
            if (!receta.strIngredient8.isNullOrEmpty()) tvI8.text = receta.strIngredient8
            if (!receta.strIngredient9.isNullOrEmpty()) tvI9.text = receta.strIngredient9
            if (!receta.strIngredient10.isNullOrEmpty()) tvI10.text = receta.strIngredient10
            if (!receta.strIngredient11.isNullOrEmpty()) tvI11.text = receta.strIngredient11
            if (!receta.strIngredient12.isNullOrEmpty()) tvI12.text = receta.strIngredient12
            if (!receta.strIngredient13.isNullOrEmpty()) tvI13.text = receta.strIngredient13
            if (!receta.strIngredient14.isNullOrEmpty()) tvI14.text = receta.strIngredient14
            if (!receta.strIngredient15.isNullOrEmpty()) tvI15.text = receta.strIngredient15
            if (!receta.strIngredient16.isNullOrEmpty()) tvI16.text = receta.strIngredient16
            if (!receta.strIngredient17.isNullOrEmpty()) tvI17.text = receta.strIngredient17
            if (!receta.strIngredient18.isNullOrEmpty()) tvI18.text = receta.strIngredient18
            if (!receta.strIngredient19.isNullOrEmpty()) tvI19.text = receta.strIngredient19
            if (!receta.strIngredient20.isNullOrEmpty()) tvI20.text = receta.strIngredient20

            if (!receta.strMeasure1.isNullOrEmpty()) tvC1.text = receta.strMeasure1
            if (!receta.strMeasure2.isNullOrEmpty()) tvC2.text = receta.strMeasure2
            if (!receta.strMeasure3.isNullOrEmpty()) tvC3.text = receta.strMeasure3
            if (!receta.strMeasure4.isNullOrEmpty()) tvC4.text = receta.strMeasure4
            if (!receta.strMeasure5.isNullOrEmpty()) tvC5.text = receta.strMeasure5
            if (!receta.strMeasure6.isNullOrEmpty()) tvC6.text = receta.strMeasure6
            if (!receta.strMeasure7.isNullOrEmpty()) tvC7.text = receta.strMeasure7
            if (!receta.strMeasure8.isNullOrEmpty()) tvC8.text = receta.strMeasure8
            if (!receta.strMeasure9.isNullOrEmpty()) tvC9.text = receta.strMeasure9
            if (!receta.strMeasure10.isNullOrEmpty()) tvC10.text = receta.strMeasure10
            if (!receta.strMeasure11.isNullOrEmpty()) tvC11.text = receta.strMeasure11
            if (!receta.strMeasure12.isNullOrEmpty()) tvC12.text = receta.strMeasure12
            if (!receta.strMeasure13.isNullOrEmpty()) tvC13.text = receta.strMeasure13
            if (!receta.strMeasure14.isNullOrEmpty()) tvC14.text = receta.strMeasure14
            if (!receta.strMeasure15.isNullOrEmpty()) tvC15.text = receta.strMeasure15
            if (!receta.strMeasure16.isNullOrEmpty()) tvC16.text = receta.strMeasure16
            if (!receta.strMeasure17.isNullOrEmpty()) tvC17.text = receta.strMeasure17
            if (!receta.strMeasure18.isNullOrEmpty()) tvC18.text = receta.strMeasure18
            if (!receta.strMeasure19.isNullOrEmpty()) tvC19.text = receta.strMeasure19
            if (!receta.strMeasure20.isNullOrEmpty()) tvC20.text = receta.strMeasure20

            Glide.with(baseContext)
                .load(receta.strMealThumb)
                .into(ivReceta)

            // Configurar el reproductor de YouTube
            lifecycle.addObserver(videoYoutube)
            videoYoutube.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {
                    receta.strYoutube?.let {
                        val videoId = it.split("v=")[1]
                        youTubePlayer.cueVideo(videoId, 0f)
                    }
                }
            })

        }
    }

}