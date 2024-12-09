package com.example.cookify.componentes

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cookify.R
import com.example.cookify.adapters.ComentariosAdapter
import com.example.cookify.controladores.ControladorCarga
import com.example.cookify.modelo.Comentario
import com.example.cookify.servicios.PostService
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ComponentesComentarios : BottomSheetDialogFragment() {

    private lateinit var layout: ViewGroup
    private lateinit var btnEnviarComentario: Button
    private lateinit var etComentario: EditText
    private lateinit var rvComentarios: RecyclerView
    private lateinit var ivNoComentarios: ImageView
    private lateinit var tvNoComentarios: TextView
    private lateinit var pbCarga: ProgressBar
    private var postId: Long = 0L
    private lateinit var comentariosAdapter: ComentariosAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postId = arguments?.getLong(ARG_POST_ID) ?: 0L
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setContentView(R.layout.bottom_sheet_comentarios)

        layout = dialog.findViewById(R.id.main)
        btnEnviarComentario = dialog.findViewById(R.id.btnEnviarComentario)
        etComentario = dialog.findViewById(R.id.etComentario)
        rvComentarios = dialog.findViewById(R.id.rvComentarios)
        ivNoComentarios = dialog.findViewById(R.id.ivNoComentarios)
        tvNoComentarios = dialog.findViewById(R.id.tvNoComentarios)
        pbCarga = dialog.findViewById(R.id.progressBar)

        // Configurar RecyclerView
        rvComentarios.layoutManager = LinearLayoutManager(context)
        comentariosAdapter = ComentariosAdapter(emptyList()) // Adaptador inicial vacío
        rvComentarios.adapter = comentariosAdapter

        // Manejar el envío de comentarios
        btnEnviarComentario.setOnClickListener {
            val comentarioTexto = etComentario.text.toString()

            // Crear el objeto Comentario con los datos necesarios
            val comentario = Comentario(
                id = 0L,  // El ID generalmente lo asigna el servidor, por lo que puedes poner cualquier valor inicial
                contenido = comentarioTexto,
                post = null,  // Aquí puedes asignar el post si es necesario
                usuarioNombre = "",  // Nombre del usuario que hace el comentario
            )

            // Llamar al método enviarComentario con el objeto Comentario y postId
            enviarComentario(comentario, postId)
            etComentario.setText("")
            btnEnviarComentario.setEnabled(false)
        }

        // Cargar comentarios al abrir el fragmento
        ControladorCarga.showLoading(true,layout,pbCarga)
        getComentariosByPostId()

        return dialog
    }

    private fun getComentariosByPostId(){
        context?.let { context ->
            PostService.getToken({ token ->
                // Aquí puedes manejar el token recibido
                if (token != null) {
                    // Llamar al método para obtener comentarios por postId
                    PostService.getComentariosByPostId(postId,
                        onSuccess = { comentarios ->
                            // Actualizar el adaptador con los nuevos comentarios
                            if (comentarios != null) {
                                comentariosAdapter.actualizarComentarios(comentarios)
                                ControladorCarga.showLoading(false,layout,pbCarga)
                                ivNoComentarios.visibility = View.GONE
                                tvNoComentarios.visibility = View.GONE
                                if (comentarios.isEmpty()) ControladorCarga.showLoading(false,layout,pbCarga)
                            }

                        },
                        onFailure = { error ->
                            // Manejar el error, por ejemplo, mostrar un mensaje de error
                            Toast.makeText(context, "Error al cargar comentarios: ${error.message}", Toast.LENGTH_SHORT).show()
                        }
                    )
                } else {
                    Toast.makeText(context, "No se pudo obtener el token de usuario", Toast.LENGTH_SHORT).show()
                }
            }, context)
        }
    }


    private fun enviarComentario(comentario: Comentario, postId: Long) {
        context?.let { context ->
            PostService.getToken({ token ->
                // Aquí puedes manejar el token recibido
                if (token != null) {
                    // Llamar al método para agregar comentario a un post
                    PostService.addComentarioToPost(postId, token, comentario,
                        onSuccess = {
                            // Después de enviar el comentario con éxito, cargar los comentarios actualizados
                            getComentariosByPostId()
                            btnEnviarComentario.setEnabled(true)
                        },
                        onFailure = {
                            // Manejar el error, por ejemplo, mostrar un mensaje de error
                            Toast.makeText(context, "Error al enviar comentario.", Toast.LENGTH_SHORT).show()
                            btnEnviarComentario.setEnabled(true)
                        }
                    )
                } else {
                    Toast.makeText(context, "No se pudo obtener el token de usuario", Toast.LENGTH_SHORT).show()
                }
            }, context)
        }
    }

    // Método para crear una nueva instancia de la hoja de comentarios con el postId
    companion object {
        private const val ARG_POST_ID = "postId"

        fun newInstance(postId: Long): ComponentesComentarios {
            val fragment = ComponentesComentarios()
            val args = Bundle()
            args.putLong(ARG_POST_ID, postId)
            fragment.arguments = args
            return fragment
        }
    }
}
