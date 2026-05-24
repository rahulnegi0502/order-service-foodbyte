package com.foodbyte.user_service.repository;

import com.foodbyte.user_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);     // login

    Optional<User> findByPhone(String phone);     // OTP lookup

    boolean existsByEmail(String email);          // duplicate check on register

    boolean existsByPhone(String phone);          // duplicate check on register
}
