package com.example.appcitas.model

data class Usuario(
    val id: Long? = null,
    val username: String,
    val email: String,
    val password: String,
    val nombre: String? = null,
    val fotoUrl: String? = null,
    val estado_p: Int = 0,
    val pareja: Usuario? = null
)