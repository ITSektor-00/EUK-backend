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
@Profile("!prod") // Za sve profile osim production
public class DevelopmentSecurityConfig {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public DevelopmentSecurityConfig(JwtService jwtService, CustomUserDetailsService userDetailsService) {
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
                .requestMatchers("/api/generate-envelope-pdf").permitAll() // Dozvoli PDF generisanje
                .requestMatchers("/api/generate-envelope-back-side-pdf").permitAll() // Dozvoli back side PDF generisanje
                .requestMatchers("/api/test-envelope-pdf").permitAll() // Dozvoli test PDF endpoint
                .requestMatchers("/api/test-pdf-simple").permitAll() // Dozvoli jednostavan test
                .requestMatchers("/api/test-pdf-font").permitAll() // Dozvoli font test
                .requestMatchers("/api/test-cyrillic").permitAll() // Dozvoli ćirilica test
                .requestMatchers("/api/test-font-loading").permitAll() // Dozvoli font loading test
                .requestMatchers("/api/test-simple-pdf").permitAll() // Dozvoli simple PDF test
                .requestMatchers("/api/test-basic-pdf").permitAll() // Dozvoli basic PDF test
                .requestMatchers("/api/template/**").permitAll() // Dozvoli template endpoint-e
                .requestMatchers("/api/template/obrasci-vrste/**").permitAll() // Dozvoli template obrasci vrste
                .requestMatchers("/api/template/organizaciona-struktura/**").permitAll() // Dozvoli template organizaciona struktura
                .requestMatchers("/api/kategorije/**").permitAll() // Dozvoli kategorije endpoint-e
                .requestMatchers("/api/obrasci-vrste/**").permitAll() // Dozvoli obrasci vrste endpoint-e
                .requestMatchers("/api/organizaciona-struktura/**").permitAll() // Dozvoli organizaciona struktura endpoint-e
                .requestMatchers("/api/predmeti/**").permitAll() // Dozvoli predmeti endpoint-e
                .requestMatchers("/api/t1-lice/**").permitAll() // Dozvoli T1 lice endpoint-e
                .requestMatchers("/api/t2-lice/**").permitAll() // Dozvoli T2 lice endpoint-e
                .requestMatchers("/generated_templates/**").permitAll() // Dozvoli pristup generisanim template-ima
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
        
        // Development CORS - dozvoli samo localhost:3000
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With", "Accept"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        // Dodaj eksplicitne CORS headers za download
        configuration.addExposedHeader("Content-Disposition");
        configuration.addExposedHeader("Content-Type");
        configuration.addExposedHeader("Content-Length");
        configuration.addExposedHeader("Cache-Control");
        configuration.addExposedHeader("Pragma");
        configuration.addExposedHeader("Expires");
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
