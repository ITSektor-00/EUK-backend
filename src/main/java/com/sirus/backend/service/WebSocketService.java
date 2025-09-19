package com.sirus.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class WebSocketService {
    
    private static final Logger logger = LoggerFactory.getLogger(WebSocketService.class);
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    /**
     * Broadcast EUK entity update to all connected clients
     */
    public void broadcastEukUpdate(String entityType, String action, Object entityData) {
        Map<String, Object> update = new HashMap<>();
        update.put("entityType", entityType);
        update.put("action", action);
        update.put("entityData", entityData);
        update.put("timestamp", LocalDateTime.now());
        update.put("type", "EUK_UPDATE");
        
        messagingTemplate.convertAndSend("/topic/euk-updates", update);
        logger.info("Broadcasted EUK update: {} {} for {}", action, entityType, entityData);
    }
    
    /**
     * Send notification to specific user
     */
    public void sendUserNotification(String username, String message, String type) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("message", message);
        notification.put("type", type);
        notification.put("timestamp", LocalDateTime.now());
        
        String destination = "/user/" + username + "/notifications";
        messagingTemplate.convertAndSendToUser(username, destination, notification);
        logger.info("Sent notification to user {}: {}", username, message);
    }
    
    /**
     * Broadcast system message to all clients
     */
    public void broadcastSystemMessage(String message, String level) {
        Map<String, Object> systemMessage = new HashMap<>();
        systemMessage.put("message", message);
        systemMessage.put("level", level); // INFO, WARNING, ERROR
        systemMessage.put("timestamp", LocalDateTime.now());
        systemMessage.put("type", "SYSTEM");
        
        messagingTemplate.convertAndSend("/topic/system", systemMessage);
        logger.info("Broadcasted system message: {} (level: {})", message, level);
    }
    
    /**
     * Send EUK-specific notification
     */
    public void sendEukNotification(String message, String category) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("message", message);
        notification.put("category", category);
        notification.put("timestamp", LocalDateTime.now());
        notification.put("type", "EUK_NOTIFICATION");
        
        messagingTemplate.convertAndSend("/topic/euk-notifications", notification);
        logger.info("Sent EUK notification: {} (category: {})", message, category);
    }
    
    /**
     * Notify about new EUK predmet
     */
    public void notifyNewPredmet(String predmetName, String kategorija) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("message", "Novi predmet kreiran: " + predmetName);
        notification.put("predmetName", predmetName);
        notification.put("kategorija", kategorija);
        notification.put("timestamp", LocalDateTime.now());
        notification.put("type", "NEW_PREDMET");
        
        messagingTemplate.convertAndSend("/topic/euk-notifications", notification);
        logger.info("Notified about new predmet: {} in kategorija: {}", predmetName, kategorija);
    }
    
    /**
     * Notify about EUK predmet status change
     */
    public void notifyPredmetStatusChange(String predmetName, String oldStatus, String newStatus) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("message", "Status predmeta promenjen: " + predmetName);
        notification.put("predmetName", predmetName);
        notification.put("oldStatus", oldStatus);
        notification.put("newStatus", newStatus);
        notification.put("timestamp", LocalDateTime.now());
        notification.put("type", "STATUS_CHANGE");
        
        messagingTemplate.convertAndSend("/topic/euk-notifications", notification);
        logger.info("Notified about status change for predmet: {} from {} to {}", 
                   predmetName, oldStatus, newStatus);
    }
}
