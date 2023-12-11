package com.hangoutz.app.service;

import com.hangoutz.app.dto.JwtAuthenticationResponseDTO;
import com.hangoutz.app.dto.ResetPasswordDTO;
import com.hangoutz.app.dto.SignInRequestDTO;
import com.hangoutz.app.dto.SignUpRequestDTO;
import org.apache.coyote.BadRequestException;

public interface AuthenticationService {

    JwtAuthenticationResponseDTO signUp(SignUpRequestDTO request);

    JwtAuthenticationResponseDTO signIn(SignInRequestDTO request);

    String resetPassword(String token, ResetPasswordDTO request) throws BadRequestException;
}
