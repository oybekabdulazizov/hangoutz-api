package com.hangoutz.app.service;

import com.hangoutz.app.exception.ExceptionMessage;
import com.hangoutz.app.model.Token;
import com.hangoutz.app.model.User;
import com.hangoutz.app.repository.TokenRepository;
import com.hangoutz.app.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import java.util.Date;

import static com.hangoutz.app.service.UtilService.*;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @SneakyThrows
    @Override
    @Transactional
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
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
    }
}
