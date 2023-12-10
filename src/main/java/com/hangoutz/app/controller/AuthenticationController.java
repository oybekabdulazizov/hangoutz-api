package com.hangoutz.app.controller;

import com.hangoutz.app.dto.JwtAuthenticationResponseDTO;
import com.hangoutz.app.dto.SignInRequestDTO;
import com.hangoutz.app.dto.SignUpRequestDTO;
import com.hangoutz.app.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
