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
                .requestMatchers("/api/global-license/**").permitAll() // Dozvoli global license endpoint-e
                .requestMatchers("/api/export/**").permitAll() // Dozvoli sve export endpoint-e - pomereno na vrh da se ranije evaluira
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
                .requestMatchers("/api/test-envelope-back-side-pdf").permitAll() // Dozvoli test back side PDF
                .requestMatchers("/api/test-font-pdf").permitAll() // Dozvoli test font PDF
                .requestMatchers("/api/import/**").permitAll() // Dozvoli sve import endpoint-e
                .requestMatchers("/api/template/**").permitAll() // Dozvoli template endpoint-e
                .requestMatchers("/api/dokumenti/**").permitAll() // Dozvoli dokumenti endpoint-e (generisanje Word dokumenata)
                .requestMatchers("/api/kategorije/**").permitAll() // Dozvoli kategorije endpoint-e
                .requestMatchers("/api/predmeti/**").permitAll() // Dozvoli predmeti endpoint-e
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
        // Use strength 12 to match database hashes ($2a$12$...)
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Get allowed domains from environment variable or use defaults
        String allowedDomains = System.getenv("EUK_ALLOWED_DOMAINS");
        if (allowedDomains != null && !allowedDomains.isEmpty()) {
            // Koristimo pattern matching za fleksibilniju konfiguraciju
            // Omogućava server-to-server komunikaciju (Next.js proxy)
            List<String> domains = Arrays.asList(allowedDomains.split(","));
            configuration.setAllowedOriginPatterns(domains);
        } else {
            // Default domains - uključuje browser origin-e i Next.js server origin-e
            // Koristimo pattern matching da dozvolimo i server-to-server zahteve
            configuration.setAllowedOriginPatterns(List.of(
                "https://euk.vercel.app",
                "https://euk-it-sectors-projects.vercel.app",
                "http://localhost:*",
                "http://127.0.0.1:*",
                "http://host.docker.internal:*"  // Za Docker networking - Next.js server može pristupati preko ovoga
            ));
        }
        
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization", 
            "Content-Type", 
            "X-Requested-With", 
            "Accept",
            "Origin",
            "Referer",
            "User-Agent"
        ));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        
        // Dodaj headers za file download
        configuration.addExposedHeader("Content-Disposition");
        configuration.addExposedHeader("Content-Type");
        configuration.addExposedHeader("Content-Length");
        configuration.addExposedHeader("Cache-Control");
        configuration.addExposedHeader("Pragma");
        configuration.addExposedHeader("Expires");
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Registruj CORS samo za API endpoint-e, ne za sve
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }


} 