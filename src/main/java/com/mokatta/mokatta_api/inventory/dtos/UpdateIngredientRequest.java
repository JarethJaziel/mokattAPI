package com.mokatta.mokatta_api.inventory.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateIngredientRequest {

    @Size(max = 150, message = "Name must not exceed 150 characters")
    private String name;

    @Size(max = 20, message = "Unit must not exceed 20 characters")
    private String unit;

    @DecimalMin(value = "0.0", message = "Minimum stock cannot be negative")
    private BigDecimal minStock;
}
