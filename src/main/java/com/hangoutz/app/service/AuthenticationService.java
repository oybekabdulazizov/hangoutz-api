package com.hangoutz.app.service;

import com.hangoutz.app.dto.JwtAuthenticationResponseDTO;
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
public class AuthenticationService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public JwtAuthenticationResponseDTO signUp(SignUpRequestDTO request) {
        Role role = request.getEmailAddress().contains("@admin.") ? Role.ROLE_ADMIN : Role.ROLE_USER;
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

    public String resetPassword(String token, ResetPasswordDTO request) throws BadRequestException {
        // extract the token without the 'Bearer ' part
        String jwt = token.substring(7);

        // find the user using the request properties
        User user = userService.findByEmailAddress(request.getEmailAddress());

        // 1st IF checks the token's validity,
        //  meaning the requester username and username from the request dto match
        // the 2nd and 3rd IFs are self-explanatory
        if (!jwtService.isTokenValid(jwt, user)) {
            throw new BadCredentialsException("You can reset only your own password");
        }
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BadCredentialsException("Username or password is incorrect");
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
