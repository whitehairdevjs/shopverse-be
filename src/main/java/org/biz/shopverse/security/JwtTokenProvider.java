package org.biz.shopverse.security;

import org.biz.shopverse.exception.auth.JwtInvalidException;
import org.biz.shopverse.exception.auth.JwtTokenExpiredException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.issuer}")
    private String issuer;

    private Key key;

    @PostConstruct
    protected void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateAccessToken(String userId, List<String> roles, long accessTokenValidityInMs) {
        return Jwts.builder()
                .setSubject(userId)
                .claim("roles", roles)
                .setIssuer(issuer)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenValidityInMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 단일 역할을 위한 오버로드 메서드 (하위 호환성 유지)
    public String generateAccessToken(String userId, String role, long accessTokenValidityInMs) {
        return generateAccessToken(userId, List.of(role), accessTokenValidityInMs);
    }

    public String generateRefreshToken(String userId, long refreshTokenValidityInMs) {
        return Jwts.builder()
                .setSubject(userId)
                .setIssuer(issuer)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenValidityInMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isTokenValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            throw new JwtTokenExpiredException("Access Token has expired");
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtInvalidException("JWT is invalid");
        }
    }

    public String getUserId(String token) {
        return parseClaims(token).getSubject();
    }

    @SuppressWarnings("unchecked")
    public List<String> getUserRoles(String token) {
        return (List<String>) parseClaims(token).get("roles");
    }

    // 하위 호환성을 위한 단일 역할 반환 메서드
    public String getUserRole(String token) {
        List<String> roles = getUserRoles(token);
        return roles != null && !roles.isEmpty() ? roles.get(0) : null;
    }

}