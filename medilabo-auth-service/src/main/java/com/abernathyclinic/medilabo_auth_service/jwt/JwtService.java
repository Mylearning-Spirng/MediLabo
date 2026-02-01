package com.abernathyclinic.medilabo_auth_service.jwt;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
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

    private final RSAPrivateKey privateKey;
    private final String issuer;
    private final long ttlMinutes;

    public JwtService(
            @Value("${jwt.private-key-location}") Resource privateKeyResource,
            @Value("${jwt.issuer}") String issuer,
            @Value("${jwt.ttl-minutes}") long ttlMinutes
    ) {
        try {
            this.privateKey = loadPrivateKeyPkcs8(privateKeyResource);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load RSA private key", e);
        }
        this.issuer = issuer;
        this.ttlMinutes = ttlMinutes;
    }

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
            return jwt.serialize();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to generate JWT", e);
        }
    }

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
