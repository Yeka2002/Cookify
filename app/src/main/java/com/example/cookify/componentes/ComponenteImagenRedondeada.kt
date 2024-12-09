package com.example.cookify.componentes

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.cookify.R
import com.google.android.material.imageview.ShapeableImageView

class ComponenteImagenRedondeada: ConstraintLayout {

    lateinit var imagenEditable: ShapeableImageView

    constructor(context: Context) : super(context) {
        initialize(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initialize(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initialize(context, attrs)
    }

    private fun initialize(context: Context, attrs: AttributeSet?) {
        // Inflar el diseño XML que contiene ShapeableImageView
        inflate(context, R.layout.componente_imagen_redondeada, this)

        // Obtener referencia a ShapeableImageView por su ID
        imagenEditable = findViewById(R.id.imagenEditable)
        imagenEditable.setImageResource(R.drawable.ic_account_24dp)
    }


}