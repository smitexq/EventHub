package com.eventhub.AuthMicroService.service;

import com.eventhub.AuthMicroService.dto.AccessTokenDTO;
import com.eventhub.AuthMicroService.dto.LoginCredentialsDTO;
import com.eventhub.AuthMicroService.dto.UserDataDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.naming.AuthenticationException;

public interface AuthService {
    String addUser(UserDataDTO userDataDTO);
    AccessTokenDTO login(LoginCredentialsDTO loginCredentialsDTO, HttpServletResponse response) throws AuthenticationException;
    AccessTokenDTO refreshAccessToken(HttpServletRequest request) throws AuthenticationException;
}
