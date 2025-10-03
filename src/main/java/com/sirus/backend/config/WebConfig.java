package com.sirus.backend.config;

import com.sirus.backend.interceptor.GlobalLicenseCheckInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.lang.NonNull;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Autowired
    private GlobalLicenseCheckInterceptor globalLicenseCheckInterceptor;
    
    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(globalLicenseCheckInterceptor)
                .addPathPatterns("/api/**") // Primjenjuje se na sve API endpoint-e
                .excludePathPatterns(
                    "/api/global-license/**",  // ✅ Exclude sve global license endpoint-e
                    "/api/auth/**",
                    "/api/public/**",
                    "/error",
                    "/actuator/**"
                );
    }
}
