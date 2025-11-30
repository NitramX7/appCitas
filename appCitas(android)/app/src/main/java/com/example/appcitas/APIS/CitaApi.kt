package com.example.appcitas.APIS

import CitaFiltroRequest
import com.example.appcitas.model.Cita
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface CitaApi {

    @POST("citas/filtrar")
    suspend fun filtrarCitas(@Body filtro: CitaFiltroRequest): List<Cita>

    @POST("citas")
    suspend fun crearCita(@Body cita: Cita): Cita

    @DELETE("citas/{id}")
    suspend fun eliminarCita(@Path("id") id: Long)

    // --- NUEVOS ENDPOINTS PARA EDITAR ---

    @GET("citas/{id}")
    suspend fun getCitaPorId(@Path("id") id: Long): Cita

    @PUT("citas/{id}")
    suspend fun actualizarCita(@Path("id") id: Long, @Body cita: Cita): Cita
}
