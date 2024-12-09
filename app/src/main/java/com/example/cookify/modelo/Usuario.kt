package com.example.cookify.modelo

import java.io.Serializable

data class Usuario(
    val id: Long,
    val firebaseUid: String,
    val email: String,
    val name: String,
    var photoUrl: String,
    val description: String,
    val privacy: Boolean
) : Serializable
