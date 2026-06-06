package com.foodbyte.inventory.dto;

import com.foodbyte.inventory.entity.Category;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateProductRequest {

    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    private String imageUrl;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.1", message = "Price must be greater than 0")
    private BigDecimal price;

    @NotBlank(message = "SKU is required")
    private String sku;

    @NotNull(message = "Category is required")
    private Category category;

    @NotBlank(message = "Unit is required")
    private String unit;

    private String brand;
}