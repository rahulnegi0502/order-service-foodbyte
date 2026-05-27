package com.foodbyte.user_service.service;

import com.foodbyte.user_service.dto.*;
import com.foodbyte.user_service.entity.RefreshToken;
import com.foodbyte.user_service.entity.Role;
import com.foodbyte.user_service.entity.User;
import com.foodbyte.user_service.repository.RefreshTokenRepository;
import com.foodbyte.user_service.repository.UserRepository;
import com.foodbyte.user_service.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpirationMs;

    @Value("${otp.ttl-seconds}")
    private long otpTtlSeconds;

    // ── Register Customer ─────────────────────

    @Transactional
    public AuthResponse registerCustomer(RegisterRequest request) {
        return register(request, Role.CUSTOMER);
    }

    // ── Register Warehouse Admin ──────────────

    @Transactional
    public AuthResponse registerWarehouse(RegisterRequest request) {
        return register(request, Role.WAREHOUSE_ADMIN);
    }

    // ── Shared Register Logic ─────────────────

    private AuthResponse register(RegisterRequest request, Role role) {

        // Step 1 — confirm passwords match
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }

        // Step 2 — check duplicates
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new RuntimeException("Phone already registered");
        }

        // Step 3 — build and save user
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .isActive(true)
                .isPhoneVerified(false)
                .build();

        userRepository.save(user);
        log.info("New {} registered: {}", role, request.getEmail());

        // Step 4 — return success (no token on register)
        return AuthResponse.builder()
                .success(true)
                .message("Registration successful. Please login.")
                .build();
    }

    // ── Login ─────────────────────────────────

    @Transactional
    public AuthResponse login(LoginRequest request) {

        // Step 1 — verify email + password via AuthenticationManager
        // internally calls CustomUserDetailsService.loadUserByUsername()
        // throws exception if invalid
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Step 2 — load user
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Step 3 — generate access token
        String accessToken = jwtUtil.generateToken(
                user.getId(),
                user.getEmail(),
                user.getRole()
        );

        // Step 4 — create and save refresh token
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiresAt(LocalDateTime.now()
                        .plusSeconds(refreshExpirationMs / 1000))
                .isRevoked(false)
                .build();

        refreshTokenRepository.save(refreshToken);
        log.info("User logged in: {}", user.getEmail());

        // Step 5 — return auth response
        return AuthResponse.builder()
                .success(true)
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .expiresIn(refreshExpirationMs)
                .userId(user.getId())
                .name(user.getName())
                .role(user.getRole())
                .build();
    }

    // ── Logout ────────────────────────────────

    @Transactional
    public void logout(String authHeader) {

        // Step 1 — extract token
        String token = authHeader.substring(7);

        // Step 2 — blacklist access token in Redis
        String jti = jwtUtil.extractJti(token);
        Date expiry = jwtUtil.extractExpiration(token);
        long ttl = expiry.getTime() - System.currentTimeMillis();

        redisTemplate.opsForValue().set(
                "blacklist:" + jti,
                "true",
                ttl,
                TimeUnit.MILLISECONDS
        );

        // Step 3 — revoke refresh token in DB
        User user = userRepository
                .findByEmail(jwtUtil.extractEmail(token))
                .orElseThrow(() -> new RuntimeException("User not found"));

        refreshTokenRepository.findByUser(user)
                .forEach(rt -> {
                    rt.setRevoked(true);
                    refreshTokenRepository.save(rt);
                });

        log.info("User logged out: {}", user.getEmail());
    }

    // ── Refresh Token ─────────────────────────

    @Transactional
    public AuthResponse refresh(String refreshTokenString) {

        // Step 1 — find refresh token in DB
        RefreshToken refreshToken = refreshTokenRepository
                .findByToken(refreshTokenString)
                .orElseThrow(() ->
                        new RuntimeException("Refresh token not found"));

        // Step 2 — check not revoked
        if (refreshToken.isRevoked()) {
            throw new RuntimeException("Refresh token has been revoked");
        }

        // Step 3 — check not expired
        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Refresh token has expired");
        }

        // Step 4 — check user is active
        User user = refreshToken.getUser();
        if (!user.isActive()) {
            throw new RuntimeException("User account is deactivated");
        }

        // Step 5 — revoke old refresh token (rotation)
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);

        // Step 6 — generate new access token
        String newAccessToken = jwtUtil.generateToken(
                user.getId(),
                user.getEmail(),
                user.getRole()
        );

        // Step 7 — generate new refresh token (rotation)
        RefreshToken newRefreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiresAt(LocalDateTime.now()
                        .plusSeconds(refreshExpirationMs / 1000))
                .isRevoked(false)
                .build();

        refreshTokenRepository.save(newRefreshToken);
        log.info("Token refreshed for user: {}", user.getEmail());

        // Step 8 — return new tokens
        return AuthResponse.builder()
                .success(true)
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken.getToken())
                .expiresIn(refreshExpirationMs)
                .userId(user.getId())
                .name(user.getName())
                .role(user.getRole())
                .build();
    }

    // ── OTP Send ──────────────────────────────

    public void sendOtp(OtpSendRequest request) {

        // Step 1 — check user exists
        userRepository.findByPhone(request.getPhone())
                .orElseThrow(() ->
                        new RuntimeException("No user found with this phone"));

        // Step 2 — generate 6 digit OTP
        String otp = String.format("%06d",
                new Random().nextInt(999999));

        // Step 3 — store in Redis with TTL
        redisTemplate.opsForValue().set(
                "otp:" + request.getPhone(),
                otp,
                otpTtlSeconds,
                TimeUnit.SECONDS
        );

        // Step 4 — log (replace with SMS/email service later)
        log.info("OTP for {}: {}", request.getPhone(), otp);
    }

    // ── OTP Verify ────────────────────────────

    @Transactional
    public void verifyOtp(OtpVerifyRequest request) {

        // Step 1 — get OTP from Redis
        String storedOtp = redisTemplate.opsForValue()
                .get("otp:" + request.getPhone());

        // Step 2 — check OTP exists (not expired)
        if (storedOtp == null) {
            throw new RuntimeException("OTP expired or not sent");
        }

        // Step 3 — check OTP matches
        if (!storedOtp.equals(request.getOtp())) {
            throw new RuntimeException("Invalid OTP");
        }

        // Step 4 — delete OTP from Redis (prevent reuse)
        redisTemplate.delete("otp:" + request.getPhone());

        // Step 5 — mark phone as verified
        User user = userRepository.findByPhone(request.getPhone())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        user.setPhoneVerified(true);
        userRepository.save(user);

        log.info("Phone verified for user: {}", user.getEmail());
    }
}