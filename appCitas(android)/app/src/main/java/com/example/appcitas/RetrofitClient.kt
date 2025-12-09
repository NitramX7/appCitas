package com.example.appcitas

import com.example.appcitas.APIS.CitaApi
import com.example.appcitas.APIS.CoupleApi
import com.example.appcitas.APIS.InvitationApi
import com.example.appcitas.APIS.UsuarioApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // Dirección de tu backend Spring Boot
    private const val BASE_URL = "http://10.0.2.2:8090/"

    // Retrofit se construye una sola vez (lazy = cuando se usa por primera vez)
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // === API de USUARIOS (lo que ya usabas como RetrofitClient.api) ===
    val api: UsuarioApi by lazy {
        retrofit.create(UsuarioApi::class.java)
    }

    // === API de CITAS (para los filtros en Pantalla1) ===
    val citaApi: CitaApi by lazy {
        retrofit.create(CitaApi::class.java)
    }

    // === API de COUPLES ===
    val coupleApi: CoupleApi by lazy {
        retrofit.create(CoupleApi::class.java)
    }

    // === API de INVITATIONS ===
    val invitationApi: InvitationApi by lazy {
        retrofit.create(InvitationApi::class.java)
    }
}
