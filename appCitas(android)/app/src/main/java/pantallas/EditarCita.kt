package pantallas

import CitaFiltroRequest
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.appcitas.R
import com.example.appcitas.RetrofitClient
import com.example.appcitas.databinding.ActivityEditarCitaBinding
import com.example.appcitas.model.Cita
import com.example.appcitas.CitaFiltroRequest
import kotlinx.coroutines.launch

class EditarCita : AppCompatActivity() {

    private lateinit var binding: ActivityEditarCitaBinding
    private var citaId: Long? = null
    private var citaActual: Cita? = null // Para guardar la cita cargada y usarla al actualizar

    private lateinit var cache: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditarCitaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cache = getSharedPreferences("cache", MODE_PRIVATE)

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
                // Usamos el endpoint de búsqueda para obtener la cita por su ID
                val cita = RetrofitClient.citaApi.buscarCitaId(CitaFiltroRequest(id = citaId!!)).first()
                citaActual = cita // Guardamos la cita en la variable de la clase

                binding.etTituloCita.setText(cita.titulo)
                binding.etDescripcionCita.setText(cita.descripcion)

                // Establecemos los botones de los filtros según los datos de la cita
                binding.groupTemporada.check(when (cita.temporada) {
                    1 -> R.id.btnTemporadaBaja
                    3 -> R.id.btnTemporadaAlta
                    else -> R.id.btnTemporadaMedia // Por defecto si es 2 o null
                })

                binding.groupDinero.check(when (cita.dinero) {
                    1 -> R.id.btnDineroBajo
                    3 -> R.id.btnDineroAlto
                    else -> R.id.btnDineroMedio
                })

                binding.groupIntensidad.check(when (cita.intensidad) {
                    1 -> R.id.btnIntensidadBaja
                    3 -> R.id.btnIntensidadAlta
                    else -> R.id.btnIntensidadMedia
                })

                binding.groupCercania.check(when (cita.cercania) {
                    1 -> R.id.btnCercaniaAlta  // Cerca
                    3 -> R.id.btnCercaniaBaja   // Lejos
                    else -> R.id.btnCercaniaMedia
                })

                binding.groupFacilidad.check(when (cita.facilidad) {
                    1 -> R.id.btnFacilidadAlta  // Fácil
                    3 -> R.id.btnFacilidadBaja   // Difícil
                    else -> R.id.btnFacilidadMedia
                })

            } catch (e: Exception) {
                Toast.makeText(this@EditarCita, "Error al cargar los datos de la cita: ${e.message}", Toast.LENGTH_SHORT).show()
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

        fun obtenerTemporada(): Int = when (binding.groupTemporada.checkedButtonId) { R.id.btnTemporadaBaja -> 1; R.id.btnTemporadaMedia -> 2; R.id.btnTemporadaAlta -> 3; else -> 2 }
        fun obtenerDinero(): Int = when (binding.groupDinero.checkedButtonId) { R.id.btnDineroBajo -> 1; R.id.btnDineroMedio -> 2; R.id.btnDineroAlto -> 3; else -> 2 }
        fun obtenerIntensidad(): Int = when (binding.groupIntensidad.checkedButtonId) { R.id.btnIntensidadBaja -> 1; R.id.btnIntensidadMedia -> 2; R.id.btnIntensidadAlta -> 3; else -> 2 }
        fun obtenerCercania(): Int = when (binding.groupCercania.checkedButtonId) { R.id.btnCercaniaBaja -> 3; R.id.btnCercaniaMedia -> 2; R.id.btnCercaniaAlta -> 1; else -> 2 }
        fun obtenerFacilidad(): Int = when (binding.groupFacilidad.checkedButtonId) { R.id.btnFacilidadBaja -> 3; R.id.btnFacilidadMedia -> 2; R.id.btnFacilidadAlta -> 1; else -> 2 }

        val citaActualizada = Cita(
            id = citaId!!,
            titulo = titulo,
            descripcion = descripcion,
            temporada = obtenerTemporada(),
            dinero = obtenerDinero(),
            intensidad = obtenerIntensidad(),
            cercania = obtenerCercania(),
            facilidad = obtenerFacilidad(),
            creadorId = citaActual?.creadorId // Corregido: Usamos el id del creador de la cita cargada
        )

        lifecycleScope.launch {
            try {
                // Asumo que tienes un endpoint para actualizar una cita
                RetrofitClient.citaApi.actualizarCita(citaId!!, citaActualizada)
                Toast.makeText(this@EditarCita, "Cita actualizada con éxito", Toast.LENGTH_SHORT).show()
                finish() // Volver a la lista
            } catch (e: Exception) {
                Toast.makeText(this@EditarCita, "Error al actualizar la cita", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
