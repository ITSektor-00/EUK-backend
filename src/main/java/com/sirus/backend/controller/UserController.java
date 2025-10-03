package com.sirus.backend.controller;

import com.sirus.backend.service.UserService;
import com.sirus.backend.dto.UserDto;
import com.sirus.backend.dto.PaginatedResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    
    @Autowired
    private UserService userService;
    
    /**
     * GET /api/users - Dohvatanje svih korisnika sa paginacijom
     */
    @GetMapping
    public ResponseEntity<PaginatedResponse<UserDto>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) String search) {
        
        logger.info("GET /api/users - Fetching users with pagination - page: {}, size: {}, role: {}, isActive: {}, search: {}", 
                   page, size, role, isActive, search);
        
        try {
            Page<UserDto> users = userService.findAllUsers(page, size, role, isActive, search);
            return ResponseEntity.ok(PaginatedResponse.from(users));
        } catch (Exception e) {
            logger.error("Error fetching users: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * GET /api/users/{id} - Dohvatanje korisnika po ID-u
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        logger.info("GET /api/users/{} - Fetching user by ID", id);
        
        try {
            Optional<UserDto> user = userService.findUserById(id);
            if (user.isPresent()) {
                return ResponseEntity.ok(user.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error fetching user by ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * GET /api/users/username/{username} - Dohvatanje korisnika po username-u
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<UserDto> getUserByUsername(@PathVariable String username) {
        logger.info("GET /api/users/username/{} - Fetching user by username", username);
        
        try {
            Optional<UserDto> user = userService.findUserByUsername(username);
            if (user.isPresent()) {
                return ResponseEntity.ok(user.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error fetching user by username {}: {}", username, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * GET /api/users/email/{email} - Dohvatanje korisnika po email-u
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<UserDto> getUserByEmail(@PathVariable String email) {
        logger.info("GET /api/users/email/{} - Fetching user by email", email);
        
        try {
            Optional<UserDto> user = userService.findUserByEmail(email);
            if (user.isPresent()) {
                return ResponseEntity.ok(user.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error fetching user by email {}: {}", email, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * GET /api/users/role/{role} - Dohvatanje korisnika po roli
     */
    @GetMapping("/role/{role}")
    public ResponseEntity<List<UserDto>> getUsersByRole(@PathVariable String role) {
        logger.info("GET /api/users/role/{} - Fetching users by role", role);
        
        try {
            List<UserDto> users = userService.findUsersByRole(role);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            logger.error("Error fetching users by role {}: {}", role, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * GET /api/users/active - Dohvatanje aktivnih korisnika
     */
    @GetMapping("/active")
    public ResponseEntity<List<UserDto>> getActiveUsers() {
        logger.info("GET /api/users/active - Fetching active users");
        
        try {
            List<UserDto> users = userService.findActiveUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            logger.error("Error fetching active users: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * GET /api/users/inactive - Dohvatanje neaktivnih korisnika
     */
    @GetMapping("/inactive")
    public ResponseEntity<List<UserDto>> getInactiveUsers() {
        logger.info("GET /api/users/inactive - Fetching inactive users");
        
        try {
            List<UserDto> users = userService.findInactiveUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            logger.error("Error fetching inactive users: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * GET /api/users/count - Broj ukupnih korisnika
     */
    @GetMapping("/count")
    public ResponseEntity<Long> getUserCount() {
        logger.info("GET /api/users/count - Getting user count");
        
        try {
            long count = userService.getUserCount();
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            logger.error("Error getting user count: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * GET /api/users/count/active - Broj aktivnih korisnika
     */
    @GetMapping("/count/active")
    public ResponseEntity<Long> getActiveUserCount() {
        logger.info("GET /api/users/count/active - Getting active user count");
        
        try {
            long count = userService.getActiveUserCount();
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            logger.error("Error getting active user count: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
