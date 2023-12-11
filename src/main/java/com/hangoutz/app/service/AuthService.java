package com.hangoutz.app.service;

import com.hangoutz.app.dto.JwtAuthResponseDTO;
import com.hangoutz.app.dto.ResetPasswordDTO;
import com.hangoutz.app.dto.SignInRequestDTO;
import com.hangoutz.app.dto.SignUpRequestDTO;
import org.apache.coyote.BadRequestException;

public interface AuthService {

    JwtAuthResponseDTO signUp(SignUpRequestDTO request);

    JwtAuthResponseDTO signIn(SignInRequestDTO request);

    String resetPassword(String token, ResetPasswordDTO request) throws BadRequestException;
}
