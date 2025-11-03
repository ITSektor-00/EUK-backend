package com.sirus.backend.controller;

import com.sirus.backend.dto.SignInRequest;
import com.sirus.backend.dto.SignUpRequest;
import com.sirus.backend.dto.UpdateProfileRequest;
import com.sirus.backend.dto.UpdateProfileResponse;
import com.sirus.backend.dto.AuthResponse;
import com.sirus.backend.dto.ErrorResponse;
import com.sirus.backend.dto.UsernameAvailabilityResponse;
import com.sirus.backend.entity.User;
import com.sirus.backend.service.AuthService;
import com.sirus.backend.service.JwtService;
import com.sirus.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private UserRepository userRepository;
    
    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody SignUpRequest request) {
        try {
            // Validacija
            if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
                logger.error("Username is null or empty");
                return ResponseEntity.badRequest().body(new ErrorResponse("VALIDATION_ERROR", "Korisničko ime je obavezno", "/api/auth/signup"));
            }
            if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                logger.error("Email is null or empty");
                return ResponseEntity.badRequest().body(new ErrorResponse("VALIDATION_ERROR", "Email adresa je obavezna", "/api/auth/signup"));
            }
            if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                logger.error("Password is null or empty");
                return ResponseEntity.badRequest().body(new ErrorResponse("VALIDATION_ERROR", "Lozinka je obavezna", "/api/auth/signup"));
            }
            if (request.getFirstName() == null || request.getFirstName().trim().isEmpty()) {
                logger.error("FirstName is null or empty");
                return ResponseEntity.badRequest().body(new ErrorResponse("VALIDATION_ERROR", "Ime je obavezno", "/api/auth/signup"));
            }
            if (request.getLastName() == null || request.getLastName().trim().isEmpty()) {
                logger.error("LastName is null or empty");
                return ResponseEntity.badRequest().body(new ErrorResponse("VALIDATION_ERROR", "Prezime je obavezno", "/api/auth/signup"));
            }
            
            AuthResponse response = authService.signUp(request);
            logger.info("User {} successfully registered and waiting for approval", request.getUsername());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error during signup: {}", e.getMessage(), e);
            // GlobalExceptionHandler će hendlati AuthException
            throw e;
        }
    }
    
    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@RequestBody SignInRequest request) {
        try {
            // Validacija
            if (request.getUsernameOrEmail() == null || request.getUsernameOrEmail().trim().isEmpty()) {
                logger.error("UsernameOrEmail is null or empty");
                return ResponseEntity.badRequest().body(new ErrorResponse("VALIDATION_ERROR", "Korisničko ime ili email je obavezan", "/api/auth/signin"));
            }
            if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                logger.error("Password is null or empty");
                return ResponseEntity.badRequest().body(new ErrorResponse("VALIDATION_ERROR", "Lozinka je obavezna", "/api/auth/signin"));
            }
            
            AuthResponse response = authService.signIn(request);
            logger.info("User {} successfully signed in", request.getUsernameOrEmail());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error during signin for user '{}': {}", 
                        request.getUsernameOrEmail(), e.getMessage(), e);
            
            // Vrati specifičnu grešku umesto da baciš exception
            if (e.getMessage().contains("Invalid credentials") || 
                e.getMessage().contains("User not found") ||
                e.getMessage().contains("Incorrect password")) {
                return ResponseEntity.status(401).body(new ErrorResponse(
                    "AUTH_ERROR", 
                    "Neispravno korisničko ime ili lozinka", 
                    "/api/auth/signin"
                ));
            }
            
            // GlobalExceptionHandler će hendlati ostale AuthException
            throw e;
        }
    }
    
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        try {
            // Uklonjen debug logging da se spreči beskonačna petlja
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body("Invalid authorization header");
            }
            
            String token = authHeader.substring(7); // Remove "Bearer "
            
            if (!jwtService.validateToken(token)) {
                return ResponseEntity.status(401).body("Invalid token");
            }
            
            String username = jwtService.getUsernameFromToken(token);
            String role = jwtService.getRoleFromToken(token);
            Long userId = jwtService.getUserIdFromToken(token);
            
            User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Vrati user data sa role iz JWT tokena (ne iz baze!)
            return ResponseEntity.ok(Map.of(
                "id", userId, // Koristi ID iz tokena
                "username", user.getUsername(),
                "email", user.getEmail(),
                "firstName", user.getFirstName(),
                "lastName", user.getLastName(),
                "role", role, // ✅ KORISTI ROLE IZ TOKENA!
                "isActive", user.isActive()
            ));
            
        } catch (Exception e) {
            logger.error("Error getting current user: {}", e.getMessage());
            return ResponseEntity.status(401).body("Error getting current user: " + e.getMessage());
        }
    }
    
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        logger.info("Auth test endpoint called");
        return ResponseEntity.ok("Auth API is working!");
    }
    
    @GetMapping("/debug")
    public ResponseEntity<Map<String, Object>> debug() {
        logger.info("Auth debug endpoint called");
        Map<String, Object> debug = new HashMap<>();
        debug.put("status", "OK");
        debug.put("timestamp", java.time.LocalDateTime.now());
        debug.put("endpoints", new String[]{
            "/api/auth/signup",
            "/api/auth/signin", 
            "/api/auth/me",
            "/api/auth/test",
            "/api/auth/debug"
        });
        debug.put("cors_enabled", true);
        debug.put("rate_limiting_enabled", true);
        
        return ResponseEntity.ok(debug);
    }
    
    @GetMapping("/check-username")
    public ResponseEntity<UsernameAvailabilityResponse> checkUsernameAvailability(
            @RequestParam String username) {
        try {
            logger.info("Checking username availability for: '{}'", username);
            
            // Validacija
            if (username == null || username.trim().isEmpty()) {
                logger.error("Username parameter is null or empty");
                return ResponseEntity.badRequest().body(new UsernameAvailabilityResponse(false, username));
            }
            
            boolean isAvailable = authService.isUsernameAvailable(username.trim());
            
            logger.info("Username '{}' availability: {}", username, isAvailable);
            
            return ResponseEntity.ok(new UsernameAvailabilityResponse(isAvailable, username));
            
        } catch (Exception e) {
            logger.error("Error checking username availability: {}", e.getMessage(), e);
            return ResponseEntity.ok(new UsernameAvailabilityResponse(false, username));
        }
    }
    
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestHeader("Authorization") String authHeader,
                                         @RequestBody UpdateProfileRequest request) {
        try {
            // Validacija Authorization header-a
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                logger.error("Invalid authorization header");
                return ResponseEntity.status(401).body(new ErrorResponse("AUTH_ERROR", "Nevažeći authorization header", "/api/auth/profile"));
            }
            
            String token = authHeader.substring(7); // Remove "Bearer "
            
            if (!jwtService.validateToken(token)) {
                return ResponseEntity.status(401).body(new ErrorResponse("AUTH_ERROR", "Nevažeći token", "/api/auth/profile"));
            }
            
            String username = jwtService.getUsernameFromToken(token);
            
            // Validacija request podataka
            if (request.getFirstName() == null || request.getFirstName().trim().isEmpty()) {
                logger.error("FirstName is null or empty");
                return ResponseEntity.badRequest().body(new ErrorResponse("VALIDATION_ERROR", "Ime je obavezno", "/api/auth/profile"));
            }
            if (request.getLastName() == null || request.getLastName().trim().isEmpty()) {
                logger.error("LastName is null or empty");
                return ResponseEntity.badRequest().body(new ErrorResponse("VALIDATION_ERROR", "Prezime je obavezno", "/api/auth/profile"));
            }
            if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                logger.error("Email is null or empty");
                return ResponseEntity.badRequest().body(new ErrorResponse("VALIDATION_ERROR", "Email adresa je obavezna", "/api/auth/profile"));
            }
            
            UpdateProfileResponse response = authService.updateProfile(username, request);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error updating profile: {}", e.getMessage(), e);
            
            // Ako je PostgreSQL greška, vrati specifičnu poruku
            if (e.getMessage().contains("prepared statement") || e.getMessage().contains("BadSqlGrammarException")) {
                return ResponseEntity.status(500).body(new ErrorResponse(
                    "DATABASE_ERROR", 
                    "Greška u bazi podataka. Molimo pokušajte ponovo.", 
                    "/api/auth/profile"
                ));
            }
            
            // GlobalExceptionHandler će hendlati AuthException
            throw e;
        }
    }
} 