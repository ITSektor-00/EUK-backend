package com.sirus.backend.controller;

import com.sirus.backend.dto.RouteDto;
import com.sirus.backend.service.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/routes")
@CrossOrigin(origins = "*")
public class RouteController {
    
    @Autowired
    private RouteService routeService;
    
    // DEPRECATED - getAllRoutes moved to AdminController to avoid ambiguous mapping
    
    // GET /api/admin/routes/{id} - Get route by ID
    @GetMapping("/{id}")
    public ResponseEntity<RouteDto> getRouteById(@PathVariable Long id) {
        Optional<RouteDto> route = routeService.getRouteById(id);
        return route.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }
    
    // GET /api/admin/routes/path/{ruta} - Get route by path
    @GetMapping("/path/{ruta}")
    public ResponseEntity<RouteDto> getRouteByPath(@PathVariable String ruta) {
        Optional<RouteDto> route = routeService.getRouteByPath(ruta);
        return route.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }
    
    // GET /api/admin/routes/section/{sekcija} - Get routes by section
    @GetMapping("/section/{sekcija}")
    public ResponseEntity<List<RouteDto>> getRoutesBySection(@PathVariable String sekcija) {
        List<RouteDto> routes = routeService.getRoutesBySection(sekcija);
        return ResponseEntity.ok(routes);
    }
    
    // GET /api/admin/routes/active - Get active routes
    @GetMapping("/active")
    public ResponseEntity<List<RouteDto>> getActiveRoutes() {
        List<RouteDto> routes = routeService.getActiveRoutes();
        return ResponseEntity.ok(routes);
    }
    
    // DEPRECATED ENDPOINTS - Removed due to level-based system migration to role-based
    // These endpoints used nivoMin/nivoMax fields which have been removed
    
    // GET /api/admin/routes/check/{ruta} - Check if route is active
    @GetMapping("/check/{ruta}")
    public ResponseEntity<Boolean> isRouteActive(@PathVariable String ruta) {
        boolean isActive = routeService.isRouteActive(ruta);
        return ResponseEntity.ok(isActive);
    }
    
    // GET /api/admin/routes/search/name/{naziv} - Search routes by name
    @GetMapping("/search/name/{naziv}")
    public ResponseEntity<List<RouteDto>> searchRoutesByName(@PathVariable String naziv) {
        List<RouteDto> routes = routeService.searchRoutesByName(naziv);
        return ResponseEntity.ok(routes);
    }
    
    // GET /api/admin/routes/search/description/{opis} - Search routes by description
    @GetMapping("/search/description/{opis}")
    public ResponseEntity<List<RouteDto>> searchRoutesByDescription(@PathVariable String opis) {
        List<RouteDto> routes = routeService.searchRoutesByDescription(opis);
        return ResponseEntity.ok(routes);
    }
    
    // POST /api/admin/routes - Create new route
    @PostMapping
    public ResponseEntity<RouteDto> createRoute(@RequestBody RouteDto routeDto) {
        try {
            RouteDto createdRoute = routeService.createRoute(routeDto);
            return ResponseEntity.ok(createdRoute);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // PUT /api/admin/routes/{id} - Update route
    @PutMapping("/{id}")
    public ResponseEntity<RouteDto> updateRoute(@PathVariable Long id, @RequestBody RouteDto routeDto) {
        Optional<RouteDto> updatedRoute = routeService.updateRoute(id, routeDto);
        return updatedRoute.map(ResponseEntity::ok)
                          .orElse(ResponseEntity.notFound().build());
    }
    
    // DELETE /api/admin/routes/{id} - Delete route
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoute(@PathVariable Long id) {
        boolean deleted = routeService.deleteRoute(id);
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}
