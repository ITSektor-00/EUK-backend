package com.sirus.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serviranje generisanih template fajlova
        registry.addResourceHandler("/generated_templates/**")
                .addResourceLocations("file:generated_templates/");
    }
}