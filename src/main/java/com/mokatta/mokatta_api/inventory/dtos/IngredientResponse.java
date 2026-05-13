package com.mokatta.mokatta_api.inventory.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class IngredientResponse {
    private String id;
    private String name;
    private BigDecimal quantity;
    private String unit;
    private BigDecimal minStock;
    private boolean active;
    private boolean lowStock;
    private LocalDateTime updatedAt;
}
