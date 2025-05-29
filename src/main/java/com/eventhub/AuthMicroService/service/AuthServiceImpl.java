package com.eventhub.AuthMicroService.service;

import com.eventhub.AuthMicroService.dao.UserRepository;
import com.eventhub.AuthMicroService.dto.JwtTokenDTO;
import com.eventhub.AuthMicroService.dto.LoginCredentialsDTO;
import com.eventhub.AuthMicroService.dto.UserDataDTO;
import com.eventhub.AuthMicroService.models.User;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import java.util.Optional;
import java.util.UUID;

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

        UUID uuid = UUID.randomUUID();
        User new_user = new User(userDataDTO.getUsername(), userDataDTO.getPassword(), userDataDTO.getEmail(), uuid);
        userRepository.save(new_user);
        return String.format("Пользователь %s успешно зарегестрирован!", userDataDTO.getUsername());

        //TODO: стоит добавить пользователя в контекст безопасности сразу ИЛИ лучше вызвать login
    }

    //Ищем пользователя в БД по логину, сравниваем с паролем, если всё хорошо - выдаем пару токенов
    @Override
    public ResponseEntity<JwtTokenDTO> login(LoginCredentialsDTO loginCredentialsDTO) throws AuthenticationException {
        Optional<User> user = userRepository.findByUsername(loginCredentialsDTO.getUsername());
        if (user.isPresent()) {
            //TODO: процесс выдачи токенов
            return jwtService.generateAuthToken(loginCredentialsDTO.getUsername());
        }
        throw new AuthenticationException("Неверный логин или пароль!");
    }

}
