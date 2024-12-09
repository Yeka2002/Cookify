package com.example.cookify

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.cookify.databinding.ActivityMainBinding
import com.google.firebase.FirebaseApp

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseApp.initializeApp(this)

        navView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_perfil, R.id.navigation_inicio, R.id.navigation_buscar, R.id.navigation_crear
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    fun showBottomNavigationView() {
        for (i in 0 until navView.menu.size()) {
            navView.menu.getItem(i).isEnabled = true
        }
    }

    fun hideBottomNavigationView() {
        for (i in 0 until navView.menu.size()) {
            navView.menu.getItem(i).isEnabled = false
        }
    }

    @SuppressLint("MissingSuperCall")
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        mostrarDialogoCerrarApp()
    }

    private fun mostrarDialogoCerrarApp() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("¿Deseas cerrar la aplicación?")
            .setCancelable(false)
            .setPositiveButton("Sí") { _, _ ->
                finishAffinity() // Cierra la aplicación
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }
}
