package com.backend_sistema_clinico.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.backend_sistema_clinico.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {


    private final String secretKey;
    private final long accessExpiration;  // 15 minutos
    private final long refreshExpiration; // 7 días

    public JwtService(@Value("${jwt.secret}") String secretKey,
                      @Value("${jwt.access.expiration}") long accessExpiration,
                      @Value("${jwt.refresh.expiration}") long refreshExpiration) {
        this.secretKey = secretKey;
        this.accessExpiration = accessExpiration;
        this.refreshExpiration = refreshExpiration;
    }

    public String generateAccessToken(CustomUserDetails user) {
        return JWT.create()
                .withIssuer("backend-sistema-clinico")
                .withSubject(user.getId().toString())
                .withClaim("email", user.getUsername())
                .withClaim("role", user.getRole().name())
                .withClaim("clinicaId", user.getClinicaId())
                .withClaim("type", TokenType.ACCESS.name())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + accessExpiration))
                .sign(generateAlgorithm());
    }

    public String generateRefreshToken(CustomUserDetails user) {
        return JWT.create()
                .withIssuer("backend-sistema-clinico")
                .withSubject(user.getId().toString())
                .withClaim("type", TokenType.REFRESH.name())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + refreshExpiration))
                .sign(generateAlgorithm());
    }

    public DecodedJWT validateToken(String token) {
        return JWT.require(generateAlgorithm())
                .withIssuer("backend-sistema-clinico")
                .build()
                .verify(token);
    }

    private Algorithm generateAlgorithm() {
        return Algorithm.HMAC256(secretKey);
    }










}
