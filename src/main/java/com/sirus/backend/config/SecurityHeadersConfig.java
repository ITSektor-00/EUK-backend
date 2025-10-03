package com.sirus.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import java.io.IOException;

@Configuration
public class SecurityHeadersConfig {

    @Bean
    public OncePerRequestFilter securityHeadersFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(@NonNull HttpServletRequest request, 
                                          @NonNull HttpServletResponse response, 
                                          @NonNull FilterChain filterChain) throws ServletException, IOException {
                
                // Security Headers
                response.setHeader("X-Content-Type-Options", "nosniff");
                response.setHeader("X-Frame-Options", "DENY");
                response.setHeader("X-XSS-Protection", "1; mode=block");
                response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
                
                // Content-Security-Policy - dozvoli download
                response.setHeader("Content-Security-Policy", "default-src 'self'; script-src 'self' 'unsafe-inline' https://euk.vercel.app https://euk-it-sectors-projects.vercel.app; style-src 'self' 'unsafe-inline' https://euk.vercel.app https://euk-it-sectors-projects.vercel.app; connect-src 'self' https://euk.vercel.app https://euk-it-sectors-projects.vercel.app; object-src 'none'; base-uri 'self';");
                
                response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
                response.setHeader("Permissions-Policy", "geolocation=(), microphone=(), camera=()");
                
                filterChain.doFilter(request, response);
            }
        };
    }
} 