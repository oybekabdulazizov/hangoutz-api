package com.hangoutz.app.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hangoutz.app.dto.ExceptionResponseDTO;
import com.hangoutz.app.service.JwtService;
import com.hangoutz.app.service.UserService;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;

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
        Date expirtyDate = getExpiryDate(jwt);
        if (expirtyDate == null || expirtyDate.before(new Date())) {
            returnInvalidTokenResponse(response);
            return;
        }
        userEmail = getUsername(jwt);
        if (userEmail == null) {
            returnInvalidTokenResponse(response);
            return;
        }

        if (userEmail != null
                && !userEmail.isBlank()
                && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userService.userDetailsService().loadUserByUsername(userEmail);

            if (jwtService.isTokenValid(jwt, userDetails)) {
                SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails,
                                                                null,
                                                                userDetails.getAuthorities());

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                securityContext.setAuthentication(authToken);
                SecurityContextHolder.setContext(securityContext);
            }
        }
        filterChain.doFilter(request, response);
    }

    private Date getExpiryDate(String jwt) {
        try {
            DecodedJWT decodedJWT = JWT.decode(jwt);
            return decodedJWT.getExpiresAt();
        } catch (JWTDecodeException ex) {
            return null;
        }
    }

    private String getUsername(String jwt) {
        try {
            return jwtService.extractUsername(jwt);
        } catch (MalformedJwtException ex) {
            return null;
        }
    }

    private void returnInvalidTokenResponse(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ExceptionResponseDTO res = ExceptionResponseDTO.builder()
                                                       .message("Invalid token")
                                                       .status(HttpServletResponse.SC_UNAUTHORIZED)
                                                       .build();
        new ObjectMapper().writeValue(response.getOutputStream(), res);
    }
}
