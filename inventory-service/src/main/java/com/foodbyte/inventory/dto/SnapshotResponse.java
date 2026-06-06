package com.foodbyte.inventory.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class SnapshotResponse {

    private UUID id;
    private UUID warehouseStockId;
    private String productName;
    private String warehouseName;
    private int quantity;
    private int reservedQuantity;
    private int availableQuantity;
    private LocalDateTime snapshotTime;
}