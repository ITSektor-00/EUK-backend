package com.sirus.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RoleUpdateRequest {
    
    @JsonProperty("role")
    private String role;
    
    public RoleUpdateRequest() {}
    
    public RoleUpdateRequest(String role) {
        this.role = role;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    @Override
    public String toString() {
        return "RoleUpdateRequest{" +
                "role='" + role + '\'' +
                '}';
    }
}
