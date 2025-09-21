package br.com.fiap.api_rest.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
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
    private final Algorithm algorithm;


    private final Set<String> revoked = ConcurrentHashMap.newKeySet();

    public TokenService(@Value("${api.security.token.secret}") String secret) {
        this.algorithm = Algorithm.HMAC256(secret);
    }

    public String generate(UserDetails user) {
        Instant now = Instant.now();
        return JWT.create()
                .withIssuer(ISSUER)
                .withSubject(user.getUsername())
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(now.plus(2, ChronoUnit.HOURS)))
                .sign(algorithm);
    }

    public String getSubject(String token) {
        return JWT.require(algorithm).withIssuer(ISSUER).build().verify(token).getSubject();
    }

    public boolean isValid(String token, UserDetails user) {
        if (revoked.contains(token)) return false;
        DecodedJWT jwt = JWT.require(algorithm).withIssuer(ISSUER).build().verify(token);
        return jwt.getSubject().equals(user.getUsername()) && jwt.getExpiresAt().after(new Date());
    }

    public void revoke(String token) { revoked.add(token); }
}
