package com.sirus.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/websocket")
public class TestWebSocketController {

    @GetMapping("/test")
    public Map<String, Object> testWebSocket() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "WebSocket test endpoint is working!");
        response.put("timestamp", LocalDateTime.now());
        response.put("status", "SUCCESS");
        response.put("cors", "enabled");
        
        return response;
    }
    
    @GetMapping("/status")
    public Map<String, Object> getWebSocketStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("websocket", "enabled");
        status.put("endpoint", "/ws");
        status.put("cors", "enabled");
        status.put("timestamp", LocalDateTime.now());
        
        return status;
    }
}
