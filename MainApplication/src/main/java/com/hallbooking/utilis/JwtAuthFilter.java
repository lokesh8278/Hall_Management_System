package com.hallbooking.utilis;

import com.hallbooking.userservice.service.serviceimpl.JwtRedisService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final JwtRedisService jwtRedisService;

    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/auth", "/bookingauth/login", "/admin", "/error"
    );

    private boolean isPublicEndpoint(String path) {
        return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getServletPath();

        if (isPublicEndpoint(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        log.info("🧾 Checking token: {}", token);

        if (jwtRedisService.isTokenBlacklisted(token)) {
            log.warn("🚫 Token is blacklisted or expired");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token is blacklisted or expired");
            return;
        }

        try {
            String subject = jwtUtil.extractSubject(token);
            String role = jwtUtil.extractClaim(token, claims -> claims.get("role", String.class));
            Long userId = jwtUtil.extractUserId(token);

            log.info("✅ Raw Role from token: {}", role);

            if (role != null && role.startsWith("ROLE_")) {
                role = role.substring(5); // Normalize
            }

            log.info("✅ Normalized Role: {}", role);

            if (subject != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = org.springframework.security.core.userdetails.User
                        .withUsername(subject)
                        .password("") // dummy
                        .authorities("ROLE_" + role)
                        .build();

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);

                log.info("🔐 Authenticated user: {}", userDetails.getUsername());
                log.info("🔐 Authority injected: ROLE_{}", role);
            }

        } catch (Exception e) {
            log.warn("⚠️ Primary token parse failed, checking fallback...");
            try {
                Claims claims = Jwts.parserBuilder().build().parseClaimsJws(token).getBody();
                String role = claims.get("role", String.class);
                if ("ROLE_ADMIN".equals(role)) {
                    UserDetails userDetails = org.springframework.security.core.userdetails.User
                            .withUsername("external-admin")
                            .password("")
                            .authorities("ROLE_ADMIN")
                            .build();

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    log.info("🔐 Fallback Authenticated as ADMIN");
                } else {
                    log.warn("❌ Access denied: fallback role was not ADMIN");
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied.");
                    return;
                }
            } catch (Exception ex) {
                log.error("❌ Invalid token format: {}", ex.getMessage());
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid token format.");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
