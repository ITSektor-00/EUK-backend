package com.sirus.backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Controller
public class WebSocketController {
    
    private static final Logger logger = LoggerFactory.getLogger(WebSocketController.class);
    
    private final SimpMessagingTemplate messagingTemplate;
    
    public WebSocketController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }
    
    /**
     * Handle messages sent to /app/message
     * Broadcasts to /topic/messages
     */
    @MessageMapping("/message")
    @SendTo("/topic/messages")
    public Map<String, Object> handleMessage(Map<String, Object> message) {
        logger.info("Received WebSocket message: {}", message);
        
        Map<String, Object> response = new HashMap<>();
        response.put("content", message.get("content"));
        response.put("timestamp", LocalDateTime.now());
        response.put("type", "MESSAGE");
        
        return response;
    }
    
    /**
     * Handle EUK-specific messages
     */
    @MessageMapping("/euk/update")
    @SendTo("/topic/euk-updates")
    public Map<String, Object> handleEukUpdate(Map<String, Object> update) {
        logger.info("Received EUK update via WebSocket: {}", update);
        
        Map<String, Object> response = new HashMap<>();
        response.put("entity", update.get("entity"));
        response.put("action", update.get("action"));
        response.put("timestamp", LocalDateTime.now());
        response.put("type", "EUK_UPDATE");
        
        return response;
    }
    
    /**
     * Send notification to specific user
     */
    public void sendNotificationToUser(String username, Map<String, Object> notification) {
        String destination = "/user/" + username + "/notifications";
        messagingTemplate.convertAndSendToUser(username, destination, notification);
        logger.info("Sent notification to user {}: {}", username, notification);
    }
    
    /**
     * Broadcast system message to all connected clients
     */
    public void broadcastSystemMessage(String message) {
        Map<String, Object> systemMessage = new HashMap<>();
        systemMessage.put("content", message);
        systemMessage.put("timestamp", LocalDateTime.now());
        systemMessage.put("type", "SYSTEM");
        
        messagingTemplate.convertAndSend("/topic/system", systemMessage);
        logger.info("Broadcasted system message: {}", message);
    }
    
    /**
     * Health check endpoint for WebSocket
     */
    @GetMapping("/ws/health")
    @ResponseBody
    public Map<String, Object> websocketHealth() {
        logger.info("WebSocket health check requested");
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "WebSocket");
        health.put("timestamp", LocalDateTime.now());
        health.put("endpoints", new String[]{
            "/ws",
            "/topic/messages",
            "/topic/euk-updates",
            "/topic/system"
        });
        health.put("cors_enabled", true);
        health.put("sockjs_enabled", true);
        
        return health;
    }
    
    /**
     * Simple WebSocket test endpoint
     */
    @GetMapping("/ws/test")
    @ResponseBody
    public Map<String, Object> websocketTest() {
        logger.info("WebSocket test endpoint called");
        Map<String, Object> test = new HashMap<>();
        test.put("message", "WebSocket is working!");
        test.put("timestamp", LocalDateTime.now());
        test.put("status", "CONNECTED");
        test.put("endpoints", new String[]{
            "/ws (SockJS)",
            "/ws-native (Native WebSocket)",
            "/ws/health",
            "/ws/test"
        });
        test.put("cors_enabled", true);
        test.put("sockjs_enabled", true);
        
        return test;
    }
    
    /**
     * WebSocket connection test endpoint
     */
    @GetMapping("/ws/connection-test")
    @ResponseBody
    public Map<String, Object> connectionTest() {
        logger.info("WebSocket connection test endpoint called");
        Map<String, Object> test = new HashMap<>();
        test.put("status", "READY");
        test.put("message", "WebSocket server is ready for connections");
        test.put("timestamp", LocalDateTime.now());
        test.put("supported_protocols", new String[]{
            "SockJS",
            "Native WebSocket"
        });
        test.put("test_urls", new String[]{
            "ws://localhost:8080/ws",
            "ws://localhost:8080/ws-native"
        });
        
        return test;
    }
}
