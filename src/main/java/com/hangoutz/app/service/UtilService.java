package com.hangoutz.app.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hangoutz.app.dto.ExceptionResponseDTO;
import com.hangoutz.app.exception.BadRequestException;
import com.hangoutz.app.exception.ExceptionMessage;
import com.hangoutz.app.exception.InternalServerException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class UtilService {

    public static void checkEmailIsValid(String email) {
        String regex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        if (!matcher.matches()) throw new BadRequestException(ExceptionMessage.INVALID_EMAIL);
    }

    public static Map<String, Object> getMapFromObject(Object obj) {
        Map<String, Object> map = new HashMap<>();
        Field[] fields = obj.getClass().getDeclaredFields();
        try {
            for (Field field : fields) {
                field.setAccessible(true);
                var value = field.get(obj);
                // ignores if null, but throws an error if is blank
                if (value != null) {
                    if (value.toString().isBlank()) {
                        throw new BadRequestException(field.getName() + " is required");
                    }
                    map.put(field.getName(), field.get(obj));
                }
            }
        } catch (IllegalAccessException ex) {
            throw new InternalServerException(ex.getMessage());
        }
        return map;
    }

    public static Date getExpiryDate(String jwt) {
        try {
            DecodedJWT decodedJWT = JWT.decode(jwt);
            return decodedJWT.getExpiresAt();
        } catch (JWTDecodeException ex) {
            return null;
        }
    }

    public static String getUsername(JwtService jwtService, String jwt) {
        try {
            return jwtService.extractUsername(jwt);
        } catch (MalformedJwtException ex) {
            return null;
        }
    }

    public static void returnInvalidTokenResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ExceptionResponseDTO res = ExceptionResponseDTO.builder()
                                                       .message(message)
                                                       .status(HttpServletResponse.SC_UNAUTHORIZED)
                                                       .build();
        new ObjectMapper().writeValue(response.getOutputStream(), res);
    }
}
