package com.sirus.backend.dto;

import java.time.LocalDateTime;

public class UserRouteResponse {
    private Long id;
    private Long userId;
    private Long routeId;
    private String route;
    private Integer nivoDozvola;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserDto user;
    private RouteDto routeDto;
    
    // Constructors
    public UserRouteResponse() {}
    
    public UserRouteResponse(Long id, Long userId, Long routeId, String route, Integer nivoDozvola, 
                           LocalDateTime createdAt, LocalDateTime updatedAt, UserDto user, RouteDto routeDto) {
        this.id = id;
        this.userId = userId;
        this.routeId = routeId;
        this.route = route;
        this.nivoDozvola = nivoDozvola;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.user = user;
        this.routeDto = routeDto;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public Long getRouteId() {
        return routeId;
    }
    
    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }
    
    public String getRoute() {
        return route;
    }
    
    public void setRoute(String route) {
        this.route = route;
    }
    
    public Integer getNivoDozvola() {
        return nivoDozvola;
    }
    
    public void setNivoDozvola(Integer nivoDozvola) {
        this.nivoDozvola = nivoDozvola;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public UserDto getUser() {
        return user;
    }
    
    public void setUser(UserDto user) {
        this.user = user;
    }
    
    public RouteDto getRouteDto() {
        return routeDto;
    }
    
    public void setRouteDto(RouteDto routeDto) {
        this.routeDto = routeDto;
    }
}
