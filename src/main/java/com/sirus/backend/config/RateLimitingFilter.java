package com.sirus.backend.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Arrays;
import java.util.List;

@Component
@ConditionalOnProperty(name = "euk.rate-limit.enabled", havingValue = "true", matchIfMissing = false)
public class RateLimitingFilter extends OncePerRequestFilter {

    private final ConcurrentHashMap<String, AtomicInteger> requestCounts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> lastResetTime = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> lastRequestTime = new ConcurrentHashMap<>();
    
    @Value("${euk.rate-limit.max-requests:200}")
    private int maxRequestsPerMinute;
    
    @Value("${euk.rate-limit.window-minutes:1}")
    private int windowMinutes;
    
    @Value("${euk.rate-limit.exclude-patterns:}")
    private String excludePatterns;
    
    private long getResetInterval() {
        return windowMinutes * 60000L; // Convert minutes to milliseconds
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, 
                                  @NonNull HttpServletResponse response, 
                                  @NonNull FilterChain filterChain) throws ServletException, IOException {
        
        String clientIp = getClientIpAddress(request);
        String origin = request.getHeader("Origin");
        String requestURI = request.getRequestURI();
        String key = clientIp + ":" + requestURI;
        
        // Izuzmi WebSocket endpoint-e od rate limiting-a
        if (requestURI.startsWith("/ws/") || 
            requestURI.contains("/ws/info") ||
            requestURI.contains("/ws/websocket")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // Izuzmi admin endpoint-e od rate limiting-a
        if (requestURI.startsWith("/api/admin/")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // Izuzmi auth i global license endpoint-e od rate limiting-a
        if (requestURI.startsWith("/api/auth/") || 
            requestURI.startsWith("/api/global-license/")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // Proveri exclude patterns iz konfiguracije
        if (excludePatterns != null && !excludePatterns.isEmpty()) {
            List<String> patterns = Arrays.asList(excludePatterns.split(","));
            for (String pattern : patterns) {
                String trimmedPattern = pattern.trim();
                // Konvertuj glob pattern u regex
                String regexPattern = trimmedPattern
                    .replace("**", ".*")
                    .replace("*", "[^/]*")
                    .replace(".", "\\.");
                
                if (requestURI.matches(regexPattern)) {
                    filterChain.doFilter(request, response);
                    return;
                }
            }
        }
        
        // Posebna pravila za EUK domene
        boolean isEukDomain = origin != null && (
            origin.contains("euk.vercel.app") || 
            origin.contains("euk-it-sectors-projects.vercel.app")
        );
        
        int maxRequests;
        if (isEukDomain) {
            maxRequests = Math.max(250, maxRequestsPerMinute); // Povećano za EUK domene
        } else {
            maxRequests = maxRequestsPerMinute;
        }
        
        long currentTime = System.currentTimeMillis();
        
        // Izuzmi EUK endpoint-e od duplicate request protection-a
        boolean isEukEndpoint = request.getRequestURI().startsWith("/api/euk/");
        
        // Proveri duplicate requests (isti endpoint u kratkom vremenu) - samo za non-EUK endpoint-e
        if (!isEukEndpoint) {
            Long lastTime = lastRequestTime.get(key);
            if (lastTime != null && (currentTime - lastTime) < 100) { // Smanjeno na 100ms za duplicate protection
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"Duplicate request\",\"message\":\"Please wait before retrying\",\"retryAfter\":1}");
                return;
            }
            lastRequestTime.put(key, currentTime);
        }
        
        // Reset counter if interval has passed
        lastResetTime.computeIfAbsent(key, k -> currentTime);
        if (currentTime - lastResetTime.get(key) > getResetInterval()) {
            requestCounts.remove(key);
            lastResetTime.put(key, currentTime);
        }
        
        // Increment request count
        AtomicInteger count = requestCounts.computeIfAbsent(key, k -> new AtomicInteger(0));
        int currentCount = count.incrementAndGet();
        
        if (currentCount > maxRequests) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Rate limit exceeded\",\"message\":\"Please try again later\",\"retryAfter\":" + (getResetInterval()/1000) + "}");
            return;
        }
        
        // Add rate limit headers
        response.setHeader("X-RateLimit-Limit", String.valueOf(maxRequests));
        response.setHeader("X-RateLimit-Remaining", String.valueOf(maxRequests - currentCount));
        response.setHeader("X-RateLimit-Reset", String.valueOf(lastResetTime.get(key) + getResetInterval()));
        
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