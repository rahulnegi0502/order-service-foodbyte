package com.foodbyte.inventory.dto;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class StockCheckResponse {

    private boolean available;
    private int availableQuantity;
    private UUID warehouseId;
    private UUID productId;
    private BigDecimal price;      // current price
    private String productName;
}