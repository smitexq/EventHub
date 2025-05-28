package com.eventhub.AuthMicroService.service;

import com.eventhub.AuthMicroService.dao.UserRepository;
import com.eventhub.AuthMicroService.dto.UserDataDTO;
import com.eventhub.AuthMicroService.models.User;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String regUser(UserDataDTO userDataDTO) {
        //Проверка на то, что пользователь еще НЕ существует
        Optional<User> user = userRepository.findByUsername(userDataDTO.getUsername());
        if (user.isPresent()) {
            return "Пользователь с таким именем уже существует!";
        }

        UUID uuid = UUID.randomUUID();
        User new_user = new User(userDataDTO.getUsername(), userDataDTO.getPassword(), userDataDTO.getEmail(), uuid);
        userRepository.save(new_user);
        return String.format("Пользователь %s успешно зарегестрирован!", userDataDTO.getUsername());
    }

}
