package pantallas

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appcitas.R
import com.example.appcitas.RetrofitClient
import com.example.appcitas.adapter.SolicitudAdapter
import com.example.appcitas.databinding.ActivityParejaBinding
import com.example.appcitas.model.SolicitudPareja
import com.example.appcitas.model.SolicitudRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ParejaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityParejaBinding
    private lateinit var solicitudAdapter: SolicitudAdapter
    private lateinit var cache: SharedPreferences
    private var currentUserId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParejaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cache = getSharedPreferences("cache", MODE_PRIVATE)
        currentUserId = cache.getLong("id", -1L)

        if (currentUserId == -1L) {
            Toast.makeText(this, "Error: Usuario no autenticado.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        setupToolbar()
        setupRecyclerView()
        setupBottomNavigation()

        binding.btnEnviarInvitacion.setOnClickListener {
            val email = binding.etEmailPareja.text.toString()
            if (email.isNotBlank()) {
                sendInvitation(email)
            } else {
                Toast.makeText(this, "Por favor, introduce un email", Toast.LENGTH_SHORT).show()
            }
        }

        loadPendingInvitations()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Pareja"
    }

    private fun setupRecyclerView() {
        // Inicialmente vacía
        solicitudAdapter = SolicitudAdapter(
            emptyList(),
            { solicitud -> aceptarSolicitud(solicitud.id) },
            { solicitud -> rechazarSolicitud(solicitud.id) }
        )
        binding.rvInvitaciones.layoutManager = LinearLayoutManager(this)
        binding.rvInvitaciones.adapter = solicitudAdapter
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.nav_pareja

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_mis_citas -> {
                    startActivity(Intent(this, MisCitas::class.java))
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    finish()
                    true
                }
                R.id.nav_crear_cita -> {
                    startActivity(Intent(this, CrearCita::class.java))
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    finish()
                    true
                }
                R.id.nav_perfil -> {
                    startActivity(Intent(this, MiPerfil::class.java))
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    finish()
                    true
                }
                R.id.nav_pareja -> true
                else -> false
            }
        }
    }

    private fun sendInvitation(email: String) {
        val request = SolicitudRequest(email)
        RetrofitClient.solicitudApi.enviarSolicitud(currentUserId, request)
            .enqueue(object : Callback<SolicitudPareja> {
                override fun onResponse(call: Call<SolicitudPareja>, response: Response<SolicitudPareja>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@ParejaActivity, "Solicitud enviada", Toast.LENGTH_SHORT).show()
                        binding.etEmailPareja.text?.clear()
                    } else {
                        Toast.makeText(this@ParejaActivity, "Error al enviar: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<SolicitudPareja>, t: Throwable) {
                    Toast.makeText(this@ParejaActivity, "Error de red: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun loadPendingInvitations() {
        RetrofitClient.solicitudApi.obtenerSolicitudesRecibidas(currentUserId)
            .enqueue(object : Callback<List<SolicitudPareja>> {
                override fun onResponse(call: Call<List<SolicitudPareja>>, response: Response<List<SolicitudPareja>>) {
                    if (response.isSuccessful && response.body() != null) {
                        val solicitudes = response.body()!!
                        // Recrear el adapter con la nueva lista
                        solicitudAdapter = SolicitudAdapter(
                            solicitudes,
                            { solicitud -> aceptarSolicitud(solicitud.id) },
                            { solicitud -> rechazarSolicitud(solicitud.id) }
                        )
                        binding.rvInvitaciones.adapter = solicitudAdapter
                    } else {
                        Log.e("ParejaActivity", "Error loading requests: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<List<SolicitudPareja>>, t: Throwable) {
                    Log.e("ParejaActivity", "Network error", t)
                }
            })
    }

    private fun aceptarSolicitud(id: Long) {
        RetrofitClient.solicitudApi.aceptarSolicitud(id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@ParejaActivity, "Solicitud aceptada", Toast.LENGTH_SHORT).show()
                    cache.edit().putInt("estado_p", 1).apply()
                    loadPendingInvitations()
                } else {
                    Toast.makeText(this@ParejaActivity, "Error al aceptar", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@ParejaActivity, "Error de red", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun rechazarSolicitud(id: Long) {
        RetrofitClient.solicitudApi.rechazarSolicitud(id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@ParejaActivity, "Solicitud rechazada", Toast.LENGTH_SHORT).show()
                    loadPendingInvitations()
                } else {
                    Toast.makeText(this@ParejaActivity, "Error al rechazar", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@ParejaActivity, "Error de red", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
