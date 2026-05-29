package com.example.minitwitter.auth.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import com.example.minitwitter.user.domain.User;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;

@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final long accessTokenExpirationMillis;

    public JwtTokenProvider(
        @Value("${jwt.secret}") String secret,
        @Value("${jwt.access-token-expiration-millis}") long accessTokenExpirationMillis
    ){
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpirationMillis = accessTokenExpirationMillis;
    }

    public String createAccessToken(User user){
        Date now = new Date();
        Date expiration = new Date(now.getTime() + this.accessTokenExpirationMillis);

        return Jwts.builder()
            .subject(String.valueOf(user.getId()))
            .claim("loginId", user.getLoginId())
            .claim("nickName", user.getNickName())
            .issuedAt(now)
            .expiration(expiration)
            .signWith(secretKey)
            .compact();
    }

    public boolean validateToken(String token){
        try{
            Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token);

            return true;
        }
        catch (Exception exception){
            return false;
        }
    }

    public Long getUserId(String token){
        String subject = Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .getSubject();

        return Long.valueOf(subject);
    }
}
