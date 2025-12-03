package pantallas

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.appcitas.R
import com.example.appcitas.RetrofitClient
import com.example.appcitas.databinding.ActivityMainBinding
import com.example.appcitas.model.Usuario
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.OAuthCredential
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
    private lateinit var callbackManager: CallbackManager

    companion object {
        private const val TAG = "MainActivity"
        private const val PENDING_FB_TOKEN_KEY = "pending_facebook_token"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        cache = getSharedPreferences("cache", MODE_PRIVATE)

        setupGoogleSignIn()
        setupFacebookSignIn()

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

        googleSignInLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode != RESULT_OK) {
                    // Si el usuario cancela, limpiamos cualquier token pendiente de vinculación
                    cache.edit().remove(PENDING_FB_TOKEN_KEY).apply()
                    return@registerForActivityResult
                }

                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)!!
                    val googleCredential =
                        GoogleAuthProvider.getCredential(account.idToken!!, null)

                    // ¿Estamos en flujo de vinculación Facebook -> Google?
                    val pendingFacebookToken = cache.getString(PENDING_FB_TOKEN_KEY, null)

                    if (pendingFacebookToken != null) {
                        // Limpiamos el token para no reutilizarlo
                        cache.edit().remove(PENDING_FB_TOKEN_KEY).apply()

                        val facebookCredential =
                            FacebookAuthProvider.getCredential(pendingFacebookToken)

                        // 1) Iniciamos sesión con Google (cuenta ya existente)
                        firebaseAuth.signInWithCredential(googleCredential)
                            .addOnSuccessListener { authResult ->
                                val user = authResult.user
                                if (user == null) {
                                    Toast.makeText(
                                        this,
                                        "Error: usuario de Google nulo.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    return@addOnSuccessListener
                                }

                                // 2) Vinculamos Facebook a ese usuario
                                user.linkWithCredential(facebookCredential)
                                    .addOnSuccessListener { linkResult ->
                                        Toast.makeText(
                                            this,
                                            "Tu cuenta de Facebook ha sido vinculada.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        verificarTokenConBackend(linkResult.user)
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(
                                            this,
                                            "Error al vincular: ${e.message}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        // Seguimos al menos con el usuario de Google
                                        verificarTokenConBackend(user)
                                    }
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(
                                    this,
                                    "Error al iniciar sesión con Google: ${e.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                    } else {
                        // ⭐ Flujo normal de inicio de sesión con Google
                        firebaseAuth.signInWithCredential(googleCredential)
                            .addOnSuccessListener { authResult ->
                                verificarTokenConBackend(authResult.user)
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(
                                    this,
                                    "Fallo en la autenticación con Firebase: ${e.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                    }

                } catch (e: ApiException) {
                    cache.edit().remove(PENDING_FB_TOKEN_KEY).apply()
                    Toast.makeText(
                        this,
                        "Fallo en el inicio de sesión con Google: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }


    private fun setupFacebookSignIn() {
        callbackManager = CallbackManager.Factory.create()
        binding.loginButton.setReadPermissions("email", "public_profile")
        binding.loginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d(TAG, "Facebook login onSuccess: $loginResult")
                handleFacebookAccessToken(loginResult.accessToken)
            }

            override fun onCancel() {
                Log.d(TAG, "Facebook login onCancel")
                Toast.makeText(this@MainActivity, "Inicio de sesión con Facebook cancelado.", Toast.LENGTH_SHORT).show()
            }

            override fun onError(exception: FacebookException) {
                Log.e(TAG, "Facebook login onError", exception)
                Toast.makeText(this@MainActivity, "Error en el inicio de sesión con Facebook: ${exception.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(TAG, "handleFacebookAccessToken: ${token.token}")
        val facebookCredential = FacebookAuthProvider.getCredential(token.token)

        firebaseAuth.signInWithCredential(facebookCredential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // ✅ Facebook logueado / registrado sin problemas
                    Log.d(TAG, "Firebase auth with Facebook successful: ${task.result.user?.uid}")
                    verificarTokenConBackend(task.result.user)
                } else {
                    Log.e(TAG, "Firebase auth with Facebook failed", task.exception)

                    if (task.exception is FirebaseAuthUserCollisionException) {
                        // ❗ Ya existe una cuenta con ese email (Google o password)
                        // Guardamos el token de Facebook para usarlo después al vincular
                        cache.edit()
                            .putString(PENDING_FB_TOKEN_KEY, token.token)
                            .apply()

                        // Cerramos sesión de Facebook para evitar líos
                        LoginManager.getInstance().logOut()

                        AlertDialog.Builder(this)
                            .setTitle("Vincular Cuentas")
                            .setMessage(
                                "Ya tienes una cuenta con este email. " +
                                        "Para usar Facebook, primero debes vincular las cuentas iniciando sesión con Google."
                            )
                            .setPositiveButton("Vincular con Google") { _, _ ->
                                iniciarSesionConGoogle()
                            }
                            .setNegativeButton("Cancelar") { dialog, _ ->
                                cache.edit().remove(PENDING_FB_TOKEN_KEY).apply()
                                dialog.dismiss()
                            }
                            .setCancelable(false)
                            .show()

                    } else {
                        Toast.makeText(
                            this,
                            "Fallo en la autenticación con Firebase: ${task.exception?.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
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
                    val firebaseUser = task.result.user
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(username)
                        .build()

                    firebaseUser?.updateProfile(profileUpdates)?.addOnCompleteListener { profileTask ->
                        if (profileTask.isSuccessful) {
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
            Log.e(TAG, "verificarTokenConBackend: firebaseUser is null")
            Toast.makeText(this, "Error: No se pudo obtener el usuario de Firebase.", Toast.LENGTH_LONG).show()
            return
        }
        Log.d(TAG, "Verificando token con el backend para el usuario: ${firebaseUser.uid}")

        firebaseUser.getIdToken(true).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val idToken = task.result.token
                if (idToken != null) {
                    Log.d(TAG, "Token de Firebase obtenido con éxito.")
                    val tokenMap = mapOf("token" to idToken)
                    RetrofitClient.api.verificarToken(tokenMap).enqueue(object : Callback<Usuario> {
                        override fun onResponse(call: Call<Usuario>, response: Response<Usuario>) {
                            if (response.isSuccessful && response.body() != null) {
                                val usuarioBackend = response.body()!!
                                Log.d(TAG, "Verificación del backend exitosa para el usuario: ${usuarioBackend.username}")
                                Toast.makeText(this@MainActivity, "¡Bienvenido, ${usuarioBackend.username}!", Toast.LENGTH_SHORT).show()
                                guardarDatosYNavegar(usuarioBackend)
                            } else {
                                Log.e(TAG, "La verificación del backend falló. Código: ${response.code()}, Mensaje: ${response.message()}")
                                Toast.makeText(this@MainActivity, "Error del servidor: ${response.code()}", Toast.LENGTH_LONG).show()
                                cerrarSesion()
                            }
                        }

                        override fun onFailure(call: Call<Usuario>, t: Throwable) {
                            Log.e(TAG, "La conexión con el backend falló", t)
                            Toast.makeText(this@MainActivity, "Error de conexión con el servidor: ${t.message}", Toast.LENGTH_LONG).show()
                            cerrarSesion()
                        }
                    })

                } else {
                    Log.e(TAG, "El token de Firebase es nulo.")
                }
            } else {
                Log.e(TAG, "Fallo al obtener el token de Firebase.", task.exception)
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
        val intent = Intent(this, MisCitas::class.java)
        startActivity(intent)
        finish()
    }

    private fun cerrarSesion() {
        // Cierra sesión en todos los proveedores
        firebaseAuth.signOut()
        googleSignInClient.signOut()
        LoginManager.getInstance().logOut()
        cache.edit().clear().apply()
    }

    private fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
