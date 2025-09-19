package com.sirus.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.profiles.active=dev"
})
public class WebSocketConfigTest {

    @Test
    public void contextLoads() {
        // This test will fail if the WebSocket configuration is incorrect
        // If it passes, the WebSocket beans are properly configured
    }
}
