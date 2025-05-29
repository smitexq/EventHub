package com.eventhub.AuthMicroService.service;

import com.eventhub.AuthMicroService.dto.JwtTokenDTO;
import com.eventhub.AuthMicroService.dto.LoginCredentialsDTO;
import com.eventhub.AuthMicroService.dto.UserDataDTO;
import org.springframework.http.ResponseEntity;

import javax.naming.AuthenticationException;

public interface AuthService {
    String addUser(UserDataDTO userDataDTO);
    ResponseEntity<JwtTokenDTO> login(LoginCredentialsDTO loginCredentialsDTO) throws AuthenticationException;
}
