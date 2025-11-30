package pantallas

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.appcitas.LoginRequest
import com.example.appcitas.R
import com.example.appcitas.RetrofitClient
import com.example.appcitas.databinding.ActivityMainBinding
import com.example.appcitas.model.Usuario
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.security.MessageDigest
import java.util.UUID

class MainActivity : AppCompatActivity() {

    private lateinit var cache: SharedPreferences
    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        firebaseAuth = FirebaseAuth.getInstance()
        cache = getSharedPreferences("cache", MODE_PRIVATE)

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
                    firebaseAuthWithGoogle(account.idToken!!) {
                        handleBackendForGoogleUser(account.email!!, account.displayName ?: "Usuario de Google")
                    }
                } catch (e: ApiException) {
                    Toast.makeText(this, "Fallo en el inicio de sesión con Google: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

        if (firebaseAuth.currentUser != null) {
            navegarAPantallaPrincipal(firebaseAuth.currentUser!!.email!!, firebaseAuth.currentUser!!.displayName ?: "Usuario")
        }

        binding.iniciarSes.setOnClickListener {
            iniciarSesionFirebase()
            iniciarSesion()
        }

        binding.crearUser.setOnClickListener {
            registrarUsuarioFirebase()
            registrarUsuario()
        }

        binding.googleSignInButton.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            googleSignInLauncher.launch(signInIntent)
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String, onSuccess: () -> Unit) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Autenticación con Google exitosa", Toast.LENGTH_SHORT).show()
                    onSuccess()
                } else {
                    Toast.makeText(this, "Fallo en la autenticación con Firebase: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun handleBackendForGoogleUser(email: String, displayName: String) {
        val randomPassword = UUID.randomUUID().toString()
        val passwordHash = hashPassword(randomPassword)

        val usuario = Usuario(
            id = null,
            username = displayName,
            email = email,
            password = passwordHash,
            nombre = displayName
        )

        // Llamamos al endpoint de registrar/loguear con Google
        RetrofitClient.api.registrar(usuario).enqueue(object : Callback<Usuario> {
            override fun onResponse(call: Call<Usuario>, response: Response<Usuario>) {
                // --- LÓGICA MEJORADA ---
                if (response.isSuccessful) {
                    val usuarioRecibido = response.body()
                    if (usuarioRecibido != null) {
                        Toast.makeText(this@MainActivity, "Bienvenido, ${usuarioRecibido.username}", Toast.LENGTH_LONG).show()
                        enviarDatos(usuarioRecibido) // Inicia sesión con los datos del servidor
                    }
                } else {
                    // Manejar errores del servidor (ej. 500 Internal Server Error)
                    Toast.makeText(this@MainActivity, "Error del servidor: ${response.code()}", Toast.LENGTH_LONG).show()
                    firebaseAuth.signOut() // Desloguear si el backend falla
                }
            }

            override fun onFailure(call: Call<Usuario>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error de conexión: ${t.message}", Toast.LENGTH_LONG).show()
                firebaseAuth.signOut()
            }
        })
    }

    private fun navegarAPantallaPrincipal(email: String, username: String) {
        cache.edit()
            .putString("email", email)
            .putString("username", username)
            .apply()
        val intent = Intent(this, Pantalla1::class.java)
        startActivity(intent)
        finish()
    }

    private fun enviarDatos(user : Usuario) {
        val username = user.username.toString()
        val email = user.email.toString()
        val id = user.id!!.toLong()

        cache.edit().putString("username", username)
            .putString("email", email)
            .putLong("id", id)
            .apply()

        val ventanaEnvio = Intent(this, Pantalla1::class.java)
        startActivity(ventanaEnvio)
        finish()
    }

    private fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }
    
    private fun registrarUsuarioFirebase() {
        val email = binding.email.text.toString().trim()
        val pass = binding.pass.text.toString().trim()

        if (email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        firebaseAuth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Usuario registrado en Firebase", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Error al registrar en Firebase: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun iniciarSesionFirebase() {
        val email = binding.email.text.toString().trim()
        val pass = binding.pass.text.toString().trim()

        if (email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        firebaseAuth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = task.result?.user
                    navegarAPantallaPrincipal(user?.email!!, user?.displayName ?: binding.user.text.toString())
                } else {
                    Toast.makeText(this, "Error al iniciar sesión en Firebase: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun cerrarSesion() {
        googleSignInClient.signOut().addOnCompleteListener {
            firebaseAuth.signOut()
            cache.edit().clear().apply()
        }
    }

    private fun registrarUsuario() {
        val username = binding.user.text.toString().trim()
        val email = binding.email.text.toString().trim()
        val passPlain = binding.pass.text.toString().trim()

        if (username.isEmpty() || email.isEmpty() || passPlain.isEmpty()) {
            Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val password = hashPassword(passPlain)
        val usuario = Usuario(id = null, username = username, email = email, password = password, nombre = null)

        RetrofitClient.api.registrar(usuario)
            .enqueue(object : Callback<Usuario> {
                override fun onResponse(call: Call<Usuario>, response: Response<Usuario>) {
                    if (response.isSuccessful){
                        Toast.makeText(this@MainActivity, "Usuario Registrado en tu BBDD", Toast.LENGTH_SHORT).show()
                    } else {
                         Toast.makeText(this@MainActivity, "Fallo al registrar en tu BBDD: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<Usuario>, t: Throwable) {
                    Toast.makeText(this@MainActivity, "Error de red (Retrofit): ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun iniciarSesion() {
        val email = binding.email.text.toString().trim()
        val passPlain = binding.pass.text.toString().trim()

        if (email.isEmpty() || passPlain.isEmpty()) {
            Toast.makeText(this, "Introduce email y contraseña", Toast.LENGTH_SHORT).show()
            return
        }

        val passwordHash = hashPassword(passPlain)
        val loginRequest = LoginRequest(email = email, password = passwordHash)

        RetrofitClient.api.login(loginRequest)
            .enqueue(object : Callback<Usuario> {
                override fun onResponse(call: Call<Usuario>, response: Response<Usuario>) {
                    if (response.isSuccessful){
                        val user = response.body()
                        if(user != null){
                            enviarDatos(user)
                        }
                    } else {
                        Toast.makeText(this@MainActivity, "Error en login de tu BBDD: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<Usuario>, t: Throwable) {
                     Toast.makeText(this@MainActivity, "Error de red en login (Retrofit): ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}