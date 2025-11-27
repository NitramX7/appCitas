package com.example.appcitas

import CitaFiltroRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface CitaApi {

    @POST("citas/filtrar")
    suspend fun filtrarCitas(
        @Body filtro: CitaFiltroRequest
    ): List<CitaResponse>
}
