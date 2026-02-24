package com.abernathyclinic.medilabo_risk_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    /**
     * ✅ For the Risk Service, we want to secure all endpoints and require a valid JWT for access
     * ✅ This is because the Risk Service contains sensitive patient information and should only be accessible to authenticated users
     * ✅ The Auth Service will issue JWTs that contain the necessary claims for authentication and authorization
     * ✅ The Risk Service will validate the JWTs on each request to ensure that only authorized users can access the endpoints
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth -> oauth.jwt());

        return http.build();
    }
}
