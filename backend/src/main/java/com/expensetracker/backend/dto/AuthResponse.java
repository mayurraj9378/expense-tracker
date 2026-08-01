package com.expensetracker.backend.dto;

public class AuthResponse {
    private String token;
    private String username;
    private String fullName;
    private String email;
    private Long userId;
    private String message;

    // Default constructor
    public AuthResponse() {}

    // Constructor with all fields
    public AuthResponse(String token, String username, String fullName, String email, Long userId, String message) {
        this.token = token;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.userId = userId;
        this.message = message;
    }

    // Getters and Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    // Builder Pattern (Manual)
    public static AuthResponseBuilder builder() {
        return new AuthResponseBuilder();
    }

    public static class AuthResponseBuilder {
        private String token;
        private String username;
        private String fullName;
        private String email;
        private Long userId;
        private String message;

        public AuthResponseBuilder token(String token) { this.token = token; return this; }
        public AuthResponseBuilder username(String username) { this.username = username; return this; }
        public AuthResponseBuilder fullName(String fullName) { this.fullName = fullName; return this; }
        public AuthResponseBuilder email(String email) { this.email = email; return this; }
        public AuthResponseBuilder userId(Long userId) { this.userId = userId; return this; }
        public AuthResponseBuilder message(String message) { this.message = message; return this; }

        public AuthResponse build() {
            return new AuthResponse(token, username, fullName, email, userId, message);
        }
    }
}