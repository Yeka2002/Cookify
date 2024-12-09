package com.example.cookify.ui.inicio

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.cookify.R
import com.example.cookify.retrofit.RetrofitClientTraductor
import com.example.cookify.servicios.LibreTranslateService
import com.example.cookify.servicios.ServicioTemporada
import com.example.cookify.servicios.Traductor
import com.example.cookify.servicios.TranslateResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TemporadasActivity : AppCompatActivity() {

    private lateinit var gridLayout : GridLayout
    private lateinit var ivCerrarTemporadas : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_temporadas)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        ivCerrarTemporadas = findViewById(R.id.ivCerrarTemporadas)
        ivCerrarTemporadas.setOnClickListener {
            finish()
        }

        gridLayout = findViewById(R.id.gridLayout)
        getTemporadas()

    }

    private fun translateText(text: String, tv : TextView, sourceLang: String, targetLang: String) {
        val service = RetrofitClientTraductor.instance.create(LibreTranslateService::class.java)
        val request = Traductor(q = text, source = sourceLang, target = targetLang)

        service.translate(request).enqueue(object : Callback<TranslateResponse> {
            override fun onResponse(call: Call<TranslateResponse>, response: Response<TranslateResponse>) {
                if (response.isSuccessful) {
                    tv.text = response.body()?.translatedText
                } else {
                    tv.text = text
                }
            }

            override fun onFailure(call: Call<TranslateResponse>, t: Throwable) {
                tv.text = text
            }
        })
    }


    private fun getTemporadas() {

        ServicioTemporada.getTemporada { temporadas ->
            // Configura el RecyclerView con el adaptador
            for (temporada in temporadas.categories) {

                val cardView = LayoutInflater.from(this).inflate(R.layout.card_item, gridLayout, false) as CardView
                val imageView = cardView.findViewById<ImageView>(R.id.imageView)
                val textView = cardView.findViewById<TextView>(R.id.textView)

                // Aquí puedes cargar la imagen usando una biblioteca como Glide o Picasso
                Glide.with(this).load(temporada.strCategoryThumb).into(imageView)
                //textView.text = temporada.strCategory
                translateText(temporada.strCategory,textView,"en","es")

                cardView.setOnClickListener {
                    val intent = Intent(this, DetalleCategoriaActivity::class.java)
                    intent.putExtra("categoria", temporada)
                    startActivity(intent)
                }

                gridLayout.addView(cardView)

            }
        }


    }
}
