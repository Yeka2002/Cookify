package com.example.cookify.componentes

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.cookify.R
import com.example.cookify.servicios.TokenManagerService
import com.example.cookify.ui.LoginActivity
import com.google.firebase.auth.FirebaseAuth

class ComponenteModalSalir : DialogFragment() {

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.componente_modal_salir, null)

            builder.setView(view)
                .setPositiveButton(R.string.confirmar_salir,
                    DialogInterface.OnClickListener { dialog, id ->
                        // Aquí puedes ejecutar la acción al confirmar la salida
                        // Por ejemplo, puedes cerrar la actividad actual
                        auth.signOut()

                        //Elimino el token
                        context?.let { it1 -> TokenManagerService(it1) }?.clearUserToken()

                        val intent = Intent(activity, LoginActivity::class.java)
                        startActivity(intent)
                    })
                .setNegativeButton(R.string.cancelar,
                    DialogInterface.OnClickListener { dialog, id ->
                        // Cancelar la acción, cerrar el diálogo
                        dialog.cancel()
                    })
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}
