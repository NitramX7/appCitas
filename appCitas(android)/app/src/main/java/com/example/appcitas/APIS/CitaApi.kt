
package com.example.appcitas.APIS

import CitaFiltroRequest
import com.example.appcitas.model.Cita
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface CitaApi {

    @GET("citas")
    suspend fun getCitas(): Response<List<Cita>>

    @GET("citas/{id}")
    suspend fun getCita(@Path("id") id: Long): Response<Cita>

    @POST("citas")
    suspend fun crearCita(@Body cita: Cita): Response<Cita>

    @PUT("citas/{id}")
    suspend fun actualizarCita(@Path("id") id: Long, @Body cita: Cita): Response<Cita>

    @DELETE("citas/{id}")
    suspend fun eliminarCita(@Path("id") id: Long): Response<Void>

    @POST("citas/filtrar")
    suspend fun filtrarCitas(@Body filtros: CitaFiltroRequest): Response<List<Cita>>
}
