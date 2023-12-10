package com.hangoutz.app.controller;

import com.hangoutz.app.dto.JwtAuthenticationResponseDTO;
import com.hangoutz.app.dto.ResetPasswordDTO;
import com.hangoutz.app.dto.SignInRequestDTO;
import com.hangoutz.app.dto.SignUpRequestDTO;
import com.hangoutz.app.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.security.authentication.BadCredentialsException;
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
    ) throws BadRequestException, BadCredentialsException {
        return authenticationService.resetPassword(jwt, request);
    }
}
