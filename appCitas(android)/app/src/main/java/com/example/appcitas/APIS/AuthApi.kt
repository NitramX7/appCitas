package com.example.appcitas.APIS

import com.example.appcitas.model.Usuario
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("auth/verify-token")
    fun verificarToken(@Body token: Map<String, String>): Call<Usuario>
}
