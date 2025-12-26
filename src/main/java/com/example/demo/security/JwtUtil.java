package com.example.demo.security;

import com.example.demo.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    // Rule 8.1: Uses JJWT 0.12.x secure key generation for HS256
    private final SecretKey key = Jwts.SIG.HS256.key().build();

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractRole(String token) {
        return extractClaim(token, claims -> (String) claims.get("role"));
    }

    public Long extractUserId(String token) {
        return extractClaim(token, claims -> Long.valueOf(claims.get("userId").toString()));
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = parseToken(token).getPayload();
        return claimsResolver.apply(claims);
    }

    /**
     * Rule 8.1: Parses and validates the token.
     * Returns Jws<Claims> to allow tests to call .getPayload()
     */
    public Jws<Claims> parseToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);
    }

    /**
     * Rule 8.1: Generic token builder using modern JJWT 0.12.x syntax
     */
    public String generateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                // 10 hours expiration
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) 
                .signWith(key)
                .compact();
    }

    /**
     * Rule 8.1: Specifically sets userId, email, and role claims
     */
    public String generateTokenForUser(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("email", user.getEmail());
        claims.put("role", user.getRole());
        return generateToken(claims, user.getEmail());
    }

    public boolean isTokenValid(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }
}