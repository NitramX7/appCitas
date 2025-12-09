package com.appcitas.dto;

public class SolicitudRequest {
    private String email; // O username, según prefieras. Usaremos email por ser único.

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
