package com.example.cookify.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cookify.R
import com.example.cookify.modelo.Usuario
import com.example.cookify.retrofit.RetrofitClient
import com.example.cookify.servicios.ServicioRegistro
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.UserProfileChangeRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegistroActivity : AppCompatActivity() {

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    //Elementos usados en la Activity.
    private lateinit var etNombre : EditText
    private lateinit var etEmail : EditText
    private lateinit var etPassword : EditText
    private lateinit var etRepPassword : EditText
    private lateinit var btnRegistrarse : Button
    private lateinit var btnCancelar : Button

    // Longitud mínima de la contraseña
    private val longitudContrasenaMax : Int = 6

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Oculto la barra superior que viene por defecto.
        supportActionBar?.hide()

        setContentView(R.layout.activity_registro)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Seleccion de componentes del layout
        etNombre = findViewById(R.id.etNombreRegistro)
        etEmail = findViewById(R.id.etEmailRegistro)
        etPassword = findViewById(R.id.etContrasenaRegistro)
        etRepPassword = findViewById(R.id.etCRepontrasenaRegistro)
        btnRegistrarse = findViewById(R.id.btnAceptarRegistro)
        btnCancelar = findViewById(R.id.btnCancelarRegistro)


        //Evento de Registro
        btnRegistrarse.setOnClickListener {
            btnRegistrarse.setEnabled(false)
            btnCancelar.setEnabled(false)
            registrarUsuario(
                etEmail.text.toString(),
                etPassword.text.toString(),
                etRepPassword.text.toString(),
                etNombre.text.toString())
        }

        //Evento de cancelar y volver al Login
        btnCancelar.setOnClickListener {
            finish()
        }

    }



    private fun registrarUsuario(email:String, password: String, repPassword: String, name:String) {

        //Variables predefinidas para todos los usuarios.
        val photoUrl = ""
        val description = ""
        val privacy = false

        //Error en el backend de Cookify
        //val errorCookify = mostrarDialogoError(getString(R.string.reg_fallo1), getString(R.string.btn_modal))

        if (!manejarErrores(email,password,repPassword,name)) {

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val currentUser = FirebaseAuth.getInstance().currentUser
                        currentUser?.updateProfile(UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .build())
                            ?.addOnCompleteListener { updateProfileTask ->
                                if (updateProfileTask.isSuccessful) {
                                    // El perfil del usuario se actualizó con éxito
                                    // Obtener el token de ID del usuario aquí
                                    val tokenTask = currentUser.getIdToken(true)
                                    tokenTask.addOnCompleteListener { tokenTask ->
                                        if (tokenTask.isSuccessful) {
                                            val token = tokenTask.result?.token
                                            if (token != null) {
                                                val usuario = Usuario(0, currentUser.uid, email, name, photoUrl, description, privacy)
                                                ServicioRegistro.enviarDatosAlBackend(token, usuario, this@RegistroActivity, currentUser)
                                            }
                                        } else {
                                            // Manejar el error al obtener el token de ID del usuario
                                            Toast.makeText(this@RegistroActivity, "Error al obtener el token de ID del usuario", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                } else {
                                    // No se pudo actualizar el perfil del usuario
                                    // Manejar el error según sea necesario
                                    Toast.makeText(this@RegistroActivity, "Error al actualizar el perfil del usuario", Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else {
                        when (task.exception) {
                            is FirebaseAuthWeakPasswordException -> mostrarDialogoError("Contraseña demasiado débil.", getString(R.string.btn_modal))
                            is FirebaseAuthInvalidCredentialsException -> mostrarDialogoError("Correo electrónico inválido.", getString(R.string.btn_modal))
                            is FirebaseAuthUserCollisionException -> mostrarDialogoError("El correo electrónico ya está en uso.", getString(R.string.btn_modal))
                            else -> mostrarDialogoError("Error en la autenticación: ${task.exception?.message}", getString(R.string.btn_modal))
                        }
                    }
                }



        }

    }


    /**
     * Maneja errores simples que puede tener el usuario al registrarse.
     */
    private fun manejarErrores(email: String, password: String, repPassword: String, nombre: String) : Boolean {

        if (nombre.isEmpty()) {
            etNombre.error = getString(R.string.reg_fallo3)
        }

        if (email.isEmpty() || !emailValido(email)) {
            etEmail.error = getString(R.string.txt_error_email)
            return true
        }

        if (password.isEmpty() || password.length < longitudContrasenaMax) {
            etPassword.error = getString(R.string.txt_error_password)
            return true
        }

        if (repPassword != password) {
            etRepPassword.error = getString(R.string.reg_fallo2)
            return true
        }

        return false
    }


    /**
     * Función para validar el correo electrónico (ejemplo básico)
     */
    private fun emailValido(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    /**
     * Crea una ventana modal.
     */
    private fun mostrarDialogoError(contenido:String,boton:String) : AlertDialog {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(contenido)
            .setCancelable(false)
            .setPositiveButton(boton) { dialog, _ ->
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()

        return alert
    }


}