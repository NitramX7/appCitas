package com.example.appcitas

import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Pantalla1 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pantalla1)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnCita = findViewById<Button>(R.id.btnCita)

        btnCita.setOnClickListener {

            // 1. Inflar la vista del diálogo
            val dialogView = layoutInflater.inflate(R.layout.dialog_filtros, null)

            // 2. Crear el diálogo asdf
            val dialog = AlertDialog.Builder(this)
                .setView(dialogView)
                .create()

            // 3. Mostrarlo
            dialog.show()

            // 4. Botón aplicar dentro del pop-up
            val btnAplicar = dialogView.findViewById<Button>(R.id.btnAplicarFiltros)

            btnAplicar.setOnClickListener {

                // Aquí leeremos los filtros seleccionados
                // Ejemplo:
                val groupCercania = dialogView.findViewById<RadioGroup>(R.id.groupCercania)
                val cercaniaSeleccionada = when (groupCercania.checkedRadioButtonId) {
                    R.id.cercania_cerca -> "CERCA"
                    R.id.cercania_media -> "MEDIA"
                    R.id.cercania_lejos -> "LEJOS"
                    else -> null
                }

                // TODO: Hacer lo mismo con dinero, facilidad, intensidad, etc.

                dialog.dismiss()
            }
        } //hola

    }
}