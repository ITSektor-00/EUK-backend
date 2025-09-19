package com.sirus.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AdminErrorResponse {
    
    @JsonProperty("error")
    private String error;
    
    @JsonProperty("code")
    private String code;
    
    @JsonProperty("message")
    private String message;
    
    @JsonProperty("timestamp")
    private String timestamp;
    
    public AdminErrorResponse() {
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
    
    public AdminErrorResponse(String error, String code, String message) {
        this();
        this.error = error;
        this.code = code;
        this.message = message;
    }
    
    public static AdminErrorResponse userNotFound(Long userId) {
        return new AdminErrorResponse(
            "User not found",
            "USER_NOT_FOUND",
            "User with ID " + userId + " not found"
        );
    }
    
    public static AdminErrorResponse invalidRole(String role) {
        return new AdminErrorResponse(
            "Invalid role",
            "INVALID_ROLE",
            "Role '" + role + "' is not valid. Valid roles are: admin, obradjivaci predmeta, potpisnik"
        );
    }
    
    public static AdminErrorResponse unauthorized() {
        return new AdminErrorResponse(
            "Unauthorized",
            "UNAUTHORIZED",
            "You don't have permission to perform this action"
        );
    }
    
    public static AdminErrorResponse serverError(String message) {
        return new AdminErrorResponse(
            "Server error",
            "SERVER_ERROR",
            message
        );
    }
    
    // Getters and setters
    public String getError() {
        return error;
    }
    
    public void setError(String error) {
        this.error = error;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    
    @Override
    public String toString() {
        return "AdminErrorResponse{" +
                "error='" + error + '\'' +
                ", code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
