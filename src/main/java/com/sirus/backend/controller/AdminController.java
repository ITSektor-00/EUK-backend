package com.sirus.backend.controller;

import com.sirus.backend.dto.UserDto;
import com.sirus.backend.dto.PaginatedResponse;
import com.sirus.backend.dto.RoleUpdateRequest;
import com.sirus.backend.dto.AdminErrorResponse;
import com.sirus.backend.dto.UserRouteDto;
import com.sirus.backend.dto.UserRouteRequest;
import com.sirus.backend.service.UserService;
import com.sirus.backend.service.UserRouteService;
import com.sirus.backend.repository.RouteRepository;
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
    
    @Autowired
    private UserRouteService userRouteService;
    
    @Autowired
    private RouteRepository routeRepository;
    
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
     * GET /api/admin/routes - Dohvatanje svih ruta za admin panel
     */
    @GetMapping("/routes")
    public ResponseEntity<?> getAllRoutes() {
        logger.info("GET /api/admin/routes - Fetching all routes for admin panel");
        
        try {
            java.util.List<com.sirus.backend.dto.RouteDto> routes = routeRepository.findAll().stream()
                .map(route -> {
                    com.sirus.backend.dto.RouteDto routeDto = new com.sirus.backend.dto.RouteDto();
                    routeDto.setId(route.getId());
                    routeDto.setRuta(route.getRuta());
                    routeDto.setNaziv(route.getNaziv());
                    routeDto.setOpis(route.getOpis());
                    routeDto.setSekcija(route.getSekcija());
                    routeDto.setAktivna(route.getAktivna());
                    routeDto.setDatumKreiranja(route.getDatumKreiranja());
                    return routeDto;
                })
                .collect(java.util.stream.Collectors.toList());
            
            logger.info("Successfully fetched {} routes", routes.size());
            return ResponseEntity.ok(routes);
        } catch (Exception e) {
            logger.error("Error fetching routes: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(AdminErrorResponse.serverError("Failed to fetch routes"));
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
    public ResponseEntity<PaginatedResponse<UserDto>> getAdminActiveUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size) {
        
        logger.info("GET /api/admin/users/active - Fetching active users for admin panel - page: {}, size: {}", page, size);
        
        try {
            Page<UserDto> users = userService.findAllUsers(page, size, null, true, null);
            return ResponseEntity.ok(PaginatedResponse.from(users));
        } catch (Exception e) {
            logger.error("Error fetching admin active users: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * GET /api/admin/users/inactive - Neaktivni korisnici za admin panel
     */
    @GetMapping("/users/inactive")
    public ResponseEntity<PaginatedResponse<UserDto>> getAdminInactiveUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size) {
        
        logger.info("GET /api/admin/users/inactive - Fetching inactive users for admin panel - page: {}, size: {}", page, size);
        
        try {
            Page<UserDto> users = userService.findAllUsers(page, size, null, false, null);
            return ResponseEntity.ok(PaginatedResponse.from(users));
        } catch (Exception e) {
            logger.error("Error fetching admin inactive users: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * PUT /api/admin/users/{id}/role - Ažuriranje role korisnika
     */
    @PutMapping("/users/{id}/role")
    public ResponseEntity<?> updateUserRole(@PathVariable Long id, @RequestBody RoleUpdateRequest request) {
        logger.info("PUT /api/admin/users/{}/role - Updating user role to: {}", id, request.getRole());
        
        try {
            // Validacija request-a
            if (request.getRole() == null || request.getRole().trim().isEmpty()) {
                logger.error("Role is null or empty");
                return ResponseEntity.badRequest().body(AdminErrorResponse.invalidRole(request.getRole()));
            }
            
            UserDto updatedUser = userService.updateUserRole(id, request.getRole().trim());
            logger.info("Successfully updated user {} role to {}", id, request.getRole());
            
            return ResponseEntity.ok(updatedUser);
            
        } catch (RuntimeException e) {
            if (e.getMessage().contains("User not found")) {
                logger.error("User not found with ID: {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(AdminErrorResponse.userNotFound(id));
            } else if (e.getMessage().contains("Invalid role")) {
                logger.error("Invalid role: {}", request.getRole());
                return ResponseEntity.badRequest().body(AdminErrorResponse.invalidRole(request.getRole()));
            } else {
                logger.error("Error updating user role: {}", e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AdminErrorResponse.serverError("Failed to update user role"));
            }
        } catch (Exception e) {
            logger.error("Unexpected error updating user role: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(AdminErrorResponse.serverError("Unexpected error occurred"));
        }
    }
    
    /**
     * PUT /api/admin/users/{id}/approve - Aktiviranje korisnika
     */
    @PutMapping("/users/{id}/approve")
    public ResponseEntity<?> approveUser(@PathVariable Long id) {
        logger.info("PUT /api/admin/users/{}/approve - Approving user", id);
        
        try {
            UserDto updatedUser = userService.approveUser(id);
            logger.info("Successfully approved user {}", id);
            
            return ResponseEntity.ok(updatedUser);
            
        } catch (RuntimeException e) {
            if (e.getMessage().contains("User not found")) {
                logger.error("User not found with ID: {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(AdminErrorResponse.userNotFound(id));
            } else {
                logger.error("Error approving user: {}", e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AdminErrorResponse.serverError("Failed to approve user"));
            }
        } catch (Exception e) {
            logger.error("Unexpected error approving user: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(AdminErrorResponse.serverError("Unexpected error occurred"));
        }
    }
    
    /**
     * PUT /api/admin/users/{id}/reject - Deaktiviranje korisnika
     */
    @PutMapping("/users/{id}/reject")
    public ResponseEntity<?> rejectUser(@PathVariable Long id) {
        logger.info("PUT /api/admin/users/{}/reject - Rejecting user", id);
        
        try {
            UserDto updatedUser = userService.rejectUser(id);
            logger.info("Successfully rejected user {}", id);
            
            return ResponseEntity.ok(updatedUser);
            
        } catch (RuntimeException e) {
            if (e.getMessage().contains("User not found")) {
                logger.error("User not found with ID: {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(AdminErrorResponse.userNotFound(id));
            } else {
                logger.error("Error rejecting user: {}", e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AdminErrorResponse.serverError("Failed to reject user"));
            }
        } catch (Exception e) {
            logger.error("Unexpected error rejecting user: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(AdminErrorResponse.serverError("Unexpected error occurred"));
        }
    }
    
    /**
     * POST /api/admin/user-routes - Kreiranje nove user route veze
     */
    @PostMapping("/user-routes")
    public ResponseEntity<?> createUserRoute(@RequestBody UserRouteRequest request) {
        logger.info("POST /api/admin/user-routes - Creating user route - userId: {}, routeId: {}", 
                   request.getUserId(), request.getRouteId());
        
        try {
            // Validacija request-a
            if (request.getUserId() == null) {
                logger.error("User ID is null");
                return ResponseEntity.badRequest().body(AdminErrorResponse.serverError("User ID is required"));
            }
            if (request.getRouteId() == null) {
                logger.error("Route ID is null");
                return ResponseEntity.badRequest().body(AdminErrorResponse.serverError("Route ID is required"));
            }
            
            // Kreiranje UserRouteDto
            UserRouteDto userRouteDto = new UserRouteDto();
            userRouteDto.setUserId(request.getUserId());
            userRouteDto.setRouteId(request.getRouteId());
            
            UserRouteDto createdUserRoute = userRouteService.createUserRoute(userRouteDto);
            logger.info("Successfully created user route - userId: {}, routeId: {}", 
                       request.getUserId(), request.getRouteId());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUserRoute);
            
        } catch (IllegalArgumentException e) {
            logger.error("Invalid argument for user route creation: {}", e.getMessage());
            return ResponseEntity.badRequest().body(AdminErrorResponse.serverError(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error creating user route: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(AdminErrorResponse.serverError("Failed to create user route"));
        }
    }
    
    /**
     * GET /api/admin/user-routes - Dohvatanje svih user route veza
     */
    @GetMapping("/user-routes")
    public ResponseEntity<?> getAllUserRoutes() {
        logger.info("GET /api/admin/user-routes - Fetching all user routes");
        
        try {
            java.util.List<UserRouteDto> userRoutes = userRouteService.getAllUserRoutes();
            logger.info("Successfully fetched {} user routes", userRoutes.size());
            return ResponseEntity.ok(userRoutes);
        } catch (Exception e) {
            logger.error("Error fetching user routes: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(AdminErrorResponse.serverError("Failed to fetch user routes"));
        }
    }
    
    /**
     * GET /api/admin/user-routes/{userId} - Dohvatanje user route veza za određenog korisnika
     */
    @GetMapping("/user-routes/{userId}")
    public ResponseEntity<?> getUserRoutesByUserId(@PathVariable Long userId) {
        logger.info("GET /api/admin/user-routes/{} - Fetching user routes for user", userId);
        
        try {
            java.util.List<UserRouteDto> userRoutes = userRouteService.getUserRoutesByUserId(userId);
            logger.info("Successfully fetched {} user routes for user {}", userRoutes.size(), userId);
            return ResponseEntity.ok(userRoutes);
        } catch (Exception e) {
            logger.error("Error fetching user routes for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(AdminErrorResponse.serverError("Failed to fetch user routes"));
        }
    }
    
    
    /**
     * DELETE /api/admin/user-routes/{userId}/{routeId} - Brisanje user route veze
     */
    @DeleteMapping("/user-routes/{userId}/{routeId}")
    public ResponseEntity<?> deleteUserRoute(@PathVariable Long userId, @PathVariable Long routeId) {
        logger.info("DELETE /api/admin/user-routes/{}/{} - Deleting user route", userId, routeId);
        
        try {
            boolean deleted = userRouteService.deleteUserRoute(userId, routeId);
            if (deleted) {
                logger.info("Successfully deleted user route - userId: {}, routeId: {}", userId, routeId);
                return ResponseEntity.ok().build();
            } else {
                logger.error("User route not found for deletion - userId: {}, routeId: {}", userId, routeId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(AdminErrorResponse.serverError("User route not found"));
            }
        } catch (Exception e) {
            logger.error("Error deleting user route: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(AdminErrorResponse.serverError("Failed to delete user route"));
        }
    }
    
    /**
     * GET /api/admin/accessible-routes/{userId} - Rute dostupne korisniku
     */
    @GetMapping("/accessible-routes/{userId}")
    public ResponseEntity<?> getAccessibleRoutesForUser(@PathVariable Long userId) {
        logger.info("GET /api/admin/accessible-routes/{} - Getting accessible routes for user", userId);
        
        try {
            java.util.List<com.sirus.backend.dto.RouteDto> routes = userRouteService.getAccessibleRoutesForUser(userId);
            logger.info("Successfully fetched {} accessible routes for user {}", routes.size(), userId);
            return ResponseEntity.ok(routes);
        } catch (Exception e) {
            logger.error("Error fetching accessible routes for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(AdminErrorResponse.serverError("Failed to fetch accessible routes"));
        }
    }
    
    /**
     * GET /api/admin/accessible-user-routes/{userId} - User-routes dostupne korisniku
     */
    @GetMapping("/accessible-user-routes/{userId}")
    public ResponseEntity<?> getAccessibleUserRoutesForUser(@PathVariable Long userId) {
        logger.info("GET /api/admin/accessible-user-routes/{} - Getting accessible user routes for user", userId);
        
        try {
            java.util.List<com.sirus.backend.dto.UserRouteDto> userRoutes = userRouteService.getAccessibleUserRoutesForUser(userId);
            logger.info("Successfully fetched {} accessible user routes for user {}", userRoutes.size(), userId);
            return ResponseEntity.ok(userRoutes);
        } catch (Exception e) {
            logger.error("Error fetching accessible user routes for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(AdminErrorResponse.serverError("Failed to fetch accessible user routes"));
        }
    }
    
    /**
     * POST /api/admin/assign-route - Dodeli rutu korisniku (samo ako ima pristup)
     */
    @PostMapping("/assign-route")
    public ResponseEntity<?> assignRouteToUser(@RequestBody UserRouteRequest request) {
        logger.info("POST /api/admin/assign-route - Assigning route {} to user {}", 
                   request.getRouteId(), request.getUserId());
        
        try {
            // Proveri da li admin može da dodeli ovu rutu
            String routeSection = routeRepository.findById(request.getRouteId())
                .map(com.sirus.backend.entity.Route::getSekcija)
                .orElse("");
            
            if (!userRouteService.hasUserAccessToSection(request.getUserId(), routeSection)) {
                logger.error("User {} does not have access to route section: {}", request.getUserId(), routeSection);
                return ResponseEntity.badRequest()
                    .body(AdminErrorResponse.serverError("User does not have access to this route section"));
            }
            
            UserRouteDto userRouteDto = new UserRouteDto();
            userRouteDto.setUserId(request.getUserId());
            userRouteDto.setRouteId(request.getRouteId());
            
            UserRouteDto createdUserRoute = userRouteService.createUserRoute(userRouteDto);
            logger.info("Successfully assigned route {} to user {}", request.getRouteId(), request.getUserId());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUserRoute);
            
        } catch (Exception e) {
            logger.error("Error assigning route to user: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(AdminErrorResponse.serverError("Failed to assign route"));
        }
    }
    
    /**
     * GET /api/admin/test - Test endpoint za admin funkcionalnost
     */
    @GetMapping("/test")
    public ResponseEntity<?> adminTest() {
        logger.info("GET /api/admin/test - Admin test endpoint called");
        
        java.util.Map<String, Object> test = new java.util.HashMap<>();
        test.put("status", "OK");
        test.put("service", "Admin API");
        test.put("timestamp", java.time.LocalDateTime.now());
        test.put("endpoints", new String[]{
            "GET /api/admin/users",
            "GET /api/admin/users/count",
            "PUT /api/admin/users/{id}/role",
            "PUT /api/admin/users/{id}/approve",
            "PUT /api/admin/users/{id}/reject",
            "POST /api/admin/user-routes",
            "GET /api/admin/user-routes",
            "GET /api/admin/user-routes/{userId}",
            "DELETE /api/admin/user-routes/{userId}/{routeId}",
            "GET /api/admin/accessible-routes/{userId}",
            "GET /api/admin/accessible-user-routes/{userId}",
            "POST /api/admin/assign-route",
            "GET /api/admin/test"
        });
        test.put("cors_enabled", true);
        test.put("rate_limiting_enabled", true);
        
        return ResponseEntity.ok(test);
    }
}
