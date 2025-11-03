package com.sirus.backend.config;

import com.sirus.backend.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.lang.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        
        // Log CORS informacije za debugging (samo za API endpoint-e)
        String requestURI = request.getRequestURI();
        if (requestURI.startsWith("/api/")) {
            String origin = request.getHeader("Origin");
            String referer = request.getHeader("Referer");
            String userAgent = request.getHeader("User-Agent");
            String remoteAddr = request.getRemoteAddr();
            
            logger.debug("API Request - URI: {}, Origin: {}, Referer: {}, User-Agent: {}, RemoteAddr: {}", 
                requestURI, origin != null ? origin : "null", 
                referer != null ? referer : "null",
                userAgent != null ? userAgent : "null",
                remoteAddr);
        }
        
        // Proveri da li je zahtev za public endpoint
        if (requestURI.startsWith("/api/auth/") || 
            requestURI.startsWith("/api/test/") || 
            requestURI.startsWith("/actuator/") ||
            requestURI.startsWith("/api/global-license/") ||
            requestURI.startsWith("/api/generate-envelope-pdf") ||
            requestURI.startsWith("/api/generate-envelope-back-side-pdf") ||
            requestURI.startsWith("/api/test-envelope-pdf") ||
            requestURI.startsWith("/api/test-pdf") ||
            requestURI.startsWith("/api/export/") ||
            requestURI.startsWith("/api/template/") ||
            requestURI.startsWith("/api/dokumenti/") ||
            requestURI.startsWith("/api/kategorije/") ||
            requestURI.startsWith("/api/obrasci-vrste") ||
            requestURI.startsWith("/api/organizaciona-struktura") ||
            requestURI.startsWith("/api/predmeti/") ||
            requestURI.startsWith("/api/euk/") ||
            requestURI.startsWith("/api/t1-lice") ||
            requestURI.startsWith("/api/t2-lice") ||
            requestURI.equals("/h2-console")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        try {
            username = jwtService.getUsernameFromToken(jwt);
            
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                
                if (jwtService.validateToken(jwt)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Log error silently and continue
            // Don't set authentication if there's an error
        }

        filterChain.doFilter(request, response);
    }
} 