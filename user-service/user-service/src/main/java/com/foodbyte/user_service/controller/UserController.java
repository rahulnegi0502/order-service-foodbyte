package com.foodbyte.user_service.controller;

import com.foodbyte.user_service.dto.ApiResponse;
import com.foodbyte.user_service.dto.UserResponse;
import com.foodbyte.user_service.dto.UpdateProfileRequest;
import com.foodbyte.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    // ── Get My Profile ────────────────────────

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'WAREHOUSE_ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> getMe(
            Authentication authentication) {

        // principal is UUID — set in JwtAuthFilter
        UUID userId = (UUID) authentication.getPrincipal();
        UserResponse response = userService.getMe(userId);
        return ResponseEntity
                .ok(ApiResponse.success(response));
    }

    // ── Update My Profile ─────────────────────

    @PutMapping("/me")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'WAREHOUSE_ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> updateMe(
            Authentication authentication,
            @RequestBody UpdateProfileRequest request) {

        UUID userId = (UUID) authentication.getPrincipal();
        UserResponse response = userService.updateMe(userId, request);
        return ResponseEntity
                .ok(ApiResponse.success("Profile updated", response));
    }
}