package com.abernathyclinic.medilabo_auth_service.controller;

import com.abernathyclinic.medilabo_auth_service.jwt.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")   // ✅ IMPORTANT: matches UI + gateway
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authManager, JwtService jwtService) {
        this.authManager = authManager;
        this.jwtService = jwtService;
    }

    /**
     * 1. Authenticate the user using the provided username and password.
     * 2. If authentication is successful, generate a JWT token containing the username and roles.
     * 3. Return the token in the response body as {"token": "the_generated_token"}.
     * 4. If authentication fails, return a 401 Unauthorized status with an appropriate message.
     */
    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> body) {
        try {
            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(body.get("username"), body.get("password"))
            );

            List<String> roles = auth.getAuthorities().stream()
                    .map(a -> a.getAuthority())
                    .toList();

            String token = jwtService.generateToken(auth.getName(), roles);

            // ✅ IMPORTANT: return "token" because your React code expects res.data.token
            return Map.of("token", token);

        } catch (BadCredentialsException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username/password");
        }
    }
}
