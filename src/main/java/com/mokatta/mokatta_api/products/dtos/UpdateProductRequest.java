package com.mokatta.mokatta_api.products.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class UpdateProductRequest {

    @Size(max = 150, message = "Name must not exceed 150 characters")
    private String name;

    private String description;

    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;

    private UUID categoryId;

    private String imageUrl;
}
