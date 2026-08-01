package com.expensetracker.backend.security;

import com.expensetracker.backend.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final JwtConfig jwtConfig;

    public JwtTokenProvider(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    private byte[] getSigningKey() {
        String secret = jwtConfig.getSecret();
        System.out.println("🔑 Using SECRET: " + secret);
        System.out.println("📏 Secret length: " + secret.length());
        return secret.getBytes(StandardCharsets.UTF_8);
    }

    public String generateToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtConfig.getExpiration());

        String token = Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(Keys.hmacShaKeyFor(getSigningKey()))
                .compact();
        
        System.out.println("🔑 Generated token for: " + userDetails.getUsername());
        System.out.println("🔑 Token: " + token.substring(0, 30) + "...");
        return token;
    }

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(getSigningKey()))
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        try {
            System.out.println("🔍 Validating token...");
            Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(getSigningKey()))
                .build()
                .parseSignedClaims(token);
            System.out.println("✅ Token VALID!");
            return true;
        } catch (Exception e) {
            System.out.println("❌ Token INVALID: " + e.getMessage());
            return false;
        }
    }
}