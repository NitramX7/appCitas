package pantallas

import CitaFiltroRequest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appcitas.R
import com.example.appcitas.RetrofitClient
import com.example.appcitas.adapters.CitaActionListener
import com.example.appcitas.adapters.CitasAdapter
import com.example.appcitas.databinding.ActivityMisCitasBinding
import com.example.appcitas.model.Cita
import kotlinx.coroutines.launch
import kotlin.math.sqrt

class MisCitas : AppCompatActivity(), CitaActionListener, SensorEventListener {

    private lateinit var binding: ActivityMisCitasBinding
    private lateinit var citasAdapter: CitasAdapter
    private lateinit var cache: SharedPreferences

    // --- Propiedades para la Búsqueda por Agitación ---
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var filtroSeleccionado: CitaFiltroRequest? = null
    private var lastShakeTime: Long = 0
    private val shakeThreshold = 12f
    private val shakeCooldownMs = 1500L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMisCitasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cache = getSharedPreferences("cache", MODE_PRIVATE)

        setupToolbar()
        setupRecyclerView()
        setupBottomNavigation()
        setupSensor()

        binding.fabBuscarCita.setOnClickListener {
            mostrarDialogoFiltros()
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            val idUsuario = cache.getLong("id", 0L)
            if (idUsuario != 0L) {
                cargarCitasUsuario(idUsuario)
            }
        }

        val idUsuario = cache.getLong("id", 0L)
        if (idUsuario != 0L) {
            cargarCitasUsuario(idUsuario)
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "CitaPlanner"
    }

