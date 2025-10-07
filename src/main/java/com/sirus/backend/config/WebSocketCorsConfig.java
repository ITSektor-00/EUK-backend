package com.sirus.backend.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketCorsConfig implements WebSocketMessageBrokerConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketCorsConfig.class);

    @Override
    public void registerStompEndpoints(@NonNull StompEndpointRegistry registry) {
        logger.info("Configuring WebSocket endpoints...");
        
        // WebSocket endpoint sa CORS podrškom - samo za WebSocket
        registry.addEndpoint("/ws")
                .setAllowedOrigins(
                    "https://euk.vercel.app", 
                    "https://euk-it-sectors-projects.vercel.app", 
                    "http://localhost:3000", 
                    "http://127.0.0.1:3000",
                    "http://localhost:8080",
                    "http://127.0.0.1:8080",
                    "http://localhost:3001",
                    "http://127.0.0.1:3001"
                )
                .withSockJS()
                .setHeartbeatTime(25000)
                .setDisconnectDelay(5000)
                .setStreamBytesLimit(128 * 1024)
                .setHttpMessageCacheSize(1000)
                .setSessionCookieNeeded(false);
                
        // Dodajemo i alternativni endpoint bez SockJS
        registry.addEndpoint("/ws-native")
                .setAllowedOrigins(
                    "https://euk.vercel.app", 
                    "https://euk-it-sectors-projects.vercel.app", 
                    "http://localhost:3000", 
                    "http://127.0.0.1:3000",
                    "http://localhost:8080",
                    "http://127.0.0.1:8080"
                )
;
                
        logger.info("WebSocket endpoints configured successfully");
    }

    @Override
    public void configureMessageBroker(@NonNull MessageBrokerRegistry registry) {
        // Simple message broker
        registry.enableSimpleBroker("/topic", "/queue");
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
    }
    
    @Override
    public void configureWebSocketTransport(@NonNull WebSocketTransportRegistration registration) {
        // Transport konfiguracija
        registration.setSendTimeLimit(30000);
        registration.setSendBufferSizeLimit(1024 * 1024);
        registration.setMessageSizeLimit(256 * 1024);
    }
}
