package com.foodbyte.inventory.dto;

import com.foodbyte.inventory.entity.Category;
import jakarta.validation.constraints.DecimalMin;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateProductRequest {

    private String name;

    private String description;

    private String imageUrl;

    @DecimalMin(value = "0.1", message = "Price must be greater than 0")
    private BigDecimal price;

    private Category category;

    private String brand;
}