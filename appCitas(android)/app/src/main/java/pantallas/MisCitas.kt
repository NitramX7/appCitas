package pantallas

import CitaFiltroRequest
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appcitas.APIS.CitaApi
import com.example.appcitas.R
import com.example.appcitas.RetrofitClient

import com.example.appcitas.adapters.CitasAdapter
import com.example.appcitas.model.Cita
import com.google.android.material.navigation.NavigationView
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.launch

class MisCitas : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var toolbar: MaterialToolbar
    private lateinit var rvMisCitas: RecyclerView
    private lateinit var layoutSinCitas: View
    private lateinit var citasAdapter: CitasAdapter
    private lateinit var cache: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mis_citas)

        // 1. Inicializar vistas
        drawerLayout = findViewById(R.id.drawerLayout)
        navView = findViewById(R.id.navView)
        toolbar = findViewById(R.id.toolbar)
        rvMisCitas = findViewById(R.id.rvMisCitas)
        layoutSinCitas = findViewById(R.id.layoutSinCitas)
        cache = getSharedPreferences("cache", MODE_PRIVATE)

        // 2. Configurar Toolbar y Navegación
        setupToolbarAndDrawer()

        // 3. Configurar RecyclerView
        setupRecyclerView()

        // 4. Cargar las citas del usuario
        val idUsuario = cache.getLong("id", 0L)
        if (idUsuario != 0L) {
            cargarCitasUsuario(idUsuario)
        }
    }

    private fun setupToolbarAndDrawer() {
        setSupportActionBar(toolbar)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_inicio -> {
                    if (this !is Pantalla1) {
                        startActivity(Intent(this, Pantalla1::class.java))
                        finish() // Cierra esta actividad para no apilarlas
                    }
                }
                R.id.menu_crear_cita -> {
                    startActivity(Intent(this, CrearCita::class.java))
                }
                R.id.menu_lista_citas -> { /* No hacer nada, ya estamos aquí */ }
            }
            drawerLayout.closeDrawers()
            true
        }
    }

    private fun setupRecyclerView() {
        citasAdapter = CitasAdapter(emptyList()) // Inicializa con lista vacía
        rvMisCitas.layoutManager = LinearLayoutManager(this)
        rvMisCitas.adapter = citasAdapter
    }

    private fun cargarCitasUsuario(idUsuario: Long) {
        lifecycleScope.launch {
            try {
                // Usamos el endpoint de filtrar, pasando el ID del creador
                val filtro = CitaFiltroRequest(creadorId = idUsuario)
                val citas = RetrofitClient.citaApi.filtrarCitas(filtro)

                if (citas.isNotEmpty()) {
                    // Si hay citas, actualiza el adapter y muestra el RecyclerView
                    citasAdapter.updateData(citas)
                    rvMisCitas.visibility = View.VISIBLE
                    layoutSinCitas.visibility = View.GONE
                } else {
                    // Si no hay citas, muestra el layout de estado vacío
                    rvMisCitas.visibility = View.GONE
                    layoutSinCitas.visibility = View.VISIBLE
                }

            } catch (e: Exception) {
                Log.e("CARGAR_CITAS_ERROR", "Error al cargar las citas del usuario", e)
                Toast.makeText(this@MisCitas, "Error al obtener tus citas: ${e.message}", Toast.LENGTH_LONG).show()
                // En caso de error, también mostramos el estado vacío
                rvMisCitas.visibility = View.GONE
                layoutSinCitas.visibility = View.VISIBLE
            }
        }
    }
}