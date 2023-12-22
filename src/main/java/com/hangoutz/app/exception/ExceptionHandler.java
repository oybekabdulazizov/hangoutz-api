package com.hangoutz.app.exception;

import com.hangoutz.app.dto.ExceptionResponseDTO;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, Object> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String field = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(field, message);
        });
        Map<String, Object> errResponse = new HashMap<>();
        errResponse.put("title", "Validation Failed");
        errResponse.put("status", HttpStatus.BAD_REQUEST.toString());
        errResponse.put("errors", errors);
        return new ResponseEntity<>(errResponse, HttpStatus.BAD_REQUEST);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler({
            IllegalArgumentException.class,
            BadRequestException.class
    })
    public ResponseEntity<ExceptionResponseDTO> handleIllegalArgumentException(Exception ex) {
        ExceptionResponseDTO res = ExceptionResponseDTO.builder()
                                                       .message(ex.getMessage())
                                                       .status(HttpStatus.BAD_REQUEST.value())
                                                       .build();
        return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ExceptionResponseDTO> handleNotFoundException(NotFoundException ex) {
        ExceptionResponseDTO res = ExceptionResponseDTO.builder()
                                                       .message(ex.getMessage())
                                                       .status(HttpStatus.NOT_FOUND.value())
                                                       .build();
        return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<ExceptionResponseDTO> handleDateTimeParseException(DateTimeParseException ex) {
        ExceptionResponseDTO res = ExceptionResponseDTO.builder()
                                                       .message("Invalid date format. Please use: yyyy-MM-dd HH:mm")
                                                       .status(HttpStatus.BAD_REQUEST.value())
                                                       .build();
        return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler({
            BadCredentialsException.class,
            InternalAuthenticationServiceException.class
    })
    public ResponseEntity<ExceptionResponseDTO> handleAuthException(Exception ex) {
        ExceptionResponseDTO res = ExceptionResponseDTO.builder()
                                                       .message(ex.getMessage())
                                                       .status(HttpStatus.UNAUTHORIZED.value())
                                                       .build();
        return new ResponseEntity<>(res, HttpStatus.UNAUTHORIZED);
    }
}
