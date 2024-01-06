package com.hangoutz.app.filter;

import com.hangoutz.app.exception.ExceptionMessage;
import com.hangoutz.app.model.Token;
import com.hangoutz.app.model.User;
import com.hangoutz.app.repository.TokenRepository;
import com.hangoutz.app.repository.UserRepository;
import com.hangoutz.app.service.JwtService;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;

import static com.hangoutz.app.service.UtilService.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            @Nonnull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @Nonnull FilterChain filterChain
    ) throws ServletException, IOException {
        final String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String jwt;
        final String userEmail;

        if (bearerToken == null || bearerToken.isBlank() || !bearerToken.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = jwtService.extractJwt(bearerToken);
        Token token = tokenRepository.findByToken(jwt).orElse(null);
        if (token == null) {
            returnInvalidTokenResponse(response, ExceptionMessage.TOKEN_EXPIRED);
            return;
        }

        Date expirtyDate = getExpiryDate(jwt);
        if (expirtyDate == null || expirtyDate.before(new Date())) {
            returnInvalidTokenResponse(response, ExceptionMessage.TOKEN_EXPIRED);
            return;
        }

        userEmail = getUsername(jwtService, jwt);
        if (userEmail == null) {
            returnInvalidTokenResponse(response, ExceptionMessage.INVALID_TOKEN);
            return;
        }

        if (!userEmail.isBlank()
                && SecurityContextHolder.getContext().getAuthentication() == null) {
            Optional<User> user = userRepository.findByEmail(userEmail);
            if (user.isEmpty()) {
                returnInvalidTokenResponse(response, ExceptionMessage.INVALID_TOKEN);
                return;
            }

            if (jwtService.isTokenValid(jwt, user.get())) {
                SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(user.get(),
                                                                null,
                                                                user.get().getAuthorities());

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                securityContext.setAuthentication(authToken);
                SecurityContextHolder.setContext(securityContext);
            }
        }
        filterChain.doFilter(request, response);
    }
}
