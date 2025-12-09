package com.example.appcitas.model

import java.time.LocalDateTime

data class Couple(
    val id: Long? = null,
    val user1Id: Long,
    val user2Id: Long,
    val createdAt: LocalDateTime? = null
)
