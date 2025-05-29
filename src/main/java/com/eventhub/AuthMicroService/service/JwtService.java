package com.eventhub.AuthMicroService.service;

import com.eventhub.AuthMicroService.dto.JwtTokenDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

public interface JwtService {
    String getTokenFromRequest(HttpServletRequest request);
    boolean validateJWTToken(String token);
    ResponseEntity<JwtTokenDTO> generateAuthToken(String username);
    String getUsernameFromToken(String token);
}