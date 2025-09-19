package com.sirus.backend.controller;

import com.sirus.backend.dto.UserRouteDto;
import com.sirus.backend.dto.UserLevelUpdateDto;
import com.sirus.backend.service.UserRouteService;
import com.sirus.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/user-routes")
@CrossOrigin(origins = "*")
public class UserRouteController {
    
    @Autowired
    private UserRouteService userRouteService;
    
    @Autowired
    private UserService userService;
    
    // GET /api/user-routes - Get all user routes
    @GetMapping
    public ResponseEntity<List<UserRouteDto>> getAllUserRoutes() {
        List<UserRouteDto> userRoutes = userRouteService.getAllUserRoutes();
        return ResponseEntity.ok(userRoutes);
    }
    
    // GET /api/user-routes/{userId} - Get user routes by user ID
    @GetMapping("/{userId}")
    public ResponseEntity<List<UserRouteDto>> getUserRoutesByUserId(@PathVariable Long userId) {
        List<UserRouteDto> userRoutes = userRouteService.getUserRoutesByUserId(userId);
        return ResponseEntity.ok(userRoutes);
    }
    
    // GET /api/user-routes/{userId}/{routeId} - Get specific user route
    @GetMapping("/{userId}/{routeId}")
    public ResponseEntity<UserRouteDto> getUserRoute(@PathVariable Long userId, @PathVariable Long routeId) {
        Optional<UserRouteDto> userRoute = userRouteService.getUserRoute(userId, routeId);
        return userRoute.map(ResponseEntity::ok)
                       .orElse(ResponseEntity.notFound().build());
    }
    
    // GET /api/user-routes/{userId}/check/{routeId} - Check if user has access to route
    @GetMapping("/{userId}/check/{routeId}")
    public ResponseEntity<Boolean> hasUserAccessToRoute(@PathVariable Long userId, @PathVariable Long routeId) {
        boolean hasAccess = userRouteService.hasUserAccessToRoute(userId, routeId);
        return ResponseEntity.ok(hasAccess);
    }
    
    // DEPRECATED ENDPOINTS - Removed due to nivo-based system migration to role-based
    // These endpoints are no longer available as nivoPristupa/nivoDozvole were removed
    
    // GET /api/user-routes/{userId}/route-name/{routeName} - Get user routes by route name
    @GetMapping("/{userId}/route-name/{routeName}")
    public ResponseEntity<List<UserRouteDto>> getUserRoutesByRouteName(
            @PathVariable Long userId, 
            @PathVariable String routeName) {
        List<UserRouteDto> userRoutes = userRouteService.getUserRoutesByRouteName(userId, routeName);
        return ResponseEntity.ok(userRoutes);
    }
    
    // GET /api/user-routes/{userId}/count - Count user routes
    @GetMapping("/{userId}/count")
    public ResponseEntity<Long> countUserRoutes(@PathVariable Long userId) {
        long count = userRouteService.countUserRoutes(userId);
        return ResponseEntity.ok(count);
    }
    
    // PUT /api/user-routes/users/{userId}/level - Update user access level
    @PutMapping("/users/{userId}/level")
    public ResponseEntity<com.sirus.backend.dto.UserDto> updateUserLevel(@PathVariable Long userId, @RequestBody UserLevelUpdateDto levelUpdate) {
        try {
            Optional<com.sirus.backend.dto.UserDto> updatedUser = userService.updateUserAccessLevel(userId, levelUpdate.getNivoPristupa());
            return updatedUser.map(ResponseEntity::ok)
                             .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}