package com.hallbooking.utilis;

import com.hallbooking.userservice.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Slf4j
@Component
public class JWTUtil {

    @Value("${jwt.secret}")
    private String secret;

    /**
     * Access token expiration in milliseconds (default: 1 day).
     */
    @Value("${jwt.expiration:86400000}")
    private long expiration;

    private Key secretKey;

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        log.info("JWTUtil initialized. Access token expiration: {} ms", expiration);
    }

    /* =========================
     *       GENERATORS
     * ========================= */

    /**
     * Generates an access token with subject = countryCode|mobile and claims:
     * role, userId, email, name
     */
    public String generateToken(User user) {
        try {
            String compositeMobile = user.getCountryCode() + "|" + user.getMobile();
            return Jwts.builder()
                    .setSubject(compositeMobile)
                    .claim("role", "ROLE_" + user.getRole().name())
                    .claim("userId", user.getId())
                    .claim("email", user.getEmail())
                    .claim("name", user.getName())
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + expiration))
                    .signWith(secretKey, SignatureAlgorithm.HS256)
                    .compact();
        } catch (Exception e) {
            log.error("Error generating JWT token for user {}: {}", user.getId(), e.getMessage());
            throw new RuntimeException("Token generation failed", e);
        }
    }

    /**
     * Generates a refresh token (7x longer by default) with subject = countryCode|mobile and claims:
     * userId, type=refresh
     */
    public String generateRefreshToken(User user) {
        try {
            String compositeMobile = user.getCountryCode() + "|" + user.getMobile();
            return Jwts.builder()
                    .setSubject(compositeMobile)
                    .claim("userId", user.getId())
                    .claim("type", "refresh")
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + (expiration * 7)))
                    .signWith(secretKey, SignatureAlgorithm.HS256)
                    .compact();
        } catch (Exception e) {
            log.error("Error generating refresh token for user {}: {}", user.getId(), e.getMessage());
            throw new RuntimeException("Refresh token generation failed", e);
        }
    }

    /* =========================
     *    EXTRACTION / PARSING
     * ========================= */

    /**
     * Cached claims extraction to avoid re-parsing the same token repeatedly.
     * Requires Spring Cache to be enabled (you already have it).
     */
    @Cacheable(value = "jwtClaims", key = "#token")
    public Claims extractClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.warn("JWT token expired: {}", e.getMessage());
            throw new RuntimeException("Token expired", e);
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token: {}", e.getMessage());
            throw new RuntimeException("Token format not supported", e);
        } catch (MalformedJwtException e) {
            log.error("Malformed JWT token: {}", e.getMessage());
            throw new RuntimeException("Token format invalid", e);
        } catch (SignatureException e) {
            log.error("JWT signature validation failed: {}", e.getMessage());
            throw new RuntimeException("Token signature invalid", e);
        } catch (IllegalArgumentException e) {
            log.error("JWT token compact or handler invalid: {}", e.getMessage());
            throw new RuntimeException("Token invalid", e);
        }
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractClaims(token);
        return claimsResolver.apply(claims);
    }

    public String extractSubject(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /** Alias for subject (countryCode|mobile). */
    public String extractMobile(String token) {
        return extractSubject(token);
    }

    public Long extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("userId", Long.class));
    }

    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    public String extractEmail(String token) {
        return extractClaim(token, claims -> claims.get("email", String.class));
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public Date extractIssuedAt(String token) {
        return extractClaim(token, Claims::getIssuedAt);
    }

    /** Optional vendor-specific claim if present */
    public Long extractVendorId(String token) {
        return extractClaim(token, claims -> claims.get("vendorId", Long.class));
    }

    /* =========================
     *      VALIDATION / META
     * ========================= */

    public boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (Exception e) {
            log.warn("Error checking token expiration: {}", e.getMessage());
            return true; // treat parsing issues as expired/invalid
        }
    }

    /** Validate by matching subject and checking expiry */
    public boolean validateToken(String token, String expectedSubject) {
        try {
            final String subject = extractSubject(token);
            return subject.equals(expectedSubject) && !isTokenExpired(token);
        } catch (Exception e) {
            log.warn("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    /** Validate structurally and by expiry */
    public boolean validateToken(String token) {
        try {
            extractClaims(token); // throws for invalid tokens
            return !isTokenExpired(token);
        } catch (Exception e) {
            log.warn("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    public long getExpirationTime() {
        return expiration;
    }

    public long getRemainingTime(String token) {
        try {
            Date exp = extractExpiration(token);
            return exp.getTime() - System.currentTimeMillis();
        } catch (Exception e) {
            log.warn("Error calculating remaining time: {}", e.getMessage());
            return 0;
        }
    }

    /**
     * True if token is within 10 minutes of expiry (and not already expired).
     */
    public boolean shouldRefresh(String token) {
        try {
            long remaining = getRemainingTime(token);
            return remaining < 600_000 && remaining > 0;
        } catch (Exception e) {
            return false;
        }
    }

    /* =========================
     *      ROLE HELPERS
     * ========================= */

    public boolean isAdmin(String token) {
        String role = extractRole(token);
        return "ROLE_ADMIN".equals(role);
    }

    public boolean isVendor(String token) {
        String role = extractRole(token);
        return role != null && role.contains("VENDOR");
    }

    public boolean isHallVendor(String token) {
        String role = extractRole(token);
        return "ROLE_HALL_VENDOR".equals(role);
    }
}
