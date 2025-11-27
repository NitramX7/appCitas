package com.example.appcitas

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface CitaApi {

    @POST("api/citas/filtrar")
    suspend fun filtrarCitas(
        @Body filtros: CitaFiltroRequest
    ): Response<List<CitaResponse>>
}
