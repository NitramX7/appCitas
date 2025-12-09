package pantallas

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.appcitas.R
import com.example.appcitas.databinding.ActivityMiPerfilBinding
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth

class MiPerfil : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMiPerfilBinding
    private lateinit var mMap: GoogleMap
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMiPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Mi Perfil"

        setupBottomNavigation()

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.btnCerrarSesion.setOnClickListener {
            cerrarSesion()
        }
        
        setupCoupleLogic()
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.nav_perfil

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_mis_citas -> {
                    startActivity(Intent(this, MisCitas::class.java))
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    finish() // Cierra esta actividad para no apilarlas
                    true
                }
                R.id.nav_crear_cita -> {
                    startActivity(Intent(this, CrearCita::class.java))
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    finish()
                    true
                }
                 R.id.nav_pareja -> {
                    startActivity(Intent(this, ParejaActivity::class.java))
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    finish()
                    true
                }
                R.id.nav_perfil -> {
                    // Ya estamos aquí
                    true
                }
                else -> false
            }
        }
    }

    private fun cerrarSesion() {
        // 1. Cierra sesión en Firebase y Facebook (síncrono)
        firebaseAuth.signOut()
        LoginManager.getInstance().logOut()

        // 2. Cierra sesión en Google (asíncrono)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(this, gso)

        googleSignInClient.signOut().addOnCompleteListener(this) { task ->
            // 3. Borramos la caché local
            val cache = getSharedPreferences("cache", MODE_PRIVATE)
            cache.edit().clear().apply()

            // 4. Navegamos al Login
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        // Asegura que el ítem de perfil esté seleccionado al volver a esta pantalla
        binding.bottomNavigation.selectedItemId = R.id.nav_perfil
    }

    private fun setupCoupleLogic() {
        val cache = getSharedPreferences("cache", MODE_PRIVATE)
        val estadoP = cache.getInt("estado_p", 0)
        val idPareja = cache.getLong("id_pareja", 0L)
        val userId = cache.getLong("id", 0L)

        val layoutContainer = findViewById<android.widget.LinearLayout>(R.id.layout_pareja_container)
        val tvStatus = findViewById<android.widget.TextView>(R.id.tvParejaStatus)
        val btnVincular = findViewById<com.google.android.material.button.MaterialButton>(R.id.btnVincularPareja)
        val rvSolicitudes = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rvSolicitudes)
        val tvSolicitudesTitle = findViewById<android.widget.TextView>(R.id.tvSolicitudesTitle)

        if (estadoP == 1) {
            tvStatus.text = "Tienes una pareja vinculada (ID: $idPareja)"
            btnVincular.visibility = android.view.View.GONE
        } else {
            tvStatus.text = "No tienes pareja vinculada"
            btnVincular.visibility = android.view.View.VISIBLE
            btnVincular.setOnClickListener {
                mostrarDialogoVincular(userId)
            }
            cargarSolicitudes(userId, rvSolicitudes, tvSolicitudesTitle)
        }
    }

    private fun mostrarDialogoVincular(userId: Long) {
        val input = android.widget.EditText(this)
        input.hint = "Email del usuario"
        
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Vincular con pareja")
            .setMessage("Introduce el email de tu pareja:")
            .setView(input)
            .setPositiveButton("Enviar") { _, _ ->
                val email = input.text.toString()
                if (email.isNotEmpty()) {
                    enviarSolicitud(userId, email)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun enviarSolicitud(userId: Long, email: String) {
        val request = com.example.appcitas.model.SolicitudRequest(email)
        com.example.appcitas.RetrofitClient.solicitudApi.enviarSolicitud(userId, request)
            .enqueue(object : retrofit2.Callback<com.example.appcitas.model.SolicitudPareja> {
                override fun onResponse(call: retrofit2.Call<com.example.appcitas.model.SolicitudPareja>, response: retrofit2.Response<com.example.appcitas.model.SolicitudPareja>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@MiPerfil, "Solicitud enviada", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@MiPerfil, "Error al enviar: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: retrofit2.Call<com.example.appcitas.model.SolicitudPareja>, t: Throwable) {
                    Toast.makeText(this@MiPerfil, "Error de red: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun cargarSolicitudes(userId: Long, rv: androidx.recyclerview.widget.RecyclerView, title: android.widget.TextView) {
        com.example.appcitas.RetrofitClient.solicitudApi.obtenerSolicitudesRecibidas(userId)
            .enqueue(object : retrofit2.Callback<List<com.example.appcitas.model.SolicitudPareja>> {
                override fun onResponse(call: retrofit2.Call<List<com.example.appcitas.model.SolicitudPareja>>, response: retrofit2.Response<List<com.example.appcitas.model.SolicitudPareja>>) {
                    if (response.isSuccessful && response.body() != null) {
                        val solicitudes = response.body()!!
                        if (solicitudes.isNotEmpty()) {
                            title.visibility = android.view.View.VISIBLE
                            rv.visibility = android.view.View.VISIBLE
                            rv.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this@MiPerfil)
                            rv.adapter = com.example.appcitas.adapter.SolicitudAdapter(solicitudes, 
                                { aceptarSolicitud(it.id) },
                                { rechazarSolicitud(it.id) }
                            )
                        } else {
                            title.visibility = android.view.View.GONE
                            rv.visibility = android.view.View.GONE
                        }
                    }
                }
                override fun onFailure(call: retrofit2.Call<List<com.example.appcitas.model.SolicitudPareja>>, t: Throwable) {
                    // Ignorar error silenciosamente o loguear
                }
            })
    }

    private fun aceptarSolicitud(id: Long) {
        com.example.appcitas.RetrofitClient.solicitudApi.aceptarSolicitud(id)
            .enqueue(object : retrofit2.Callback<Void> {
                override fun onResponse(call: retrofit2.Call<Void>, response: retrofit2.Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@MiPerfil, "Solicitud aceptada", Toast.LENGTH_SHORT).show()
                        // Actualizar estado localmente y recargar UI
                        val cache = getSharedPreferences("cache", MODE_PRIVATE)
                        cache.edit().putInt("estado_p", 1).apply()
                        recreate()
                    } else {
                        Toast.makeText(this@MiPerfil, "Error al aceptar", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: retrofit2.Call<Void>, t: Throwable) {
                    Toast.makeText(this@MiPerfil, "Error de red", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun rechazarSolicitud(id: Long) {
        com.example.appcitas.RetrofitClient.solicitudApi.rechazarSolicitud(id)
            .enqueue(object : retrofit2.Callback<Void> {
                override fun onResponse(call: retrofit2.Call<Void>, response: retrofit2.Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@MiPerfil, "Solicitud rechazada", Toast.LENGTH_SHORT).show()
                        recreate()
                    }
                }
                override fun onFailure(call: retrofit2.Call<Void>, t: Throwable) {
                    Toast.makeText(this@MiPerfil, "Error de red", Toast.LENGTH_SHORT).show()
                }
            })
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val madrid = LatLng(40.416775, -3.703790)
        mMap.addMarker(MarkerOptions().position(madrid).title("Marcador en Madrid"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(madrid, 12f))
    }
}
