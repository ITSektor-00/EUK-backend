package com.sirus.backend.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

@Component
public class WebSocketEventListener {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        String origin = "unknown";
        if (headerAccessor.getSessionAttributes() != null) {
            Object originObj = headerAccessor.getSessionAttributes().get("origin");
            if (originObj != null) {
                origin = originObj.toString();
            }
        }
        
        logger.info("WebSocket connected - Session ID: {}, Origin: {}", sessionId, origin);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        String origin = "unknown";
        if (headerAccessor.getSessionAttributes() != null) {
            Object originObj = headerAccessor.getSessionAttributes().get("origin");
            if (originObj != null) {
                origin = originObj.toString();
            }
        }
        
        logger.info("WebSocket disconnected - Session ID: {}, Origin: {}", sessionId, origin);
    }

    @EventListener
    public void handleWebSocketSubscribeListener(SessionSubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        String destination = headerAccessor.getDestination();
        
        logger.info("WebSocket subscribed - Session ID: {}, Destination: {}", sessionId, destination);
    }

    @EventListener
    public void handleWebSocketUnsubscribeListener(SessionUnsubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        String destination = headerAccessor.getDestination();
        
        logger.info("WebSocket unsubscribed - Session ID: {}, Destination: {}", sessionId, destination);
    }
}
