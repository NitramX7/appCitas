package com.example.appcitas

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.appcitas.databinding.ActivityCrearCitaBinding
import com.example.appcitas.model.Cita
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class CrearCita : AppCompatActivity() {

    private lateinit var binding: ActivityCrearCitaBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCrearCitaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

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
            //guardarCita()
        }
    }

    // TEMPORADA
    private fun obtenerTemporada(): Int? =
        when (binding.groupTemporada.checkedButtonId) {
            R.id.temp_invierno -> 1
            R.id.temp_verano   -> 2
            R.id.temp_otro     -> 3
            R.id.temp_cualquiera -> null
            else -> null
        }

    // DINERO
    private fun obtenerDinero(): Int? =
        when (binding.groupDinero.checkedButtonId) {
            R.id.dinero_bajo  -> 1
            R.id.dinero_medio -> 2
            R.id.dinero_alto  -> 3
            else -> null
        }

    // INTENSIDAD
    private fun obtenerIntensidad(): Int? =
        when (binding.groupIntensidad.checkedButtonId) {
            R.id.int_tranqui -> 1
            R.id.int_normal  -> 2
            R.id.int_intenso -> 3
            else -> null
        }

    // CERCANÍA
    private fun obtenerCercania(): Int? =
        when (binding.groupCercania.checkedButtonId) {
            R.id.cercania_cerca -> 1
            R.id.cercania_media -> 2
            R.id.cercania_lejos -> 3
            else -> null
        }

    // FACILIDAD
    private fun obtenerFacilidad(): Int? =
        when (binding.groupFacilidad.checkedButtonId) {
            R.id.facil_facil   -> 1
            R.id.facil_normal  -> 2
            R.id.facil_dificil -> 3
            else -> null
        }


    /* private fun guardarCita() {
         val titulo = binding.etTituloCita.text.toString().trim()
         val descripcion = binding.etDescripcionCita.text.toString().trim()
         val creadorId = auth.currentUser?.uid

         // Validación de datos
         if (titulo.isEmpty() || descripcion.isEmpty()) {
             Toast.makeText(this, "El título y la descripción son obligatorios", Toast.LENGTH_SHORT).show()
             return
         }

         if (creadorId == null) {
             Toast.makeText(this, "Error de autenticación. Intente iniciar sesión de nuevo.", Toast.LENGTH_LONG).show()
             return
         }

         // Recoger valores de los botones
         val temporada = obtenerTemporada()
         val dinero = obtenerDinero()
         val intensidad = obtenerIntensidad()
         val cercania = obtenerCercania()
         val facilidad = obtenerFacilidad()


         // Crear el objeto para la petición
         val nuevaCitaRequest = Cita(
             titulo = titulo,
             descripcion = descripcion,
             temporada = temporada,
             dinero = dinero,
             intensidad = intensidad,
             cercania = cercania,
             facilidad = facilidad,
             //creadorId = creadorId
         )

        // Llamar a la API usando una corrutina
        lifecycleScope.launch {
            try {
                RetrofitClient.citaApi.crearCita(nuevaCitaRequest)
                // Éxito
                Toast.makeText(this@CrearCita, "Cita guardada con éxito", Toast.LENGTH_SHORT).show()
                finish() // Cierra la pantalla y vuelve a Pantalla1
            } catch (e: Exception) {
                // Error
                Toast.makeText(this@CrearCita, "Error al guardar la cita: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }*/

    // Función de ayuda para obtener el texto del botón seleccionado
    private fun obtenerValorBoton(group: MaterialButtonToggleGroup): String? {
        if (group.checkedButtonId == -1) return null // Ninguno seleccionado
        val button = findViewById<MaterialButton>(group.checkedButtonId)
        return button.text.toString()
    }
}