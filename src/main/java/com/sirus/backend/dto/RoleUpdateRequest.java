package com.sirus.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Pattern;

public class RoleUpdateRequest {
    
    @JsonProperty("role")
    @Pattern(regexp = "^(admin|korisnik)$", message = "Role must be either 'admin' or 'korisnik'")
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
