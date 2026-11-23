package com.example.usuarios.model;

import jakarta.validation.constraints.NotBlank;

// Datos que se envian para iniciar sesion
public class LoginRequest {

    @NotBlank(message = "El correo es obligatorio")
    private String correo;

    @NotBlank(message = "La clave es obligatoria")
    private String clave;

    public String getCorreo() {
        return correo;
    }

    public String getClave() {
        return clave;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

}
