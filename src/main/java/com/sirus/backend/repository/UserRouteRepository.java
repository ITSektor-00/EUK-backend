package com.sirus.backend.repository;

import com.sirus.backend.entity.UserRoute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRouteRepository extends JpaRepository<UserRoute, Long> {
    
    // Find by user ID
    List<UserRoute> findByUserId(Long userId);
    
    // Find by route ID
    List<UserRoute> findByRouteId(Long routeId);
    
    // Find by user and route
    Optional<UserRoute> findByUserIdAndRouteId(Long userId, Long routeId);
    
    // Check if user has access to route
    boolean existsByUserIdAndRouteId(Long userId, Long routeId);
    
    // DEPRECATED METHODS - Removed due to nivoDozvole field removal
    
    // Find all user routes with user and route details
    @Query("SELECT ur FROM UserRoute ur " +
           "LEFT JOIN FETCH ur.user u " +
           "LEFT JOIN FETCH ur.route r " +
           "WHERE ur.userId = :userId")
    List<UserRoute> findByUserIdWithDetails(@Param("userId") Long userId);
    
    // Find all user routes with user and route details
    @Query("SELECT ur FROM UserRoute ur " +
           "LEFT JOIN FETCH ur.user u " +
           "LEFT JOIN FETCH ur.route r")
    List<UserRoute> findAllWithDetails();
    
    // DEPRECATED - findByAccessLevelRange removed due to nivoDozvole field removal
    
    // Delete by user and route
    void deleteByUserIdAndRouteId(Long userId, Long routeId);
    
    // Count user routes
    long countByUserId(Long userId);
    
    // Find user routes with specific route name
    @Query("SELECT ur FROM UserRoute ur " +
           "LEFT JOIN FETCH ur.user u " +
           "LEFT JOIN FETCH ur.route r " +
           "WHERE ur.userId = :userId AND r.ruta = :routeName")
    List<UserRoute> findByUserIdAndRouteName(@Param("userId") Long userId, @Param("routeName") String routeName);
    
    // Find user routes by user ID and route section
    @Query("SELECT ur FROM UserRoute ur " +
           "LEFT JOIN FETCH ur.user u " +
           "LEFT JOIN FETCH ur.route r " +
           "WHERE ur.userId = :userId AND r.sekcija = :sekcija")
    List<UserRoute> findByUserIdAndRouteSekcija(@Param("userId") Long userId, @Param("sekcija") String sekcija);
}