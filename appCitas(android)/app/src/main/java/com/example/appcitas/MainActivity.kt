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
            iniciarSesion()
        }

        crearUser.setOnClickListener {
            registrarUsuario()

        }
    }
    private fun hashPassword(password: String): String {
        val bytes = MessageDigest
            .getInstance("SHA-256")
            .digest(password.toByteArray(Charsets.UTF_8))

        return bytes.joinToString("") { "%02x".format(it) }
    }
    private fun enviarDatos() {

        val username = binding.user.text.toString().trim()
        val email = binding.email.text.toString().trim()
        val passPlain = binding.pass.text.toString().trim()

        if (username.isEmpty() || email.isEmpty() || passPlain.isEmpty()) {
            Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
            return
        }


        val passHash = hashPassword(passPlain)

        // 👉 Guardamos en cache lo que quieras reutilizar
        cache.edit().putString("username", username)
            .putString("email", email)
            .apply()

        val ventanaEnvio = Intent(this, Pantalla1::class.java)
        startActivity(ventanaEnvio)



    }
    private fun registrarUsuario() {
        val username = binding.user.text.toString().trim()
        val email = binding.email.text.toString().trim()
        val passPlain = binding.pass.text.toString().trim()

        // 1. Validación básica
        if (username.isEmpty() || email.isEmpty() || passPlain.isEmpty()) {
            Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        // 2. Contraseña que se enviará (si quieres, aquí puedes hacer hashPassword(passPlain))
        val password = hashPassword(passPlain)

        // 3. Crear objeto Usuario igual que en el backend
        val usuario = Usuario(
            id = null,
            username = username,
            email = email,
            password = password,
            nombre = null
        )

        // 4. Llamar a la API
        RetrofitClient.api.registrar(usuario)
            .enqueue(object : retrofit2.Callback<Usuario> {

                override fun onResponse(
                    call: retrofit2.Call<Usuario>,
                    response: retrofit2.Response<Usuario>
                ) {
                    when (response.code()) {
                        200 -> {
                            Toast.makeText(
                                this@MainActivity,
                                "Usuario creado correctamente",
                                Toast.LENGTH_LONG
                            ).show()
                            enviarDatos()

                        }
                        409 -> {
                            Toast.makeText(
                                this@MainActivity,
                                "Ese correo ya está registrado",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        else -> {
                            Toast.makeText(
                                this@MainActivity,
                                "Error en servidor: ${response.code()}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }

                override fun onFailure(call: retrofit2.Call<Usuario>, t: Throwable) {
                    Toast.makeText(
                        this@MainActivity,
                        "Error de conexión: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun iniciarSesion() {
        val email = binding.email.text.toString().trim()
        val passPlain = binding.pass.text.toString().trim()

        // 1. Validación básica
        if (email.isEmpty() || passPlain.isEmpty()) {
            Toast.makeText(this, "Introduce email y contraseña", Toast.LENGTH_SHORT).show()
            return
        }

        // 2. Hashear contraseña para enviarla al backend
        val passwordHash = hashPassword(passPlain)

        // 3. Crear objeto para la petición
        val loginRequest = LoginRequest(
            email = email,
            password = passwordHash
        )

        // 4. Llamada a la API
        RetrofitClient.api.login(loginRequest)
            .enqueue(object : retrofit2.Callback<Usuario> {

                override fun onResponse(
                    call: retrofit2.Call<Usuario>,
                    response: retrofit2.Response<Usuario>
                ) {
                    when (response.code()) {

                        200 -> {
                            val usuario = response.body()
                            Toast.makeText(
                                this@MainActivity,
                                "Inicio de sesión correcto",
                                Toast.LENGTH_LONG
                            ).show()

                            // Aquí puedes guardar el usuario en cache si quieres:
                            enviarDatos()

                        }

                        401 -> {
                            Toast.makeText(
                                this@MainActivity,
                                "Email o contraseña incorrectos",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        else -> {
                            Toast.makeText(
                                this@MainActivity,
                                "Error del servidor: ${response.code()}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }

                override fun onFailure(call: retrofit2.Call<Usuario>, t: Throwable) {
                    Toast.makeText(
                        this@MainActivity,
                        "Error de conexión: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }



}