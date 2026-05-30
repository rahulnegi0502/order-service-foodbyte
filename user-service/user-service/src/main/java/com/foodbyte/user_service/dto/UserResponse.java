package com.foodbyte.user_service.dto;

import com.foodbyte.user_service.entity.Role;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class UserResponse {
    private UUID id;
    private String name;
    private String email;
    private String phone;
    private Role role;
    private boolean isPhoneVerified;
}