package pantallas

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.appcitas.R
import com.example.appcitas.RetrofitClient
import com.example.appcitas.databinding.ActivityEditarCitaBinding
import com.example.appcitas.model.Cita
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class EditarCita : AppCompatActivity() {

    private lateinit var binding: ActivityEditarCitaBinding
    private var citaId: Long? = null
    private var citaActual: Cita? = null

    private lateinit var cache: SharedPreferences
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditarCitaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        cache = getSharedPreferences("cache", MODE_PRIVATE)

        // Custom back button logic (toolbar removed/customized)
        binding.btnBack.setOnClickListener {
             onBackPressedDispatcher.onBackPressed()
        }

        // Drawer handling removed/simplified to just focus on form for now
        // If drawer is needed, we need to bind standard toolbar or similar, but layout uses custom layout.
        
        citaId = intent.getLongExtra("CITA_ID", -1)

        if (citaId == -1L) {
            Toast.makeText(this, "Error: ID de cita no válido", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        cargarDatosDeLaCita()

        binding.btnGuardarCambios.setOnClickListener {
            actualizarCita()
        }
    }

    private fun cargarDatosDeLaCita() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.citaApi.getCita(citaId!!)
                if (response.isSuccessful) {
                    val citaRecibida = response.body()
                    if (citaRecibida != null) {
                        citaActual = citaRecibida
                        binding.etTituloCita.setText(citaRecibida.titulo)
                        binding.etDescripcionCita.setText(citaRecibida.descripcion)
                        
                        // Temporada (RadioGroup)
                        binding.groupTemporada.check(when (citaRecibida.temporada) { 
                            1 -> R.id.btnTemporadaBaja 
                            3 -> R.id.btnTemporadaAlta 
                            else -> R.id.btnTemporadaMedia 
                        })
                        
                        // Sliders (Values 1-3)
                        binding.sliderDinero.value = citaRecibida.dinero?.toFloat() ?: 2.0f
                        binding.sliderIntensidad.value = citaRecibida.intensidad?.toFloat() ?: 2.0f
                        binding.sliderCercania.value = citaRecibida.cercania?.toFloat() ?: 2.0f
                        binding.sliderFacilidad.value = citaRecibida.facilidad?.toFloat() ?: 2.0f
                        
                    } else {
                        Toast.makeText(this@EditarCita, "La cita no fue encontrada.", Toast.LENGTH_LONG).show()
                        finish()
                    }
                } else {
                    Toast.makeText(this@EditarCita, "Error al cargar la cita: ${response.code()}", Toast.LENGTH_LONG).show()
                    finish()
                }
            } catch (e: Exception) {
                Toast.makeText(this@EditarCita, "Error de conexión: ${e.message}", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    private fun actualizarCita() {
        val titulo = binding.etTituloCita.text.toString().trim()
        val descripcion = binding.etDescripcionCita.text.toString().trim()
        if (titulo.isEmpty()) {
            Toast.makeText(this, "El título no puede estar vacío", Toast.LENGTH_SHORT).show()
            return
        }
        
        val temporada = when (binding.groupTemporada.checkedRadioButtonId) { 
            R.id.btnTemporadaBaja -> 1
            R.id.btnTemporadaAlta -> 3
            else -> 2 
        }
        
        val dinero = binding.sliderDinero.value.toInt()
        val intensidad = binding.sliderIntensidad.value.toInt()
        val cercania = binding.sliderCercania.value.toInt()
        val facilidad = binding.sliderFacilidad.value.toInt()
        
        val creadorId = citaActual?.creadorId
        if (creadorId == null) {
            Toast.makeText(this, "Error: No se pudo identificar al creador de la cita.", Toast.LENGTH_LONG).show()
            return
        }
        val citaActualizada = Cita(id = citaId!!, titulo = titulo, descripcion = descripcion, temporada = temporada, dinero = dinero, intensidad = intensidad, cercania = cercania, facilidad = facilidad, creadorId = creadorId)
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.citaApi.actualizarCita(citaId!!, citaActualizada)
                if (response.isSuccessful) {
                    Toast.makeText(this@EditarCita, "Cita actualizada con éxito", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@EditarCita, "Error al actualizar: ${response.code()}", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@EditarCita, "Error al actualizar: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}