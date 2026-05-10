package com.ramilastanli.docflow.common.util;

import com.ramilastanli.docflow.security.user.CustomUserDetails;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final Environment env;

    public String generateJwtToken(Authentication authentication) {
        String secret = env.getProperty(ApplicationConstants.JWT_SECRET_KEY);
        SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        String roleWithPrefix = "ROLE_" + userDetails.getRole().name();

        return Jwts.builder()
                .setIssuer("DocumentFlow API")
                .setSubject("Document Workflow Access Token")
                .claim("id", userDetails.getId())
                .claim("email", userDetails.getUsername())
                .claim("roles", roleWithPrefix)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 30_000_000))
                .signWith(secretKey)
                .compact();
    }


    public String extractEmail(String token) {
        String secret = env.getProperty(ApplicationConstants.JWT_SECRET_KEY);
        SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("email", String.class);
    }

    public boolean isTokenValid(String token) {
        try {
            String secret = env.getProperty(ApplicationConstants.JWT_SECRET_KEY);
            SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}