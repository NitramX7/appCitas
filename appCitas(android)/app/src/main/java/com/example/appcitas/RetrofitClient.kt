package com.example.appcitas

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.appcitas.APIS.CitaApi
import com.example.appcitas.APIS.AuthApi
import com.example.appcitas.APIS.SolicitudApi

object RetrofitClient {

    private const val BASE_URL = "http://10.0.2.2:8090/"

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val citaApi: CitaApi by lazy {
        retrofit.create(CitaApi::class.java)
    }
    
    val authApi: AuthApi by lazy {
        retrofit.create(AuthApi::class.java)
    }

    val solicitudApi: SolicitudApi by lazy {
        retrofit.create(SolicitudApi::class.java)
    }
}