package com.hangoutz.app.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hangoutz.app.dto.ExceptionResponseDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AccessDeniedExceptionHandler implements AccessDeniedHandler {

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    )
            throws IOException, ServletException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        ExceptionResponseDTO res = ExceptionResponseDTO.builder()
                                                       .message(accessDeniedException.getMessage())
                                                       .status(HttpServletResponse.SC_UNAUTHORIZED)
                                                       .build();
        new ObjectMapper().writeValue(response.getOutputStream(), res);
    }
}
