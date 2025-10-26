package by.baraznov.userservice.util;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.time.Instant;
import java.util.UUID;

@Component
public class JwtUtils {

    @Value("${keycloak.url}")
    private static String JWKS_URL;

    private JWKSet jwkSet;
    private Instant jwkSetExpiry;
    private static final long CACHE_DURATION_SECONDS = 300;

    public JwtUtils() {
        this.jwkSet = null;
        this.jwkSetExpiry = Instant.EPOCH;
    }

    private synchronized void loadJwkSetIfNeeded() throws IOException, ParseException {
        Instant now = Instant.now();
        if (jwkSet == null || now.isAfter(jwkSetExpiry)) {
            this.jwkSet = JWKSet.load(new URL(JWKS_URL));
            this.jwkSetExpiry = now.plusSeconds(CACHE_DURATION_SECONDS);
        }
    }

    public UUID getAccessClaims(String token) {
        try {
            loadJwkSetIfNeeded();

            SignedJWT signedJWT = SignedJWT.parse(token);
            String kid = signedJWT.getHeader().getKeyID();
            JWK jwk = jwkSet.getKeyByKeyId(kid);
            if (jwk == null) {
                this.jwkSet = JWKSet.load(new URL(JWKS_URL));
                jwk = jwkSet.getKeyByKeyId(kid);
                if (jwk == null) {
                    throw new RuntimeException("Public key not found for kid: " + kid);
                }
            }
            return UUID.fromString(signedJWT.getJWTClaimsSet().getSubject());
        } catch (ParseException | IOException e) {
            throw new RuntimeException("JWT validation failed", e);
        }
    }

}