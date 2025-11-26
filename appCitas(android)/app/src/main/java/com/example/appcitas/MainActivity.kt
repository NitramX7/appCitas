package com.example.appcitas

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.appcitas.databinding.ActivityMainBinding
import java.security.MessageDigest


class MainActivity : AppCompatActivity() {

    private lateinit var cache : SharedPreferences
    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        cache = getSharedPreferences("cache", MODE_PRIVATE)
        var iniciarSesion = binding.iniciarSes
        var crearUser = binding.crearUser




        iniciarSesion.setOnClickListener {

        }

        crearUser.setOnClickListener {


        }
    }

    private fun enviarDatos() {

        val username = binding.user.text.toString().trim()
        val email = binding.email.text.toString().trim()
        val passPlain = binding.pass.text.toString().trim()

        if (username.isEmpty() || email.isEmpty() || passPlain.isEmpty()) {
            Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
            return
        }




      
        cache.edit().putString("username", username)
            .putString("email", email)
            .apply()

        val ventanaEnvio = Intent(this, Pantalla1::class.java)
        startActivity(ventanaEnvio)



    }





}