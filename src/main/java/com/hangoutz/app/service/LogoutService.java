package com.hangoutz.app.service;

import com.hangoutz.app.exception.ExceptionMessage;
import com.hangoutz.app.exception.NotFoundException;
import com.hangoutz.app.model.User;
import com.hangoutz.app.repository.TokenRepository;
import com.hangoutz.app.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Override
    @Transactional
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        final String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String sessionToken;
        final String userEmail;

        if (bearerToken == null || bearerToken.isBlank() || !bearerToken.startsWith("Bearer ")) {
            return;
        }

        sessionToken = jwtService.extractJwt(bearerToken);
        userEmail = jwtService.extractUsername(sessionToken);
        User user = userRepository.findByEmail(userEmail).orElse(null);
        if (user == null) throw new NotFoundException(ExceptionMessage.USER_NOT_FOUND);
        if (!user.getTokens().isEmpty()) tokenRepository.deleteAllTokensOfUser(user.getId());
    }
}
