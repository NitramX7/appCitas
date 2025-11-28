package com.example.appcitas.model

data class Cita(
    val id: Long,
    val titulo: String,
    val descripcion: String,
    val temporada: Int?,
    val dinero: Int?,
    val intensidad: Int?,
    val cercania: Int?,
    val facilidad: Int?,
    val creadorId: String
)
