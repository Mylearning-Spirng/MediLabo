package com.abernathyclinic.medilabo_notes.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    /**
     * ✅ For the Notes Service, we want to secure all endpoints and require a valid JWT for access
     * ✅ This is because the Notes Service contains sensitive patient information and should only be accessible to authenticated users
     * ✅ The Auth Service will issue JWTs that contain the necessary claims for authentication and authorization
     * ✅ The Notes Service will validate the JWTs on each request to ensure that only authorized users can access the endpoints
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        logger.info("Configuring notes service security");
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth -> oauth.jwt(Customizer.withDefaults()));

        return http.build();
    }
}
