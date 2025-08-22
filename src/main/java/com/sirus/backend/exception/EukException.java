package com.sirus.backend.exception;

public class EukException extends RuntimeException {
    
    public EukException(String message) {
        super(message);
    }
    
    public EukException(String message, Throwable cause) {
        super(message, cause);
    }
}
