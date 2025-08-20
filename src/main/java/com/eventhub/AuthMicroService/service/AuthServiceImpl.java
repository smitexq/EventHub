package com.eventhub.AuthMicroService.service;

import com.eventhub.AuthMicroService.dao.UserRepository;
import com.eventhub.AuthMicroService.dto.AccessTokenDTO;
import com.eventhub.AuthMicroService.dto.JwtTokenDTO;
import com.eventhub.AuthMicroService.dto.LoginCredentialsDTO;
import com.eventhub.AuthMicroService.dto.UserDataDTO;
import com.eventhub.AuthMicroService.models.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.naming.AuthenticationException;
import java.util.Arrays;
import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService{

    private final UserRepository userRepository;
    private final JwtServiceImpl jwtService;
    private final PasswordEncoder passwordEncoder;
    private final WebClient webClient;

    public AuthServiceImpl(UserRepository userRepository, JwtServiceImpl jwtService, PasswordEncoder passwordEncoder, WebClient gateWayWebClient) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;

        this.webClient = gateWayWebClient;
    }


    @Override
    public String addUser(UserDataDTO userDataDTO) {
        //Проверка на то, что пользователь еще НЕ существует
        Optional<User> user = userRepository.findByUsername(userDataDTO.getUsername());
        if (user.isPresent()) {
            return "Пользователь с таким именем уже существует!";
        }

        User new_user = new User(
                userDataDTO.getUsername(),
                userDataDTO.getEmail(),
                passwordEncoder.encode(userDataDTO.getPassword())
        );
        userRepository.save(new_user);


        webClient.post()
                .uri("/profile-service/test")
                .retrieve()
                .toBodilessEntity()
                .subscribe();

//        Mono<ResponseEntity<Profile>> response = webClient.get()
//                .uri("/auth/main")
//                .retrieve()
//                .toEntity(Profile.class);
//
//        System.out.println(response.block().getBody());

        //TODO: стоит добавить пользователя в контекст безопасности сразу ИЛИ лучше вызвать login

        return String.format("Пользователь %s успешно зарегестрирован!", userDataDTO.getUsername());

    }

    //Ищем пользователя в БД по логину, выдаем пару токенов
    @Override
    public AccessTokenDTO login(LoginCredentialsDTO loginCredentialsDTO, HttpServletResponse response) throws AuthenticationException {
        Optional<User> user = userRepository.findByUsername(loginCredentialsDTO.getUsername());

        if (user.isPresent()) {
            User current_user = user.get(); //Сравнение паролей
            if (passwordEncoder.matches(loginCredentialsDTO.getPassword(), current_user.getPassword())) {
                return new AccessTokenDTO(setAuth(response, loginCredentialsDTO.getUsername()));
            }
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


    /**
    Возвращает Access токен и кладет Refresh в куки
     **/
    private String setAuth(HttpServletResponse response, String username) {
        JwtTokenDTO jwt = jwtService.generateAuthToken(username);

        Cookie cook = new Cookie("RefreshToken", jwt.getRefreshToken());
        cook.setHttpOnly(true);
        //            cook.setMaxAge();
        response.addCookie(cook);

        return jwt.getAccessToken();
    }
}
