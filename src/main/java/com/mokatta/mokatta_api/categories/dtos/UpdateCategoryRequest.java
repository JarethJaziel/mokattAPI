package com.mokatta.mokatta_api.categories.dtos;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateCategoryRequest {

    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;
}
