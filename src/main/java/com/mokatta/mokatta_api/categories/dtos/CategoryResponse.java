package com.mokatta.mokatta_api.categories.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CategoryResponse {
    private String id;
    private String name;
    private boolean active;
    private LocalDateTime createdAt;
}
