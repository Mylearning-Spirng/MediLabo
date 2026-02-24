package com.abernathyclinic.medilabo_gateway_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

    /**
     * ✅ CORS is needed to allow our React frontend (http://localhost:3000) to call our Spring Boot backend (http://localhost:8080)
     * ✅ We configure it here in the API Gateway so it applies to all routes
     * ✅ We allow all methods and headers for simplicity
     * ✅ We do NOT allow credentials (cookies/sessions) since we are using JWTs for auth
     */
    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // ✅ React app origin
        config.setAllowedOrigins(List.of("http://localhost:3000"));

        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));

        // ✅ We are NOT using cookies/sessions for auth (JWT only)
        config.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsWebFilter(source);
    }
}
