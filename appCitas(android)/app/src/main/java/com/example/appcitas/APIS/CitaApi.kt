package com.example.appcitas.APIS

import CitaFiltroRequest
import com.example.appcitas.model.Cita
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface CitaApi {

    @POST("citas/filtrar")
    suspend fun filtrarCitas(
        @Body filtro: CitaFiltroRequest
    ): List<Cita>

    @DELETE("citas/{id}")
    suspend fun eliminarCita(@Path("id") id: Long?): Response<Cita>

    @POST("citas/buscarId")
    suspend fun buscarCitaId(
        @Body filtro: CitaFiltroRequest
    ): List<Cita>

    @POST("citas/crear")
    suspend fun crearCita(@Body cita: Cita): Cita


}