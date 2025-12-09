package pantallas

import SendInvitationRequest
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appcitas.R
import com.example.appcitas.RetrofitClient
import com.example.appcitas.adapters.InvitationAdapter
import com.example.appcitas.adapters.InvitationActionListener
import com.example.appcitas.databinding.ActivityParejaBinding
import com.example.appcitas.model.Invitation
import kotlinx.coroutines.launch

class ParejaActivity : AppCompatActivity(), InvitationActionListener {

    private lateinit var binding: ActivityParejaBinding
    private lateinit var invitationAdapter: InvitationAdapter
    private lateinit var cache: SharedPreferences
    private var currentUserId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParejaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cache = getSharedPreferences("cache", MODE_PRIVATE)

        currentUserId = cache.getLong("id", -1L)

        if (currentUserId == -1L) {
            Toast.makeText(this, "Error Crítico: Usuario no autenticado. Por favor, inicie sesión de nuevo.", Toast.LENGTH_LONG).show()
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
        invitationAdapter = InvitationAdapter(mutableListOf(), this)
        binding.rvInvitaciones.layoutManager = LinearLayoutManager(this)
        binding.rvInvitaciones.adapter = invitationAdapter
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.nav_pareja

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_mis_citas -> {
                    startActivity(Intent(this, MisCitas::class.java))
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    true
                }
                R.id.nav_crear_cita -> {
                    startActivity(Intent(this, CrearCita::class.java))
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    true
                }
                R.id.nav_perfil -> {
                    startActivity(Intent(this, MiPerfil::class.java))
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    true
                }
                R.id.nav_pareja -> true
                else -> false
            }
        }
    }

    private fun sendInvitation(email: String) {
        // Creamos la petición solo con los campos necesarios
        val request = SendInvitationRequest(
            fromUserId = currentUserId,
            toUserId = null,
            toEmail = email
        )

        lifecycleScope.launch {
            try {
                // Asumimos que la API de invitación es la correcta para enviar
                val response = RetrofitClient.invitationApi.sendInvitation(request)

                if (response.isSuccessful) {
                    Toast.makeText(this@ParejaActivity, "Solicitud enviada correctamente", Toast.LENGTH_SHORT).show()
                    binding.etEmailPareja.text?.clear()
                    loadPendingInvitations() // Actualiza la lista por si acaso
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("SEND_INVITATION", "Error al enviar solicitud: ${response.code()} - ${response.message()} - $errorBody")
                    Toast.makeText(this@ParejaActivity, "Error al enviar la solicitud (código ${response.code()})", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Log.e("SEND_INVITATION", "Error de conexión", e)
                Toast.makeText(this@ParejaActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun loadPendingInvitations() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.invitationApi.getInvitationsByUser(currentUserId)

                if (response.isSuccessful) {
                    val invitations = response.body() ?: emptyList()
                    invitationAdapter.updateData(invitations)
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("LOAD_INVITATIONS", "Error al cargar: ${response.code()} - ${response.message()} - $errorBody")
                    Toast.makeText(this@ParejaActivity, "Error al cargar las solicitudes (código ${response.code()})", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Log.e("LOAD_INVITATIONS", "Error de conexión", e)
                Toast.makeText(this@ParejaActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onAcceptInvitation(invitation: Invitation) {
       // Implementar lógica de aceptación aquí
    }

    override fun onRejectInvitation(invitation: Invitation) {
       // Implementar lógica de rechazo aquí
    }
}
