package com.sirus.backend.service;

import com.sirus.backend.dto.UserRouteDto;
import com.sirus.backend.entity.UserRoute;
import com.sirus.backend.repository.UserRouteRepository;
import com.sirus.backend.repository.UserRepository;
import com.sirus.backend.repository.RouteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserRouteService {
    
    @Autowired
    private UserRouteRepository userRouteRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RouteRepository routeRepository;
    
    // Get all user routes
    public List<UserRouteDto> getAllUserRoutes() {
        return userRouteRepository.findAllWithDetails().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    // Get user routes by user ID
    public List<UserRouteDto> getUserRoutesByUserId(Long userId) {
        return userRouteRepository.findByUserIdWithDetails(userId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    // Get user route by user ID and route ID
    public Optional<UserRouteDto> getUserRoute(Long userId, Long routeId) {
        return userRouteRepository.findByUserIdAndRouteId(userId, routeId)
                .map(this::convertToDto);
    }
    
    // Check if user has access to route
    public boolean hasUserAccessToRoute(Long userId, Long routeId) {
        return userRouteRepository.existsByUserIdAndRouteId(userId, routeId);
    }
    
    
    // Get user routes by route name
    public List<UserRouteDto> getUserRoutesByRouteName(Long userId, String routeName) {
        return userRouteRepository.findByUserIdAndRouteName(userId, routeName).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    // Create new user route
    public UserRouteDto createUserRoute(UserRouteDto userRouteDto) {
        // Validate user exists
        if (!userRepository.existsById(userRouteDto.getUserId())) {
            throw new IllegalArgumentException("User with ID " + userRouteDto.getUserId() + " does not exist");
        }
        
        // Validate route exists
        if (!routeRepository.existsById(userRouteDto.getRouteId())) {
            throw new IllegalArgumentException("Route with ID " + userRouteDto.getRouteId() + " does not exist");
        }
        
        // Check if user route already exists
        if (userRouteRepository.existsByUserIdAndRouteId(userRouteDto.getUserId(), userRouteDto.getRouteId())) {
            throw new IllegalArgumentException("User route already exists");
        }
        
        UserRoute userRoute = convertToEntity(userRouteDto);
        UserRoute savedUserRoute = userRouteRepository.save(userRoute);
        return convertToDto(savedUserRoute);
    }
    
    
    // Delete user route
    public boolean deleteUserRoute(Long userId, Long routeId) {
        if (userRouteRepository.existsByUserIdAndRouteId(userId, routeId)) {
            userRouteRepository.deleteByUserIdAndRouteId(userId, routeId);
            return true;
        }
        return false;
    }
    
    // Count user routes
    public long countUserRoutes(Long userId) {
        return userRouteRepository.countByUserId(userId);
    }
    
    // Proveri da li korisnik ima pristup sekciji
    public boolean hasUserAccessToSection(Long userId, String section) {
        String userRole = userRepository.findById(userId)
            .map(com.sirus.backend.entity.User::getRole)
            .orElse(null);
        
        if (userRole == null) return false;
        
        // ADMIN ima pristup samo ADMIN sekcijama
        if ("ADMIN".equals(userRole)) {
            return "ADMIN".equals(section);
        }
        
        // Ostali korisnici imaju pristup samo EUK sekciji
        return "EUK".equals(section);
    }
    
    // Dohvati rute koje korisnik može da vidi
    public java.util.List<com.sirus.backend.dto.RouteDto> getAccessibleRoutesForUser(Long userId) {
        String userRole = userRepository.findById(userId)
            .map(com.sirus.backend.entity.User::getRole)
            .orElse(null);
        
        if (userRole == null) return java.util.Collections.emptyList();
        
        if ("ADMIN".equals(userRole)) {
            // ADMIN vidi samo ADMIN rute
            return routeRepository.findBySekcija("ADMIN").stream()
                .map(this::convertRouteToDto)
                .collect(java.util.stream.Collectors.toList());
        } else {
            // Ostali korisnici vide samo EUK rute
            return routeRepository.findBySekcija("EUK").stream()
                .map(this::convertRouteToDto)
                .collect(java.util.stream.Collectors.toList());
        }
    }
    
    // Dohvati user-routes koje korisnik može da vidi
    public java.util.List<UserRouteDto> getAccessibleUserRoutesForUser(Long userId) {
        String userRole = userRepository.findById(userId)
            .map(com.sirus.backend.entity.User::getRole)
            .orElse(null);
        
        if (userRole == null) return java.util.Collections.emptyList();
        
        if ("ADMIN".equals(userRole)) {
            // ADMIN vidi samo ADMIN user-routes
            return userRouteRepository.findByUserIdAndRouteSekcija(userId, "ADMIN").stream()
                .map(this::convertToDto)
                .collect(java.util.stream.Collectors.toList());
        } else {
            // Ostali korisnici vide samo EUK user-routes
            return userRouteRepository.findByUserIdAndRouteSekcija(userId, "EUK").stream()
                .map(this::convertToDto)
                .collect(java.util.stream.Collectors.toList());
        }
    }
    
    // Convert Entity to DTO
    private UserRouteDto convertToDto(UserRoute userRoute) {
        UserRouteDto dto = new UserRouteDto(
                userRoute.getId(),
                userRoute.getUserId(),
                userRoute.getRouteId(),
                userRoute.getRoute() != null ? userRoute.getRoute().getRuta() : null,
                userRoute.getCreatedAt(),
                userRoute.getUpdatedAt()
        );
        
        // Set nested DTOs if available
        if (userRoute.getUser() != null) {
            dto.setUser(convertUserToDto(userRoute.getUser()));
        }
        
        if (userRoute.getRoute() != null) {
            dto.setRouteDto(convertRouteToDto(userRoute.getRoute()));
        }
        
        return dto;
    }
    
    // Convert DTO to Entity
    private UserRoute convertToEntity(UserRouteDto userRouteDto) {
        UserRoute userRoute = new UserRoute();
        userRoute.setUserId(userRouteDto.getUserId());
        userRoute.setRouteId(userRouteDto.getRouteId());
        return userRoute;
    }
    
    // Helper method to convert User to UserDto
    private com.sirus.backend.dto.UserDto convertUserToDto(com.sirus.backend.entity.User user) {
        com.sirus.backend.dto.UserDto userDto = new com.sirus.backend.dto.UserDto();
        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setEmail(user.getEmail());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setRole(user.getRole());
        userDto.setIsActive(user.isActive());
        userDto.setCreatedAt(user.getCreatedAt());
        userDto.setUpdatedAt(user.getUpdatedAt());
        return userDto;
    }
    
    // Helper method to convert Route to RouteDto
    private com.sirus.backend.dto.RouteDto convertRouteToDto(com.sirus.backend.entity.Route route) {
        com.sirus.backend.dto.RouteDto routeDto = new com.sirus.backend.dto.RouteDto();
        routeDto.setId(route.getId());
        routeDto.setRuta(route.getRuta());
        routeDto.setNaziv(route.getNaziv());
        routeDto.setOpis(route.getOpis());
        routeDto.setSekcija(route.getSekcija());
        routeDto.setAktivna(route.getAktivna());
        routeDto.setDatumKreiranja(route.getDatumKreiranja());
        return routeDto;
    }
}