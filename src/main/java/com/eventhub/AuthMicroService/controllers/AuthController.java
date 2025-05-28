package com.eventhub.AuthMicroService.controllers;

import com.eventhub.AuthMicroService.dto.UserDataDTO;
import com.eventhub.AuthMicroService.service.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/auth")
public class AuthController {

    private final AuthService authService;
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/sign-up")
    public String sign_up(@RequestBody UserDataDTO userDataDTO) {
        return authService.regUser(userDataDTO);
    }

    @PostMapping("/sign-in")
    public String sign_in() {

    }

}
