package com.example.cookify.ui

import android.content.Intent
import android.os.Bundle
import android.graphics.Paint
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cookify.MainActivity
import com.example.cookify.R
import com.example.cookify.servicios.TokenManagerService
import com.google.firebase.FirebaseApp

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions


/**
 * Activity donde se realiza el login, únicamente depende de FirebaseAuth que facilita varias funciones.
 */
class LoginActivity : AppCompatActivity() {

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    //Elementos usados en la Activity.
    private lateinit var etEmail : EditText
    private lateinit var etPassword : EditText
    private lateinit var btnIniciarSesion : Button
    private lateinit var btnRegistro : Button
    private lateinit var tvAvisoLegal : TextView
    private lateinit var tvOlvidaContrasena : TextView
    private lateinit var tvMasInfo : TextView


    // Longitud mínima de la contraseña
    private val longitudContrasenaMax : Int = 6



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Oculto la barra superior que viene por defecto.
        supportActionBar?.hide()

        setContentView(R.layout.activity_login)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Seleccion de componentes del layout
        etEmail = findViewById(R.id.etEmailLogin)
        etPassword = findViewById(R.id.etContrasenaLogin)
        btnIniciarSesion = findViewById(R.id.btnIniciarSesion)
        btnRegistro = findViewById(R.id.btnRegistro)
        tvMasInfo = findViewById(R.id.tvMasInfo)
        tvAvisoLegal = findViewById(R.id.tvAvisoLegal)
        tvOlvidaContrasena = findViewById(R.id.tvOlvidaContrasena)
        //Lo pongo subrayado
        tvOlvidaContrasena.paintFlags = tvOlvidaContrasena.paintFlags or Paint.UNDERLINE_TEXT_FLAG


        //Evento botón Iniciar sesión
        btnIniciarSesion.setOnClickListener{
            btnRegistro.setEnabled(false)
            btnIniciarSesion.setEnabled(false)
            iniciarSesion()
        }

        //Evento botón registro
        btnRegistro.setOnClickListener {
            val intent = Intent(this, RegistroActivity::class.java)
            startActivity(intent)
        }

        //Evento para recuperar contraseña.
        tvOlvidaContrasena.setOnClickListener{
            sendPasswordResetEmail(etEmail.text.toString().trim())
        }
        //iniciarSiHayToken()


        /*val inputText = "HELLO"

        // Configura el traductor
        val options = FirebaseTranslatorOptions.Builder()
            .setSourceLanguage(FirebaseTranslateLanguage.EN)
            .setTargetLanguage(FirebaseTranslateLanguage.ES)
            .build()
        val translator = FirebaseNaturalLanguage.getInstance().getTranslator(options)

        // Inicia la traducción
        translator.downloadModelIfNeeded()
            .addOnSuccessListener {
                translator.translate(inputText)
                    .addOnSuccessListener { translatedText ->
                        Toast.makeText(baseContext,translatedText,Toast.LENGTH_LONG).show()
                    }
                    .addOnFailureListener { exception ->
                        // Manejar errores de traducción

                    }
            }
            .addOnFailureListener { exception ->
                // Manejar errores de descarga del modelo

            }*/

    }



    /**
     * Inicia sesión automático si ya existe el token.
     */
    private fun iniciarSiHayToken(){
        if (TokenManagerService(baseContext).hasUserToken()) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }


    private fun iniciarSesion(){

        val email : String = etEmail.text.toString()
        val password : String = etPassword.text.toString()

        if (!manejarErrores(email,password)) {

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        // Comprobar fallo
                        try {
                            throw task.exception ?: java.lang.Exception(R.string.error_unknown.toString())
                        } catch (e: FirebaseAuthInvalidUserException) {
                            // Usuario no registrado
                            etEmail.error = getString(R.string.error_user_not_found)
                        } catch (e: FirebaseAuthInvalidCredentialsException) {
                            // Contraseña incorrecta
                            etPassword.error = getString(R.string.error_invalid_password)
                        } catch (e: Exception) {
                            // Error desconocido
                            mostrarDialogoError(getString(R.string.txt_modal),getString(R.string.btn_modal))
                        }
                    }
                }

        }


    }

    /**
     * Maneja errores simples que puede tener el usuario al iniciar sesión.
     */
    private fun manejarErrores(email: String, password: String) : Boolean {

        if (email.isEmpty() || !emailValido(email)) {
            etEmail.error = getString(R.string.txt_error_email)
            return true
        }

        if (password.isEmpty() || password.length < longitudContrasenaMax) {
            etPassword.error = getString(R.string.txt_error_password)
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
    private fun mostrarDialogoError(contenido:String,boton:String) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(contenido)
            .setCancelable(false)
            .setPositiveButton(boton) { dialog, _ ->
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }


    private fun sendPasswordResetEmail(email: String) {

        if (email.isEmpty()) {
            mostrarDialogoError(getString(R.string.txt_modal_rc),getString(R.string.btn_modal))
        }
        else {

            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        mostrarDialogoError("Email enviado a $email", getString(R.string.btn_modal))
                    } else {
                        mostrarDialogoError("ERROR", R.string.btn_modal.toString())
                    }
                }

        }
    }



}
