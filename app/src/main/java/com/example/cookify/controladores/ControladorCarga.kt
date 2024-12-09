package com.example.cookify.controladores

import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar

class ControladorCarga {

    companion object{

        // Función para mostrar u ocultar el ProgressBar y el layout
        fun showLoading(isLoading: Boolean, layout: ViewGroup, progressBar: ProgressBar) {
            if (isLoading) {
                layout.setAllChildrenGone()
                progressBar.visibility = View.VISIBLE
            } else {
                layout.setAllChildrenVisible()
                progressBar.visibility = View.GONE
            }
        }

        // Función para hacer invisibles todos los componentes del layout
        private fun ViewGroup.setAllChildrenGone() {
            for (i in 0 until childCount) {
                val child = getChildAt(i)
                child.visibility = View.GONE
            }
        }

        // Función para hacer visibles todos los componentes del layout
        private fun ViewGroup.setAllChildrenVisible() {
            for (i in 0 until childCount) {
                val child = getChildAt(i)
                child.visibility = View.VISIBLE
            }
        }

    }

}