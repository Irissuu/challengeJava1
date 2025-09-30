package br.com.fiap.api_rest.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenService {

    private static final String ISSUER = "java-advanced-api";
    private static final long EXP_HOURS = 2;
    private static final long LEEWAY_SECONDS = 60;

    private final Algorithm algorithm;
    private final JWTVerifier verifier;
    private final Set<String> revoked = ConcurrentHashMap.newKeySet();

    public TokenService(@Value("${api.security.token.secret:change-me-please-32-chars-min}") String secret) {
        if (secret == null || secret.length() < 32) {
            throw new IllegalStateException("api.security.token.secret precisa ter pelo menos 32 caracteres");
        }
        this.algorithm = Algorithm.HMAC256(secret);
        this.verifier  = JWT.require(algorithm).withIssuer(ISSUER).acceptLeeway(LEEWAY_SECONDS).build();
    }

    public String generate(UserDetails user) {
        Instant now = Instant.now();
        return JWT.create()
                .withIssuer(ISSUER)
                .withSubject(user.getUsername())
                .withIssuedAt(Date.from(now))
                .withNotBefore(Date.from(now.minusSeconds(5)))
                .withExpiresAt(Date.from(now.plus(EXP_HOURS, ChronoUnit.HOURS)))
                .sign(algorithm);
    }

    public String getSubject(String tokenOrBearer) {
        return verify(stripBearer(tokenOrBearer)).getSubject();
    }

    public boolean isValid(String tokenOrBearer, UserDetails user) {
        String raw = stripBearer(tokenOrBearer);
        if (raw == null || raw.isBlank() || revoked.contains(raw)) return false;
        try {
            DecodedJWT jwt = verify(raw);
            return user.getUsername().equals(jwt.getSubject())
                    && jwt.getExpiresAt() != null
                    && jwt.getExpiresAt().after(new Date());
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    public void revoke(String tokenOrBearer) {
        String raw = stripBearer(tokenOrBearer);
        if (raw != null && !raw.isBlank()) revoked.add(raw);
    }

    private DecodedJWT verify(String rawToken) {
        try {
            return verifier.verify(rawToken);
        } catch (JWTVerificationException e) {
            throw new IllegalArgumentException("Token inválido", e);
        }
    }

    public static String stripBearer(String token) {
        if (token == null) return null;
        String t = token.trim();
        return t.regionMatches(true, 0, "Bearer ", 0, 7) ? t.substring(7).trim() : t;
    }
}
