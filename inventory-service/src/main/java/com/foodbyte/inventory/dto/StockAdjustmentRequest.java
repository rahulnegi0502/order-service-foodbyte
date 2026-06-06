package com.foodbyte.inventory.dto;

import com.foodbyte.inventory.entity.AdjustmentType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class StockAdjustmentRequest {

    @NotNull(message = "Warehouse ID is required")
    private UUID warehouseId;

    @NotNull(message = "Product ID is required")
    private UUID productId;

    @NotNull(message = "Adjustment type is required")
    private AdjustmentType adjustmentType;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;

    private String reason;
}