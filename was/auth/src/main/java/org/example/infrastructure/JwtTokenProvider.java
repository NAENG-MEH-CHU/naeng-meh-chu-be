package org.example.infrastructure;

import io.jsonwebtoken.*;
import org.example.exception.exceptions.NeedToLoginException;
import org.example.exception.exceptions.TokenExpiredException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${security.jwt.token.secret-key}")
    private String secretKey;

    @Value("${security.jwt.token.expire-length}")
    private long validityInMilliseconds;

    public String createAccessToken(final String payload) {
        Claims claims = Jwts.claims()
                .setSubject(payload);

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public String getPayload(final String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token).getBody().getSubject();
    }

    public void validateToken(final String token) {
        try {
            Jws<Claims> claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token);
            if(claims.getBody().getExpiration().before(new Date())) {
                throw new TokenExpiredException();
            }
        } catch (JwtException | IllegalArgumentException e) {
            throw new NeedToLoginException();
        }
    }
}
