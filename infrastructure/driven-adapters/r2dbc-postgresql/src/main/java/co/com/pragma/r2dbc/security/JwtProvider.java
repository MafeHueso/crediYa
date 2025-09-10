package co.com.pragma.r2dbc.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Jwts;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Component
public class JwtProvider {

    private static final Logger LOGGER =  Logger.getLogger(JwtProvider.class.getName());

    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration}")
    private Integer expiration;

    public String generateToken(String email, List<String> roles) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        SecretKey key = getKey(secret);

        return Jwts.builder()
                .setSubject(email)
                .claim("roleId", roles)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key)
                .compact();
    }

    public String getEmailFromToken(String token) {
        Claims claims = getClaims(token);
        return claims.getSubject();
    }

   public List<String> getRoleIdFromToken(String token) {
        Claims claims = getClaims(token);
        return claims.get("roleId", List.class);
    }

   public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public List<String> getRoles(String token) {
        Claims claims = getClaims(token);
        Object roleObj = claims.get("roleId");
        if (roleObj instanceof List<?>) {
            List<?> rawList = (List<?>) roleObj;
            return rawList.stream()
                    .map(item -> {
                        if (item instanceof Map<?, ?>) {
                            Map<?, ?> map = (Map<?, ?>) item;
                            Object authority = map.get("authority");
                            return authority != null ? authority.toString() : null;
                        }
                        return null;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }
        return List.of();
    }

   public Claims getClaims(String token) {
        SecretKey key = getKey(secret);
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getKey(String secret) {
        byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(secretBytes);
    }
}
