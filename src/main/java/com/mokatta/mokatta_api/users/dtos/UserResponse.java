package com.mokatta.mokatta_api.users.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class UserResponse {
    private String id;
    private String name;
    private String email;
    private boolean active;
    private LocalDateTime createdAt;
}
