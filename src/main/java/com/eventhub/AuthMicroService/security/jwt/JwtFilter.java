package com.eventhub.AuthMicroService.security.jwt;

import com.eventhub.AuthMicroService.security.CustomUserDetails;
import com.eventhub.AuthMicroService.security.CustomUserServiceImpl;
import com.eventhub.AuthMicroService.service.JwtServiceImpl;
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
        //Получение tokena из запроса и добавление в SecurityContextHolder. Иначе -> попытка обновить токен за счет RefreshToken'a
        String token = jwtServiceImpl.getTokenFromRequest(request);

        //TODO: проверка на то, куда хочет перейти пользователь, чтобы проверить, с тем ли токеном он это делает (refresh токен только для обновления access токена)
//        System.out.println(request.getRequestURI().toString());
//        System.out.println("/auth/refresh");


        if (token != null && jwtServiceImpl.validateJWTToken(token)) {
            String username = jwtServiceImpl.getUsernameFromToken(token);
            CustomUserDetails userDetails = userService.loadUserByUsername(username);

            Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
//        else if (!jwtServiceImpl.validateJWTToken(token)) {//попытка обновить токен
//            request.getCookies();
//        }

        filterChain.doFilter(request, response);
    }
}
