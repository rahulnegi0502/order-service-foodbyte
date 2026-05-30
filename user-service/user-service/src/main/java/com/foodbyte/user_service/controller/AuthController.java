package com.foodbyte.user_service.controller;

import com.foodbyte.user_service.dto.*;
import com.foodbyte.user_service.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    // ── Register Customer ─────────────────────

    @PostMapping("/register/customer")
    public ResponseEntity<ApiResponse<AuthResponse>> registerCustomer(
            @Valid @RequestBody RegisterRequest request) {

        AuthResponse response = authService.registerCustomer(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Registration successful", response));
    }

    // ── Register Warehouse Admin ──────────────

    @PostMapping("/register/warehouse")
    public ResponseEntity<ApiResponse<AuthResponse>> registerWarehouse(
            @Valid @RequestBody RegisterRequest request) {

        AuthResponse response = authService.registerWarehouse(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Registration successful", response));
    }

    // ── Login ─────────────────────────────────

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        AuthResponse response = authService.login(request);
        return ResponseEntity
                .ok(ApiResponse.success("Login successful", response));
    }

    // ── Logout ────────────────────────────────

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestHeader("Authorization") String authHeader) {

        authService.logout(authHeader);
        return ResponseEntity
                .ok(ApiResponse.success("Logged out successfully", null));
    }

    // ── Refresh Token ─────────────────────────

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(
            @RequestBody String refreshToken) {

        AuthResponse response = authService.refresh(refreshToken);
        return ResponseEntity
                .ok(ApiResponse.success("Token refreshed", response));
    }

    // ── OTP Send ──────────────────────────────

    @PostMapping("/otp/send")
    public ResponseEntity<ApiResponse<Void>> sendOtp(
            @Valid @RequestBody OtpSendRequest request) {

        authService.sendOtp(request);
        return ResponseEntity
                .ok(ApiResponse.success("OTP sent successfully", null));
    }

    // ── OTP Verify ────────────────────────────

    @PostMapping("/otp/verify")
    public ResponseEntity<ApiResponse<Void>> verifyOtp(
            @Valid @RequestBody OtpVerifyRequest request) {

        authService.verifyOtp(request);
        return ResponseEntity
                .ok(ApiResponse.success("Phone verified successfully", null));
    }
}