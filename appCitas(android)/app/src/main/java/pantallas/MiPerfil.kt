package pantallas

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.example.appcitas.R
import com.example.appcitas.databinding.ActivityMiPerfilBinding
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
    private lateinit var cache: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMiPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cache = getSharedPreferences("cache", MODE_PRIVATE)

        setupToolbarAndDrawer()

        // --- ACTUALIZAR HEADER CON USERNAME ---
        val headerView = binding.navView.getHeaderView(0)
        val usernameTextViewHeader = headerView.findViewById<TextView>(R.id.tvNavHeaderUsername)
        val username = cache.getString("username", "Usuario")
        usernameTextViewHeader.text = "BIENVENIDO,\n$username!"

        // --- ACTUALIZAR TARJETA DE PERFIL CON USERNAME ---
        binding.main.findViewById<TextView>(R.id.tvUsername).text = "Nombre de usuario: $username"

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun setupToolbarAndDrawer() {
        setSupportActionBar(binding.toolbar)
        val toggle = ActionBarDrawerToggle(
            this, binding.drawerLayout, binding.toolbar, R.string.open_drawer, R.string.close_drawer
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_inicio -> {
                    startActivity(Intent(this, Pantalla1::class.java))
                    finish()
                }
                R.id.menu_crear_cita -> {
                    startActivity(Intent(this, CrearCita::class.java))
                }
                R.id.menu_lista_citas -> {
                    startActivity(Intent(this, MisCitas::class.java))
                }
                R.id.menu_perfil -> { /* Ya estamos aquí */ }
                R.id.menu_cerrar_sesion -> {
                    cerrarSesion()
                }
            }
            binding.drawerLayout.closeDrawers()
            true
        }
    }

    private fun cerrarSesion() {
        FirebaseAuth.getInstance().signOut()
        cache.edit().clear().apply()
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val madrid = LatLng(40.416775, -3.703790)
        mMap.addMarker(MarkerOptions().position(madrid).title("Marcador en Madrid"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(madrid, 12f))
    }
}
