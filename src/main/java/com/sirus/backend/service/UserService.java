package com.sirus.backend.service;

import com.sirus.backend.dto.UserDto;
import com.sirus.backend.entity.User;
import com.sirus.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Dohvatanje svih korisnika sa paginacijom i filterima
     */
    public Page<UserDto> findAllUsers(int page, int size, String role, Boolean isActive, String search) {
        Pageable pageable = PageRequest.of(page, size);
        
        // Mapiranje frontend role na backend role
        String mappedRole = mapFrontendRoleToBackend(role);
        
        if (search != null && !search.trim().isEmpty()) {
            // Pretraga po username, email, firstName ili lastName
            return userRepository.findByUsernameContainingOrEmailContainingOrFirstNameContainingOrLastNameContaining(
                search.trim(), search.trim(), search.trim(), search.trim(), pageable)
                .map(this::convertToDto);
        }
        
        if (mappedRole != null && isActive != null) {
            return userRepository.findByRoleAndIsActive(mappedRole, isActive, pageable)
                .map(this::convertToDto);
        } else if (mappedRole != null) {
            return userRepository.findByRole(mappedRole, pageable)
                .map(this::convertToDto);
        } else if (isActive != null) {
            return userRepository.findByIsActive(isActive, pageable)
                .map(this::convertToDto);
        }
        
        // Bez filtera - svi korisnici
        return userRepository.findAll(pageable).map(this::convertToDto);
    }
    
    /**
     * Mapiranje frontend role na backend role
     */
    private String mapFrontendRoleToBackend(String frontendRole) {
        if (frontendRole == null) return null;
        
        switch (frontendRole.toUpperCase()) {
            case "ADMIN":
                return "admin";
            case "OBRADJIVAC":
                return "obradjivaci predmeta";
            case "POTPISNIK":
                return "potpisnik";
            default:
                return frontendRole; // Vrati originalni ako ne prepoznaje
        }
    }
    
    /**
     * Dohvatanje korisnika po ID-u
     */
    public Optional<UserDto> findUserById(Long id) {
        return userRepository.findById(id).map(this::convertToDto);
    }
    
    /**
     * Dohvatanje korisnika po username-u
     */
    public Optional<UserDto> findUserByUsername(String username) {
        return userRepository.findByUsername(username).map(this::convertToDto);
    }
    
    /**
     * Dohvatanje korisnika po email-u
     */
    public Optional<UserDto> findUserByEmail(String email) {
        return userRepository.findByEmail(email).map(this::convertToDto);
    }
    
    /**
     * Dohvatanje korisnika po roli
     */
    public List<UserDto> findUsersByRole(String role) {
        return userRepository.findByRole(role).stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }
    
    /**
     * Dohvatanje aktivnih korisnika
     */
    public List<UserDto> findActiveUsers() {
        return userRepository.findByIsActiveTrue().stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }
    
    /**
     * Dohvatanje neaktivnih korisnika
     */
    public List<UserDto> findInactiveUsers() {
        return userRepository.findByIsActiveFalse().stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }
    
    /**
     * Broj ukupnih korisnika
     */
    public long getUserCount() {
        return userRepository.count();
    }
    
    /**
     * Broj aktivnih korisnika
     */
    public long getActiveUserCount() {
        return userRepository.countByIsActiveTrue();
    }
    
    /**
     * Broj korisnika koji čekaju odobrenje
     */
    public long getPendingUserCount() {
        return userRepository.countByIsActiveFalse();
    }
    
    /**
     * Ažuriranje role korisnika
     */
    public UserDto updateUserRole(Long userId, String newRole) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        
        // Validacija role
        if (!isValidRole(newRole)) {
            throw new RuntimeException("Invalid role: " + newRole + ". Valid roles are: admin, obradjivaci predmeta, potpisnik");
        }
        
        user.setRole(newRole);
        User updatedUser = userRepository.save(user);
        
        return convertToDto(updatedUser);
    }
    
    /**
     * Aktiviranje korisnika (approve)
     */
    public UserDto approveUser(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        
        user.setActive(true);
        User updatedUser = userRepository.save(user);
        
        return convertToDto(updatedUser);
    }
    
    /**
     * Deaktiviranje korisnika (reject)
     */
    public UserDto rejectUser(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        
        user.setActive(false);
        User updatedUser = userRepository.save(user);
        
        return convertToDto(updatedUser);
    }
    
    /**
     * Validacija role
     */
    private boolean isValidRole(String role) {
        return role != null && (
            role.equals("admin") || role.equals("ADMIN") ||
            role.equals("obradjivaci predmeta") || role.equals("OBRADJIVAC") ||
            role.equals("potpisnik") || role.equals("POTPISNIK")
        );
    }
    
    /**
     * Konverzija User entity u UserDto
     */
    private UserDto convertToDto(User user) {
        return new UserDto(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getFirstName(),
            user.getLastName(),
            user.getRole(),
            user.isActive(),
            user.getCreatedAt(),
            user.getUpdatedAt()
        );
    }
    
    // Update user access level - DEPRECATED (nivoPristupa removed)
    public Optional<UserDto> updateUserAccessLevel(Long userId, Integer nivoPristupa) {
        // This method is deprecated since nivoPristupa was removed
        // Use updateUserRole instead
        return userRepository.findById(userId)
                .map(user -> convertToDto(user));
    }
}
