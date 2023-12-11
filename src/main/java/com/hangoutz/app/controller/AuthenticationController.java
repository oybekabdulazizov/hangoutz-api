package com.hangoutz.app.controller;

import com.hangoutz.app.dto.JwtAuthenticationResponseDTO;
import com.hangoutz.app.dto.ResetPasswordDTO;
import com.hangoutz.app.dto.SignInRequestDTO;
import com.hangoutz.app.dto.SignUpRequestDTO;
import com.hangoutz.app.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;


    @PostMapping("/signup")
    public ResponseEntity<JwtAuthenticationResponseDTO> singUp(@RequestBody SignUpRequestDTO request) {
        return new ResponseEntity<>(authenticationService.signUp(request), HttpStatus.OK);
    }


    @PostMapping("/signin")
    public ResponseEntity<JwtAuthenticationResponseDTO> singIn(@RequestBody SignInRequestDTO request) {
        return new ResponseEntity<>(authenticationService.signIn(request), HttpStatus.OK);
    }


    @PutMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestHeader(name = "Authorization") String jwt,
            @Valid @RequestBody ResetPasswordDTO request
    ) throws BadRequestException {
        return new ResponseEntity<>(authenticationService.resetPassword(jwt, request), HttpStatus.OK);
    }
}
