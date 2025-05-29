package com.eventhub.AuthMicroService.controllers;

import com.eventhub.AuthMicroService.dto.UserDataDTO;
import com.eventhub.AuthMicroService.service.AuthServiceImpl;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
    public String sign_in() {

    }

}
