package com.recipebook.service;

import com.recipebook.domain.values.UserPrincipal;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.Key;
import java.time.Instant;
import java.util.Date;

/**
 * @author - AvanishKishorPandey
 */
@Component
@Slf4j
public class TokenProvider {
    private static final String ID_KEY = "ID";

    @Value("${api.auth.jwt.secret}")
    private String secret;

    @Value("${api.auth.jwt.tokenvalidityinseconds: 120}")
    private long tokenValidityInSeconds;

    private Key secretKey;

    @PostConstruct
    public void init() {
        final byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String createToken(@NonNull final UserPrincipal userPrincipal) {
        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .claim(ID_KEY, String.valueOf(userPrincipal.getId()))
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .setExpiration(Date.from(Instant.now().plusSeconds(this.tokenValidityInSeconds)))
                .setIssuedAt(Date.from(Instant.now()))
                .compact();
    }

    public Authentication getAuthentication(final String authToken) {
        Claims claims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(authToken).getBody();
        return new UsernamePasswordAuthenticationToken(new UserPrincipal(Long.valueOf(claims.get(ID_KEY).toString()),
                claims.getSubject(), "", "",  null)
                , authToken, null);
    }

    public boolean validateToken(final String authToken) {
        boolean isValidToken = false;
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(authToken);
            isValidToken = true;
        } catch (io.jsonwebtoken.security.SecurityException signatureException) {
            log.info("Invalid JWT signature.");
            log.trace("Invalid JWT signature trace.", signatureException);
        } catch (ExpiredJwtException expiredJwtException) {
            log.info("Expired JWT token.");
            log.trace("Expired JWT token trace.", expiredJwtException);
        } catch (UnsupportedJwtException unsupportedJwtException) {
            log.info("Unsupported JWT token.");
            log.trace("Unsupported JWT token trace.", unsupportedJwtException);
        } catch (IllegalArgumentException illegalArgumentException) {
            log.info("JWT token compact of handler are invalid.");
            log.trace("JWT token compact of handler are invalid trace.", illegalArgumentException);
        }  catch (MalformedJwtException malformedJwtException) {
            log.info("Invalid JWT token.");
            log.trace("Invalid JWT token trace.", malformedJwtException);
        }
        return isValidToken;
    }
}
