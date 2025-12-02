package pantallas

import CitaFiltroRequest
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.ViewGroup
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
import androidx.core.view.updateLayoutParams
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import com.example.appcitas.R
import com.example.appcitas.RetrofitClient
import com.example.appcitas.databinding.ActivityPantalla1Binding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class Pantalla1 : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding : ActivityPantalla1Binding

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var toolbar: MaterialToolbar

    private lateinit var cache : SharedPreferences
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

        if (auth.currentUser == null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        enableEdgeToEdge()

        binding = ActivityPantalla1Binding.inflate(layoutInflater)
        setContentView(binding.root)

        cache = getSharedPreferences("cache", MODE_PRIVATE)
        binding.saludo.text = "Hola, ${cache.getString("username","")}"

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        drawerLayout = findViewById(R.id.drawerLayout)
        navView = findViewById(R.id.navView)
        toolbar = findViewById(R.id.toolbar)

        ViewCompat.setOnApplyWindowInsetsListener(toolbar) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.open_drawer,
            R.string.close_drawer
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // --- ACTUALIZAR HEADER CON USERNAME ---
        val headerView = navView.getHeaderView(0)
        val usernameTextView = headerView.findViewById<TextView>(R.id.tvNavHeaderUsername)
        val username = cache.getString("username", "Usuario")
        usernameTextView.text = "BIENVENIDO,\n$username!"

        navView.setNavigationItemSelectedListener(this)

        val btnCita = findViewById<Button>(R.id.btnCita)
        val btnAtras = findViewById<Button>(R.id.btnAtras)

        btnAtras.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnCita.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.dialog_filtros, null)
            val dialog = AlertDialog.Builder(this).setView(dialogView).create()
            dialog.show()

            val btnAplicar = dialogView.findViewById<Button>(R.id.btnAplicarFiltros)
            btnAplicar.setOnClickListener {
                val groupCercania = dialogView.findViewById<RadioGroup>(R.id.groupCercania)
                val cercaniaSeleccionada: Int? = when (groupCercania.checkedRadioButtonId) {
                    R.id.cercania_cerca -> 1
                    R.id.cercania_media -> 2
                    R.id.cercania_lejos -> 3
                    else -> null
                }

                val groupDinero = dialogView.findViewById<RadioGroup>(R.id.groupDinero)
                val dineroSeleccionado: Int? = when (groupDinero.checkedRadioButtonId) {
                    R.id.dinero_bajo -> 1
                    R.id.dinero_medio -> 2
                    R.id.dinero_alto -> 3
                    else -> null
                }

                val groupFacilidad = dialogView.findViewById<RadioGroup>(R.id.groupFacilidad)
                val facilidadSeleccionada: Int? = when (groupFacilidad.checkedRadioButtonId) {
                    R.id.facil_facil -> 1
                    R.id.facil_normal -> 2
                    R.id.facil_dificil -> 3
                    else -> null
                }

                val groupIntensidad = dialogView.findViewById<RadioGroup>(R.id.groupIntensidad)
                val intensidadSeleccionada: Int? = when (groupIntensidad.checkedRadioButtonId) {
                    R.id.int_tranqui -> 1
                    R.id.int_normal -> 2
                    R.id.int_intenso -> 3
                    else -> null
                }

                val tempCualquiera = dialogView.findViewById<RadioButton>(R.id.temp_cualquiera)
                val tempVerano = dialogView.findViewById<RadioButton>(R.id.temp_verano)
                val tempInvierno = dialogView.findViewById<RadioButton>(R.id.temp_invierno)
                val tempOtro = dialogView.findViewById<RadioButton>(R.id.temp_otro)

                val temporadaSeleccionada: Int? = when {
                    tempInvierno.isChecked -> 1
                    tempVerano.isChecked   -> 2
                    tempOtro.isChecked     -> 3
                    tempCualquiera.isChecked -> null
                    else -> null
                }

                val filtro = CitaFiltroRequest(
                    temporada = temporadaSeleccionada,
                    dinero = dineroSeleccionado,
                    intensidad = intensidadSeleccionada,
                    cercania = cercaniaSeleccionada,
                    facilidad = facilidadSeleccionada
                )

                aplicarFiltrosYCargarCitas(filtro)
                dialog.dismiss()
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_inicio -> { /* Ya estamos aquí */ }
            R.id.menu_crear_cita -> {
                startActivity(Intent(this, CrearCita::class.java))
            }
            R.id.menu_lista_citas -> {
                startActivity(Intent(this, MisCitas::class.java))
            }
            R.id.menu_perfil -> {
                startActivity(Intent(this, MiPerfil::class.java))
            }
            R.id.menu_cerrar_sesion -> {
                cerrarSesion()
            }
        }
        drawerLayout.closeDrawers()
        return true
    }

    private fun cerrarSesion() {
        FirebaseAuth.getInstance().signOut()
        googleSignInClient.signOut() // Añadido para el logout de Google
        cache.edit().clear().apply()
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
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
                val cita = citas[0]
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