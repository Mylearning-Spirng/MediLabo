package com.abernathyclinic.medilabo_auth_service.jwt;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Component
public class JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    private final RSAPrivateKey privateKey;
    private final String issuer;
    private final long ttlMinutes;

    /**
     * Constructs the JwtService by loading the RSA private key and initializing configuration values.
     *
     * @param privateKeyResource the Spring Resource pointing to the PEM-encoded PKCS#8 private key file
     * @param issuer             the issuer string to include in JWT claims
     * @param ttlMinutes         the time-to-live for generated tokens in minutes
     */
    public JwtService(
            @Value("${jwt.private-key-location}") Resource privateKeyResource,
            @Value("${jwt.issuer}") String issuer,
            @Value("${jwt.ttl-minutes}") long ttlMinutes
    ) {
        try {
            this.privateKey = loadPrivateKeyPkcs8(privateKeyResource);
            logger.info("Loaded RSA private key for JWT generation from {}", privateKeyResource.getFilename());
        } catch (Exception e) {
            logger.error("Failed to load RSA private key for JWT generation", e);
            throw new IllegalStateException("Failed to load RSA private key", e);
        }
        this.issuer = issuer;
        this.ttlMinutes = ttlMinutes;
    }

    /**
     * Generates a JWT token for the given username and roles.
     *
     * @param username the username to include in the token's subject
     * @param roles    the list of roles to include in the token's claims
     * @return a signed JWT token as a String
     */
    public String generateToken(String username, List<String> roles) {
        try {
            Instant now = Instant.now();
            Instant exp = now.plusSeconds(ttlMinutes * 60);

            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .issuer(issuer)
                    .subject(username)
                    .issueTime(Date.from(now))
                    .expirationTime(Date.from(exp))
                    .claim("roles", roles)
                    .build();

            SignedJWT jwt = new SignedJWT(
                    new JWSHeader.Builder(JWSAlgorithm.RS256).type(JOSEObjectType.JWT).build(),
                    claims
            );

            jwt.sign(new RSASSASigner(privateKey));
            String token = jwt.serialize();
            logger.debug("Generated JWT for user={}", username);
            return token;
        } catch (Exception e) {
            logger.error("Failed to generate JWT for user=" + username, e);
            throw new IllegalStateException("Failed to generate JWT", e);
        }
    }

    /**
     * Loads an RSA private key from a PEM-encoded PKCS#8 file.
     *
     * @param res the Spring Resource pointing to the PEM file
     * @return the loaded RSAPrivateKey
     * @throws Exception if there is an error reading or parsing the key
     */
    private RSAPrivateKey loadPrivateKeyPkcs8(Resource res) throws Exception {
        String pem = new String(res.getInputStream().readAllBytes(), StandardCharsets.UTF_8)
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] decoded = Base64.getDecoder().decode(pem);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
        return (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(spec);
    }
}
