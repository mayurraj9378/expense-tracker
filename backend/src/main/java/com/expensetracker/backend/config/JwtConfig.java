package com.expensetracker.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {
    
    @Value("${jwt.secret:}")
    private String secret;
    
    @Value("${jwt.expiration:86400000}")
    private long expiration;
    
    @Value("${jwt.header:Authorization}")
    private String header;
    
    @Value("${jwt.prefix:Bearer }")
    private String prefix;
    
    public String getSecret() {
        if (secret == null || secret.isEmpty()) {
            secret = "MySuperSecretJWTKey12345678901234567890123456789012";
        }
        System.out.println("🔑 JWT Secret: " + secret);
        return secret;
    }
    
    public long getExpiration() { return expiration; }
    public String getHeader() { return header; }
    public String getPrefix() { return prefix; }
}