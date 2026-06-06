package com.foodbyte.inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class StockCheckRequest {

    @NotNull
    private UUID productId;

    @NotNull
    private UUID warehouseId;

    @Min(value = 1)
    private int quantity;
}