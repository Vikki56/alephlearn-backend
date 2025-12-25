package com.example.demo.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    private final Key key;
    private final long expMillis;

    public JwtService(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.exp-min:60}") long expMinutes
    ) {
byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.expMillis = expMinutes * 60_000;
    }

    public String generate(String subject, Map<String, Object> claims) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(subject)
                .addClaims(claims)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + expMillis))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Jws<Claims> parse(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
    }

    public String generateToken(com.example.demo.user.User user) {
        Map<String, Object> claims = Map.of("role", user.getRole().name());
        return generate(user.getEmail(), claims);
    }
public com.example.demo.user.User extractUser(String authHeader, com.example.demo.user.UserRepository users) {
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        throw new RuntimeException("Invalid token");
    }

    String token = authHeader.substring(7);
    Jws<Claims> claims = parse(token);

    String email = claims.getBody().getSubject();
    return users.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found with email = " + email));
}
}
