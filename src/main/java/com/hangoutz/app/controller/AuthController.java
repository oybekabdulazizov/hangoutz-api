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
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;


    @PostMapping("/sign-up")
    public ResponseEntity<JwtAuthResponseDTO> signUp(@Valid @RequestBody SignUpRequestDTO request) {
        return new ResponseEntity<>(authService.signUp(request), HttpStatus.OK);
    }


    @PostMapping("/sign-in")
    public ResponseEntity<JwtAuthResponseDTO> signIn(@Valid @RequestBody SignInRequestDTO request) {
        return new ResponseEntity<>(authService.signIn(request), HttpStatus.OK);
    }


    @PostMapping("/reset-password")
    @ResponseStatus(code = HttpStatus.OK)
    public void resetPassword(@Valid @RequestBody ResetPasswordDTO request) {
        authService.resetPassword(request);
    }
}
