package com.hangoutz.app.service;

import com.hangoutz.app.dto.JwtAuthenticationResponseDTO;
import com.hangoutz.app.dto.SignInRequestDTO;
import com.hangoutz.app.dto.SignUpRequestDTO;
import com.hangoutz.app.exception.NotFoundException;
import com.hangoutz.app.model.Role;
import com.hangoutz.app.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public JwtAuthenticationResponseDTO signUp(SignUpRequestDTO request) {
        Role role = request.getEmailAddress().contains("@admin.") ? Role.ROLE_ADMIN : Role.ROLE_USER;
        System.out.println("1");
        User user = User
                .builder()
                .name(request.getName())
                .lastname(request.getLastname())
                .dateOfBirth(request.getDateOfBirth())
                .emailAddress(request.getEmailAddress())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .build();

        userService.save(user);
        String jwt = jwtService.generateToken(user);

        return JwtAuthenticationResponseDTO
                .builder()
                .token(jwt)
                .expiresAt(getExpirationTime(jwt))
                .build();
    }

    public JwtAuthenticationResponseDTO signIn(SignInRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmailAddress(), request.getPassword())
        );
        var user = userService.findByEmailAddress(request.getEmailAddress());
        if (user == null) {
            throw new NotFoundException("User not found");
        }

        String jwt = jwtService.generateToken(user);

        return JwtAuthenticationResponseDTO
                .builder()
                .token(jwt)
                .expiresAt(getExpirationTime(jwt))
                .build();
    }

    private LocalDateTime getExpirationTime(String token) {
        DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime ldt = jwtService.extractExpiration(token).toInstant()
                                      .atZone(ZoneId.systemDefault()).toLocalDateTime();
        String ldt_str = ldt.format(dateTimeFormat);
        LocalDateTime expiresAt = LocalDateTime.parse(ldt_str, dateTimeFormat);
        return expiresAt;
    }
}
