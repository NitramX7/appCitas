package com.example.appcitas.model

data class Cita(
    val id: Long,
    val titulo: String,
    val descripcion: String,
    val temporada: Integer,
    val dinero: Integer,
    val intensidad: Integer,
    val cercania: Integer,
    val facilidad: Integer
)