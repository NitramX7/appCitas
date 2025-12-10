package pantallas

import CitaFiltroRequest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
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

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var filtroSeleccionado: CitaFiltroRequest? = null
    private var lastShakeTime: Long = 0
    private val shakeThreshold = 10f
    private val shakeCooldownMs = 1500L

    companion object {
        const val CHANNEL_ID = "citas_channel"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMisCitasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cache = getSharedPreferences("cache", MODE_PRIVATE)

        createNotificationChannel()
        createNotificationChannel()
        // setupToolbar() // Removed as we have a custom header now
        setupRecyclerView()
        setupBottomNavigation()
        setupSensor()
        setupUserInfo()

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

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Notificaciones de Citas"
            val descriptionText = "Canal para notificaciones de la app de citas"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun setupUserInfo() {
        val username = cache.getString("username", "Usuario")
        binding.tvUsername.text = username
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
                R.id.nav_pareja -> {
                    startActivity(Intent(this, ParejaActivity::class.java))
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
                val response = RetrofitClient.citaApi.filtrarCitas(filtro)

                if (response.isSuccessful) {
                    val citas = response.body()
                    if (!citas.isNullOrEmpty()) {
                        citasAdapter.updateData(citas)
                        binding.rvMisCitas.visibility = View.VISIBLE
                        binding.layoutSinCitas.visibility = View.GONE
                    } else {
                        binding.rvMisCitas.visibility = View.GONE
                        binding.layoutSinCitas.visibility = View.VISIBLE
                    }
                } else {
                    Toast.makeText(this@MisCitas, "Error al obtener tus citas: ${response.message()}", Toast.LENGTH_LONG).show()
                    binding.rvMisCitas.visibility = View.GONE
                    binding.layoutSinCitas.visibility = View.VISIBLE
                }
            } catch (e: Exception) {
                Log.e("CARGAR_CITAS_ERROR", "Error de conexión al cargar las citas", e)
                Toast.makeText(this@MisCitas, "Error de conexión", Toast.LENGTH_LONG).show()
                binding.rvMisCitas.visibility = View.GONE
                binding.layoutSinCitas.visibility = View.VISIBLE
            } finally {
                binding.swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    private fun setupClearableRadioGroup(radioGroup: RadioGroup) {
        val radioButtons = (0 until radioGroup.childCount).map { radioGroup.getChildAt(it) as RadioButton }
        for (radioButton in radioButtons) {
            radioButton.setOnClickListener {
                if (radioButton.tag != null) {
                    radioGroup.clearCheck()
                    radioButton.tag = null
                } else {
                    radioButtons.forEach { it.tag = null }
                    radioButton.tag = true
                }
            }
        }
    }

    private fun mostrarDialogoFiltros() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_search_wizard, null)
        val dialog = AlertDialog.Builder(this).setView(dialogView).create()
        dialog.window?.setBackgroundDrawable(android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT))
        dialog.show()

        val flipper = dialogView.findViewById<android.widget.ViewFlipper>(R.id.wizardFlipper)
        val tvTitle = dialogView.findViewById<TextView>(R.id.tvWizardTitle)
        val btnNext = dialogView.findViewById<Button>(R.id.btnWizardNext)
        val btnBack = dialogView.findViewById<Button>(R.id.btnWizardBack)

        // UI Components
        val chipGroupTemporada = dialogView.findViewById<com.google.android.material.chip.ChipGroup>(R.id.chipGroupTemporada)
        val sliderDinero = dialogView.findViewById<com.google.android.material.slider.Slider>(R.id.sliderDinero)
        val sliderIntensidad = dialogView.findViewById<com.google.android.material.slider.Slider>(R.id.sliderIntensidad)
        val sliderCercania = dialogView.findViewById<com.google.android.material.slider.Slider>(R.id.sliderCercania)
        val sliderFacilidad = dialogView.findViewById<com.google.android.material.slider.Slider>(R.id.sliderFacilidad)

        var currentStep = 0
        val totalSteps = 5
        val titles = listOf("Temporada", "Dinero", "Intensidad", "Cercanía", "Facilidad")

        fun updateUI() {
            flipper.displayedChild = currentStep
            tvTitle.text = "Paso ${currentStep + 1}/$totalSteps: ${titles[currentStep]}"
            
            btnBack.visibility = if (currentStep == 0) View.INVISIBLE else View.VISIBLE
            btnNext.text = if (currentStep == totalSteps - 1) "¡Buscar!" else "Siguiente"
        }

        btnNext.setOnClickListener {
            if (currentStep < totalSteps - 1) {
                currentStep++
                updateUI()
            } else {
                // Collect Data
                val temporadaSeleccionada = when (chipGroupTemporada?.checkedChipId) {
                    R.id.chip_temp_invierno -> 1
                    R.id.chip_temp_verano -> 2
                    R.id.chip_temp_otono -> 3
                    R.id.chip_temp_primavera -> 4
                    else -> null
                }
                
                // Sliders return float (1.0, 2.0, 3.0), convert to Int
                val dineroSeleccionado = sliderDinero?.value?.toInt()
                val intensidadSeleccionada = sliderIntensidad?.value?.toInt()
                val cercaniaSeleccionada = sliderCercania?.value?.toInt()
                val facilidadSeleccionada = sliderFacilidad?.value?.toInt()

                filtroSeleccionado = CitaFiltroRequest(
                    temporada = temporadaSeleccionada,
                    dinero = dineroSeleccionado,
                    intensidad = intensidadSeleccionada,
                    cercania = cercaniaSeleccionada,
                    facilidad = facilidadSeleccionada
                )

                Toast.makeText(this, "Filtros aplicados. ¡Buscando...!", Toast.LENGTH_SHORT).show()
                filtroSeleccionado?.let { aplicarFiltrosYCargarCitas(it) }
                dialog.dismiss()
            }
        }

        btnBack.setOnClickListener {
            if (currentStep > 0) {
                currentStep--
                updateUI()
            }
        }
        
        // Initialize
        updateUI()
    }

    private fun aplicarFiltrosYCargarCitas(filtro: CitaFiltroRequest) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.citaApi.filtrarCitas(filtro)
                if (response.isSuccessful) {
                    val citas = response.body()
                    if (!citas.isNullOrEmpty()) {
                        val cita = citas.random()
                        mostrarDialogoDetallesCita(cita)
                    } else {
                        Toast.makeText(this@MisCitas, "No se han encontrado citas con esos filtros", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@MisCitas, "Error al aplicar los filtros: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("CITAS_FILTRADAS", "Error de conexión al cargar citas", e)
                Toast.makeText(this@MisCitas, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
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

    fun onCitaClick(cita: Cita) {
        mostrarDialogoDetallesCita(cita)
    }

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
        val citaId = cita.id ?: run {
            Toast.makeText(this, "Error: la cita no tiene un ID válido", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.citaApi.eliminarCita(citaId)
                if (response.isSuccessful) {
                    Toast.makeText(this@MisCitas, "Cita eliminada", Toast.LENGTH_SHORT).show()
                    citasAdapter.removeItem(position)
                    if (citasAdapter.itemCount == 0) {
                        binding.rvMisCitas.visibility = View.GONE
                        binding.layoutSinCitas.visibility = View.VISIBLE
                    }
                } else {
                    Toast.makeText(this@MisCitas, "Error al eliminar la cita: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("ELIMINAR_CITA", "Error de conexión al eliminar la cita", e)
                Toast.makeText(this@MisCitas, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun mostrarDialogoDetallesCita(cita: Cita) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_cita_resultado, null)
        val dialog = AlertDialog.Builder(this@MisCitas).setView(dialogView).create()
        dialog.show()

        dialog.findViewById<ImageButton>(R.id.btnCloseDialog)?.setOnClickListener {
            dialog.dismiss()
        }

        val txtTitulo = dialogView.findViewById<TextView>(R.id.txtTituloDialog)
        val txtDescripcion = dialogView.findViewById<TextView>(R.id.txtDescripcionDialog)
        val txtDetalles = dialogView.findViewById<TextView>(R.id.txtDetallesDialog)

        txtTitulo.text = cita.titulo
        txtDescripcion.text = cita.descripcion
        txtDetalles.text = buildString {
            append("Cercanía: ${mapCercania(cita.cercania)}\n")
            append("Dinero: ${mapDinero(cita.dinero)}\n")
            append("Facilidad: ${mapFacilidad(cita.facilidad)}\n")
            append("Intensidad: ${mapIntensidad(cita.intensidad)}\n")
            append("Temporada: ${mapTemporada(cita.temporada)}")
        }
    }

    private fun mapCercania(valor: Int?): String = when (valor) { 1 -> "Lej -> Cerca"; 2 -> "Lej -> Normal"; 3 -> "Lejanía -> Lejos"; else -> "N/A" }
    private fun mapDinero(valor: Int?): String = when (valor) { 1 -> "Gratis"; 2 -> "Poco"; 3 -> "Mucho"; else -> "N/A" }
    private fun mapFacilidad(valor: Int?): String = when (valor) { 1 -> "Fácil"; 2 -> "Normal"; 3 -> "Difícil"; else -> "N/A" }
    private fun mapIntensidad(valor: Int?): String = when (valor) { 1 -> "Int-> Tranqui"; 2 -> "Int-> Normal"; 3 -> "Int-> Intenso"; else -> "N/A" }
    private fun mapTemporada(valor: Int?): String = when (valor) { 1 -> "Invierno"; 2 -> "Verano"; 3 -> "Otro"; else -> "N/A" }
}
