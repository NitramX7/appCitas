package com.example.appcitas

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface UsuarioApi {

    // Crear usuario
    @POST("usuarios")
    fun registrar(@Body usuario: Usuario): Call<Usuario>

    // Obtener lista de usuarios
    @GET("usuarios")
    fun getUsuarios(): Call<List<Usuario>>

    @POST("login")
    fun login(@Body loginRequest: LoginRequest): Call<Usuario>
}
