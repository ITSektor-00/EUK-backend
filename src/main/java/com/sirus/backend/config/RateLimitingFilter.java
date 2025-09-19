package com.sirus.backend.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private final ConcurrentHashMap<String, AtomicInteger> requestCounts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> lastResetTime = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> lastRequestTime = new ConcurrentHashMap<>();
    
    private static final int MAX_REQUESTS_PER_MINUTE = 200; // Povećano za normalne zahteve
    private static final long RESET_INTERVAL = 60000; // 1 minute

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, 
                                  @NonNull HttpServletResponse response, 
                                  @NonNull FilterChain filterChain) throws ServletException, IOException {
        
        String clientIp = getClientIpAddress(request);
        String origin = request.getHeader("Origin");
        String key = clientIp + ":" + request.getRequestURI();
        
        // Izuzmi WebSocket endpoint-e od rate limiting-a
        if (request.getRequestURI().startsWith("/ws/") || 
            request.getRequestURI().contains("/ws/info") ||
            request.getRequestURI().contains("/ws/websocket")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // Izuzmi admin endpoint-e od rate limiting-a
        if (request.getRequestURI().startsWith("/api/admin/")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // Posebna pravila za EUK domene
        boolean isEukDomain = origin != null && (
            origin.contains("euk.vercel.app") || 
            origin.contains("euk-it-sectors-projects.vercel.app")
        );
        
        int maxRequests;
        if (isEukDomain) {
            maxRequests = 250; // Povećano za EUK domene
        } else {
            maxRequests = MAX_REQUESTS_PER_MINUTE;
        }
        
        long currentTime = System.currentTimeMillis();
        
        // Proveri duplicate requests (isti endpoint u kratkom vremenu)
        Long lastTime = lastRequestTime.get(key);
        if (lastTime != null && (currentTime - lastTime) < 100) { // Smanjeno na 100ms za duplicate protection
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Duplicate request\",\"message\":\"Please wait before retrying\",\"retryAfter\":1}");
            return;
        }
        lastRequestTime.put(key, currentTime);
        
        // Reset counter if interval has passed
        lastResetTime.computeIfAbsent(key, k -> currentTime);
        if (currentTime - lastResetTime.get(key) > RESET_INTERVAL) {
            requestCounts.remove(key);
            lastResetTime.put(key, currentTime);
        }
        
        // Increment request count
        AtomicInteger count = requestCounts.computeIfAbsent(key, k -> new AtomicInteger(0));
        int currentCount = count.incrementAndGet();
        
        if (currentCount > maxRequests) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Rate limit exceeded\",\"message\":\"Please try again later\",\"retryAfter\":" + (RESET_INTERVAL/1000) + "}");
            return;
        }
        
        // Add rate limit headers
        response.setHeader("X-RateLimit-Limit", String.valueOf(maxRequests));
        response.setHeader("X-RateLimit-Remaining", String.valueOf(maxRequests - currentCount));
        response.setHeader("X-RateLimit-Reset", String.valueOf(lastResetTime.get(key) + RESET_INTERVAL));
        
        filterChain.doFilter(request, response);
    }
    
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0];
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
} 