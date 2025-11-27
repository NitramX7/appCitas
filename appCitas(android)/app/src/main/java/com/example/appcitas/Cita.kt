package com.example.appcitas

data class CitaResponse(
    val id: Long,
    val titulo: String,
    val descripcion: String,
    val temporada: String,
    val dinero: String,
    val intensidad: String,
    val cercania: String,
    val facilidad: String
)