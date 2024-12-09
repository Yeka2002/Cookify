package com.example.cookify.ui.buscar

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cookify.adapters.UsuariosBuscadosAdapter
import com.example.cookify.controladores.ControladorCarga
import com.example.cookify.databinding.FragmentBuscarBinding
import com.example.cookify.modelo.Usuario


class BuscarFragment : Fragment() {

    private var _binding: FragmentBuscarBinding? = null
    private val binding get() = _binding!!

    private lateinit var layout : ViewGroup
    private lateinit var pbCarga : ProgressBar
    private lateinit var tvNoResultados : TextView
    private lateinit var ivNoResultados : ImageView
    private lateinit var rvUsuarios: RecyclerView
    private lateinit var usuariosAdapter: UsuariosBuscadosAdapter
    private var searchHandler: Handler? = null
    private var searchRunnable: Runnable? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBuscarBinding.inflate(inflater, container, false)
        val root: View = binding.root

        layout = binding.main
        pbCarga = binding.progressBar
        rvUsuarios = binding.rvUsuarios
        tvNoResultados = binding.tvNoResultados
        ivNoResultados = binding.ivNoResultados

        // Inicializa el Handler y Runnable para el retardo de búsqueda
        searchHandler = Handler(Looper.getMainLooper())
        searchRunnable = Runnable {
            val query = binding.searchEditText.text.toString()
            ControladorCarga.showLoading(true,layout,pbCarga)
            mostrarBuscador()
            cargarMiUsuario(query)
        }

        // Configura el TextWatcher para el campo de búsqueda
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Elimina las llamadas pendientes al Runnable
                searchHandler?.removeCallbacks(searchRunnable!!)
                // Vuelve a poner el Runnable en la cola con un retardo de 1 segundo
                searchHandler?.postDelayed(searchRunnable!!, 1000)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.closeIcon.setOnClickListener {
            binding.searchEditText.setText("")
        }

        return root
    }

    @SuppressLint("ResourceAsColor")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun cargarMiUsuario(query: String) {

        context?.let {
            UserService.getToken({ token ->
                // Aquí puedes manejar el token recibido
                if (token != null) {
                    //Cargo el perfil (datos de usurio)
                    UserService.cargarPerfil(token, onSuccess = {usuario ->
                        if (usuario != null){
                            cargarUsuarios(query, usuario)
                        }
                    }, onFailure = {
                        Toast.makeText(context,"Algo ha ido mal...", Toast.LENGTH_LONG).show()
                    })
                } else {
                    Toast.makeText(context,"Algo ha ido mal...", Toast.LENGTH_LONG).show()
                }
            }, it)

        }


    }

    private fun cargarUsuarios(query: String, yo:Usuario) {
        UserService.searchUsers(query, { usuarios ->
            // Configura el RecyclerView
            val permitidos = usuarios.filter { usuario ->
                usuario?.privacy == false && usuario.name != yo.name
            }

            rvUsuarios.layoutManager = LinearLayoutManager(context)
            usuariosAdapter = UsuariosBuscadosAdapter(permitidos)
            rvUsuarios.adapter = usuariosAdapter

            ControladorCarga.showLoading(false,layout,pbCarga)


            if (permitidos.isNotEmpty()) {
                tvNoResultados.visibility = View.GONE
                ivNoResultados.visibility = View.GONE
            }
            else{
                tvNoResultados.visibility = View.VISIBLE
                ivNoResultados.visibility = View.VISIBLE
            }
        }, {
            // Maneja el error
        })
    }

    private fun mostrarBuscador(){
        binding.searchIcon.visibility = View.VISIBLE
        binding.searchEditText.visibility = View.VISIBLE
        binding.closeIcon.visibility = View.VISIBLE
        binding.barraBusqueda.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        searchHandler?.removeCallbacks(searchRunnable!!)
    }
}
