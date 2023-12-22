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
        final String bearerToken = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        if (bearerToken == null || bearerToken.isBlank() || !bearerToken.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = jwtService.extractJwt(bearerToken);
        if (tokenExpired(response, jwt)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            ExceptionResponseDTO res = ExceptionResponseDTO.builder()
                                                           .message("Token expired")
                                                           .status(HttpServletResponse.SC_UNAUTHORIZED)
                                                           .build();
            new ObjectMapper().writeValue(response.getOutputStream(), res);
            return;
        }

        userEmail = getUsername(response, jwt);
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

    private boolean tokenExpired(HttpServletResponse response, String jwt) throws IOException {
        Date expiryTime = new Date();
        try {
            DecodedJWT decodedJWT = JWT.decode(jwt);
            expiryTime = decodedJWT.getExpiresAt();
        } catch (JWTDecodeException ex) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Failed to parse your token");
        }

        return expiryTime.before(new Date());
    }

    private String getUsername(HttpServletResponse response, String jwt) throws IOException {
        String username = "";
        try {
            username = jwtService.extractUsername(jwt);
        } catch (MalformedJwtException ex) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid JWT");
        }
        return username;
    }
}
