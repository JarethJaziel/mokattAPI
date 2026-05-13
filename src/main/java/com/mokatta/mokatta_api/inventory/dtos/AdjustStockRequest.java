package com.mokatta.mokatta_api.inventory.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AdjustStockRequest {

    public enum AdjustType {
        ADD, SUBTRACT
    }

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotNull(message = "Type is required (ADD or SUBTRACT)")
    private AdjustType type;
}
