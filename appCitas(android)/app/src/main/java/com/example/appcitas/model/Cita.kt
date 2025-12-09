package com.example.appcitas.model



data class Cita(
    val id: Long? = null,
    val titulo: String,
    val descripcion: String? = null,
    val temporada: Int? = null,
    val dinero: Int? = null,
    val intensidad: Int? = null,
    val cercania: Int? = null,
    val facilidad: Int? = null,
    val esDefault: Boolean = false,
    val fechaCreacion: String? = null,
    val tokenFirebase: String? = null,
    val coupleId: Long? = null,
    val creadorId: Long? = null
)
