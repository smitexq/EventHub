package com.eventhub.AuthMicroService.service;

import com.eventhub.AuthMicroService.dao.InMemoryUserDAO;
import com.eventhub.AuthMicroService.dao.UserRepository;
import com.eventhub.AuthMicroService.dto.AccessTokenDTO;
import com.eventhub.AuthMicroService.dto.JwtTokenDTO;
import com.eventhub.AuthMicroService.dto.LoginCredentialsDTO;
import com.eventhub.AuthMicroService.dto.UserDataDTO;
import com.eventhub.AuthMicroService.dto.to_profile.NewProfile;
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
    private final InMemoryUserDAO userDAO;

    public AuthServiceImpl(UserRepository userRepository,
                           JwtServiceImpl jwtService,
                           PasswordEncoder passwordEncoder,
                           WebClient gateWayWebClient, InMemoryUserDAO userDAO) {

        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.webClient = gateWayWebClient;
        this.userDAO = userDAO;
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
                userDataDTO.getAge(),
                passwordEncoder.encode(userDataDTO.getPassword())
        );

        userDAO.addUser(new_user);


        userRepository.save(new_user);


        //Создание профиля
        webClient.post()
                .uri("/profile-service/add_profile")
                .bodyValue(
                        new NewProfile(
                                new_user.getId(),
                                new_user.getUsername(),
                                new_user.getAge()
                                )
                )
                .retrieve()
                .toBodilessEntity()
                .subscribe();


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
        //            cook.setSecure();
        //            cook.setMaxAge();
        response.addCookie(cook);

        return jwt.getAccessToken();
    }
}
