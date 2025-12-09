package com.example.appcitas.model

data class Invitation(
    val id: Long,
    val senderId: Long,
    val senderEmail: String,
    val receiverId: Long,
    val status: InvitationStatus
)
