package com.example.appcitas.APIS

import com.example.appcitas.LoginRequest
import com.example.appcitas.model.Usuario
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface UsuarioApi {

    // COINCIDE con @PostMapping("/usuarios")
    @POST("usuarios")
    fun registrar(
        @Body usuario: Usuario
    ): Call<Usuario>

    @GET("usuarios/by-email")
    suspend fun getUsuarioByEmail(
        @Query("email") email: String
    ): com.example.appcitas.model.Usuario

    // COINCIDE con @PostMapping("/login")
    @POST("login")
    fun login(
        @Body loginRequest: LoginRequest
    ): Call<Usuario>
}