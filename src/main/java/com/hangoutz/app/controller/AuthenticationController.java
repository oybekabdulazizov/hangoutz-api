package com.hangoutz.app.controller;

import com.hangoutz.app.dto.JwtAuthenticationResponseDTO;
import com.hangoutz.app.dto.ResetPasswordDTO;
import com.hangoutz.app.dto.SignInRequestDTO;
import com.hangoutz.app.dto.SignUpRequestDTO;
import com.hangoutz.app.service.AuthenticationService;
import jakarta.security.auth.message.AuthException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class AuthenticationController {

    private final AuthenticationService authenticationService;


    @PostMapping("/signup")
    public JwtAuthenticationResponseDTO singUp(@RequestBody SignUpRequestDTO request) {
        return authenticationService.signUp(request);
    }


    @PostMapping("/signin")
    public JwtAuthenticationResponseDTO singIn(@RequestBody SignInRequestDTO request) {
        return authenticationService.signIn(request);
    }


    @PutMapping("/reset-password")
    public String resetPassword(
            @RequestHeader(name = "Authorization") String jwt,
            @Valid @RequestBody ResetPasswordDTO request
    ) throws AuthException {
        String result = authenticationService.resetPassword(jwt, request);
        if (result == null) {
            throw new AuthException("Password reset failed. Please try again.");
        }
        return result;
    }
}
