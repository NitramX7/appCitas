package pantallas

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.appcitas.R
import com.example.appcitas.databinding.ActivityMiPerfilBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MiPerfil : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMiPerfilBinding
    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMiPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Mi Perfil"

        setupBottomNavigation()

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
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
                R.id.nav_perfil -> {
                    // Ya estamos aquí
                    true
                }
                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Asegura que el ítem de perfil esté seleccionado al volver a esta pantalla
        binding.bottomNavigation.selectedItemId = R.id.nav_perfil
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val madrid = LatLng(40.416775, -3.703790)
        mMap.addMarker(MarkerOptions().position(madrid).title("Marcador en Madrid"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(madrid, 12f))
    }
}
