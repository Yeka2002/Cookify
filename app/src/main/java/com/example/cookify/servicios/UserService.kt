import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.example.cookify.modelo.Reporte
import com.example.cookify.modelo.Usuario
import com.example.cookify.retrofit.RetrofitClient
import com.example.cookify.servicios.TokenManagerService
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class UserService {
    companion object {

        private var auth: FirebaseAuth = FirebaseAuth.getInstance()

        fun getToken(onTokenReceived: (String?) -> Unit, context : Context) {
            val user = auth.currentUser
            user?.getIdToken(true)?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result?.token

                    //Guardo el token en mis preferencias para mantener la sesión.
                    context.let {
                        if (token != null) {
                            TokenManagerService(it).saveUserToken(token)
                        }
                    }

                    onTokenReceived(token)
                } else {
                    // Manejar el error al obtener el token
                    onTokenReceived(null)
                }
            }
        }

        @SuppressLint("UseSwitchCompatOrMaterialCode")
        fun cargarPerfil(token: String, onSuccess: (Usuario?) -> Unit, onFailure: (Throwable) -> Unit){

            val retrofitService = RetrofitClient.usuarioApi
            val call = retrofitService.getProfile("Bearer $token")

            call.enqueue(object : Callback<Usuario> {
                override fun onResponse(call: Call<Usuario>, response: Response<Usuario>) {
                    if (response.isSuccessful) {
                        val usuarioIniciado = response.body()
                        onSuccess(usuarioIniciado)
                    } else {
                        onFailure(Exception("Error en la respuesta del servidor"))
                    }
                }

                override fun onFailure(call: Call<Usuario>, t: Throwable) {
                    onFailure(t)
                }
            })
        }

        @SuppressLint("UseSwitchCompatOrMaterialCode")
        fun usuarioModificado(base64 : String, nombre : String, descripcion : String, privacidad : Boolean) : Usuario{
            return Usuario(0,"","",nombre,base64,descripcion,privacidad)
        }

        @SuppressLint("UseSwitchCompatOrMaterialCode")
        fun editarPerfil(token:String, usuario: Usuario, onSuccess: (Usuario?) -> Unit, onFailure: (Throwable) -> Unit){
            val retrofitService = RetrofitClient.usuarioApi
            val call = retrofitService.updateProfile("Bearer $token", usuario)

            call.enqueue(object : Callback<Usuario> {
                override fun onResponse(call: Call<Usuario>, response: Response<Usuario>) {
                    if (response.isSuccessful) {
                        onSuccess(response.body())
                    } else {
                        onFailure(Exception("Error en la respuesta del servidor"))
                    }
                }

                override fun onFailure(call: Call<Usuario>, t: Throwable) {
                    onFailure(t)
                }
            })
        }

        fun searchUsers(name: String, onSuccess: (List<Usuario?>) -> Unit, onFailure: (Throwable) -> Unit) {
            val call = RetrofitClient.usuarioApi.searchUsers(name)
            call.enqueue(object : Callback<List<Usuario>> {
                override fun onResponse(call: Call<List<Usuario>>, response: Response<List<Usuario>>) {
                    if (response.isSuccessful) {
                        val users = response.body()
                        if (users != null) onSuccess(users)
                    }
                }
                override fun onFailure(call: Call<List<Usuario>>, t: Throwable) {
                    onFailure(t)
                }
            })
        }

    }
}
