package com.example.cookify.servicios

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import java.io.ByteArrayOutputStream

class ServicioBase64 {

    companion object {

        fun base64ToBitmap(base64String: String): Bitmap? {
            return try {
                val decodedString = Base64.decode(base64String.trim(), Base64.DEFAULT)
                BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
                null
            }
        }

        fun bitmapToBase64(bitmap: Bitmap): String {
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            return Base64.encodeToString(byteArray, Base64.DEFAULT)
        }

        fun getCompressedBitmapFromUri(contexto: Context, uri: Uri): Bitmap {
            val input = contexto.contentResolver.openInputStream(uri)
            val options = BitmapFactory.Options()
            options.inSampleSize = 4 // Reducir a la mitad el tamaño del bitmap (puedes ajustar este valor según tus necesidades)
            val bitmap = BitmapFactory.decodeStream(input, null, options)
            input?.close()
            return bitmap ?: throw IllegalArgumentException("Bitmap no válido")
        }

        fun resizeBitmap(bitmap: Bitmap, newWidth: Int, newHeight: Int): Bitmap {
            return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
        }


    }

}