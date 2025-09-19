package com.sirus.backend.dto;

public class UpdateRouteRequest {
    private Integer nivoDozvola;
    
    // Constructors
    public UpdateRouteRequest() {}
    
    public UpdateRouteRequest(Integer nivoDozvola) {
        this.nivoDozvola = nivoDozvola;
    }
    
    // Getters and Setters
    public Integer getNivoDozvola() {
        return nivoDozvola;
    }
    
    public void setNivoDozvola(Integer nivoDozvola) {
        this.nivoDozvola = nivoDozvola;
    }
}
