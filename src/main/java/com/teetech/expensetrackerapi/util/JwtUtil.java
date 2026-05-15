package com.teetech.expensetrackerapi.util;

import com.teetech.expensetrackerapi.dto.TokenResult;
import com.teetech.expensetrackerapi.security.model.UserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JwtUtil {
    private final JwtProperties jwtProperties;
    private SecretKey signingKey;

    @PostConstruct
    private void init(){
        String secret = jwtProperties.getSecretKey();

        if (secret == null || secret.isBlank()){
            throw new IllegalArgumentException("JWT secret key must not be blank");
        }

        byte[] decoded;

        try{
            decoded = Decoders.BASE64.decode(secret);
        }catch (Exception e){
            throw new IllegalArgumentException("JWT secret key is not valid Base64", e);
        }

        if (decoded.length < 32){
            throw new IllegalArgumentException("JWT secret key must be at least 32 bytes, got: "
            + decoded.length);
        }

        if (jwtProperties.getAccessTokenExpiration() <= 0
                || jwtProperties.getRefreshTokenExpiration() <= 0){
            throw new IllegalArgumentException("JWT expiration values must be positive");
        }

        if (jwtProperties.getIssuer() == null || jwtProperties.getIssuer().isBlank()){
            throw new IllegalArgumentException("JWT issuer must not be blank");
        }

        this.signingKey = Keys.hmacShaKeyFor(decoded);
    }

    private TokenResult tokenBuilder(UserPrincipal userPrincipal, Map<String, Object> claims, long expiration){
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        String token = Jwts.builder()
                        .header()
                        .add("type", "JWT").and()
                        .id(UUID.randomUUID().toString())
                        .subject(userPrincipal.getUsername())
                        .claims(claims)
                        .issuer(jwtProperties.getIssuer())
                        .issuedAt(now)
                        .expiration(expiryDate)
                        .signWith(signingKey)
                        .compact();
        return new TokenResult(token, expiryDate.toInstant());
    }

    public TokenResult generateAccessToken(UserPrincipal userPrincipal, UUID sessionId){
        Map<String, Object> claims = new LinkedHashMap<>();
        claims.put("userId", userPrincipal.getId());
        claims.put("token_type", "access");
        claims.put("sessionId", sessionId.toString());
        claims.put("authorities", userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList())
        );

        return tokenBuilder(userPrincipal, claims, jwtProperties.getAccessTokenExpiration());
    }


    public TokenResult generateRefreshToken(UserPrincipal userPrincipal, UUID sessionId){
        Map<String, Object> claims = new LinkedHashMap<>();
        claims.put("token_type", "refresh");
        claims.put("sessionId", sessionId.toString());

        return tokenBuilder(userPrincipal, claims, jwtProperties.getRefreshTokenExpiration());
    }

    private Claims extractAllClaims(String token){
        return Jwts.parser()
                .verifyWith(signingKey)
                .requireIssuer(jwtProperties.getIssuer())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractUsername(String token){
        return extractAllClaims(token).getSubject();
    }

    public UUID extractUserId(String token){
        String userId = extractAllClaims(token).get("userId").toString();

        return UUID.fromString(userId);
    }

    public Collection<? extends GrantedAuthority> extractAuthorities(String token){
        List<String> authorities = extractAllClaims(token).get("authorities", List.class);

        return authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    public String extractSessionId(String token){
        return extractAllClaims(token).get("sessionId").toString();
    }

    public boolean isAccessTokenValid(String token){
        Claims claims = extractAllClaims(token);
        return "access".equals(claims.get("token_type"));
    }

    public boolean isRefreshTokenValid(String token){
        Claims claims = extractAllClaims(token);
        return "refresh".equals(claims.get("token_type"));
    }
}
