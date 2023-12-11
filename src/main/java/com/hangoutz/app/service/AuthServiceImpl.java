package com.hangoutz.app.service;

import com.hangoutz.app.dto.JwtAuthResponseDTO;
import com.hangoutz.app.dto.ResetPasswordDTO;
import com.hangoutz.app.dto.SignInRequestDTO;
import com.hangoutz.app.dto.SignUpRequestDTO;
import com.hangoutz.app.exception.NotFoundException;
import com.hangoutz.app.model.Role;
import com.hangoutz.app.model.User;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public JwtAuthResponseDTO signUp(SignUpRequestDTO request) {
        Role role = request.getEmail().contains("@admin.") ? Role.ROLE_ADMIN : Role.ROLE_USER;
        User user = User
                .builder()
                .name(request.getName())
                .lastname(request.getLastname())
                .dateOfBirth(request.getDateOfBirth())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .build();
        userService.save(user);
        String jwt = jwtService.generateToken(user);
        return JwtAuthResponseDTO
                .builder()
                .token(jwt)
                .expiresAt(getExpirationTime(jwt))
                .build();
    }

    @Override
    public JwtAuthResponseDTO signIn(SignInRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        var user = userService.findByEmail(request.getEmail());
        if (user == null) {
            throw new NotFoundException("User not found");
        }
        String jwt = jwtService.generateToken(user);
        return JwtAuthResponseDTO
                .builder()
                .token(jwt)
                .expiresAt(getExpirationTime(jwt))
                .build();
    }

    @Override
    public String resetPassword(String token, ResetPasswordDTO request) throws BadRequestException {
        // extract the token without the 'Bearer ' part
        String jwt = token.substring(7);
        String requesterUsernameFromToken = jwtService.extractUsername(jwt);

        // find the user using the request properties
        User user = (User) userService.userDetailsService().loadUserByUsername(request.getEmail());
        if (user == null
                || !user.getUsername().equals(requesterUsernameFromToken)
                || !passwordEncoder.matches(request.getOldPassword(), user.getPassword())
        ) {
            throw new BadCredentialsException("Username or password is incorrect");
        }
        if (jwtService.isTokenExpired(jwt)) {
            throw new BadCredentialsException("Invalid token");
        }
        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new BadRequestException("New password and its confirmation must match");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userService.update(user);
        return "The password has been reset successfully!";
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
