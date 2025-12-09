package com.example.appcitas.model

data class SolicitudPareja(
    val id: Long,
    val solicitante: Usuario,
    val solicitado: Usuario,
    val estado: String,
    val fechaCreacion: String? = null
)
