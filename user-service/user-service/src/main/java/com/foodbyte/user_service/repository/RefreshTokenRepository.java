package com.foodbyte.user_service.repository;

import com.foodbyte.user_service.entity.RefreshToken;
import com.foodbyte.user_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    // Used on every /auth/refresh call
    Optional<RefreshToken> findByToken(String token);

    // Used for logout all devices
    List<RefreshToken> findByUser(User user);

    // Used for cleanup job — delete expired tokens
    @Modifying
    void deleteByExpiresAtBefore(LocalDateTime dateTime);
}