package com.expensetracker.backend.security;

import com.expensetracker.backend.config.JwtConfig;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    
    private final JwtTokenProvider tokenProvider;
    private final UserDetailsService userDetailsService;
    private final JwtConfig jwtConfig;

    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider, 
                                   UserDetailsService userDetailsService,
                                   JwtConfig jwtConfig) {
        this.tokenProvider = tokenProvider;
        this.userDetailsService = userDetailsService;
        this.jwtConfig = jwtConfig;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   FilterChain filterChain) throws ServletException, IOException {
        
        String path = request.getRequestURI();
        log.info("📥 Request to: {}", path);
        
        // Skip authentication for login/register
        if (path.startsWith("/api/auth/")) {
            log.info("✅ Auth endpoint - skipping token validation");
            filterChain.doFilter(request, response);
            return;
        }
        
        String token = getTokenFromRequest(request);
        
        if (token != null) {
            log.info("🔑 Token present: {}", token.substring(0, 20) + "...");
            
            boolean isValid = tokenProvider.validateToken(token);
            log.info("✅ Token valid: {}", isValid);
            
            if (isValid) {
                String username = tokenProvider.getUsernameFromToken(token);
                log.info("👤 Username from token: {}", username);
                
                if (username != null && !username.isEmpty()) {
                    try {
                        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                        log.info("👤 User details loaded: {}", userDetails.getUsername());
                        
                        UsernamePasswordAuthenticationToken authentication = 
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        log.info("✅ Authentication set for user: {}", username);
                    } catch (Exception e) {
                        log.error("❌ Error loading user: {}", e.getMessage());
                    }
                } else {
                    log.warn("⚠️ No username extracted from token");
                }
            } else {
                log.warn("⚠️ Token validation failed");
            }
        } else {
            log.warn("⚠️ No token found in request to: {}", path);
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(jwtConfig.getHeader());
        String prefix = jwtConfig.getPrefix().trim();
        if (bearerToken != null && bearerToken.startsWith(prefix)) {
            String token = bearerToken.substring(prefix.length()).trim();
            log.info("🔑 Token extracted from Authorization header");
            return token;
        }
        log.warn("⚠️ No Authorization header found");
        return null;
    }
}