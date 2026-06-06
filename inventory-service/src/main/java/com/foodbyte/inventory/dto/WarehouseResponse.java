package com.foodbyte.inventory.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class WarehouseResponse {

    private UUID id;
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;
    private String city;
    private String pincode;
    private boolean isActive;
    private Double distanceKm;  // calculated — nearest warehouse feature
}