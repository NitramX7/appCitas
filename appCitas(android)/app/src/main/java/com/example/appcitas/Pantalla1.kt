package com.example.appcitas

import CitaFiltroRequest
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import com.example.appcitas.databinding.ActivityMainBinding
import com.example.appcitas.databinding.ActivityPantalla1Binding
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class Pantalla1 : AppCompatActivity() {
    private lateinit var binding : ActivityPantalla1Binding


    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var toolbar: MaterialToolbar

    private lateinit var cache : SharedPreferences

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pantalla1)

        binding = ActivityPantalla1Binding.inflate(layoutInflater)
        setContentView(binding.root)

        cache = getSharedPreferences("cache", MODE_PRIVATE)
        val username = cache.getString("username", "")
        val id = cache.getLong("id", 0L)

        binding.saludo.text = "Hola, ${cache.getString("username","")}"





        auth = FirebaseAuth.getInstance()

        // --- ¡AÑADE ESTA COMPROBACIÓN AQUÍ! ---
        if (auth.currentUser == null) {
            // Si no hay sesión, no dejes que el usuario vea esta pantalla.
            // Redirige al Login.
            val intent = Intent(this, MainActivity::class.java) // <-- CAMBIA LoginActivity por el nombre de tu Activity de Login
            startActivity(intent)
            finish() // Cierra Pantalla1 para que no se quede en el historial
            return   // Detiene la ejecución para no inflar la vista innecesariamente
        }
        enableEdgeToEdge()


        // ─────────────────────────────
        // 1. REFERENCIAS DEL DRAWER
        // ─────────────────────────────
        drawerLayout = findViewById(R.id.drawerLayout)
        navView = findViewById(R.id.navView)
        toolbar = findViewById(R.id.toolbar)



        // Toolbar como ActionBar
        setSupportActionBar(toolbar)

        // Botón hamburguesa
        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.open_drawer,
            R.string.close_drawer
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Listener del menú lateral
        navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_crear_cita -> {
                    // Ir a la pantalla de crear cita
                    val intent = Intent(this, CrearCita::class.java)
                    startActivity(intent)
                }
                R.id.menu_lista_citas -> {
                    // TODO: cuando tengas la lista de citas
                    Toast.makeText(this, "Mis citas (pendiente)", Toast.LENGTH_SHORT).show()
                }
                R.id.menu_ajustes -> {
                    Toast.makeText(this, "Ajustes (pendiente)", Toast.LENGTH_SHORT).show()
                }
            }
            drawerLayout.closeDrawers()
            true
        }

        // Opcional: mantener lo del edge-to-edge si quieres
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // ─────────────────────────────
        // 2. TUS BOTONES ORIGINALES
        // ─────────────────────────────
        val btnCita = findViewById<Button>(R.id.btnCita)
        val btnAtras = findViewById<Button>(R.id.btnAtras)

        btnAtras.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnCita.setOnClickListener {

            // 1. Inflar la vista del diálogo
            val dialogView = layoutInflater.inflate(R.layout.dialog_filtros, null)

            // 2. Crear el diálogo
            val dialog = AlertDialog.Builder(this)
                .setView(dialogView)
                .create()

            // 3. Mostrarlo
            dialog.show()

            // 4. Botón aplicar dentro del pop-up
            val btnAplicar = dialogView.findViewById<Button>(R.id.btnAplicarFiltros)

            btnAplicar.setOnClickListener {

                // ============================
                //   CERCANÍA
                // ============================
                val groupCercania = dialogView.findViewById<RadioGroup>(R.id.groupCercania)
                val cercaniaSeleccionada: Int? = when (groupCercania.checkedRadioButtonId) {
                    R.id.cercania_cerca -> 1
                    R.id.cercania_media -> 2
                    R.id.cercania_lejos -> 3
                    else -> null
                }

                // ============================
                //   DINERO
                // ============================
                val groupDinero = dialogView.findViewById<RadioGroup>(R.id.groupDinero)
                val dineroSeleccionado: Int? = when (groupDinero.checkedRadioButtonId) {
                    R.id.dinero_bajo -> 1
                    R.id.dinero_medio -> 2
                    R.id.dinero_alto -> 3
                    else -> null
                }

                // ============================
                //   FACILIDAD
                // ============================
                val groupFacilidad = dialogView.findViewById<RadioGroup>(R.id.groupFacilidad)
                val facilidadSeleccionada: Int? = when (groupFacilidad.checkedRadioButtonId) {
                    R.id.facil_facil -> 1
                    R.id.facil_normal -> 2
                    R.id.facil_dificil -> 3
                    else -> null
                }

                // ============================
                //   INTENSIDAD
                // ============================
                val groupIntensidad = dialogView.findViewById<RadioGroup>(R.id.groupIntensidad)
                val intensidadSeleccionada: Int? = when (groupIntensidad.checkedRadioButtonId) {
                    R.id.int_tranqui -> 1
                    R.id.int_normal -> 2
                    R.id.int_intenso -> 3
                    else -> null
                }

                // ============================
                //   TEMPORADA
                // ============================
                val tempCualquiera = dialogView.findViewById<RadioButton>(R.id.temp_cualquiera)
                val tempVerano = dialogView.findViewById<RadioButton>(R.id.temp_verano)
                val tempInvierno = dialogView.findViewById<RadioButton>(R.id.temp_invierno)
                val tempOtro = dialogView.findViewById<RadioButton>(R.id.temp_otro)

                val temporadaSeleccionada: Int? = when {
                    tempInvierno.isChecked -> 1
                    tempVerano.isChecked   -> 2
                    tempOtro.isChecked     -> 3
                    tempCualquiera.isChecked -> null   // no filtrar por temporada
                    else -> null
                }

                // ============================
                //   ARMAR OBJETO FILTRO
                // ============================
                val filtro = CitaFiltroRequest(
                    temporada = temporadaSeleccionada,
                    dinero = dineroSeleccionado,
                    intensidad = intensidadSeleccionada,
                    cercania = cercaniaSeleccionada,
                    facilidad = facilidadSeleccionada
                )

                // ============================
                //   LLAMADA A LA API
                // ============================
                aplicarFiltrosYCargarCitas(filtro)

                // ============================
                //   CERRAR POP-UP
                // ============================
                dialog.dismiss()
            }

        }
    }

    private fun aplicarFiltrosYCargarCitas(filtro: CitaFiltroRequest) {
        lifecycleScope.launch {
            try {
                val citas = RetrofitClient.citaApi.filtrarCitas(filtro)

                Log.d("CITAS_FILTRADAS", "Resultado: $citas")

                if (citas.isEmpty()) {
                    Toast.makeText(this@Pantalla1, "No se han encontrado citas", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val cita = citas[0]   // primera cita recomendada

                val dialogView = layoutInflater.inflate(R.layout.dialog_cita_resultado, null)

                val txtTitulo = dialogView.findViewById<TextView>(R.id.txtTituloDialog)
                val txtDescripcion = dialogView.findViewById<TextView>(R.id.txtDescripcionDialog)
                val txtDetalles = dialogView.findViewById<TextView>(R.id.txtDetallesDialog)

                txtTitulo.text = cita.titulo
                txtDescripcion.text = cita.descripcion

                val cercaniaTxt = when (cita.cercania) {
                    1 -> "Cerca"
                    2 -> "Media"
                    3 -> "Lejos"
                    else -> "-"
                }

                val dineroTxt = when (cita.dinero) {
                    1 -> "Bajo"
                    2 -> "Medio"
                    3 -> "Alto"
                    else -> "-"
                }

                val facilidadTxt = when (cita.facilidad) {
                    1 -> "Fácil"
                    2 -> "Normal"
                    3 -> "Difícil"
                    else -> "-"
                }

                val intensidadTxt = when (cita.intensidad) {
                    1 -> "Tranqui"
                    2 -> "Normal"
                    3 -> "Intenso"
                    else -> "-"
                }

                val temporadaTxt = when (cita.temporada) {
                    1 -> "Invierno"
                    2 -> "Verano"
                    3 -> "Otra"
                    else -> "Cualquiera"
                }

                txtDetalles.text =
                    "Cercanía: $cercaniaTxt\n" +
                            "Dinero: $dineroTxt\n" +
                            "Facilidad: $facilidadTxt\n" +
                            "Intensidad: $intensidadTxt\n" +
                            "Temporada: $temporadaTxt"

                AlertDialog.Builder(this@Pantalla1)
                    .setView(dialogView)
                    .setCancelable(true)
                    .setPositiveButton("Cerrar", null)
                    .show()

            } catch (e: Exception) {
                Log.e("CITAS_FILTRADAS", "Error cargando citas", e)
                Toast.makeText(this@Pantalla1, "Error al cargar filtros", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
