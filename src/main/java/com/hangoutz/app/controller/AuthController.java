package com.hangoutz.app.controller;

import com.hangoutz.app.dto.JwtAuthResponseDTO;
import com.hangoutz.app.dto.LogInRequestDTO;
import com.hangoutz.app.dto.ResetPasswordDTO;
import com.hangoutz.app.dto.SignUpRequestDTO;
import com.hangoutz.app.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*", allowCredentials = "true", allowedHeaders = "*")
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;


    @GetMapping("/refresh-session-token")
    public ResponseEntity<JwtAuthResponseDTO> refreshSessionToken(
            HttpServletRequest request
    ) {
        return new ResponseEntity<>(authService.refreshSessionToken(request), HttpStatus.OK);
    }

    @PostMapping("/sign-up")
    public ResponseEntity<JwtAuthResponseDTO> signUp(@Valid @RequestBody SignUpRequestDTO request) {
        return new ResponseEntity<>(authService.signUp(request), HttpStatus.OK);
    }


    @PostMapping("/log-in")
    public ResponseEntity<JwtAuthResponseDTO> logIn(@Valid @RequestBody LogInRequestDTO request) {
        return new ResponseEntity<>(authService.logIn(request), HttpStatus.OK);
    }

    @GetMapping("/log-out")
    @ResponseStatus(code = HttpStatus.OK)
    public void logOut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        authService.logOut(request, response);
    }


    @PostMapping("/reset-password")
    @ResponseStatus(code = HttpStatus.OK)
    public void resetPassword(@Valid @RequestBody ResetPasswordDTO request) {
        authService.resetPassword(request);
    }
}