    private fun setupRecyclerView() {
        citasAdapter = CitasAdapter(mutableListOf(), this)
        binding.rvMisCitas.layoutManager = LinearLayoutManager(this)
        binding.rvMisCitas.adapter = citasAdapter
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.nav_mis_citas // Marca "Inicio" como seleccionado

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_mis_citas -> {
                    // Ya estamos aquí, no hacer nada
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
                else -> false
            }
        }
    }

    private fun setupSensor() {
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    private fun cargarCitasUsuario(idUsuario: Long) {
        binding.swipeRefreshLayout.isRefreshing = true
        lifecycleScope.launch {
            try {
                val filtro = CitaFiltroRequest(creadorId = idUsuario)
                val citas = RetrofitClient.citaApi.filtrarCitas(filtro)

                if (citas.isNotEmpty()) {
                    citasAdapter.updateData(citas)
                    binding.rvMisCitas.visibility = View.VISIBLE
                    binding.layoutSinCitas.visibility = View.GONE
                } else {
                    binding.rvMisCitas.visibility = View.GONE
                    binding.layoutSinCitas.visibility = View.VISIBLE
                }
            } catch (e: Exception) {
                Log.e("CARGAR_CITAS_ERROR", "Error al cargar las citas", e)
                Toast.makeText(this@MisCitas, "Error al obtener tus citas", Toast.LENGTH_LONG).show()
                binding.rvMisCitas.visibility = View.GONE
                binding.layoutSinCitas.visibility = View.VISIBLE
            } finally {
                binding.swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    private fun mostrarDialogoFiltros() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_filtros, null)
        val dialog = AlertDialog.Builder(this).setView(dialogView).create()
        dialog.show()

        dialog.findViewById<Button>(R.id.btnAplicarFiltros)?.setOnClickListener {
            val groupCercania = dialog.findViewById<RadioGroup>(R.id.groupCercania)
            val cercaniaSeleccionada = when (groupCercania?.checkedRadioButtonId) {
                R.id.cercania_cerca -> 1
                R.id.cercania_media -> 2
                R.id.cercania_lejos -> 3
                else -> null
            }

            val groupDinero = dialog.findViewById<RadioGroup>(R.id.groupDinero)
            val dineroSeleccionado = when (groupDinero?.checkedRadioButtonId) {
                R.id.dinero_bajo -> 1
                R.id.dinero_medio -> 2
                R.id.dinero_alto -> 3
                else -> null
            }

            val groupFacilidad = dialog.findViewById<RadioGroup>(R.id.groupFacilidad)
            val facilidadSeleccionada = when (groupFacilidad?.checkedRadioButtonId) {
                R.id.facil_facil -> 1
                R.id.facil_normal -> 2
                R.id.facil_dificil -> 3
                else -> null
            }

            val groupIntensidad = dialog.findViewById<RadioGroup>(R.id.groupIntensidad)
            val intensidadSeleccionada = when (groupIntensidad?.checkedRadioButtonId) {
                R.id.int_tranqui -> 1
                R.id.int_normal -> 2
                R.id.int_intenso -> 3
                else -> null
            }

            val temporadaSeleccionada = when {
                dialog.findViewById<RadioButton>(R.id.temp_invierno)?.isChecked == true -> 1
                dialog.findViewById<RadioButton>(R.id.temp_verano)?.isChecked == true -> 2
                dialog.findViewById<RadioButton>(R.id.temp_otro)?.isChecked == true -> 3
                else -> null
            }

            filtroSeleccionado = CitaFiltroRequest(
                temporada = temporadaSeleccionada,
                dinero = dineroSeleccionado,
                intensidad = intensidadSeleccionada,
                cercania = cercaniaSeleccionada,
                facilidad = facilidadSeleccionada
            )

            Toast.makeText(this, "Filtros aplicados. ¡Agita el móvil para buscar una cita!", Toast.LENGTH_LONG).show()
            dialog.dismiss()
        }
    }

    private fun aplicarFiltrosYCargarCitas(filtro: CitaFiltroRequest) {
        lifecycleScope.launch {
            try {
                val citas = RetrofitClient.citaApi.filtrarCitas(filtro)
                if (citas.isEmpty()) {
                    Toast.makeText(this@MisCitas, "No se han encontrado citas con esos filtros", Toast.LENGTH_SHORT).show()
                    return@launch
                }
                val cita = citas.random()
                val dialogView = layoutInflater.inflate(R.layout.dialog_cita_resultado, null)
                val txtTitulo = dialogView.findViewById<TextView>(R.id.txtTituloDialog)
                val txtDescripcion = dialogView.findViewById<TextView>(R.id.txtDescripcionDialog)
                val txtDetalles = dialogView.findViewById<TextView>(R.id.txtDetallesDialog)
                txtTitulo.text = cita.titulo
                txtDescripcion.text = cita.descripcion
                val cercaniaTxt = when (cita.cercania) { 1 -> "Cerca"; 2 -> "Media"; 3 -> "Lejos"; else -> "-" }
                val dineroTxt = when (cita.dinero) { 1 -> "Bajo"; 2 -> "Medio"; 3 -> "Alto"; else -> "-" }
                val facilidadTxt = when (cita.facilidad) { 1 -> "Fácil"; 2 -> "Normal"; 3 -> "Difícil"; else -> "-" }
                val intensidadTxt = when (cita.intensidad) { 1 -> "Tranqui"; 2 -> "Normal"; 3 -> "Intenso"; else -> "-" }
                val temporadaTxt = when (cita.temporada) { 1 -> "Invierno"; 2 -> "Verano"; 3 -> "Otra"; else -> "Cualquiera" }
                txtDetalles.text = "Cercanía: $cercaniaTxt\nDinero: $dineroTxt\nFacilidad: $facilidadTxt\nIntensidad: $intensidadTxt\nTemporada: $temporadaTxt"
                AlertDialog.Builder(this@MisCitas)
                    .setView(dialogView)
                    .setCancelable(true)
                    .setPositiveButton("Cerrar", null)
                    .show()
            } catch (e: Exception) {
                Log.e("CITAS_FILTRADAS", "Error cargando citas", e)
                Toast.makeText(this@MisCitas, "Error al cargar filtros", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Vuelve a seleccionar el item de "Inicio" para evitar que se quede marcado el de otra pantalla
        binding.bottomNavigation.selectedItemId = R.id.nav_mis_citas
        accelerometer?.also { accel ->
            sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val currentTime = System.currentTimeMillis()
            if ((currentTime - lastShakeTime) > shakeCooldownMs) {
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]

                val acceleration = sqrt(x * x + y * y + z * z)

                if (acceleration > shakeThreshold) {
                    lastShakeTime = currentTime
                    filtroSeleccionado?.let {
                        Log.d("SHAKE_DETECTED", "¡Agitación detectada! Buscando cita con filtro: $it")
                        aplicarFiltrosYCargarCitas(it)
                    }
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) { /* No es necesario */ }

    // --- Métodos del CitaActionListener ---

    override fun onEditarCita(cita: Cita) {
        val intent = Intent(this, EditarCita::class.java).apply {
            putExtra("CITA_ID", cita.id)
        }
        startActivity(intent)
    }

    override fun onEliminarCita(cita: Cita, position: Int) {
        AlertDialog.Builder(this)
            .setTitle("Confirmar Eliminación")
            .setMessage("¿Estás seguro de que quieres eliminar la cita '${cita.titulo}'?")
            .setPositiveButton("Eliminar") { _, _ ->
                eliminarCitaEnServidor(cita, position)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun eliminarCitaEnServidor(cita: Cita, position: Int) {
        lifecycleScope.launch {
            try {
                RetrofitClient.citaApi.eliminarCita(id = cita.id!!)
                Toast.makeText(this@MisCitas, "Cita eliminada", Toast.LENGTH_SHORT).show()
                citasAdapter.removeItem(position)
                if (citasAdapter.itemCount == 0) {
                    binding.rvMisCitas.visibility = View.GONE
                    binding.layoutSinCitas.visibility = View.VISIBLE
                }
            } catch (e: Exception) {
                Log.e("ELIMINAR_CITA_ERROR", "Error al eliminar la cita", e)
                Toast.makeText(this@MisCitas, "Error al eliminar: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
