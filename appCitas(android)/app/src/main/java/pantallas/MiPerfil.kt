package pantallas

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.appcitas.R
import com.example.appcitas.databinding.ActivityMiPerfilBinding

import com.google.android.libraries.maps.CameraUpdateFactory
import com.google.android.libraries.maps.GoogleMap
import com.google.android.libraries.maps.OnMapReadyCallback
import com.google.android.libraries.maps.SupportMapFragment
import com.google.android.libraries.maps.model.LatLng
import com.google.android.libraries.maps.model.MarkerOptions

class MiPerfil : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMiPerfilBinding
    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMiPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configura la Toolbar
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener {
            // Lógica para el botón de navegación (menú hamburguesa)
            // Por ahora, simplemente cerramos la actividad.
            finish()
        }

        // Obtiene el SupportMapFragment y notifica cuando el mapa está listo.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Se llama cuando el mapa está listo para ser usado.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Añade un marcador de ejemplo en Madrid y mueve la cámara
        val madrid = LatLng(40.416775, -3.703790)
        mMap.addMarker(MarkerOptions().position(madrid).title("Marcador en Madrid"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(madrid, 12f))
    }
}
