package com.sirus.backend.dto;

import java.time.LocalDateTime;

public class TemplateGenerationResponseDto {
    
    private Long predmetId;
    private String templateFilePath;
    private String templateStatus;
    private LocalDateTime templateGeneratedAt;
    private String message;
    private boolean success;
    
    // Constructors
    public TemplateGenerationResponseDto() {}
    
    public TemplateGenerationResponseDto(Long predmetId, String templateFilePath, 
                                      String templateStatus, LocalDateTime templateGeneratedAt, 
                                      String message, boolean success) {
        this.predmetId = predmetId;
        this.templateFilePath = templateFilePath;
        this.templateStatus = templateStatus;
        this.templateGeneratedAt = templateGeneratedAt;
        this.message = message;
        this.success = success;
    }
    
    // Getters and Setters
    public Long getPredmetId() {
        return predmetId;
    }
    
    public void setPredmetId(Long predmetId) {
        this.predmetId = predmetId;
    }
    
    public String getTemplateFilePath() {
        return templateFilePath;
    }
    
    public void setTemplateFilePath(String templateFilePath) {
        this.templateFilePath = templateFilePath;
    }
    
    public String getTemplateStatus() {
        return templateStatus;
    }
    
    public void setTemplateStatus(String templateStatus) {
        this.templateStatus = templateStatus;
    }
    
    public LocalDateTime getTemplateGeneratedAt() {
        return templateGeneratedAt;
    }
    
    public void setTemplateGeneratedAt(LocalDateTime templateGeneratedAt) {
        this.templateGeneratedAt = templateGeneratedAt;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
}
