package com.sirus.backend.service;

import com.sirus.backend.dto.RouteDto;
import com.sirus.backend.entity.Route;
import com.sirus.backend.repository.RouteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RouteService {
    
    @Autowired
    private RouteRepository routeRepository;
    
    // Get all routes
    public List<RouteDto> getAllRoutes() {
        return routeRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    // Get route by ID
    public Optional<RouteDto> getRouteById(Long id) {
        return routeRepository.findById(id)
                .map(this::convertToDto);
    }
    
    // Get route by route path
    public Optional<RouteDto> getRouteByPath(String ruta) {
        return routeRepository.findByRuta(ruta)
                .map(this::convertToDto);
    }
    
    // Get routes by section
    public List<RouteDto> getRoutesBySection(String sekcija) {
        return routeRepository.findBySekcija(sekcija).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    // Get active routes
    public List<RouteDto> getActiveRoutes() {
        return routeRepository.findByAktivnaTrue().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    // DEPRECATED METHODS - Removed due to level-based system migration to role-based
    // These methods used nivoMin/nivoMax fields which have been removed
    
    // Check if route exists and is active
    public boolean isRouteActive(String ruta) {
        return routeRepository.existsByRutaAndAktivnaTrue(ruta);
    }
    
    // Search routes by name
    public List<RouteDto> searchRoutesByName(String naziv) {
        return routeRepository.findByNazivContainingIgnoreCase(naziv).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    // Search routes by description
    public List<RouteDto> searchRoutesByDescription(String opis) {
        return routeRepository.findByOpisContainingIgnoreCase(opis).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    // Create new route
    public RouteDto createRoute(RouteDto routeDto) {
        Route route = convertToEntity(routeDto);
        Route savedRoute = routeRepository.save(route);
        return convertToDto(savedRoute);
    }
    
    // Update route
    public Optional<RouteDto> updateRoute(Long id, RouteDto routeDto) {
        return routeRepository.findById(id)
                .map(existingRoute -> {
                    existingRoute.setRuta(routeDto.getRuta());
                    existingRoute.setNaziv(routeDto.getNaziv());
                    existingRoute.setOpis(routeDto.getOpis());
                    existingRoute.setSekcija(routeDto.getSekcija());
                    existingRoute.setAktivna(routeDto.getAktivna());
                    
                    Route savedRoute = routeRepository.save(existingRoute);
                    return convertToDto(savedRoute);
                });
    }
    
    // Delete route
    public boolean deleteRoute(Long id) {
        if (routeRepository.existsById(id)) {
            routeRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    // Convert Entity to DTO
    private RouteDto convertToDto(Route route) {
        return new RouteDto(
                route.getId(),
                route.getRuta(),
                route.getNaziv(),
                route.getOpis(),
                route.getSekcija(),
                route.getAktivna(),
                route.getDatumKreiranja()
        );
    }
    
    // Convert DTO to Entity
    private Route convertToEntity(RouteDto routeDto) {
        Route route = new Route();
        route.setRuta(routeDto.getRuta());
        route.setNaziv(routeDto.getNaziv());
        route.setOpis(routeDto.getOpis());
        route.setSekcija(routeDto.getSekcija());
        route.setAktivna(routeDto.getAktivna());
        return route;
    }
}
