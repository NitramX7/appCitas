package pantallas

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.appcitas.R
import com.example.appcitas.RetrofitClient
import com.example.appcitas.databinding.ActivityMainBinding
import com.example.appcitas.model.Usuario
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.security.MessageDigest

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var cache: SharedPreferences
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        cache = getSharedPreferences("cache", MODE_PRIVATE)

        setupGoogleSignIn()

        // Lógica de botones con los IDs actualizados
        binding.btnLogin.setOnClickListener {
            val email = binding.tilEmail.editText?.text.toString()
            val pass = binding.tilPassword.editText?.text.toString()
            loginConEmail(email, pass)
        }

        binding.btnRegister.setOnClickListener {
            val username = binding.tilUsername.editText?.text.toString()
            val email = binding.tilEmail.editText?.text.toString()
            val pass = binding.tilPassword.editText?.text.toString()
            registrarConEmail(username, email, pass)
        }

        binding.btnGoogle.setOnClickListener {
            iniciarSesionConGoogle()
        }

        // Si ya hay un usuario logueado, verificamos su token.
        if (firebaseAuth.currentUser != null) {
            verificarTokenConBackend(firebaseAuth.currentUser)
        }
    }

    private fun setupGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)!!
                    val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
                    firebaseAuth.signInWithCredential(credential)
                        .addOnCompleteListener(this) { authTask ->
                            if (authTask.isSuccessful) {
                                verificarTokenConBackend(authTask.result.user)
                            } else {
                                Toast.makeText(this, "Fallo en la autenticación con Firebase: ${authTask.exception?.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                } catch (e: ApiException) {
                    Toast.makeText(this, "Fallo en el inicio de sesión con Google: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun loginConEmail(email: String, pass: String) {
        if (email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
            return
        }
        firebaseAuth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    verificarTokenConBackend(task.result.user)
                } else {
                    Toast.makeText(this, "Error al iniciar sesión: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun registrarConEmail(username: String, email: String, pass: String) {
        if (username.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
            return
        }
        firebaseAuth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Usuario creado en Firebase, ahora actualizamos su perfil con el username
                    val firebaseUser = task.result.user
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(username)
                        .build()

                    firebaseUser?.updateProfile(profileUpdates)?.addOnCompleteListener { profileTask ->
                        if (profileTask.isSuccessful) {
                            // Perfil actualizado, ahora verificamos con el backend
                            verificarTokenConBackend(firebaseUser)
                        } else {
                            Toast.makeText(this, "Error al guardar el nombre de usuario.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Error al registrar: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun iniciarSesionConGoogle() {
        googleSignInLauncher.launch(googleSignInClient.signInIntent)
    }

    private fun verificarTokenConBackend(firebaseUser: FirebaseUser?) {
        if (firebaseUser == null) {
            Toast.makeText(this, "Error: No se pudo obtener el usuario de Firebase.", Toast.LENGTH_LONG).show()
            return
        }

        firebaseUser.getIdToken(true).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val idToken = task.result.token
                if (idToken != null) {
                    val tokenMap = mapOf("token" to idToken)
                    RetrofitClient.api.verificarToken(tokenMap).enqueue(object : Callback<Usuario> {
                        override fun onResponse(call: Call<Usuario>, response: Response<Usuario>) {
                            if (response.isSuccessful && response.body() != null) {
                                val usuarioBackend = response.body()!!
                                Toast.makeText(this@MainActivity, "¡Bienvenido, ${usuarioBackend.username}!", Toast.LENGTH_SHORT).show()
                                guardarDatosYNavegar(usuarioBackend)
                            } else {
                                Toast.makeText(this@MainActivity, "Error del servidor: ${response.code()}", Toast.LENGTH_LONG).show()
                                cerrarSesion()
                            }
                        }

                        override fun onFailure(call: Call<Usuario>, t: Throwable) {
                            Toast.makeText(this@MainActivity, "Error de conexión: ${t.message}", Toast.LENGTH_LONG).show()
                            cerrarSesion()
                        }
                    })
                }
            } else {
                Toast.makeText(this, "Fallo al obtener el token de Firebase.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun guardarDatosYNavegar(user: Usuario) {
        val username = user.username ?: ""
        val email = user.email ?: ""
        val id = user.id ?: 0L

        cache.edit()
            .putString("username", username)
            .putString("email", email)
            .putLong("id", id)
            .apply()

        navegarAPantallaPrincipal()
    }

    private fun navegarAPantallaPrincipal() {
        val intent = Intent(this, Pantalla1::class.java)
        startActivity(intent)
        finish()
    }

    private fun cerrarSesion() {
        googleSignInClient.signOut().addOnCompleteListener {
            firebaseAuth.signOut()
            cache.edit().clear().apply()
        }
    }

    private fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }
}