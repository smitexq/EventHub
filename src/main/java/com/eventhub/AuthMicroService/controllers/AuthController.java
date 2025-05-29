package com.eventhub.AuthMicroService.controllers;

import com.eventhub.AuthMicroService.dto.JwtTokenDTO;
import com.eventhub.AuthMicroService.dto.LoginCredentialsDTO;
import com.eventhub.AuthMicroService.dto.UserDataDTO;
import com.eventhub.AuthMicroService.service.AuthServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.AuthenticationException;

@RestController("/auth")
public class AuthController {

    private final AuthServiceImpl authServiceImpl;
    public AuthController(AuthServiceImpl authServiceImpl) {
        this.authServiceImpl = authServiceImpl;
    }

    @PostMapping("/sign-up")
    public String sign_up(@RequestBody UserDataDTO userDataDTO) {
        return authServiceImpl.addUser(userDataDTO);
    }

    @PostMapping("/sign-in")
    public ResponseEntity<JwtTokenDTO> sign_in(@RequestBody LoginCredentialsDTO loginCredentialsDTO) {
        try {
            return authServiceImpl.login(loginCredentialsDTO);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }
}
