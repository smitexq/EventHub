package com.eventhub.AuthMicroService.service;

import com.eventhub.AuthMicroService.dto.JwtTokenDTO;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
public class JwtServiceImpl implements JwtService {

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
        return true;
    }

    @Override
    public ResponseEntity<JwtTokenDTO> generateAuthToken(String username) {
        JwtTokenDTO jwt = new JwtTokenDTO(generateAccessToken(username), generateRefreshToken(username));

        return ResponseEntity.ok(jwt);
    }

    @Override
    public String getUsernameFromToken(String token) {
        return "";
    }


    private String generateAccessToken(String username) {
        Date exp = Date.from(LocalDateTime.now().plusMinutes(3).atZone(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
                .subject(username)
                .expiration(exp)
//                .signWith() //TODO
                .compact();
    }

    private String generateRefreshToken(String username) {
        return "";
    }


}
