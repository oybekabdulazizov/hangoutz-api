package com.hangoutz.app.controller;

import com.hangoutz.app.dto.JwtAuthResponseDTO;
import com.hangoutz.app.dto.ResetPasswordDTO;
import com.hangoutz.app.dto.SignInRequestDTO;
import com.hangoutz.app.dto.SignUpRequestDTO;
import com.hangoutz.app.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;


    @PostMapping("/signup")
    public ResponseEntity<JwtAuthResponseDTO> singUp(@RequestBody SignUpRequestDTO request) {
        return new ResponseEntity<>(authService.signUp(request), HttpStatus.OK);
    }


    @PostMapping("/signin")
    public ResponseEntity<JwtAuthResponseDTO> singIn(@RequestBody SignInRequestDTO request) {
        return new ResponseEntity<>(authService.signIn(request), HttpStatus.OK);
    }


    @PostMapping("/reset-password")
    public ResponseEntity resetPassword(
            @Valid @RequestBody ResetPasswordDTO request
    ) {
        authService.resetPassword(request);
        return ResponseEntity.ok().build();
    }
}
