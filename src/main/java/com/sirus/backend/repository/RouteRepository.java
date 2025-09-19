package com.sirus.backend.repository;

import com.sirus.backend.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {
    
    // Find by route path
    Optional<Route> findByRuta(String ruta);
    
    // Find by section
    List<Route> findBySekcija(String sekcija);
    
    // Find active routes
    List<Route> findByAktivnaTrue();
    
    // DEPRECATED METHODS - Removed due to nivoMin/nivoMax field removal
    // These methods used level-based access control which has been replaced with role-based
    
    // Check if route exists and is active
    boolean existsByRutaAndAktivnaTrue(String ruta);
    
    // Find routes by name containing
    List<Route> findByNazivContainingIgnoreCase(String naziv);
    
    // Find routes by description containing
    List<Route> findByOpisContainingIgnoreCase(String opis);
}