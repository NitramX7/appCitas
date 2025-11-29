package com.example.appcitas.APIS

import CitaFiltroRequest
import com.example.appcitas.model.Cita
import retrofit2.http.Body
import retrofit2.http.POST

interface CitaApi {

    @POST("citas/filtrar")
    suspend fun filtrarCitas(
        @Body filtro: CitaFiltroRequest
    ): List<Cita>

    @POST("citas/eliminar")
    suspend fun eliminarCita(
        @Body filtro: CitaFiltroRequest
    ): List<Cita>

    @POST("citas/buscarId")
    suspend fun buscarCitaId(
        @Body filtro: CitaFiltroRequest
    ): List<Cita>

    @POST("citas/crear")
    suspend fun crearCita(@Body cita: Cita): Cita

}