package com.eventhub.AuthMicroService.security.jwt;

import com.eventhub.AuthMicroService.security.CustomUserDetails;
import com.eventhub.AuthMicroService.security.CustomUserServiceImpl;
import com.eventhub.AuthMicroService.service.JwtServiceImpl;
import com.eventhub.AuthMicroService.tools.PathTool;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtServiceImpl jwtServiceImpl;
    private final CustomUserServiceImpl userService;
    public JwtFilter(JwtServiceImpl jwtServiceImpl, CustomUserServiceImpl userService) {
        this.jwtServiceImpl = jwtServiceImpl;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        //Получение tokena из запроса и добавление в SecurityContextHolder
        String token = jwtServiceImpl.getTokenFromRequest(request);

        if (token != null && jwtServiceImpl.validateJWTToken(token)) {
            if (isCorrectPath(request, token)) { //Проверка типа токена
                String username = jwtServiceImpl.getUsernameFromToken(token);
                CustomUserDetails userDetails = userService.loadUserByUsername(username);

                Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request, response);
    }


    /**
     * Проверяет подходит ли тип токена для данного энд-поинта.
     * Точка регистрации, логина и обновления доступны без jwt токена
     * @param request - запрос из которого берется путь
     * @param token
     * @return возвращает true, если подходит, false иначе
     */
    private boolean isCorrectPath(HttpServletRequest request, String token) {
        String path = request.getRequestURI(); // "/auth/refresh"

        //К трем энд поинтам есть доступ без ключей
        if (path.equals(PathTool.REGISTRATION.toString())) return true;
        if (path.equals(PathTool.LOGIN.toString())) return true;
        if (path.equals(PathTool.REFRESH.toString())) return true;

        //Остаются другие точки, к которым ключ должен иметь type = access
        String type = jwtServiceImpl.getTokenType(token);
        return type.equals("access");
    }
}
