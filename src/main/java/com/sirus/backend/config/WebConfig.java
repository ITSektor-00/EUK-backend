package com.sirus.backend.config;

import com.sirus.backend.interceptor.LicenseCheckInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Autowired
    private LicenseCheckInterceptor licenseCheckInterceptor;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(licenseCheckInterceptor)
                .addPathPatterns("/api/**") // Primjenjuje se na sve API endpoint-e
                .excludePathPatterns(
                    "/api/licenses/status",
                    "/api/licenses/check", 
                    "/api/licenses/create",
                    "/api/auth/**",
                    "/api/public/**",
                    "/error"
                );
    }
}
