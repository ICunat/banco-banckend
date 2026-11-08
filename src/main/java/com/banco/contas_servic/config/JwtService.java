package com.banco.contas_servic.config;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtService {

    private final SecretKey chave;
    private final long expiracaoMinutos;

    public JwtService(@Value("${jwt.secret}") String segredo,
                      @Value("${jwt.expiracao-minutos}") long expiracaoMinutos) {
        this.chave = Keys.hmacShaKeyFor(segredo.getBytes(StandardCharsets.UTF_8));
        this.expiracaoMinutos = expiracaoMinutos;
    }

    public String gerar(String identificador, String papel) {
        Date agora = new Date();
        Date fim = new Date(agora.getTime() + expiracaoMinutos * 60 * 1000);
        return Jwts.builder()
                .subject(identificador)
                .claim("papel", papel)
                .issuedAt(agora)
                .expiration(fim)
                .signWith(chave)
                .compact();
    }

    public Claims ler(String token) {
        return Jwts.parser()
                .verifyWith(chave)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}