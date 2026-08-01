package com.expensetracker.backend.controller;

import com.expensetracker.backend.dto.AuthResponse;
import com.expensetracker.backend.dto.LoginRequest;
import com.expensetracker.backend.dto.RegisterRequest;
import com.expensetracker.backend.entity.User;
import com.expensetracker.backend.security.JwtTokenProvider;
import com.expensetracker.backend.service.CategoryService;
import com.expensetracker.backend.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserService userService;
    private final CategoryService categoryService;

    public AuthController(AuthenticationManager authenticationManager, 
                          JwtTokenProvider tokenProvider,
                          UserService userService,
                          CategoryService categoryService) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.userService = userService;
        this.categoryService = categoryService;
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("🔐 Login attempt for user: {}", loginRequest.getUsername());
        
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(), 
                    loginRequest.getPassword()
                )
            );
            
            String token = tokenProvider.generateToken(authentication);
            User user = userService.getUserByUsername(loginRequest.getUsername());
            
            AuthResponse response = new AuthResponse(
                token,
                user.getUsername(),
                user.getFullName(),
                user.getEmail(),
                user.getId(),
                "Login successful"
            );
            
            log.info("✅ User logged in: {}", loginRequest.getUsername());
            log.info("📝 Token generated: {}", token.substring(0, 20) + "...");
            
            return ResponseEntity.ok(response);
            
        } catch (BadCredentialsException e) {
            log.error("❌ Invalid credentials for user: {}", loginRequest.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("{\"message\": \"Invalid username or password\"}");
        } catch (Exception e) {
            log.error("❌ Login error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("{\"message\": \"Login failed: " + e.getMessage() + "\"}");
        }
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        log.info("📝 Registration attempt for user: {}", registerRequest.getUsername());
        
        try {
            User registeredUser = userService.registerUser(registerRequest);
            
            // Auto-create default categories for the new user
            categoryService.createDefaultCategories(registeredUser.getId());
            log.info("📂 Default categories auto-created for new user: {}", registeredUser.getUsername());
            
            AuthResponse response = new AuthResponse(
                null,
                registeredUser.getUsername(),
                registeredUser.getFullName(),
                registeredUser.getEmail(),
                registeredUser.getId(),
                "Registration successful"
            );
            
            log.info("✅ User registered: {}", registeredUser.getUsername());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Registration error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("{\"message\": \"" + e.getMessage() + "\"}");
        }
    }
}