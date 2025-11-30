package pantallas

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.appcitas.RetrofitClient
import com.example.appcitas.databinding.ActivityEditarCitaBinding
import com.example.appcitas.model.Cita
import kotlinx.coroutines.launch

class EditarCita : AppCompatActivity() {

    private lateinit var binding: ActivityEditarCitaBinding
    private var citaId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditarCitaBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

                val cita = RetrofitClient.citaApi.getCitaPorId(citaId!!)
                binding.etTituloCita.setText(cita.titulo)
                binding.etDescripcionCita.setText(cita.descripcion)
                // Aquí deberías establecer también los RadioButtons de los filtros

            } catch (e: Exception) {
                Toast.makeText(this@EditarCita, "Error al cargar los datos de la cita", Toast.LENGTH_SHORT).show()
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

        val citaActualizada = Cita(
            id = citaId!!,
            titulo = titulo,
            descripcion = descripcion,
            // Aquí deberías recoger los valores actualizados de los filtros
            temporada = 1, // Ejemplo
            dinero = 1, // Ejemplo
            intensidad = 1, // Ejemplo
            cercania = 1, // Ejemplo
            facilidad = 1, // Ejemplo
            creadorId = null // El creador no debería cambiar
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