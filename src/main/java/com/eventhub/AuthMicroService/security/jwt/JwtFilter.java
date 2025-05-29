package com.eventhub.AuthMicroService.security.jwt;

import com.eventhub.AuthMicroService.service.JwtServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtFilter extends OncePerRequestFilter {

    private final JwtServiceImpl jwtServiceImpl;
    public JwtFilter(JwtServiceImpl jwtServiceImpl) {
        this.jwtServiceImpl = jwtServiceImpl;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        //Получение tokena из запроса и добавление в SecurityContextHolder. Иначе -> попытка обновить токен за счет RefreshToken'a
        String token = jwtServiceImpl.getTokenFromRequest(request);
        if (token != null && jwtServiceImpl.validateJWTToken(token)) {

        }

    }
}
