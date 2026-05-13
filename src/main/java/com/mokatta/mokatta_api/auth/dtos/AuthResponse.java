package com.mokatta.mokatta_api.auth.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {

    private String accessToken;
    private String tokenType;
    private UserInfo user;

    // Constructor personalizado para emular el comportamiento del Record original
    public AuthResponse(String accessToken, UserInfo user) {
        this.accessToken = accessToken;
        this.tokenType = "Bearer";
        this.user = user;
    }
}
