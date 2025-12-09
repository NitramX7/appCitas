package com.example.appcitas.APIS

import SendInvitationRequest
import com.example.appcitas.model.Invitation
import com.example.appcitas.model.dtos.AcceptInvitationRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface InvitationApi {

    // 👉 ENVÍO DE INVITACIÓN (coincide con Spring)
    @POST("couples/invitations/send")
    suspend fun sendInvitation(
        @Body request: SendInvitationRequest
    ): Response<Void>


    // 👉 LISTAR INVITACIONES PENDIENTES (ESTA ES LA QUE USARÁS)
    @GET("couples/invitations/pending/{userId}")
    suspend fun getInvitationsByUser(
        @Path("userId") userId: Long
    ): Response<List<Invitation>>

    // 👉 ACEPTAR INVITACIÓN
    @POST("couples/invitations/{invitationId}/accept")
    suspend fun acceptInvitation(
        @Path("invitationId") id: Long
    ): Response<Invitation>

    // 👉 RECHAZAR INVITACIÓN
    @POST("couples/invitations/{invitationId}/reject")
    suspend fun rejectInvitation(
        @Path("invitationId") id: Long
    ): Response<Invitation>
}
