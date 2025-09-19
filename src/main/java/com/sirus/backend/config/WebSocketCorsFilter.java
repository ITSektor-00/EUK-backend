package com.sirus.backend.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class WebSocketCorsFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketCorsFilter.class);

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        
        String requestURI = request.getRequestURI();
        String origin = request.getHeader("Origin");
        
        // Log WebSocket zahteve
        if (requestURI.startsWith("/ws/") || requestURI.contains("websocket") || requestURI.contains("sockjs")) {
            logger.info("WebSocket request: {} {} from origin: {}", request.getMethod(), requestURI, origin);
        }
        
        // Dodaj CORS headers za sve zahteve
        if (origin != null && isAllowedOrigin(origin)) {
            response.setHeader("Access-Control-Allow-Origin", origin);
            response.setHeader("Access-Control-Allow-Credentials", "true");
        } else {
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Allow-Credentials", "false");
        }
        
        // Dodaj WebSocket specifične headers
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", 
            "Origin, X-Requested-With, Content-Type, Accept, Authorization, " +
            "Sec-WebSocket-Extensions, Sec-WebSocket-Key, Sec-WebSocket-Protocol, Sec-WebSocket-Version");
        response.setHeader("Access-Control-Max-Age", "3600");
        
        // Dodaj WebSocket specifične headers
        if (requestURI.startsWith("/ws/") || requestURI.contains("websocket")) {
            response.setHeader("Sec-WebSocket-Accept", "*");
            response.setHeader("Upgrade", "websocket");
            response.setHeader("Connection", "Upgrade");
        }
        
        // Handle preflight OPTIONS request
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            logger.info("Handling OPTIONS preflight request for: {}", requestURI);
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }
        
        filterChain.doFilter(request, response);
    }
    
    private boolean isAllowedOrigin(String origin) {
        // Lista dozvoljenih origins
        String[] allowedOrigins = {
            "https://euk.vercel.app",
            "https://euk-it-sectors-projects.vercel.app", 
            "http://localhost:3000",
            "http://127.0.0.1:3000",
            "http://localhost:8080",
            "http://127.0.0.1:8080",
            "null" // Za file:// protokol
        };
        
        for (String allowedOrigin : allowedOrigins) {
            if (allowedOrigin.equals(origin)) {
                return true;
            }
        }
        
        return false;
    }
}
