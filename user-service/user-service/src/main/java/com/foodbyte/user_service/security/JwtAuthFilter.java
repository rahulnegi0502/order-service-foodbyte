package com.foodbyte.user_service.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // Step 1 — extract Authorization header
        String authHeader = request.getHeader("Authorization");

        // Step 2 — no token, pass through
        // public endpoints like /auth/login handled by SecurityConfig
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Step 3 — strip "Bearer " prefix
        String token = authHeader.substring(7);

        // Step 4 — validate signature and expiry first
        // no point checking blacklist if token is already invalid
        if (!jwtUtil.validateToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid or expired token");
            return;
        }

        // Step 5 — check Redis blacklist (logout check)
        String jti = jwtUtil.extractJti(token);
        Boolean isBlacklisted = redisTemplate.hasKey("blacklist:" + jti);
        if (Boolean.TRUE.equals(isBlacklisted)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token has been invalidated");
            return;
        }

        // Step 6 — extract claims directly from token
        // Role is immutable — safe to trust token claims forever
        UUID userId = jwtUtil.extractUserId(token);
        String role  = jwtUtil.extractRole(token).name();

        // Step 7 — build authentication from token claims
        // no DB call — purely stateless
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(
                        userId,   // principal — userId available in controllers
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + role))
                );

        authToken.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );

        // Step 8 — set in SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authToken);

        // Step 9 — pass to next filter
        filterChain.doFilter(request, response);
    }
}