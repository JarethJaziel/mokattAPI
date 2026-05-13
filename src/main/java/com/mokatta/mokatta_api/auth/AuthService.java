package com.mokatta.mokatta_api.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import com.mokatta.mokatta_api.auth.dtos.AuthResponse;
import com.mokatta.mokatta_api.auth.dtos.LoginRequest;
import com.mokatta.mokatta_api.auth.dtos.UserInfo;
import com.mokatta.mokatta_api.config.jwt.JwtService;
import com.mokatta.mokatta_api.users.User;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthResponse login(LoginRequest request) {
        try {
            // authenticate() valida credenciales y devuelve el principal autenticado
            User user = (User) authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()))
                    .getPrincipal();

            String token = jwtService.generateToken(user);

            return new AuthResponse(
                    token,
                    new UserInfo(
                            user.getId().toString(),
                            user.getName(),
                            user.getEmail()));

        } catch (AuthenticationException e) {
            throw new IllegalArgumentException("Invalid email or password");
        }
    }
}
