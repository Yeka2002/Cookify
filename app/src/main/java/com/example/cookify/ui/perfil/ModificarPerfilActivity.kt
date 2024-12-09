package com.example.cookify.ui.perfil

import UserService
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Switch
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cookify.R
import com.example.cookify.componentes.ComponenteImagenRedondeada
import com.example.cookify.controladores.ControladorCarga
import com.example.cookify.servicios.ServicioBase64
import java.io.ByteArrayOutputStream


class ModificarPerfilActivity : AppCompatActivity() {

    private lateinit var base64 : String
    private lateinit var etEditarNombre : EditText
    private lateinit var etEditarDescripcion : EditText
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private lateinit var swEditarPrivacidad : Switch
    private lateinit var cpEditarImagen : ComponenteImagenRedondeada
    private lateinit var btnGuardarPerfil : Button
    private lateinit var btnVolver : ImageView
    private lateinit var layout : ViewGroup
    private lateinit var progressBar: ProgressBar

    private lateinit var seleccionadorImagenes: ActivityResultLauncher<PickVisualMediaRequest>



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_modificar_perfil)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        layout = findViewById(R.id.main)
        progressBar = findViewById(R.id.progressBar)
        etEditarNombre = findViewById(R.id.etEditarNombre)
        etEditarDescripcion = findViewById(R.id.etEditarDescripcion)
        swEditarPrivacidad = findViewById(R.id.swPrivacidad)
        cpEditarImagen = findViewById(R.id.componenteImagenRedondeada2)
        btnGuardarPerfil = findViewById(R.id.btnGuardarPerfil)
        btnVolver = findViewById(R.id.ivCerrarGuardar)

        ControladorCarga.showLoading(true,layout,progressBar)
        refreshContent()

        // Registrar el seleccionador de imágenes
        seleccionadorImagenes = registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(2)) { uris ->
            if (uris.isNotEmpty()) {
                val btmp: Bitmap = ServicioBase64.getCompressedBitmapFromUri(baseContext,uris[0])
                cpEditarImagen.imagenEditable.setImageBitmap(btmp)
                base64 = ServicioBase64.bitmapToBase64(ServicioBase64.resizeBitmap(btmp,800,800))
            }
        }

        // Configurar el botón para abrir el selector de imágenes
        cpEditarImagen.setOnClickListener {
            openImagePicker()
        }

        btnGuardarPerfil.setOnClickListener {
            ControladorCarga.showLoading(true,layout,progressBar)
            guardarPerfil()
        }

        btnVolver.setOnClickListener{
            finish()
        }
    }

    private fun refreshContent() {
        baseContext?.let {
            UserService.getToken({ token ->
                // Aquí puedes manejar el token recibido
                if (token != null) {
                    UserService.cargarPerfil(token, onSuccess = {usuario ->
                        if (usuario != null){
                            base64 = usuario.photoUrl
                            cargarImagen(base64)
                            etEditarNombre.setText(usuario.name)
                            etEditarDescripcion.setText(usuario.description)
                            if (usuario.privacy)  swEditarPrivacidad.setChecked(true)
                            else swEditarPrivacidad.setChecked(false)
                            ControladorCarga.showLoading(false,layout,progressBar)
                        }
                    }, onFailure = {
                        Toast.makeText(baseContext,"Algo ha ido mal...",Toast.LENGTH_LONG).show()
                    })
                } else {
                    Toast.makeText(baseContext,"Algo ha ido mal...",Toast.LENGTH_LONG).show()
                }
            }, it)
        }
    }


    private fun guardarPerfil(){

        baseContext?.let {
            UserService.getToken({ token ->
                // Aquí puedes manejar el token recibido
                if (token != null) {
                    UserService.editarPerfil(token, UserService.usuarioModificado(base64, etEditarNombre.text.toString(),etEditarDescripcion.text.toString(),swEditarPrivacidad.isChecked), onSuccess = {usuario ->
                        if (usuario != null){
                            ControladorCarga.showLoading(false,layout,progressBar)
                            finish()
                            Toast.makeText(baseContext,"Perfil actualizado con éxito.",Toast.LENGTH_LONG).show()
                        }
                    }, onFailure = {
                        Toast.makeText(baseContext,"Algo ha ido mal...",Toast.LENGTH_LONG).show()
                    })
                } else {
                    Toast.makeText(baseContext,"Algo ha ido mal...",Toast.LENGTH_LONG).show()
                }
            }, it)
        }

    }

    // Método para obtener un Bitmap desde una URI
    private fun getBitmapFromUri(uri: Uri): Bitmap {
        return BitmapFactory.decodeStream(contentResolver.openInputStream(uri))
    }

    // Método para abrir el selector de imágenes cuando sea necesario
    private fun openImagePicker() {
        seleccionadorImagenes.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun cargarImagen(base64String: String){
        cpEditarImagen.imagenEditable.setImageBitmap(ServicioBase64.base64ToBitmap(base64String))
    }


}