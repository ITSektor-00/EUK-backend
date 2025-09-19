package com.sirus.backend.config;

import com.sirus.backend.service.CustomUserDetailsService;
import com.sirus.backend.service.JwtService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@Profile("prod") // Samo za production profil
public class ProductionSecurityConfig {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public ProductionSecurityConfig(JwtService jwtService, CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // Dozvoli sve OPTIONS zahteve
                .requestMatchers("/api/auth/**", "/actuator/health").permitAll()
                .requestMatchers("/api/test/**").permitAll()
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/api/euk/**").permitAll() // Dozvoli EUK endpoint-e
                .requestMatchers("/api/users/**").permitAll() // Dozvoli Users endpoint-e
                .requestMatchers("/api/admin/**").permitAll() // Dozvoli Admin endpoint-e
                .requestMatchers("/api/websocket/**").permitAll() // Dozvoli WebSocket test endpoint-e
                .requestMatchers("/api/user-permissions/**").permitAll() // Dozvoli User Permissions endpoint-e
                .requestMatchers("/ws/**").permitAll() // Dozvoli WebSocket endpoint-e
                .anyRequest().authenticated()
            )
            .addFilterBefore(new JwtAuthenticationFilter(jwtService, userDetailsService), 
                           UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Get allowed domains from environment variable or use defaults
        String allowedDomains = System.getenv("EUK_ALLOWED_DOMAINS");
        if (allowedDomains != null && !allowedDomains.isEmpty()) {
            configuration.setAllowedOrigins(Arrays.asList(allowedDomains.split(",")));
        } else {
            // Default domains
            configuration.setAllowedOrigins(List.of(
                "https://euk.vercel.app",
                "https://euk-it-sectors-projects.vercel.app",
                "http://localhost:3000",
                "http://localhost:3001",
                "http://127.0.0.1:3000"
            ));
        }
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With", "Accept"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        // Dodaj eksplicitne CORS headers
        configuration.addExposedHeader("Access-Control-Allow-Origin");
        configuration.addExposedHeader("Access-Control-Allow-Methods");
        configuration.addExposedHeader("Access-Control-Allow-Headers");
        configuration.addExposedHeader("Access-Control-Allow-Credentials");
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


} 