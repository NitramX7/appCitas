package service

import CitaFiltroRequest
import com.example.appcitas.model.Cita
import retrofit2.http.Body
import retrofit2.http.POST

interface CitaApi {

    @POST("citas/filtrar")
    suspend fun filtrarCitas(
        @Body filtro: CitaFiltroRequest
    ): List<Cita>
}