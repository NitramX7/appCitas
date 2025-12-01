package pantallas

import CitaFiltroRequest
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.appcitas.R
import com.example.appcitas.RetrofitClient
import com.example.appcitas.adapters.CitaActionListener
import com.example.appcitas.adapters.CitasAdapter
import com.example.appcitas.model.Cita
import com.google.android.material.navigation.NavigationView
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.launch

class MisCitas : AppCompatActivity(), CitaActionListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var toolbar: MaterialToolbar
    private lateinit var rvMisCitas: RecyclerView
    private lateinit var layoutSinCitas: View
    private lateinit var citasAdapter: CitasAdapter
    private lateinit var cache: SharedPreferences
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mis_citas)

        drawerLayout = findViewById(R.id.drawerLayout)
        navView = findViewById(R.id.navView)
        toolbar = findViewById(R.id.toolbar)
        rvMisCitas = findViewById(R.id.rvMisCitas)
        layoutSinCitas = findViewById(R.id.layoutSinCitas)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        cache = getSharedPreferences("cache", MODE_PRIVATE)

        // --- AÑADIENDO LA SOLUCIÓN AQUÍ ---
        ViewCompat.setOnApplyWindowInsetsListener(toolbar) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupToolbarAndDrawer()
        setupRecyclerView()

        swipeRefreshLayout.setOnRefreshListener {
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
                        finish()
                    }
                }
                R.id.menu_crear_cita -> {
                    startActivity(Intent(this, CrearCita::class.java))
                }
                R.id.menu_lista_citas -> { /* Ya estamos aquí */ }
            }
            drawerLayout.closeDrawers()
            true
        }
    }

    private fun setupRecyclerView() {
        citasAdapter = CitasAdapter(mutableListOf(), this)
        rvMisCitas.layoutManager = LinearLayoutManager(this)
        rvMisCitas.adapter = citasAdapter
    }

    private fun cargarCitasUsuario(idUsuario: Long) {
        swipeRefreshLayout.isRefreshing = true
        lifecycleScope.launch {
            try {
                val filtro = CitaFiltroRequest(creadorId = idUsuario)
                val citas = RetrofitClient.citaApi.filtrarCitas(filtro)

                if (citas.isNotEmpty()) {
                    citasAdapter.updateData(citas)
                    rvMisCitas.visibility = View.VISIBLE
                    layoutSinCitas.visibility = View.GONE
                } else {
                    rvMisCitas.visibility = View.GONE
                    layoutSinCitas.visibility = View.VISIBLE
                }
            } catch (e: Exception) {
                Log.e("CARGAR_CITAS_ERROR", "Error al cargar las citas", e)
                Toast.makeText(this@MisCitas, "Error al obtener tus citas", Toast.LENGTH_LONG).show()
                rvMisCitas.visibility = View.GONE
                layoutSinCitas.visibility = View.VISIBLE
            } finally {
                swipeRefreshLayout.isRefreshing = false
            }
        }
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
        lifecycleScope.launch {
            try {
                RetrofitClient.citaApi.eliminarCita(id = cita.id!!)
                Toast.makeText(this@MisCitas, "Cita eliminada", Toast.LENGTH_SHORT).show()
                citasAdapter.removeItem(position)
                if (citasAdapter.itemCount == 0) {
                    rvMisCitas.visibility = View.GONE
                    layoutSinCitas.visibility = View.VISIBLE
                }
            } catch (e: Exception) {
                Log.e("ELIMINAR_CITA_ERROR", "Error al eliminar la cita", e)
                Toast.makeText(this@MisCitas, "Error al eliminar: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}