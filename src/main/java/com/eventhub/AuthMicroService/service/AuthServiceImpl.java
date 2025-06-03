package com.eventhub.AuthMicroService.service;

import com.eventhub.AuthMicroService.dao.UserRepository;
import com.eventhub.AuthMicroService.dto.*;
import com.eventhub.AuthMicroService.models.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import java.util.Arrays;
import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService{

    private final UserRepository userRepository;
    private final JwtServiceImpl jwtService;
    public AuthServiceImpl(UserRepository userRepository, JwtServiceImpl jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }


    @Override
    public String addUser(UserDataDTO userDataDTO) {
        //Проверка на то, что пользователь еще НЕ существует
        Optional<User> user = userRepository.findByUsername(userDataDTO.getUsername());
        if (user.isPresent()) {
            return "Пользователь с таким именем уже существует!";
        }

        //TODO сохранение пароля в БД через кодировщик
        User new_user = new User(userDataDTO.getUsername(), userDataDTO.getEmail(), userDataDTO.getPassword());
        userRepository.save(new_user);
        return String.format("Пользователь %s успешно зарегестрирован!", userDataDTO.getUsername());

        //TODO: стоит добавить пользователя в контекст безопасности сразу ИЛИ лучше вызвать login
    }

    //Ищем пользователя в БД по логину, выдаем пару токенов
    @Override
    public AccessTokenDTO login(LoginCredentialsDTO loginCredentialsDTO, HttpServletResponse response) throws AuthenticationException {
        Optional<User> user = userRepository.findByUsername(loginCredentialsDTO.getUsername());
        if (user.isPresent()) {
            JwtTokenDTO jwt = jwtService.generateAuthToken(loginCredentialsDTO.getUsername());

            Cookie cook = new Cookie("RefreshToken", jwt.getRefreshToken());
            cook.setHttpOnly(true);
//            cook.setMaxAge();
            response.addCookie(cook);

            return new AccessTokenDTO(jwt.getAccessToken());
        }
        throw new AuthenticationException("Неверный логин или пароль!");
    }

    @Override
    public AccessTokenDTO refreshAccessToken(HttpServletRequest request) throws AuthenticationException {

        String refreshToken = Arrays.stream(request.getCookies())
                .filter(x -> x.getName().equals("RefreshToken"))
                .findFirst()
                .map(cook -> cook.getValue())
                .orElse(null);

        if (refreshToken != null && jwtService.validateJWTToken(refreshToken)) {
            return new AccessTokenDTO(jwtService.refreshAccessToken(refreshToken));
        }
        throw new AuthenticationException("Invalid token");
    }


}
