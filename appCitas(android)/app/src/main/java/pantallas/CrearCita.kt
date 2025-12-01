package pantallas

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import com.example.appcitas.R
import com.example.appcitas.RetrofitClient
import com.example.appcitas.databinding.ActivityCrearCitaBinding
import com.example.appcitas.model.Cita
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import kotlinx.coroutines.launch

class CrearCita : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var toolbar: MaterialToolbar
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

        if (auth.currentUser == null) {
            Toast.makeText(this, "Sesión no válida.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        drawerLayout = findViewById(R.id.drawerLayout)
        navView = findViewById(R.id.navView)
        toolbar = findViewById(R.id.toolbar)

        ViewCompat.setOnApplyWindowInsetsListener(toolbar) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupToolbarAndDrawer()

        binding.btnCrearCitaGuardar.setOnClickListener {
            guardarCita(idUsuario)
        }
    }

    private fun setupToolbarAndDrawer() {
        setSupportActionBar(toolbar)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val headerView = navView.getHeaderView(0)
        val usernameTextView = headerView.findViewById<TextView>(R.id.tvNavHeaderUsername)
        val username = cache.getString("username", "Usuario")
        usernameTextView.text = "BIENVENIDO,\n$username!"

        navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_inicio -> {
                    startActivity(Intent(this, Pantalla1::class.java))
                    finish()
                }
                R.id.menu_crear_cita -> { /* Ya estamos aquí */ }
                R.id.menu_lista_citas -> {
                    startActivity(Intent(this, MisCitas::class.java))
                }
                R.id.menu_perfil -> {
                    Toast.makeText(this, "Ir a Perfil (Pantalla por crear)", Toast.LENGTH_SHORT).show()
                }
                R.id.menu_cerrar_sesion -> {
                    cerrarSesion()
                }
            }
            drawerLayout.closeDrawers()
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

    private fun obtenerTemporada(): Int = when (binding.groupTemporada.checkedButtonId) {
        R.id.btnInvierno -> 1
        R.id.btnVerano -> 2
        R.id.btnOtono -> 3
        R.id.btnPrimavera -> 4
        else -> 2 // Valor por defecto
    }

    private fun obtenerValorSlider(sliderValue: Float): Int {
        return sliderValue.toInt()
    }

    private fun guardarCita(idUsuario: Long) {
        val titulo = binding.etTituloCita.text.toString().trim()
        val descripcion = binding.etDescripcionCita.text.toString().trim()

        if (titulo.isEmpty() || descripcion.isEmpty()) {
            Toast.makeText(this, "El título y la descripción son obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        val temporada = obtenerTemporada()
        val dinero = obtenerValorSlider(binding.sliderDinero.value)
        val intensidad = obtenerValorSlider(binding.sliderIntensidad.value)
        val cercania = obtenerValorSlider(binding.sliderCercania.value)
        val facilidad = obtenerValorSlider(binding.sliderFacilidad.value)

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