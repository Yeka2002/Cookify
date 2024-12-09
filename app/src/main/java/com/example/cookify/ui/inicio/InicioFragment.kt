package com.example.cookify.ui.inicio

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.cookify.MainActivity
import com.example.cookify.R
import com.example.cookify.adapters.PostAdapter
import com.example.cookify.controladores.ControladorCarga
import com.example.cookify.databinding.FragmentInicioBinding
import com.example.cookify.servicios.PostService
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class InicioFragment : Fragment() {

    companion object {
        private const val REQUEST_CAMERA_PERMISSION = 100
    }

    private var _binding: FragmentInicioBinding? = null
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var layout: ViewGroup
    private lateinit var pbCarga: ProgressBar
    private lateinit var currentPhotoPath: String

    private val binding get() = _binding!!

    private var currentPage = 0
    private var isLoading = false
    private val postAdapter = PostAdapter(mutableListOf())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentInicioBinding.inflate(inflater, container, false)
        val root: View = binding.root

        layout = binding.layoutInicio
        pbCarga = binding.progressBar

        binding.ivVerCategorias.setOnClickListener {
            val intent = Intent(context, TemporadasActivity::class.java)
            startActivity(intent)
        }

        binding.ivAbrirCamaraInicio.setOnClickListener {
            // Acción de abrir cámara
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
            } else {
                takePhoto()
            }
        }

        swipeRefreshLayout = root.findViewById(R.id.swipe_refresh_layout)
        swipeRefreshLayout.setOnRefreshListener {
            // Aquí puedes agregar el código para actualizar el contenido
            ControladorCarga.showLoading(true, layout, pbCarga)
            refreshContent()
        }

        binding.recyclerViewPosts.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewPosts.adapter = postAdapter
        binding.recyclerViewPosts.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(1) && !isLoading) {
                    // Llegamos al final de la lista
                    currentPage++
                    loadPosts(currentPage)
                }
            }
        })

        ControladorCarga.showLoading(true, layout, pbCarga)
        loadPosts(currentPage)

        return root
    }

    private fun loadPosts(page: Int) {
        isLoading = true
        (activity as? MainActivity)?.hideBottomNavigationView()
        // Muestra un indicador de carga si es necesario
        PostService.getAllPosts(page, 3, { posts ->
            isLoading = false
            // Oculta el indicador de carga
            binding.swipeRefreshLayout.isRefreshing = false

            // Configura el RecyclerView con el adaptador
            if (posts != null) {
                postAdapter.addPosts(posts)
                ControladorCarga.showLoading(false, layout, pbCarga)
                (activity as? MainActivity)?.showBottomNavigationView()
            }
        }, { error ->
            isLoading = false
            // Oculta el indicador de carga y maneja el error
            binding.swipeRefreshLayout.isRefreshing = false
            error.printStackTrace()
            ControladorCarga.showLoading(false, layout, pbCarga)
            Toast.makeText(context, "Error al cargar posts", Toast.LENGTH_SHORT).show()
        })
    }

    val getAction = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val photoURI: Uri = Uri.parse(currentPhotoPath)
            try {
                val bitmap = photoURI.let { uri ->
                    requireContext().contentResolver.openInputStream(uri)?.use { inputStream ->
                        BitmapFactory.decodeStream(inputStream)
                    }
                }
                // Usa el bitmap aquí
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        }
    }


    @SuppressLint("QueryPermissionsNeeded")
    private fun takePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        context?.let {
            if (intent.resolveActivity(it.packageManager) != null) {
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        requireContext(),
                        "com.example.cookify.fileprovider",
                        it
                    )
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    getAction.launch(intent)
                }
            }
        }
    }



    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File? = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Permiso concedido, continúa con la acción
                takePhoto()
            } else {
                // Permiso denegado, muestra un mensaje al usuario
                Toast.makeText(context, "Se requiere permiso de cámara para tomar fotos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun refreshContent() {
        // Simula la carga de datos
        Handler().postDelayed({
            // Detén la animación de actualización
            swipeRefreshLayout.isRefreshing = false
            currentPage = 0
            postAdapter.clearPosts()
            loadPosts(currentPage)
            // Notifica al usuario que la actualización se ha completado
            Toast.makeText(context, "Contenido actualizado", Toast.LENGTH_SHORT).show()
        }, 2000) // Simula un retardo de 2 segundos para la actualización
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
