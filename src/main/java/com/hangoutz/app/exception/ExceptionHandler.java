package com.hangoutz.app.exception;

import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
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

    @org.springframework.web.bind.annotation.ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleNotFoundException(IllegalArgumentException ex) {
        Map<String, Object> errors = new HashMap<>();
        errors.put("title", ex.getMessage());
        errors.put("status", HttpStatus.BAD_REQUEST.toString());
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFoundException(NotFoundException ex) {
        Map<String, Object> errors = new HashMap<>();
        errors.put("title", ex.getMessage());
        errors.put("status", HttpStatus.NOT_FOUND.toString());
        return new ResponseEntity<>(errors, HttpStatus.NOT_FOUND);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<Map<String, Object>> handleDateTimeParseException(DateTimeParseException ex) {
        Map<String, Object> errors = new HashMap<>();
        errors.put("title", "Invalid date format. Please use: yyyy-MM-dd HH:mm");
        errors.put("status", HttpStatus.BAD_REQUEST.toString());
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentialsException(BadCredentialsException ex) {
        Map<String, Object> errors = new HashMap<>();
        errors.put("title", ex.getMessage());
        errors.put("status", HttpStatus.UNAUTHORIZED.toString());
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequestException(BadRequestException ex) {
        Map<String, Object> errors = new HashMap<>();
        errors.put("title", ex.getMessage());
        errors.put("status", HttpStatus.UNAUTHORIZED.toString());
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}
