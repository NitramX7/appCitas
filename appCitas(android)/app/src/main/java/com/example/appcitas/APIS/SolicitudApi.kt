package com.example.appcitas.APIS

import com.example.appcitas.model.SolicitudPareja
import com.example.appcitas.model.SolicitudRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface SolicitudApi {

    @POST("api/solicitudes/enviar")
    fun enviarSolicitud(
        @Query("solicitanteId") solicitanteId: Long,
        @Body request: SolicitudRequest
    ): Call<SolicitudPareja>

    @GET("api/solicitudes/recibidas")
    fun obtenerSolicitudesRecibidas(
        @Query("userId") userId: Long
    ): Call<List<SolicitudPareja>>

    @POST("api/solicitudes/{id}/aceptar")
    fun aceptarSolicitud(@Path("id") id: Long): Call<Void>

    @POST("api/solicitudes/{id}/rechazar")
    fun rechazarSolicitud(@Path("id") id: Long): Call<Void>
}
