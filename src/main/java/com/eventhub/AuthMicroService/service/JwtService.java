package com.eventhub.AuthMicroService.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private static final String AUTHORIZATION_HEADER = HttpHeaders.AUTHORIZATION;

    public String getTokenFromRequest(HttpServletRequest request) {
        String token = request.getHeader(AUTHORIZATION_HEADER);
        if (token != null && token.startsWith("Bearer")) {
            return token.substring(7);
        }
        return null;
    }

    public boolean validateJWTToken(String token) {
        return true;
    }
}
