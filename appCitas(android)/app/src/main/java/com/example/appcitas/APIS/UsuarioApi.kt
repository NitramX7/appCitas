package com.example.appcitas.APIS

import com.example.appcitas.model.Usuario
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface UsuarioApi {

    /**
     * Este es ahora el ÚNICO endpoint que la app necesita para la autenticación.
     * Recibe un token de Firebase y devuelve el perfil de usuario de nuestro backend
     * (ya sea uno existente o uno recién creado por el servidor).
     */
    @POST("auth/verify-token")
    fun verificarToken(@Body token: Map<String, String>): Call<Usuario>

    @retrofit2.http.PUT("usuarios")
    fun updateUsuario(@Body usuario: Usuario): Call<Usuario>
}