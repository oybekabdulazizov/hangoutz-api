package com.hangoutz.app.service;

import com.hangoutz.app.exception.BadRequestException;
import com.hangoutz.app.exception.ExceptionMessage;
import com.hangoutz.app.exception.InternalServerException;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
}
