package com.hangoutz.app.service;

import com.hangoutz.app.dto.JwtAuthResponseDTO;
import com.hangoutz.app.dto.LogInRequestDTO;
import com.hangoutz.app.dto.ResetPasswordDTO;
import com.hangoutz.app.dto.SignUpRequestDTO;

public interface AuthService {

    JwtAuthResponseDTO signUp(SignUpRequestDTO newUser);

    JwtAuthResponseDTO logIn(LogInRequestDTO logInRequest);

    void resetPassword(ResetPasswordDTO passwordResetRequest);

    JwtAuthResponseDTO refreshSessionToken(String refreshBearerToken);
}
