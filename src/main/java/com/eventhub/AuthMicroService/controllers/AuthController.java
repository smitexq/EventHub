package com.eventhub.AuthMicroService.controllers;

import com.eventhub.AuthMicroService.dto.AccessTokenDTO;
import com.eventhub.AuthMicroService.dto.LoginCredentialsDTO;
import com.eventhub.AuthMicroService.dto.UserDataDTO;
import com.eventhub.AuthMicroService.service.AuthServiceImpl;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.http.HttpResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthServiceImpl authServiceImpl;
    public AuthController(AuthServiceImpl authServiceImpl) {
        this.authServiceImpl = authServiceImpl;
    }



    @PostMapping("/sign-up")
    public String sign_up(@RequestBody UserDataDTO userDataDTO, HttpServletResponse response) {
//        response.set

        return authServiceImpl.addUser(userDataDTO);
    }

    @PostMapping("/activate")
    public ResponseEntity<?> activateUser() {
        return ResponseEntity.ok("1");
    }


    @PostMapping("/sign-in")
    public ResponseEntity<AccessTokenDTO> sign_in(HttpServletResponse response, @RequestBody LoginCredentialsDTO loginCredentialsDTO) {
        try {
            return ResponseEntity.ok(authServiceImpl.login(loginCredentialsDTO, response));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<AccessTokenDTO> refresh(HttpServletRequest request) throws AuthenticationException {
        return ResponseEntity.ok(authServiceImpl.refreshAccessToken(request));
    }

    @GetMapping("/main")
    public String main_in(HttpServletRequest request) {


        if (request.getCookies() != null) {
            for (Cookie cook: request.getCookies()) {
                if (cook.getName().equals("AccessToken"))
                    System.out.println(cook.getValue());
            }
        }

//        String accessToken = Arrays.stream(request.getCookies())
//                .filter(c -> c.getName().equals("AccessToken"))
//                .findFirst()
//                .map(Cookie::getValue)
//                .orElse(null).toString();
//        System.out.println(accessToken);

        return "Главная страница";
    }
}


