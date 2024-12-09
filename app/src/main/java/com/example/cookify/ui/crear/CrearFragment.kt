package com.example.cookify.ui.crear

import UserService
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.cookify.R
import com.example.cookify.adapters.CarruselCrearPostAdapter
import com.example.cookify.controladores.ControladorCarga
import com.example.cookify.databinding.FragmentCrearBinding
import com.example.cookify.modelo.ImagenPost
import com.example.cookify.servicios.PostService
import com.example.cookify.servicios.ServicioBase64
import com.example.cookify.servicios.ServicioTemporada
import com.google.firebase.auth.FirebaseAuth

class CrearFragment : Fragment() {

    // Número máximo de imágenes que son posibles de seleccionar a la vez.
    private val maximoImagenes: Int = 5
    private var _binding: FragmentCrearBinding? = null
    private val binding get() = _binding!!

    private lateinit var etTituloCrear: EditText
    private lateinit var etDescripcionCrear: EditText
    private lateinit var imagenesSeleccionadas: List<ImagenPost>
    private lateinit var carrusel: ViewPager2
    private lateinit var spCategoria: Spinner
    private lateinit var spComida: Spinner
    private lateinit var ivCamara: ImageView
    private lateinit var ivCancelar: ImageView

    private lateinit var seleccionadorImagenes: ActivityResultLauncher<PickVisualMediaRequest>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentCrearBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Inicializo las variables
        etTituloCrear = binding.etTituloPost
        etDescripcionCrear = binding.etDescripcionPost
        carrusel = binding.crearCarrusel
        spCategoria = binding.spinnerCategoria
        spComida = binding.spinnerComida
        ivCamara = binding.ivAbrirCamara
        ivCancelar = binding.ivCancelarPublicacion
        imagenesSeleccionadas = listOf()

        // Configura el ActivityResultLauncher
        seleccionadorImagenes = registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(maximoImagenes)) { uris ->
            if (uris.isNotEmpty()) {
                manejarImagenesSeleccionadas(uris)
                val adapter = CarruselCrearPostAdapter(uris)
                carrusel.adapter = adapter
                ivCamara.visibility = View.GONE
            }
        }

        // Configurar el botón para abrir el seleccionador de imágenes
        ivCamara.setOnClickListener {
            seleccionadorImagenes.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        ivCancelar.setOnClickListener {
            activity?.recreate()
        }

        // Configura los spinners
        ControladorCarga.showLoading(true, binding.main, binding.progressBar)
        rellenarSpinners()

        binding.ivPublicar.setOnClickListener {
            ControladorCarga.showLoading(true, binding.main, binding.progressBar)
            crearPublicacion()
        }

        return root
    }

    private fun crearPublicacion() {

        if(!manejarErrores(etTituloCrear.text.toString(),etDescripcionCrear.text.toString())){

            context?.let {
                PostService.getToken({ token ->
                    // Aquí puedes manejar el token recibido
                    if (token != null) {
                        val usuario = FirebaseAuth.getInstance().currentUser?.displayName.toString()
                        PostService.crearPost(token, PostService.postCreado(etTituloCrear.text.toString(), etDescripcionCrear.text.toString(), PostService.getUserUid(), usuario, imagenesSeleccionadas, "2024-06-12T12:50:21", spCategoria.selectedItem.toString(), spComida.selectedItem.toString()), onSuccess = { post ->
                            if (post != null) {
                                //CAMBIAR FRAGMENT
                                ControladorCarga.showLoading(false, binding.main, binding.progressBar)
                                ivCamara.visibility = View.GONE
                                mostrarDialogoError("Receta publicada con éxito.","OK")
                            }
                        }, onFailure = {
                            Toast.makeText(context, "Algo ha ido mal...", Toast.LENGTH_LONG).show()
                        })
                    } else {
                        Toast.makeText(context, "Algo ha ido mal...", Toast.LENGTH_LONG).show()
                    }
                }, it)
            }

        }

    }

    private fun rellenarSpinners() {
        ServicioTemporada.getTemporada { temporadas ->
            context?.let { context ->
                // Extrae los nombres de las temporadas y agrega "Ninguna" al principio
                val nombresComida = mutableListOf("Ninguna")
                val nombresTemporadas = mutableListOf("Ninguna")
                nombresTemporadas.addAll(temporadas.categories.map { it.strCategory })

                // Crea y configura el adaptador
                val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, nombresTemporadas)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spCategoria.adapter = adapter

                val adapter2 = ArrayAdapter(context, android.R.layout.simple_spinner_item, nombresComida)
                adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spComida.adapter = adapter2
                spComida.isEnabled = false

                // Configura el listener para habilitar spComida
                spCategoria.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        val selectedItem = nombresTemporadas[position]
                        spComida.isEnabled = selectedItem != "Ninguna"
                        if (spComida.isEnabled) {
                            ServicioTemporada.getRecetasPorCategoria(spCategoria.selectedItem.toString()) { recetas ->
                                nombresComida.clear()
                                nombresComida.addAll(recetas.meals.map { it.strMeal })
                            }
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        // No hace nada
                    }
                }

                ControladorCarga.showLoading(false, binding.main, binding.progressBar)

            }
        }
    }

    private fun manejarImagenesSeleccionadas(uris: List<Uri>) {
        val imagenesBase64 = mutableListOf<String>()
        uris.forEach { uri ->
            // Obtener bitmap desde Uri con compresión ajustada
            val bitmap = ServicioBase64.getCompressedBitmapFromUri(binding.root.context,uri)
            // Convertir bitmap a Base64
            val base64String = ServicioBase64.bitmapToBase64(bitmap)
            imagenesBase64.add(base64String)
        }
        // Convertir Base64 a lista de objetos ImagenPost
        imagenesSeleccionadas = imagenesBase64.map { ImagenPost(imagenBase64 = it) }
    }


    // Método para obtener un Bitmap desde una URI
    private fun getBitmapFromUri(uri: Uri): Bitmap {
        return BitmapFactory.decodeStream(context?.contentResolver?.openInputStream(uri))
    }

    private fun manejarErrores(titulo: String, descripcion: String) : Boolean {

        if (titulo.isEmpty()) {
            etTituloCrear.error = getString(R.string.reg_fallo3)
            return true
        }

        if (descripcion.isEmpty()) {
            etDescripcionCrear.error = getString(R.string.txt_error_email)
            return true
        }


        if (imagenesSeleccionadas.isEmpty()) {
            mostrarDialogoError("No ha seleccionado ninguna imagen.","OK")
            return true
        }

        return false
    }

    /**
     * Crea una ventana modal.
     */
    private fun mostrarDialogoError(contenido:String,boton:String) : AlertDialog {
        val builder = AlertDialog.Builder(binding.root.context)
        builder.setMessage(contenido)
            .setCancelable(false)
            .setPositiveButton(boton) { dialog, _ ->
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()

        return alert
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
