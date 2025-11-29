package pantallas

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appcitas.APIS.CitaApi
import com.example.appcitas.R
import com.example.appcitas.model.Cita
import com.google.android.material.navigation.NavigationView
import com.google.android.material.appbar.MaterialToolbar


class MisCitas : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var toolbar: MaterialToolbar
    private lateinit var rvMisCitas: RecyclerView
    private lateinit var layoutSinCitas: View

    private lateinit var cache : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mis_citas)

        drawerLayout = findViewById(R.id.drawerLayout)
        navView = findViewById(R.id.navView)
        toolbar = findViewById(R.id.toolbar)
        rvMisCitas = findViewById(R.id.rvMisCitas)
        layoutSinCitas = findViewById(R.id.layoutSinCitas)

        cache = getSharedPreferences("cache", MODE_PRIVATE)

        var idUser = cache.getLong("id", 0L)

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

        navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {

                R.id.menu_inicio -> {
                    // Navega a Pantalla1 si no estamos ya en ella
                    if (this !is Pantalla1) {
                        startActivity(Intent(this, Pantalla1::class.java))
                    }
                }

                R.id.menu_crear_cita -> {
                    // Navega a CrearCita si no estamos ya allí
                    startActivity(Intent(this, CrearCita::class.java))
                }

                R.id.menu_lista_citas -> {
                    // Ya estamos en esta pantalla, no hacemos nada.
                }
            }

            drawerLayout.closeDrawers()
            true

            cargarCitasUsuario(idUser)
        }
        private void cargarCitasUsuario(long idUser){

        }

    }
}