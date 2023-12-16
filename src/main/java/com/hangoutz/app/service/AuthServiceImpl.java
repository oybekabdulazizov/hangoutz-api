package com.hangoutz.app.service;

import com.hangoutz.app.dao.UserDAO;
import com.hangoutz.app.dto.JwtAuthResponseDTO;
import com.hangoutz.app.dto.ResetPasswordDTO;
import com.hangoutz.app.dto.SignInRequestDTO;
import com.hangoutz.app.dto.SignUpRequestDTO;
import com.hangoutz.app.mappers.UserMapper;
import com.hangoutz.app.model.Role;
import com.hangoutz.app.model.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
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
    private final UserDAO userDAO;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public JwtAuthResponseDTO signUp(SignUpRequestDTO newUser) throws BadRequestException {
        if (userDAO.findByEmail(newUser.getEmail()) != null) {
            throw new BadRequestException("User with this email already exists.");
        }

        Role role = newUser.getEmail().contains("@hangoutz.com") ? Role.ROLE_ADMIN : Role.ROLE_USER;
        User user = userMapper.toModel(newUser, new User());

        user.setPassword(passwordEncoder.encode(newUser.getPassword()));
        user.setRole(role);
        userDAO.save(user);

        String jwt = jwtService.generateToken(user);
        return new JwtAuthResponseDTO(jwt, getExpirationTime(jwt));
    }

    @Override
    public JwtAuthResponseDTO signIn(SignInRequestDTO existingUser) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(existingUser.getEmail(), existingUser.getPassword())
            );
        } catch (InternalAuthenticationServiceException ex) {
            throw new InternalAuthenticationServiceException("Username or password is incorrect");
        }
        var user = userService.findByEmail(existingUser.getEmail());
        String jwt = jwtService.generateToken(user);
        return new JwtAuthResponseDTO(jwt, getExpirationTime(jwt));
    }

    @Override
    @Transactional
    public String resetPassword(String bearerToken, ResetPasswordDTO passwordResetRequest) throws BadRequestException {
        String jwt = jwtService.extractJwt(bearerToken);
        String requesterUsernameFromToken = jwtService.extractUsername(jwt);

        // find the user using the passwordResetRequest properties
        User user = (User) userService.userDetailsService().loadUserByUsername(passwordResetRequest.getEmail());
        if (user == null
                || !user.getUsername().equals(requesterUsernameFromToken)
                || !passwordEncoder.matches(passwordResetRequest.getOldPassword(), user.getPassword())
        ) {
            throw new BadCredentialsException("Username or password is incorrect");
        }
        if (jwtService.isTokenExpired(jwt)) {
            throw new BadCredentialsException("Invalid token");
        }
        if (!passwordResetRequest.getNewPassword().equals(passwordResetRequest.getConfirmNewPassword())) {
            throw new BadRequestException("New password and its confirmation must match");
        }

        user.setPassword(passwordEncoder.encode(passwordResetRequest.getNewPassword()));
        userDAO.update(user);
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
