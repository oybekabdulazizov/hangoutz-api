package com.hangoutz.app.service;

import com.hangoutz.app.dto.JwtAuthResponseDTO;
import com.hangoutz.app.dto.ResetPasswordDTO;
import com.hangoutz.app.dto.SignInRequestDTO;
import com.hangoutz.app.dto.SignUpRequestDTO;

public interface AuthService {

    JwtAuthResponseDTO signUp(SignUpRequestDTO newUser);

    JwtAuthResponseDTO signIn(SignInRequestDTO existingUser);

    void resetPassword(ResetPasswordDTO passwordResetRequest);
}
