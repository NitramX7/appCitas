package com.example.appcitas

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.appcitas.databinding.ActivityCrearCitaBinding
import com.example.appcitas.model.Cita
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import kotlinx.coroutines.launch

class CrearCita : AppCompatActivity() {

    private lateinit var binding: ActivityCrearCitaBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var cache: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCrearCitaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        cache = getSharedPreferences("cache", MODE_PRIVATE)

        val idUsuario = cache.getLong("id", 0L)

        // Doble comprobación de seguridad
        if (auth.currentUser == null) {
            Toast.makeText(this, "Sesión no válida.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnCrearCitaGuardar.setOnClickListener {
            guardarCita(idUsuario)
        }
    }

    private fun obtenerTemporada(): Int =
        when (binding.groupTemporada.checkedButtonId) {
            R.id.btnTemporadaBaja  -> 1
            R.id.btnTemporadaMedia -> 2
            R.id.btnTemporadaAlta  -> 3
            else -> 2
        }

    private fun obtenerDinero(): Int =
        when (binding.groupDinero.checkedButtonId) {
            R.id.btnDineroBajo  -> 1
            R.id.btnDineroMedio -> 2
            R.id.btnDineroAlto  -> 3
            else -> 2
        }

    private fun obtenerIntensidad(): Int =
        when (binding.groupIntensidad.checkedButtonId) {
            R.id.btnIntensidadBaja  -> 1
            R.id.btnIntensidadMedia -> 2
            R.id.btnIntensidadAlta  -> 3
            else -> 2
        }

    private fun obtenerCercania(): Int =
        when (binding.groupCercania.checkedButtonId) {
            R.id.btnCercaniaBaja  -> 3   // Lejos
            R.id.btnCercaniaMedia -> 2   // Normal
            R.id.btnCercaniaAlta  -> 1   // Cerca
            else -> 2
        }

    private fun obtenerFacilidad(): Int =
        when (binding.groupFacilidad.checkedButtonId) {
            R.id.btnFacilidadBaja  -> 3
            R.id.btnFacilidadMedia -> 2
            R.id.btnFacilidadAlta  -> 1
            else -> 2
        }

    private fun guardarCita(idUsuario: Long) {
        val titulo = binding.etTituloCita.text.toString().trim()
        val descripcion = binding.etDescripcionCita.text.toString().trim()

        if (titulo.isEmpty() || descripcion.isEmpty()) {
            Toast.makeText(this, "El título y la descripción son obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        val temporada = obtenerTemporada()
        val dinero = obtenerDinero()
        val intensidad = obtenerIntensidad()
        val cercania = obtenerCercania()
        val facilidad = obtenerFacilidad()

        val nuevaCitaRequest = Cita(
            id = null,
            titulo = titulo,
            descripcion = descripcion,
            temporada = temporada,
            dinero = dinero,
            intensidad = intensidad,
            cercania = cercania,
            facilidad = facilidad,
            creadorId = idUsuario
        )

        Log.d("CREAR_CITA_JSON", Gson().toJson(nuevaCitaRequest))

        lifecycleScope.launch {
            try {
                RetrofitClient.citaApi.crearCita(nuevaCitaRequest)
                Toast.makeText(this@CrearCita, "Cita guardada con éxito", Toast.LENGTH_SHORT).show()
                finish()
            } catch (e: Exception) {
                Log.e("CREAR_CITA_ERROR", "Error al guardar la cita", e)
                Toast.makeText(this@CrearCita, "Error al guardar la cita: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
