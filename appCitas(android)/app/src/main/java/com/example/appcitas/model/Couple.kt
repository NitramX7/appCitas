package com.example.appcitas.model

data class Couple(
    val id: Long? = null,
    val user1Id: Long,
    val user2Id: Long,
    val createdAt: String? = null   // 👈 String, NO LocalDateTime
)