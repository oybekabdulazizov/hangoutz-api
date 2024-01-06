package com.hangoutz.app.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.hangoutz.app.dto.JwtAuthResponseDTO;
import com.hangoutz.app.dto.ResetPasswordDTO;
import com.hangoutz.app.dto.SignInRequestDTO;
import com.hangoutz.app.dto.SignUpRequestDTO;
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
import io.jsonwebtoken.MalformedJwtException;
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
import java.util.Date;
import java.util.Optional;

import static com.hangoutz.app.service.UtilService.checkEmailIsValid;

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

        return new JwtAuthResponseDTO(sessionToken,
                                      refreshToken,
                                      getExpirationTime(sessionToken),
                                      getExpirationTime(refreshToken)
        );
    }

    @Override
    @Transactional
    public JwtAuthResponseDTO signIn(SignInRequestDTO signInRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(signInRequest.getEmail(), signInRequest.getPassword())
            );
        } catch (InternalAuthenticationServiceException | BadCredentialsException ex) {
            throw new AuthException(ExceptionMessage.BAD_CREDENTIALS);
        }

        User user = getUserByUsername(signInRequest.getEmail());
        deleteAllTokensOfUser(user);

        String sessionToken = jwtService.generateSessionToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        saveUserToken(user, sessionToken, TokenType.SESSION);
        saveUserToken(user, refreshToken, TokenType.REFRESH);

        return new JwtAuthResponseDTO(sessionToken,
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
    public JwtAuthResponseDTO refreshSessionToken(String refreshBearerToken) {
        final String refreshToken;
        final String userEmail;

        if (refreshBearerToken == null || refreshBearerToken.isBlank() || !refreshBearerToken.startsWith("Bearer "))
            throw new AuthException(ExceptionMessage.INVALID_TOKEN);

        refreshToken = jwtService.extractJwt(refreshBearerToken);
        Token token = tokenRepository.findByToken(refreshToken).orElse(null);
        if (token == null) throw new AuthException(ExceptionMessage.INVALID_TOKEN);

        Date expirtyDate = getExpiryDate(refreshToken);
        if (expirtyDate == null || expirtyDate.before(new Date())) {
            throw new AuthException(ExceptionMessage.TOKEN_EXPIRED);
        }

        userEmail = getUsername(refreshToken);
        if (userEmail == null || userEmail.isBlank()) throw new AuthException(ExceptionMessage.INVALID_TOKEN);


        User user = userRepository.findByEmail(userEmail).orElse(null);
        if (user == null) throw new AuthException(ExceptionMessage.INVALID_TOKEN);

        tokenRepository.deleteSessionTokenOfUser(user.getId());

        String sessionToken = jwtService.generateSessionToken(user);
        saveUserToken(user, sessionToken, TokenType.SESSION);
        return JwtAuthResponseDTO.builder()
                                 .sessionToken(sessionToken)
                                 .refreshToken(refreshToken)
                                 .sessionTokenExpiresAt(getExpirationTime(sessionToken))
                                 .refreshTokenExpiresAt(getExpirationTime(refreshToken))
                                 .build();
    }


    private String getUsername(String jwt) {
        try {
            return jwtService.extractUsername(jwt);
        } catch (MalformedJwtException ex) {
            return null;
        }
    }

    private Date getExpiryDate(String jwt) {
        try {
            DecodedJWT decodedJWT = JWT.decode(jwt);
            return decodedJWT.getExpiresAt();
        } catch (JWTDecodeException ex) {
            return null;
        }
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
