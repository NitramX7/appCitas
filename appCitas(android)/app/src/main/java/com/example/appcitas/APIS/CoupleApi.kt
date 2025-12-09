package com.example.appcitas.APIS

import com.example.appcitas.model.Couple
import com.example.appcitas.model.Invitation
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface CoupleApi {
    @GET("couples")
    suspend fun getCouples(): Response<List<Couple>>

    @GET("couples/{id}")
    suspend fun getCouple(@Path("id") id: Long): Response<Couple>

    @GET("couples/by-user/{userId}")
    suspend fun getCouplesByUser(@Path("userId") userId: Long): Response<List<Couple>>

    @POST("couples")
    suspend fun createCouple(@Body couple: Couple): Response<Couple>

    @DELETE("couples/{id}")
    suspend fun deleteCouple(@Path("id") id: Long): Response<Void>

    /**
     * Envía una solicitud de pareja al email especificado.
     * El backend recibirá el email como texto plano en el cuerpo de la petición.
     */
    @POST("sendInvitation")
    suspend fun sendInvitation(@Body email: String): Response<Void>

    @GET("/pending/{userId}")
    suspend fun getPendingInvitations(
        @Path("userId") userId: Long
    ): Response<List<Invitation>>
}
