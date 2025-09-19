package com.sirus.backend.dto;

public class UserLevelUpdateDto {
    
    private Integer nivoPristupa;
    
    // Constructors
    public UserLevelUpdateDto() {}
    
    public UserLevelUpdateDto(Integer nivoPristupa) {
        this.nivoPristupa = nivoPristupa;
    }
    
    // Getters and Setters
    public Integer getNivoPristupa() {
        return nivoPristupa;
    }
    
    public void setNivoPristupa(Integer nivoPristupa) {
        this.nivoPristupa = nivoPristupa;
    }
}
