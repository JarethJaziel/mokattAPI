package com.mokatta.mokatta_api.users.dtos;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserRequest {

    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    private String password;
}
