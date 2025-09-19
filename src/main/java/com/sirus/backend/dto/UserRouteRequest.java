package com.sirus.backend.dto;

public class UserRouteRequest {
    private Long userId;
    private Long routeId;
    
    // Constructors
    public UserRouteRequest() {}
    
    public UserRouteRequest(Long userId, Long routeId) {
        this.userId = userId;
        this.routeId = routeId;
    }
    
    // Getters and Setters
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
    
}
