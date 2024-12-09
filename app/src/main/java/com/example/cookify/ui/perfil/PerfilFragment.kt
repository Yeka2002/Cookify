package com.example.cookify.ui.perfil

import UserService
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.cookify.MainActivity
import com.example.cookify.R
import com.example.cookify.adapters.MisPostsAdapter
import com.example.cookify.componentes.ComponenteImagenRedondeada
import com.example.cookify.componentes.ComponenteModalSalir
import com.example.cookify.controladores.ControladorCarga
import com.example.cookify.databinding.FragmentPerfilBinding
import com.example.cookify.servicios.PostService
import com.example.cookify.servicios.ServicioBase64
import kotlin.properties.Delegates

class PerfilFragment : Fragment() {

    private var _binding: FragmentPerfilBinding? = null
    private val binding get() = _binding!!

    private lateinit var base64 : String
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var imgRedondeada : ComponenteImagenRedondeada
    private lateinit var layout : ViewGroup
    private lateinit var progressBar: ProgressBar
    private lateinit var etEditarNombre: TextView
    private lateinit var ivPrivacidad: ImageView
    private lateinit var ivOpciones: ImageView
    private lateinit var ivNoRecetas: ImageView
    private lateinit var tvNoRecetas: TextView

    private var cuentaPrivada by Delegates.notNull<Boolean>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentPerfilBinding.inflate(inflater, container, false)
        val root: View = binding.root

        layout = binding.layoutPerfil
        progressBar = binding.progressBar
        etEditarNombre = binding.textNombrePerfil
        ivPrivacidad = binding.ivPrivacidad
        imgRedondeada = binding.componenteImagenRedondeada
        ivOpciones = binding.ivOpciones
        ivNoRecetas = binding.ivNoRecetas
        tvNoRecetas = binding.tvNoRecetas

        // Configura el RecyclerView con un GridLayoutManager de 3 columnas
        binding.recyclerViewPosts.layoutManager = GridLayoutManager(context, 3)

        // Oculto los items de mi layout hasta que cargue el perfil al completo.
        ControladorCarga.showLoading(true, layout, progressBar)
        cargarPerfil()

        ivOpciones.setOnClickListener {
            showPopupMenu(it)
        }

        swipeRefreshLayout = root.findViewById(R.id.swipe_refresh_layout_perfil)
        swipeRefreshLayout.setOnRefreshListener {
            // Aquí puedes agregar el código para actualizar el contenido
            ControladorCarga.showLoading(true, layout, progressBar)
            refreshContent()
        }

        return root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun refreshContent() {
        // Simula la carga de datos
        Handler().postDelayed({
            // Detén la animación de actualización
            swipeRefreshLayout.isRefreshing = false
            cargarPerfil()
            // Notifica al usuario que la actualización se ha completado
            Toast.makeText(context, "Contenido actualizado", Toast.LENGTH_SHORT).show()
        }, 0) // Simula un retardo de 2 segundos para la actualización
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun cargarPerfil() {

        context?.let {
            UserService.getToken({ token ->
                // Primero comprobamos si el usuario tiene token.
                if (token != null) {
                    (activity as? MainActivity)?.hideBottomNavigationView()
                    //Cargo los datos del usuario.
                    UserService.cargarPerfil(token, onSuccess = { usuario ->

                        if (usuario != null) {

                            base64 = usuario.photoUrl
                            cargarImagen(base64)
                            etEditarNombre.text = usuario.name
                            imgRedondeada.tooltipText = usuario.description
                            cuentaPrivada = usuario.privacy

                            //Cargo los posts del usuario.
                            PostService.getUserPosts(token, { posts ->

                                if (posts != null) {

                                    binding.textNumPublicaciones.text = posts.size.toString()
                                    binding.recyclerViewPosts.adapter = MisPostsAdapter(posts, requireContext())
                                    ControladorCarga.showLoading(false, layout, progressBar)
                                    mensajeNoRecetas(posts.size)

                                    if (!cuentaPrivada) ivPrivacidad.visibility = View.GONE

                                    (activity as? MainActivity)?.showBottomNavigationView()
                                }

                            }, {
                                Toast.makeText(context, "Fallo al cargar los posts.", Toast.LENGTH_LONG).show()
                                (activity as? MainActivity)?.showBottomNavigationView()
                            })

                        }
                    }, {
                        Toast.makeText(context, "Fallo al cargar el perfil de usuario.", Toast.LENGTH_LONG).show()
                        (activity as? MainActivity)?.showBottomNavigationView()
                    })


                } else {
                    Toast.makeText(context, "Conexión fallida.", Toast.LENGTH_LONG).show()
                    (activity as? MainActivity)?.showBottomNavigationView()
                }
            }, it)
        }
    }

    private fun cargarImagen(base64String: String) {
        imgRedondeada.imagenEditable.setImageBitmap(ServicioBase64.base64ToBitmap(base64String))
    }

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(context, view)
        popupMenu.menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.option1 -> {
                    val dialog = ComponenteModalSalir()
                    dialog.show(requireActivity().supportFragmentManager, "componente_modal_salir")
                    true
                }
                R.id.option2 -> {
                    val intent = Intent(context, ModificarPerfilActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

        popupMenu.show()
    }

    private fun mensajeNoRecetas(nRecetas: Int) {
        if (nRecetas != 0) {
            tvNoRecetas.visibility = View.GONE
            ivNoRecetas.visibility = View.GONE
        } else {
            tvNoRecetas.visibility = View.VISIBLE
            ivNoRecetas.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
