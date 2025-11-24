package com.example.appcitas

data class Usuario(
    val id: Long? = null,
    val username: String,
    val email: String,
    val password: String,
    val nombre: String? = null,
)
