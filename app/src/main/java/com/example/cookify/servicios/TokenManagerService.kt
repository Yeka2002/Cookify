package com.example.cookify.servicios

import android.content.Context
import android.content.SharedPreferences

class TokenManagerService(context: Context) {

    companion object {
        private const val PREF_NAME = "UserTokenPrefs"
        private const val KEY_USER_TOKEN = "userToken"
    }

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    // Método para guardar el token de usuario
    fun saveUserToken(token: String) {
        with(sharedPreferences.edit()) {
            putString(KEY_USER_TOKEN, token)
            apply()
        }
    }

    // Método para obtener el token de usuario
    fun getUserToken(): String? {
        return sharedPreferences.getString(KEY_USER_TOKEN, null)
    }

    // Método para eliminar el token de usuario
    fun clearUserToken() {
        with(sharedPreferences.edit()) {
            remove(KEY_USER_TOKEN)
            apply()
        }
    }

    // Método para comprobar si hay un token guardado
    fun hasUserToken(): Boolean {
        return sharedPreferences.contains(KEY_USER_TOKEN)
    }
}