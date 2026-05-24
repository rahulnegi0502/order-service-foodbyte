package com.foodbyte.user_service.dto;

import com.foodbyte.user_service.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private boolean success;

    private String accessToken;
    private String refreshToken;
    private long expiresIn;      // in milliseconds

    private UUID userId;
    private String name;
    private Role role;
}