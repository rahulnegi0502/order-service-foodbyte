package com.foodbyte.inventory.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class StockReserveResponse {

    private boolean success;
    private UUID warehouseStockId;  // needed for later deduct/release
    private UUID orderId;
    private int reservedQuantity;
}
