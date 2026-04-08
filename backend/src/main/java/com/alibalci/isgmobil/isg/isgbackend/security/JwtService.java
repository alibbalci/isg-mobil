package com.alibalci.isgmobil.isg.isgbackend.security;

import com.alibalci.isgmobil.isg.isgbackend.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    private final String SECRET = "my-secret-key-my-secret-key-my-secret-key";

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    // TOKEN ÜRETİR
    public String generateToken(User user) {

        return Jwts.builder()
                .subject(user.getEmail())
                .claim("role", user.getRole().name())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(getSigningKey())
                .compact();
    }

    // TOKEN İÇİNDEN EMAIL OKUR
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // TOKEN DOĞRULAMA
    public boolean validateToken(String token) {
        return !isTokenExpired(token);
    }

    // CLAIM OKUMA
    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    // TOKEN İÇİNDEKİ TÜM BİLGİLER
    private Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    // TOKEN SÜRESİ DOLMUŞ MU
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // TOKEN EXP OKUMA
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}