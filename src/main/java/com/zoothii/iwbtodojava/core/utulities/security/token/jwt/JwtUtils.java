package com.zoothii.iwbtodojava.core.utulities.security.token.jwt;

import java.util.Date;

import com.zoothii.iwbtodojava.core.entities.User;
import com.zoothii.iwbtodojava.core.utulities.security.token.AccessToken;
import com.zoothii.iwbtodojava.core.utulities.security.token.TokenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.*;

@Component
public class JwtUtils implements TokenUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${token.secret}")
    private String tokenSecret;

    @Value("${token.expiration}")
    private int tokenExpiration;

    public AccessToken createAccessToken(Authentication authentication) {

        User userPrincipal = (User) authentication.getPrincipal();
        Date expiration = new Date((new Date()).getTime() + tokenExpiration);
        String token = Jwts.builder()
                .setId(userPrincipal.getId().toString())
                .setSubject((userPrincipal.getUsername()))
                .setIssuedAt(new Date())
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS512, tokenSecret)
                .compact();
        return new AccessToken(token, expiration);
    }

    public String getUserNameFromAccessToken(String accessToken) {
        return Jwts
                .parser()
                .setSigningKey(tokenSecret)
                .parseClaimsJws(accessToken)
                .getBody()
                .getSubject();
    }

    public String getUserIdFromAccessToken(String accessToken) {
        return Jwts
                .parser()
                .setSigningKey(tokenSecret)
                .parseClaimsJws(accessToken)
                .getBody()
                .getId();
    }

    public boolean validateAccessToken(String accessToken) {
        try {
            Jwts
                    .parser()
                    .setSigningKey(tokenSecret)
                    .parseClaimsJws(accessToken);
            return true;
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }
}
