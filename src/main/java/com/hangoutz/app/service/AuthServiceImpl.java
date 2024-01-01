package com.hangoutz.app.service;

import com.hangoutz.app.dto.JwtAuthResponseDTO;
import com.hangoutz.app.dto.ResetPasswordDTO;
import com.hangoutz.app.dto.SignInRequestDTO;
import com.hangoutz.app.dto.SignUpRequestDTO;
import com.hangoutz.app.exception.*;
import com.hangoutz.app.mappers.UserMapper;
import com.hangoutz.app.model.Role;
import com.hangoutz.app.model.User;
import com.hangoutz.app.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public JwtAuthResponseDTO signUp(SignUpRequestDTO newUser) {
        if (userRepository.findByEmail(newUser.getEmail()).isPresent()) {
            throw new BadRequestException(ExceptionMessage.EMAIL_TAKEN);
        }

        Role role = newUser.getEmail().contains("@hangoutz.com") ? Role.ROLE_ADMIN : Role.ROLE_USER;
        User user = userMapper.toModel(newUser);

        user.setPassword(passwordEncoder.encode(newUser.getPassword()));
        user.setRole(role);
        userRepository.save(user);

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
            throw new InternalServerException(ExceptionMessage.INTERNAL_ERROR);
        } catch (BadCredentialsException ex) {
            throw new AuthException(ExceptionMessage.BAD_CREDENTIALS);
        }
        var user = userRepository.findByEmail(existingUser.getEmail());
        if (user.isEmpty()) throw new NotFoundException(ExceptionMessage.USER_NOT_FOUND);
        String jwt = jwtService.generateToken(user.get());
        return new JwtAuthResponseDTO(jwt, getExpirationTime(jwt));
    }

    @Override
    @Transactional
    public void resetPassword(String bearerToken, ResetPasswordDTO passwordResetRequest) {
//        String jwt = jwtService.extractJwt(bearerToken);
//        String requesterUsernameFromToken = jwtService.extractUsername(jwt);

        // find the user using the passwordResetRequest properties
        Optional<User> user = userRepository.findByEmail(passwordResetRequest.getEmail());
        if (user.isEmpty()
//                || !user.getUsername().equals(requesterUsernameFromToken)
                || !passwordEncoder.matches(passwordResetRequest.getOldPassword(), user.get().getPassword())
        ) {
            throw new AuthException(ExceptionMessage.BAD_CREDENTIALS);
        }

        if (!passwordResetRequest.getNewPassword().equals(passwordResetRequest.getConfirmNewPassword())) {
            throw new BadRequestException(ExceptionMessage.PASSWORDS_MUST_MATCH);
        }

        user.get().setPassword(passwordEncoder.encode(passwordResetRequest.getNewPassword()));
        userRepository.save(user.get());
    }

    private LocalDateTime getExpirationTime(String token) {
        return jwtService.extractExpiration(token).toInstant()
                         .atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
