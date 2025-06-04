package com.eventhub.AuthMicroService.service;

import com.eventhub.AuthMicroService.dto.JwtTokenDTO;
import jakarta.servlet.http.HttpServletRequest;

public interface JwtService {
    String getTokenFromRequest(HttpServletRequest request);
    boolean validateJWTToken(String token);
    JwtTokenDTO generateAuthToken(String username);
    String getUsernameFromToken(String token);
    String getTokenType(String token);
    String refreshAccessToken(String refreshToken);
}