package com.hangoutz.app.service;

import com.hangoutz.app.dto.*;
import com.hangoutz.app.exception.AuthException;
import com.hangoutz.app.exception.BadRequestException;
import com.hangoutz.app.exception.ExceptionMessage;
import com.hangoutz.app.exception.NotFoundException;
import com.hangoutz.app.mappers.UserMapper;
import com.hangoutz.app.model.Role;
import com.hangoutz.app.model.Token;
import com.hangoutz.app.model.TokenType;
import com.hangoutz.app.model.User;
import com.hangoutz.app.repository.TokenRepository;
import com.hangoutz.app.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.hangoutz.app.service.UtilService.*;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public JwtAuthResponseDTO signUp(SignUpRequestDTO signUpRequest) {
        checkEmailIsValid(signUpRequest.getEmail());
        checkIfUserEmailAlreadyTaken(signUpRequest.getEmail());

        Role role = signUpRequest.getEmail().contains("@hangoutz.com") ? Role.ROLE_ADMIN : Role.ROLE_USER;
        User user = userMapper.toModel(signUpRequest);

        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        user.setLastModifiedAt(now);

        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        user.setRole(role);
        userRepository.save(user);

        String sessionToken = jwtService.generateSessionToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        saveUserToken(user, sessionToken, TokenType.SESSION);
        saveUserToken(user, refreshToken, TokenType.REFRESH);

        UserProfile userProfile = UserProfile.builder()
                                             .id(user.getId())
                                             .name(user.getName())
                                             .lastname(user.getLastname())
                                             .email(user.getEmail())
                                             .build();

        return new JwtAuthResponseDTO(
                userProfile,
                sessionToken,
                refreshToken,
                getExpirationTime(sessionToken),
                getExpirationTime(refreshToken)
        );
    }

    @Override
    @Transactional
    public JwtAuthResponseDTO logIn(LogInRequestDTO logInRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(logInRequest.getEmail(), logInRequest.getPassword())
            );
        } catch (InternalAuthenticationServiceException | BadCredentialsException ex) {
            throw new AuthException(ExceptionMessage.BAD_CREDENTIALS);
        }

        User user = getUserByUsername(logInRequest.getEmail());
        deleteAllTokensOfUser(user);

        String sessionToken = jwtService.generateSessionToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        saveUserToken(user, sessionToken, TokenType.SESSION);
        saveUserToken(user, refreshToken, TokenType.REFRESH);

        UserProfile userProfile = UserProfile.builder()
                                             .id(user.getId())
                                             .name(user.getName())
                                             .lastname(user.getLastname())
                                             .email(user.getEmail())
                                             .build();

        return new JwtAuthResponseDTO(userProfile,
                                      sessionToken,
                                      refreshToken,
                                      getExpirationTime(sessionToken),
                                      getExpirationTime(refreshToken)
        );
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordDTO passwordResetRequest) {
        // find the user using the passwordResetRequest properties
        Optional<User> user = userRepository.findByEmail(passwordResetRequest.getEmail());

        if (user.isEmpty()
                || !passwordEncoder.matches(passwordResetRequest.getOldPassword(), user.get().getPassword())
        ) throw new AuthException(ExceptionMessage.BAD_CREDENTIALS);

        if (!passwordResetRequest.getNewPassword().equals(passwordResetRequest.getConfirmNewPassword()))
            throw new BadRequestException(ExceptionMessage.PASSWORDS_MUST_MATCH);

        user.get().setPassword(passwordEncoder.encode(passwordResetRequest.getNewPassword()));
        userRepository.save(user.get());
    }

    @Override
    @Transactional
    public JwtAuthResponseDTO refreshSessionToken(HttpServletRequest request) {
        String refreshToken = Arrays.stream(request.getCookies())
                                    .map((c) -> c.getName().equals("refreshToken") ? c.getValue() : "")
                                    .collect(Collectors.joining(""));
        System.out.println("received the refresh token: " + refreshToken);
        String userEmail;

        System.out.println("attempting to look for and fetch the refresh token from the db...");
        Token token = tokenRepository.findByToken(refreshToken).orElse(null);
        System.out.println("refresh token: ");
        if (token != null) System.out.println(token.getToken());
        if (token == null) throw new AuthException(ExceptionMessage.INVALID_TOKEN);

        Date expirtyDate = getExpiryDate(refreshToken);
        if (expirtyDate == null || expirtyDate.before(new Date())) {
            throw new AuthException(ExceptionMessage.TOKEN_EXPIRED);
        }

        userEmail = getUsername(jwtService, refreshToken);
        if (userEmail == null || userEmail.isBlank()) throw new AuthException(ExceptionMessage.INVALID_TOKEN);


        User user = userRepository.findByEmail(userEmail).orElse(null);
        if (user == null) throw new AuthException(ExceptionMessage.INVALID_TOKEN);

        tokenRepository.deleteSessionTokenOfUser(user.getId());

        UserProfile userProfile = UserProfile.builder()
                                             .id(user.getId())
                                             .name(user.getName())
                                             .lastname(user.getLastname())
                                             .email(user.getEmail())
                                             .build();

        String sessionToken = jwtService.generateSessionToken(user);
        saveUserToken(user, sessionToken, TokenType.SESSION);
        return JwtAuthResponseDTO.builder()
                                 .user(userProfile)
                                 .sessionToken(sessionToken)
                                 .refreshToken(refreshToken)
                                 .sessionTokenExpiresAt(getExpirationTime(sessionToken))
                                 .refreshTokenExpiresAt(getExpirationTime(refreshToken))
                                 .build();
    }

    @Override
    @Transactional
    public void logOut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String sessionToken;
        final String userEmail;

        if (bearerToken == null || bearerToken.isBlank() || !bearerToken.startsWith("Bearer ")) {
            returnInvalidTokenResponse(response, ExceptionMessage.INVALID_TOKEN);
            return;
        }

        sessionToken = jwtService.extractJwt(bearerToken);
        Token token = tokenRepository.findByToken(sessionToken).orElse(null);
        if (token == null) {
            returnInvalidTokenResponse(response, ExceptionMessage.TOKEN_EXPIRED);
            return;
        }

        Date expirtyDate = getExpiryDate(sessionToken);
        if (expirtyDate == null || expirtyDate.before(new Date())) {
            returnInvalidTokenResponse(response, ExceptionMessage.TOKEN_EXPIRED);
            return;
        }

        userEmail = getUsername(jwtService, sessionToken);
        if (userEmail == null) {
            returnInvalidTokenResponse(response, ExceptionMessage.INVALID_TOKEN);
            return;
        }

        User user = userRepository.findByEmail(userEmail).orElse(null);
        if (user == null) {
            returnInvalidTokenResponse(response, ExceptionMessage.USER_NOT_FOUND);
            return;
        }

        if (!user.getTokens().isEmpty()) tokenRepository.deleteAllTokensOfUser(user.getId());
        System.out.println(SecurityContextHolder.getContext());
        SecurityContextHolder.clearContext();
        System.out.println(SecurityContextHolder.getContext());
    }


    private void saveUserToken(User user, String token, TokenType tokenType) {
        Token t = Token.builder()
                       .token(token)
                       .type(tokenType)
                       .user(user)
                       .build();
        tokenRepository.save(t);
    }

    private void deleteAllTokensOfUser(User user) {
        tokenRepository.deleteAllTokensOfUser(user.getId());
    }

    private void checkIfUserEmailAlreadyTaken(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new BadRequestException(ExceptionMessage.EMAIL_TAKEN);
        }
    }

    private User getUserByUsername(String username) {
        Optional<User> user = userRepository.findByEmail(username);
        if (user.isEmpty()) throw new NotFoundException(ExceptionMessage.USER_NOT_FOUND);
        return user.get();
    }

    private LocalDateTime getExpirationTime(String token) {
        return jwtService.extractExpiration(token).toInstant()
                         .atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
