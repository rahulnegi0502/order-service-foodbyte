package com.foodbyte.inventory.dto;

import com.foodbyte.inventory.entity.Category;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ProductResponse {

    private UUID id;
    private String name;
    private String description;
    private String imageUrl;
    private BigDecimal price;
    private String sku;
    private Category category;
    private String unit;
    private String brand;
    private boolean isActive;
    private LocalDateTime createdAt;
}