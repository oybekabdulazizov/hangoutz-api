package com.hangoutz.app.service;

import com.hangoutz.app.dto.JwtAuthResponseDTO;
import com.hangoutz.app.dto.LogInRequestDTO;
import com.hangoutz.app.dto.ResetPasswordDTO;
import com.hangoutz.app.dto.SignUpRequestDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface AuthService {

    JwtAuthResponseDTO signUp(SignUpRequestDTO newUser);

    JwtAuthResponseDTO logIn(LogInRequestDTO logInRequest);

    void resetPassword(ResetPasswordDTO passwordResetRequest);

    JwtAuthResponseDTO refreshSessionToken(HttpServletRequest request);

    void logOut(HttpServletRequest request, HttpServletResponse response) throws IOException;
}
