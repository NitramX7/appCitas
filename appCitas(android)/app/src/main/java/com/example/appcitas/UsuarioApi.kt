package com.example.appcitas

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface UsuarioApi {

    // COINCIDE con @PostMapping("/usuarios")
    @POST("usuarios")
    fun registrar(
        @Body usuario: Usuario
    ): Call<Usuario>

    // COINCIDE con @PostMapping("/login")
    @POST("login")
    fun login(
        @Body loginRequest: LoginRequest
    ): Call<Usuario>
}
