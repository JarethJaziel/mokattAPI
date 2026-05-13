package com.mokatta.mokatta_api.auth.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "El email es requerido")
    @Email(message = "Email inválido")
    String email;

    @NotBlank(message = "La contraseña es requerida")
    String password;
}
