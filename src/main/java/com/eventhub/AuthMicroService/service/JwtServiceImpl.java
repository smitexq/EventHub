package com.eventhub.AuthMicroService.service;

import com.eventhub.AuthMicroService.dto.JwtTokenDTO;
import com.eventhub.AuthMicroService.security.jwt.JwtAppId;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
public class JwtServiceImpl implements JwtService {
    @Value("${jwt.secret}")
    private String secret;
    private static final String AUTHORIZATION_HEADER = HttpHeaders.AUTHORIZATION;

    @Override
    public String getTokenFromRequest(HttpServletRequest request) {
        String token = request.getHeader(AUTHORIZATION_HEADER);
        if (token != null && token.startsWith("Bearer")) {
            return token.substring(7);
        }
        return null;
    }

    @Override
    public boolean validateJWTToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getEncodedKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        if(!new Date().before(claims.getExpiration())) return false;
        if(!claims.get("session").equals(JwtAppId.getSessionId())) return false;

        return true;
    }

    @Override
    public ResponseEntity<JwtTokenDTO> generateAuthToken(String username) {
        JwtTokenDTO jwt = new JwtTokenDTO(
                generateToken(username, "access", 3),
                generateToken(username, "refresh", 15)
        );
        return ResponseEntity.ok(jwt);
    }

    @Override
    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getEncodedKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    @Override
    public String refreshAccessToken(String refreshToken) {
        String username = getUsernameFromToken(refreshToken);
        return generateToken(username, "access", 3);
    }



    private String generateToken(String username, String type, int minutes) {
        Date exp = Date.from(LocalDateTime.now().plusMinutes(minutes).atZone(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
//                .issuer()
                .subject(username)
                .expiration(exp)
                .signWith(getEncodedKey())
                .claim("type",type)
                .claim("session", JwtAppId.getSessionId())
                .compact();
    }

    private SecretKey getEncodedKey() {
        byte[] key = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(key);
    }
}
