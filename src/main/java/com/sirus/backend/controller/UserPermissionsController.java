package com.sirus.backend.controller;

import com.sirus.backend.entity.User;
import com.sirus.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/user-permissions")
@CrossOrigin(origins = {"https://euk.vercel.app", "https://euk-it-sectors-projects.vercel.app", "http://localhost:3000", "http://127.0.0.1:3000"})
public class UserPermissionsController {
    
    private static final Logger logger = LoggerFactory.getLogger(UserPermissionsController.class);
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * GET /api/user-permissions/{id} - Dohvatanje dozvola za korisnika
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserPermissions(@PathVariable Long id) {
        logger.info("GET /api/user-permissions/{} - Fetching user permissions", id);
        
        try {
            Optional<User> userOpt = userRepository.findById(id);
            if (userOpt.isEmpty()) {
                logger.warn("User with ID {} not found", id);
                return ResponseEntity.notFound().build();
            }
            
            User user = userOpt.get();
            Map<String, Object> permissions = generateUserPermissions(user);
            
            logger.info("Successfully generated permissions for user {}: {}", user.getUsername(), permissions);
            return ResponseEntity.ok(permissions);
            
        } catch (Exception e) {
            logger.error("Error fetching user permissions for ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of(
                "error", "Internal server error",
                "message", "Failed to fetch user permissions"
            ));
        }
    }
    
    /**
     * Generiše dozvole na osnovu role korisnika (pojednostavljeni sistem)
     */
    private Map<String, Object> generateUserPermissions(User user) {
        Map<String, Object> permissions = new HashMap<>();
        
        // Osnovne informacije o korisniku
        permissions.put("userId", user.getId());
        permissions.put("username", user.getUsername());
        permissions.put("role", user.getRole());
        permissions.put("isActive", user.isActive());
        
        // Pojednostavljeni role-based permissions
        Map<String, Boolean> routePermissions = new HashMap<>();
        
        if (user.isAdmin()) {
            // Admin ima pristup admin dashboard-u
            routePermissions.put("admin", true);
            routePermissions.put("adminDashboard", true);
            routePermissions.put("userManagement", true);
            routePermissions.put("euk", true);
        } else {
            // Korisnici imaju pristup samo EUK funkcionalnostima
            routePermissions.put("admin", false);
            routePermissions.put("adminDashboard", false);
            routePermissions.put("userManagement", false);
            routePermissions.put("euk", true);
        }
        
        // EUK-specific permissions (svi korisnici imaju pristup)
        Map<String, Boolean> eukPermissions = new HashMap<>();
        eukPermissions.put("kategorije", true);
        eukPermissions.put("predmeti", true);
        eukPermissions.put("ugrozena-lica", true);
        eukPermissions.put("create", true);
        eukPermissions.put("read", true);
        eukPermissions.put("update", true);
        eukPermissions.put("delete", user.isAdmin()); // Samo admin može brisati
        
        permissions.put("routes", routePermissions);
        permissions.put("euk", eukPermissions);
        permissions.put("canDelete", user.isAdmin());
        permissions.put("canManageUsers", user.getRole().equals("admin"));
        permissions.put("canViewAnalytics", user.getRole().equals("admin"));
        
        return permissions;
    }
    
    /**
     * GET /api/user-permissions/me - Dohvatanje dozvola za trenutnog korisnika
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUserPermissions(@RequestHeader("Authorization") String authHeader) {
        logger.info("GET /api/user-permissions/me - Fetching current user permissions");
        
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                logger.error("Invalid authorization header");
                return ResponseEntity.status(401).body(Map.of(
                    "error", "Unauthorized",
                    "message", "Invalid authorization header"
                ));
            }
            
            // Ovde bi trebalo da validirate JWT token i dohvatite korisnika
            // Za sada vraćamo generičke dozvole
            Map<String, Object> defaultPermissions = new HashMap<>();
            defaultPermissions.put("userId", null);
            defaultPermissions.put("username", "anonymous");
            defaultPermissions.put("role", "obradjivaci predmeta");
            defaultPermissions.put("isActive", false);
            
            Map<String, Boolean> routePermissions = new HashMap<>();
            routePermissions.put("admin", false);
            routePermissions.put("users", false);
            routePermissions.put("euk", true);
            routePermissions.put("reports", false);
            routePermissions.put("settings", false);
            routePermissions.put("analytics", false);
            
            defaultPermissions.put("routes", routePermissions);
            defaultPermissions.put("euk", Map.of(
                "kategorije", true,
                "predmeti", true,
                "ugrozena-lica", true,
                "create", true,
                "read", true,
                "update", true,
                "delete", false
            ));
            
            return ResponseEntity.ok(defaultPermissions);
            
        } catch (Exception e) {
            logger.error("Error fetching current user permissions: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of(
                "error", "Internal server error",
                "message", "Failed to fetch current user permissions"
            ));
        }
    }
}
