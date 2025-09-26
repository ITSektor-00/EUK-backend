package com.sirus.backend.interceptor;

import com.sirus.backend.service.LicenseService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.List;

@Component
public class LicenseCheckInterceptor implements HandlerInterceptor {
    
    @Autowired
    private LicenseService licenseService;
    
    // Endpoint-i koji ne zahtevaju licencnu proveru
    private static final List<String> EXCLUDED_PATHS = Arrays.asList(
        "/api/licenses/status",
        "/api/licenses/check",
        "/api/auth/login",
        "/api/auth/register",
        "/api/licenses/create", // Admin endpoint za kreiranje licence
        "/error",
        "/actuator",
        "/swagger-ui",
        "/v3/api-docs"
    );
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestPath = request.getRequestURI();
        String method = request.getMethod();
        
        // Preskoči proveru za excluded path-ove
        if (isExcludedPath(requestPath)) {
            return true;
        }
        
        // Preskoči proveru za OPTIONS zahteve (CORS)
        if ("OPTIONS".equals(method)) {
            return true;
        }
        
        // Preskoči proveru za static resurse
        if (requestPath.startsWith("/static/") || requestPath.startsWith("/css/") || 
            requestPath.startsWith("/js/") || requestPath.startsWith("/images/")) {
            return true;
        }
        
        try {
            // Pokušaj da dobiješ user ID iz request-a
            Long userId = getUserIdFromRequest(request);
            
            if (userId == null) {
                // Ako nema user ID-a, preskoči proveru (možda je public endpoint)
                return true;
            }
            
            // Proveri da li korisnik ima važeću licencu
            boolean hasValidLicense = licenseService.checkAndUpdateLicenseStatus(userId);
            
            if (!hasValidLicense) {
                // Blokiraj pristup ako nema važeću licencu
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                
                String errorResponse = "{\n" +
                    "  \"error\": \"License expired or invalid\",\n" +
                    "  \"message\": \"Your license has expired. Please contact administrator to renew your license.\",\n" +
                    "  \"code\": \"LICENSE_EXPIRED\"\n" +
                    "}";
                
                response.getWriter().write(errorResponse);
                return false;
            }
            
            return true;
            
        } catch (Exception e) {
            // U slučaju greške, dozvoli pristup (fail-open approach)
            System.err.println("Error checking license: " + e.getMessage());
            return true;
        }
    }
    
    /**
     * Proverava da li je path excluded od licencne provere
     */
    private boolean isExcludedPath(String path) {
        return EXCLUDED_PATHS.stream().anyMatch(path::startsWith);
    }
    
    /**
     * Pokušava da dobije user ID iz request-a
     * Ovo treba prilagoditi na osnovu vašeg authentication sistema
     */
    private Long getUserIdFromRequest(HttpServletRequest request) {
        try {
            // Opcija 1: Iz header-a
            String userIdHeader = request.getHeader("X-User-ID");
            if (userIdHeader != null && !userIdHeader.isEmpty()) {
                return Long.parseLong(userIdHeader);
            }
            
            // Opcija 2: Iz session-a
            Object userIdSession = request.getSession().getAttribute("userId");
            if (userIdSession != null) {
                return Long.parseLong(userIdSession.toString());
            }
            
            // Opcija 3: Iz JWT token-a (ako koristite JWT)
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                // Ovdje bi trebalo da dekodirate JWT token i izvučete user ID
                // return extractUserIdFromJWT(authHeader.substring(7));
            }
            
            // Opcija 4: Iz request parametara
            String userIdParam = request.getParameter("userId");
            if (userIdParam != null && !userIdParam.isEmpty()) {
                return Long.parseLong(userIdParam);
            }
            
            return null;
            
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
