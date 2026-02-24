package com.abernathyclinic.medilabo_gateway_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class SecurityConfig {

    /**
     * ✅ For the API Gateway, we want to allow all requests through without authentication
     * ✅ This is because the API Gateway is just a proxy and does not handle authentication itself
     * ✅ The actual authentication is handled by the Auth Service, which will validate JWTs and issue them
     * ✅ The API Gateway will simply forward requests to the appropriate services based on the URL path
     */
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(ex -> ex
                        .pathMatchers("/api/auth/**").permitAll()
                        .anyExchange().permitAll()
                )
                .build();
    }
}
