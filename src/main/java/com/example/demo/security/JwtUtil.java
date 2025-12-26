// // File: src/main/java/com/example/demo/security/JwtUtil.java
// package com.example.demo.security;

// import com.example.demo.entity.User;
// import io.jsonwebtoken.Claims;
// import io.jsonwebtoken.Jwts;
// import io.jsonwebtoken.SignatureAlgorithm;
// import io.jsonwebtoken.security.Keys;
// import org.springframework.stereotype.Component;

// import java.security.Key;
// import java.util.Date;
// import java.util.HashMap;
// import java.util.Map;

// @Component
// public class JwtUtil {
    
//     private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
//     private static final long EXPIRATION_TIME = 86400000; // 24 hours
    
//     // Inner class to wrap Claims and provide getPayload() method
//     public static class JwtToken {
//         private final Claims claims;
        
//         public JwtToken(Claims claims) {
//             this.claims = claims;
//         }
        
//         public Claims getPayload() {
//             return claims;
//         }
        
//         public Claims getBody() {
//             return claims;
//         }
//     }
    
//     public String generateToken(Map<String, Object> claims, String subject) {
//         return Jwts.builder()
//                 .setClaims(claims)
//                 .setSubject(subject)
//                 .setIssuedAt(new Date())
//                 .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
//                 .signWith(SECRET_KEY)
//                 .compact();
//     }
    
//     public String generateTokenForUser(User user) {
//         Map<String, Object> claims = new HashMap<>();
//         claims.put("userId", user.getId());
//         claims.put("email", user.getEmail());
//         claims.put("role", user.getRole());
//         return generateToken(claims, user.getEmail());
//     }
    
//     public JwtToken parseToken(String token) {
//         Claims claims = Jwts.parserBuilder()
//                 .setSigningKey(SECRET_KEY)
//                 .build()
//                 .parseClaimsJws(token)
//                 .getBody();
//         return new JwtToken(claims);
//     }
    
//     public Claims extractClaims(String token) {
//         return parseToken(token).getPayload();
//     }
    
//     public String extractUsername(String token) {
//         return extractClaims(token).getSubject();
//     }
    
//     public String extractRole(String token) {
//         return (String) extractClaims(token).get("role");
//     }
    
//     public Long extractUserId(String token) {
//         Object userId = extractClaims(token).get("userId");
//         if (userId instanceof Integer) {
//             return ((Integer) userId).longValue();
//         }
//         return (Long) userId;
//     }
    
//     public boolean isTokenValid(String token, String username) {
//         try {
//             String extractedUsername = extractUsername(token);
//             return extractedUsername.equals(username) && !isTokenExpired(token);
//         } catch (Exception e) {
//             return false;
//         }
//     }
    
//     private boolean isTokenExpired(String token) {
//         return extractClaims(token).getExpiration().before(new Date());
//     }
// }
package com.example.demo.security;

import com.example.demo.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    private static final long EXPIRATION_TIME = 1000 * 60 * 60; // 1 hour

    // ✅ REQUIRED for JJWT 0.12.x (must be at least 256 bits)
    private static final SecretKey KEY =
            Keys.hmacShaKeyFor("my-super-secret-key-my-super-secret-key-123456".getBytes());

    /* =========================
       CORE TOKEN GENERATION
       ========================= */

    public String generateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)               // NEW API
                .subject(subject)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(KEY)                // NEW API
                .compact();
    }

    /* =========================
       REQUIRED BY TEST CASES
       ========================= */

    public String generateTokenForUser(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", user.getEmail());
        claims.put("role", user.getRole());
        claims.put("userId", user.getId());
        return generateToken(claims, user.getEmail());
    }

    // ✅ MUST return Jws<Claims> AND support getPayload()
    public Jws<Claims> parseToken(String token) {
        return Jwts.parser()
                .verifyWith(KEY)       // ✅ JJWT 0.12.x API
                .build()
                .parseSignedClaims(token);
    }

    // =========================
    // Used by tests
    // =========================

    public String extractUsername(String token) {
        return parseToken(token).getPayload().getSubject();
    }

    public Long extractUserId(String token) {
        Object id = parseToken(token).getPayload().get("userId");
        return id == null ? null : Long.valueOf(id.toString());
    }

    public String extractRole(String token) {
        return (String) parseToken(token).getPayload().get("role");
    }

    public boolean isTokenExpired(String token) {
        return parseToken(token)
                .getPayload()
                .getExpiration()
                .before(new Date());
    }

    // REQUIRED BY TESTS
    public boolean isTokenValid(String token, String username) {
        return extractUsername(token).equals(username) && !isTokenExpired(token);
    }
}
