package com.sirus.backend.interceptor;

import com.sirus.backend.service.GlobalLicenseService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.List;

@Component
public class GlobalLicenseCheckInterceptor implements HandlerInterceptor {
    
    @Autowired
    private GlobalLicenseService globalLicenseService;
    
    // Endpoint-i koji ne zahtevaju globalnu licencnu proveru
    private static final List<String> EXCLUDED_PATHS = Arrays.asList(
        "/api/global-license/status",
        "/api/global-license/check",
        "/api/global-license/active",
        "/api/auth/login",
        "/api/auth/register",
        "/api/global-license/create", // Admin endpoint za kreiranje licence
        "/api/generate-envelope-pdf", // PDF generisanje
        "/api/test-envelope-pdf", // Test PDF endpoint
        "/error",
        "/actuator",
        "/swagger-ui",
        "/v3/api-docs"
    );
    
    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        String requestPath = request.getRequestURI();
        String method = request.getMethod();

        // Debug log
        System.out.println("=== GLOBAL LICENSE INTERCEPTOR DEBUG ===");
        System.out.println("Request Path: " + requestPath);
        System.out.println("Method: " + method);
        System.out.println("Handler: " + handler);

        // Preskoči proveru za excluded path-ove
        if (isExcludedPath(requestPath)) {
            System.out.println("Path excluded: " + requestPath);
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
        
        // Preskoči proveru za global license endpoint-e (da ne bi bilo circular dependency)
        if (requestPath.startsWith("/api/global-license/")) {
            System.out.println("Global license endpoint excluded: " + requestPath);
            return true;
        }
        
        try {
            // Proveri da li postoji važeća globalna licenca
            boolean hasValidGlobalLicense = globalLicenseService.checkAndUpdateGlobalLicenseStatus();
            
            if (!hasValidGlobalLicense) {
                // Blokiraj pristup ako nema važeću globalnu licencu
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                
                String errorResponse = "{\n" +
                    "  \"error\": \"Global license expired or invalid\",\n" +
                    "  \"message\": \"Software license has expired. Please contact administrator to renew the license.\",\n" +
                    "  \"code\": \"GLOBAL_LICENSE_EXPIRED\"\n" +
                    "}";
                
                response.getWriter().write(errorResponse);
                return false;
            }
            
            return true;
            
        } catch (Exception e) {
            // U slučaju greške, dozvoli pristup (fail-open approach)
            System.err.println("Error checking global license: " + e.getMessage());
            return true;
        }
    }
    
    /**
     * Proverava da li je path excluded od globalne licencne provere
     */
    private boolean isExcludedPath(String path) {
        return EXCLUDED_PATHS.stream().anyMatch(path::startsWith);
    }
}
