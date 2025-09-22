package com.sirus.backend.controller;

import com.sirus.backend.dto.UserDto;
import com.sirus.backend.dto.PaginatedResponse;
import com.sirus.backend.dto.RoleUpdateRequest;
import com.sirus.backend.dto.AdminErrorResponse;
import com.sirus.backend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = {"https://euk.vercel.app", "https://euk-it-sectors-projects.vercel.app", "http://localhost:3000", "http://127.0.0.1:3000"})
public class AdminController {
    
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    
    @Autowired
    private UserService userService;
    
    /**
     * GET /api/admin/users - Dohvatanje korisnika za admin panel sa paginacijom
     */
    @GetMapping("/users")
    public ResponseEntity<PaginatedResponse<UserDto>> getAdminUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) String search) {
        
        logger.info("GET /api/admin/users - Fetching users for admin panel - page: {}, size: {}, role: {}, isActive: {}, search: {}", 
                   page, size, role, isActive, search);
        
        try {
            Page<UserDto> users = userService.findAllUsers(page, size, role, isActive, search);
            return ResponseEntity.ok(PaginatedResponse.from(users));
        } catch (Exception e) {
            logger.error("Error fetching admin users: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * GET /api/admin/users/count - Broj ukupnih korisnika za admin panel
     */
    @GetMapping("/users/count")
    public ResponseEntity<Long> getAdminUserCount() {
        logger.info("GET /api/admin/users/count - Getting user count for admin panel");
        
        try {
            long count = userService.getUserCount();
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            logger.error("Error getting admin user count: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * GET /api/admin/users/count/active - Broj aktivnih korisnika za admin panel
     */
    @GetMapping("/users/count/active")
    public ResponseEntity<Long> getAdminActiveUserCount() {
        logger.info("GET /api/admin/users/count/active - Getting active user count for admin panel");
        
        try {
            long count = userService.getActiveUserCount();
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            logger.error("Error getting admin active user count: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * GET /api/admin/users/count/pending - Broj korisnika koji čekaju odobrenje
     */
    @GetMapping("/users/count/pending")
    public ResponseEntity<Long> getAdminPendingUserCount() {
        logger.info("GET /api/admin/users/count/pending - Getting pending user count for admin panel");
        
        try {
            long count = userService.getPendingUserCount();
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            logger.error("Error getting admin pending user count: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * GET /api/admin/users/active - Aktivni korisnici za admin panel
     */
    @GetMapping("/users/active")
    public ResponseEntity<PaginatedResponse<UserDto>> getActiveUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        logger.info("GET /api/admin/users/active - Fetching active users for admin panel - page: {}, size: {}", page, size);
        
        try {
            Page<UserDto> users = userService.findActiveUsers(page, size);
            return ResponseEntity.ok(PaginatedResponse.from(users));
        } catch (Exception e) {
            logger.error("Error fetching active users: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * GET /api/admin/users/pending - Korisnici koji čekaju odobrenje za admin panel
     */
    @GetMapping("/users/pending")
    public ResponseEntity<PaginatedResponse<UserDto>> getPendingUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        logger.info("GET /api/admin/users/pending - Fetching pending users for admin panel - page: {}, size: {}", page, size);
        
        try {
            Page<UserDto> users = userService.findPendingUsers(page, size);
            return ResponseEntity.ok(PaginatedResponse.from(users));
        } catch (Exception e) {
            logger.error("Error fetching pending users: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * PUT /api/admin/users/{id}/role - Ažuriranje role korisnika (pojednostavljeni sistem)
     */
    @PutMapping("/users/{id}/role")
    public ResponseEntity<?> updateUserRole(@PathVariable Long id, @RequestBody RoleUpdateRequest request) {
        logger.info("PUT /api/admin/users/{}/role - Updating user role to: {}", id, request.getRole());
        
        try {
            // Validacija role
            if (request.getRole() == null || (!request.getRole().equals("admin") && !request.getRole().equals("korisnik"))) {
                logger.error("Invalid role: {}", request.getRole());
                return ResponseEntity.badRequest()
                    .body(AdminErrorResponse.serverError("Role must be either 'admin' or 'korisnik'"));
            }
            
            UserDto updatedUser = userService.updateUserRole(id, request.getRole());
            if (updatedUser != null) {
                logger.info("Successfully updated user role - userId: {}, newRole: {}", id, request.getRole());
                return ResponseEntity.ok(updatedUser);
            } else {
                logger.error("User not found for role update - userId: {}", id);
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalArgumentException e) {
            logger.error("Invalid argument for role update: {}", e.getMessage());
            return ResponseEntity.badRequest().body(AdminErrorResponse.serverError(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error updating user role: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(AdminErrorResponse.serverError("Failed to update user role"));
        }
    }
    
    /**
     * PUT /api/admin/users/{id}/status - Ažuriranje statusa korisnika (aktiviranje/deaktiviranje)
     */
    @PutMapping("/users/{id}/status")
    public ResponseEntity<?> updateUserStatus(@PathVariable Long id, @RequestBody java.util.Map<String, Boolean> request) {
        Boolean isActive = request.get("isActive");
        logger.info("PUT /api/admin/users/{}/status - Updating user status to: {}", id, isActive);
        
        try {
            if (isActive == null) {
                logger.error("isActive field is required");
                return ResponseEntity.badRequest()
                    .body(AdminErrorResponse.serverError("isActive field is required"));
            }
            
            UserDto updatedUser = userService.updateUserStatus(id, isActive);
            if (updatedUser != null) {
                logger.info("Successfully updated user status - userId: {}, isActive: {}", id, isActive);
                return ResponseEntity.ok(updatedUser);
            } else {
                logger.error("User not found for status update - userId: {}", id);
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalArgumentException e) {
            logger.error("Invalid argument for status update: {}", e.getMessage());
            return ResponseEntity.badRequest().body(AdminErrorResponse.serverError(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error updating user status: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(AdminErrorResponse.serverError("Failed to update user status"));
        }
    }
    
    /**
     * DELETE /api/admin/users/{id} - Brisanje korisnika
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        logger.info("DELETE /api/admin/users/{} - Deleting user", id);
        
        try {
            boolean deleted = userService.deleteUser(id);
            if (deleted) {
                logger.info("Successfully deleted user - userId: {}", id);
                return ResponseEntity.ok().build();
            } else {
                logger.error("User not found for deletion - userId: {}", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error deleting user: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(AdminErrorResponse.serverError("Failed to delete user"));
        }
    }
    
    /**
     * GET /api/admin/dashboard - Osnovne statistike za admin dashboard
     */
    @GetMapping("/dashboard")
    public ResponseEntity<?> getAdminDashboard() {
        logger.info("GET /api/admin/dashboard - Getting admin dashboard statistics");
        
        try {
            java.util.Map<String, Object> dashboard = new java.util.HashMap<>();
            
            // Osnovne statistike
            dashboard.put("totalUsers", userService.getUserCount());
            dashboard.put("activeUsers", userService.getActiveUserCount());
            dashboard.put("pendingUsers", userService.getPendingUserCount());
            dashboard.put("adminUsers", userService.getUserCountByRole("admin"));
            dashboard.put("korisnikUsers", userService.getUserCountByRole("korisnik"));
            
            logger.info("Successfully fetched admin dashboard statistics");
            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            logger.error("Error fetching admin dashboard: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(AdminErrorResponse.serverError("Failed to fetch admin dashboard"));
        }
    }
}